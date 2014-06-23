/*
 * Copyright (c) 2011 University of Bremen, TZI
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * - Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the
 *   distribution.
 * - Neither the name of the copyright holders nor the names of
 *   its contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

#include <IPDispatch.h>
#include <lib6lowpan/lib6lowpan.h>
#include <lib6lowpan/ip.h>
#include "blip_printf.h"

#include "net.h"
#include "resource.h"

#ifdef COAP_CLIENT_ENABLED
#include "tinyos_net.h"
#include "option.h"
#include "address.h"
#endif

#ifndef COAP_SERVER_PORT
#define COAP_SERVER_PORT COAP_DEFAULT_PORT
#endif
#ifdef SSLP_CONTROL
#include "sslp.h"
#define SERVICE_REG_LIFETIME  	20	//Service Registration Lifetime
#endif
module CoapBlipP {
  uses {
    interface Boot;
    interface SplitControl as RadioControl;
    interface SplitControl as NDControl;
    interface Leds;

#ifdef COAP_SERVER_ENABLED
    interface CoAPServer;
#ifdef COAP_RESOURCE_KEY
    interface Mount;
#endif
#endif
#ifdef SSLP_CONTROL    
    interface SplitControl as SSLPControl; 
    interface SSLPNode;
    interface ServiceLocation;
    interface Timer<TMilli> as Timer;
#endif
#ifdef COAP_CLIENT_ENABLED
    interface CoAPClient;
    interface ForwardingTableEvents;
#endif
  }

} implementation {
#ifdef COAP_CLIENT_ENABLED
  uint8_t node_integrate_done = FALSE;
#endif
#ifdef SSLP_CONTROL
	reg_services services[MAX_SERVICE_ADVERTISE];
	uint8_t index=0;
	uint8_t timer_index=0;
	/*This function returns the length of the string*/
	uint8_t stringlength(char *data)
	{
		uint8_t i;
		uint8_t count=0;
		for(i=0;*(data+i)!='\0';i++)
			count++;	
		return count;
	}


   int addService(char *service_type,char *scope,char *url)
   {
	
	if(index==MAX_SERVICE_ADVERTISE)
	{
		return -1;
	}
	else{
		memset(&services[index],0,sizeof(services[index]));
		memcpy(&services[index].servicetype,service_type,stringlength(service_type));
		services[index].servicetype[stringlength(services[index].servicetype)]='\0';
		memcpy(&services[index].url,url,stringlength(url));
		services[index].url[stringlength(services[index].url)]='\0';
		memcpy(&services[index].scope,scope,stringlength(scope));
		services[index].scope[stringlength(services[index].scope)]='\0';
		return index++;
	}

   }
#endif
  event void Boot.booted() {

    call NDControl.start();
    printf("booted %i start\n", TOS_NODE_ID);
#ifdef COAP_SERVER_ENABLED
#ifdef COAP_RESOURCE_KEY
    if (call Mount.mount() == SUCCESS) {
      printf("CoapBlipP.Mount successful\n");
    }
#endif

    // needs to be before registerResource to setup context:
    call CoAPServer.setupContext(COAP_SERVER_PORT);
    call CoAPServer.registerResources();

#endif

#ifdef COAP_CLIENT_ENABLED
    // needs to be before registerResource to setup context:
    call CoAPClient.setupContext(COAP_CLIENT_PORT);
#endif

  }

#if defined (COAP_SERVER_ENABLED) && defined (COAP_RESOURCE_KEY)
  event void Mount.mountDone(error_t error) {
  }
#endif

  event void RadioControl.startDone(error_t e) {
    printf("radio startDone: %i\n", TOS_NODE_ID);
  }

  event void RadioControl.stopDone(error_t e) {
  }

  event void NDControl.stopDone(error_t error)
 	{
  	}

	event void NDControl.startDone(error_t error)
 	{
		#ifdef SSLP_CONTROL
    call SSLPNode.setSA();
    call SSLPControl.start();
    call Timer.startPeriodic(10000U);//10 seconds delay

#endif
  	}
#if defined SSLP_CONTROL
  event void SSLPControl.startDone(error_t error){
	printf("sslp start hogaya"); 
	//addService("temp","cdac","service:temp:coap://fec0::5:61616/st");
	addService("leds","cdac","service:leds:coap://fec0::5:61616/l");	//TODO:has to make generic by using TOS_NODE_ID
	//addService("hum","cdac","service:hum:coap://fec0::5:61616/sh");
	//addService("vol","cdac","service:vol:coap://fec0::5:61616/sv");
	

 }

  event void SSLPControl.stopDone(error_t error)
 {
 } //only used by UA
  event void ServiceLocation.receiveServices(servicelocation_entry *services_recvd,uint8_t count){

 }
  //only used by UA
  event void ServiceLocation.receiveServiceTypes(char *services_recvd){

  }
  event void Timer.fired()
  {
	if(timer_index<index)
	{	
		printf("timer Index is %d and service index is %d",timer_index,index);
		printf("\n adding the url:%s",services[timer_index].url);
		printfflush();
		call SSLPNode.addService(services[timer_index].servicetype,services[timer_index].scope,services[timer_index].url,SERVICE_REG_LIFETIME);
		
		//call Leds.set(timer_index);
		timer_index++;

	}


  }
#endif
#ifdef COAP_CLIENT_ENABLED
  event void ForwardingTableEvents.defaultRouteAdded() {
      //struct sockaddr_in6 sa6;
      coap_address_t dest;
      coap_list_t *optlist = NULL;

      if (node_integrate_done == FALSE) {
	  node_integrate_done = TRUE;

	  inet_pton6(COAP_CLIENT_DEST, &dest.addr.sin6_addr);
	  dest.addr.sin6_port = htons(COAP_CLIENT_PORT);

	  coap_insert( &optlist, new_option_node(COAP_OPTION_URI_PATH, sizeof("ni") - 1, "ni"), order_opts);

	  // this stuff should most likely be POST!
	  call CoAPClient.request(&dest, COAP_REQUEST_PUT, optlist, 0, NULL);
      }
  }

  event void ForwardingTableEvents.defaultRouteRemoved() {
  }

  event error_t CoAPClient.streamed_next_block (uint16_t blockno, uint16_t *len, void **data)
  {
    return FAIL;
  }

  event void CoAPClient.request_done(uint8_t code, uint16_t len, void *data) {
      //event void CoAPClient.request_done(uint8_t code, uint8_t mediatype, uint16_t len, void *data, bool more) {
    //TODO: handle the request_done
  };
#endif

  }
