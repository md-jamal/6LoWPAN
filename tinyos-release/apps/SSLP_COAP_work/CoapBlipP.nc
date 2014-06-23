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

#ifdef COAP_CLIENT_ENABLED
#include "tinyos_net.h"
#endif
#ifdef SSLP_CONTROL
#include "sslp.h"
#define SERVICE_REG_LIFETIME  	2	//Service Registration Lifetime
#endif
module CoapBlipP {
  uses {
    interface Boot;
    interface SplitControl as RadioControl;
    interface Leds;
    interface SplitControl as NDControl;
#ifdef SSLP_CONTROL    
    interface SplitControl as SSLPControl; 
    interface SSLPNode;
    interface ServiceLocation;
    interface Timer<TMilli> as Timer;
    interface IPAddress;
#endif
#ifdef COAP_SERVER_ENABLED
    interface CoAPServer;
#ifdef COAP_RESOURCE_KEY
    interface Mount;
#endif
#endif

#ifdef COAP_CLIENT_ENABLED
    interface CoAPClient;
    interface ForwardingTableEvents;
#endif
  }

  provides interface Init;

} implementation {
#ifdef COAP_CLIENT_ENABLED
  uint8_t node_integrate_done = FALSE;
#endif
#ifdef SSLP_CONTROL
	reg_services services[MAX_SERVICE_ADVERTISE];
	uint8_t index=0;
	uint8_t timer_index=0;
	char service_name[100];
	struct in6_addr global_addr;
	char buff[64];	
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

	
	//This function converts ip address to string
	char * IPtoString(struct in6_addr *ip)
	{
		inet_ntop6(ip,buff,64);
		
		return buff;
	}

	void generateService(char *service,char *service_type,char *url)
	{
			

		char IP_ADDRESS[64];


		//char service_type[30]="service:buzzer:coap://";
		memset(&IP_ADDRESS,0,sizeof(IP_ADDRESS));
		//memset(&buff,0,sizeof(buff));
		memset(&service_name,0,sizeof(service_name));
			call IPAddress.getGlobalAddr(&global_addr);
		if(global_addr.s6_addr[0]==0x0)
		{

			//call Leds.led1On();
			//call Leds.led2On();
			memset(&global_addr, 0, sizeof(global_addr));
   			inet_pton6(IN6_PREFIX, &global_addr);
    			global_addr.s6_addr16[7] = htons(TOS_NODE_ID);
    			call IPAddress.setAddress(&global_addr);
		}
		memcpy(&service_name[stringlength(service_name)],service_type,stringlength(service_type));
		memcpy(IP_ADDRESS,IPtoString(&global_addr),stringlength(IPtoString(&global_addr)));
		//printf("IPADDRESS:%s",IP_ADDRESS);printfflush();
		
		memcpy(&service_name[stringlength(service_name)],&IP_ADDRESS,stringlength(IP_ADDRESS));
		memcpy(&service_name[stringlength(service_name)],url,stringlength(url));
		
		service_name[stringlength(service_name)]='\0';


	}
#endif
  command error_t Init.init() {
   
    return SUCCESS;
  }

  event void Boot.booted() {
#ifdef COAP_SERVER_ENABLED
    uint8_t i;
#endif
    call NDControl.start();
   // printf("booted %i start\n", TOS_NODE_ID);

#ifdef COAP_SERVER_ENABLED
#ifdef COAP_RESOURCE_KEY
    if (call Mount.mount() == SUCCESS) {
     // printf("CoapBlipP.Mount successful\n");
    }
#endif
    // needs to be before registerResource to setup context:
    call CoAPServer.bind(COAP_SERVER_PORT);

    call CoAPServer.registerWellknownCore();
    for (i=0; i < NUM_URIS; i++) {
      call CoAPServer.registerResource(uri_key_map[i].uri,
				       uri_key_map[i].urilen - 1,
				       uri_key_map[i].mediatype,
				       uri_key_map[i].writable,
				       uri_key_map[i].splitphase,
				       uri_key_map[i].immediately);
    }
#endif

  }

#if defined (COAP_SERVER_ENABLED) && defined (COAP_RESOURCE_KEY)
  event void Mount.mountDone(error_t error) {
  }
#endif

#if defined SSLP_CONTROL
  event void SSLPControl.startDone(error_t error){
	//printf("sslp start hogaya"); 
	//addService("temp","cdac","service:temp:coap://fec0::5:61616/st");
	char service_type[30]="service:buzzer:coap://";
	char service_type1[30]="service:leds:coap://";
	char url[10]=":61616/b";
	char url1[10]=":61616/l";

	generateService(service_name,service_type,url);

	addService("buzzer","default",service_name);
	generateService(service_name,service_type1,url1);	
	addService("leds","default",service_name);
						call Leds.led1On();	
	//addService("buzzer","cdac","service:buzzer:coap://2001::b:61616/b");	//TODO:has to make generic by using TOS_NODE_ID
	//addService("leds","cdac","service:leds:coap://2001::b:61616/l");
	//addService("hum","cdac","service:hum:coap://fec0::5:61616/sh");
	//addService("vol","cdac","service:vol:coap://fec0::5:61616/sv");
	

 }

  event void SSLPControl.stopDone(error_t error)
 {
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
  //only used by UA
  event void ServiceLocation.receiveServices(servicelocation_entry *services_recvd,uint8_t count){

 }
  //only used by UA
  event void ServiceLocation.receiveServiceTypes(char *services_recvd){

  }
  event void Timer.fired()
  {
	if(timer_index<index)
	{	
		//printf("timer Index is %d and service index is %d",timer_index,index);
		//printf("\n adding the url:%s",services[timer_index].url);
		//printfflush();
		call SSLPNode.addService(services[timer_index].servicetype,services[timer_index].scope,services[timer_index].url,SERVICE_REG_LIFETIME);
		
		//call Leds.set(timer_index);
		timer_index++;

	}


  }

  event void IPAddress.changed(bool valid){}
#endif
  event void RadioControl.startDone(error_t e) {
 //   printf("radio startDone: %i\n", TOS_NODE_ID);
  }

  event void RadioControl.stopDone(error_t e) {
  }

#ifdef COAP_CLIENT_ENABLED
  event void ForwardingTableEvents.defaultRouteAdded() {
    struct sockaddr_in6 sa6;
    coap_list_t *optlist = NULL;

    if (node_integrate_done == FALSE) {
      node_integrate_done = TRUE;

      inet_pton6(COAP_CLIENT_DEST, &sa6.sin6_addr);
      sa6.sin6_port = htons(COAP_CLIENT_PORT);

      coap_insert( &optlist, new_option_node(COAP_OPTION_URI_PATH, sizeof("ni") - 1, "ni"), order_opts);

      call CoAPClient.request(&sa6, COAP_REQUEST_PUT, optlist, 0, NULL);
    }
  }

  event void ForwardingTableEvents.defaultRouteRemoved() {
  }

  event error_t CoAPClient.streamed_next_block (uint16_t blockno, uint16_t *len, void **data)
  {
    return FAIL;
  }

  event void CoAPClient.request_done(uint8_t code, uint8_t mediatype, uint16_t len, void *data, bool more) {
    //TODO: handle the request_done
  };
#endif

  }
