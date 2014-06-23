#include <lib6lowpan/ip.h>
#include "printf.h"
#include "blip_printf.h"
//#define LIB6LOWPAN_HC_VERSION 6
#include "sslp.h"
#define SERVICE_REG_LIFETIME  	20	
module TestC
{

	uses interface Boot;
  	uses interface SplitControl as NDControl;
	uses interface SplitControl as SSLPControl;
 	uses interface Leds;	
	uses interface IPAddress;
	uses interface IP;
	uses interface Timer<TMilli> as Timer;	
	uses interface CoAPServer;
}



implementation
{

	event void  Boot.booted()
	{
		call NDControl.start();	
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
    	}

	void send()
	{
		struct ip6_packet pkt;
		struct in6_addr global;
		struct ip_iovec v[1];
		uint8_t data=123;
		uint8_t length=sizeof(data);
		v[0].iov_base=(uint8_t *)&data;
		v[0].iov_len=length;
		v[0].iov_next=NULL;
		
		pkt.ip6_hdr.ip6_nxt=IPV6_NONEXT;
		pkt.ip6_hdr.ip6_plen=htons(length);
		pkt.ip6_data=&v[0];
		call IPAddress.getGlobalAddr(&global);
		if(TOS_NODE_ID==5)
			inet_pton6("3000::6",&pkt.ip6_hdr.ip6_dst);		
		else
			inet_pton6("3000::5",&pkt.ip6_hdr.ip6_dst);
		
		memcpy(&pkt.ip6_hdr.ip6_src,&global,sizeof(struct in6_addr));
		#if defined NODE_HOST || NODE_ROUTER
			call IP.send(&pkt);
		#endif
	
	}

	event void NDControl.startDone(error_t error)
 	 {

		if(error!=SUCCESS){
			call Leds.led1On();
			}
		else{
			call Timer.startPeriodic(5000U);
			call Leds.led0On();
		}
		
  	}

	 event void SSLPControl.stopDone(error_t error)
 	{	
 	}
	

	 event void SSLPControl.startDone(error_t error)
 	{	
 	}
	event void Timer.fired()
	{
		printf("\n Timer fired");
		send();
	}

	event void IP.recv(struct ip6_hdr *hdr, void *packet, 
                  size_t len, struct ip6_metadata *meta)
	{
		if(*(uint8_t *)packet==123)
		{
			call Leds.led1Toggle();	
			printf("\n IP Packet received from with data:%d:",*(uint8_t *)packet);
			printf_in6addr(&hdr->ip6_src);	
		}
	}


	 event void IPAddress.changed(bool valid)
	{
	}

	event void NDControl.stopDone(error_t error)
 	{
  	}


	


}
