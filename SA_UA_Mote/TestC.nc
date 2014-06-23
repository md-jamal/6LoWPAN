
#include "blip_printf.h"
#include <lib6lowpan/ip.h>
#define SERVICE_REG_LIFETIME  	2	//Service Registration Lifetime
//#define LIB6LOWPAN_HC_VERSION 6
module TestC
{

	uses interface Boot;
 	uses interface Leds;	
	uses interface IPAddress;
	uses interface Timer<TMilli>;
	uses interface SplitControl;
	uses interface SplitControl as SSLPControl;
	uses interface SplitControl as NDControl;
	uses interface SSLPNode;
	uses interface ServiceLocation;

}



implementation
{

	struct in6_addr LinkLocal;
	char service[20];
	char scope[20];
	struct in6_addr ip;
	uint16_t port;
	uint16_t test_port=1234;
	uint8_t count_services;
	uint8_t stringlength(char *data)
	{
		uint8_t i;
		uint8_t count=0;
		for(i=0;*(data+i)!='\0';i++)
			count++;	
		return count;
	}
	event void  Boot.booted()
	{
		//call SSLPNode.setUA();
		call IPAddress.getLLAddr(&LinkLocal);
		//call SplitControl.start();
		call SSLPNode.setSA();		
		call NDControl.start();	


    	}
	

	event void NDControl.stopDone(error_t error)
 	{
  	}

	event void NDControl.startDone(error_t error)
 	{
			call Timer.startOneShot(2048);
					
  	}

	event void Timer.fired()
	{
		#ifdef PRINTFUART_ENABLED
			//call Node.printServices();
		#endif
		/*if(call Node.findService("moisture","cdac")==SUCCESS)	
			printf("\nService is advertised by Me");
		else
			printf("\n Service is not advertised by Me");

		if(call Node.removeService("temp")==SUCCESS)
			printf("\n humidity Service is removed");
		else
			printf("\n humidity Service cannot be removed");*/
		//call Node.getServices();
		call SSLPControl.start();
			call Leds.led1Toggle();	
		

	}

	event void SSLPControl.startDone(error_t error)
 	{
		/* If he is a SA he will add all the services he is advertising*/	
		#ifdef NODE_SA
			printf("\n Iam SA");
			call SSLPNode.addService("leds","default","service:leds:coap://2001::4:61616/l",SERVICE_REG_LIFETIME);
			call SSLPNode.addService("humidity","default","service:hum:coap://2001::4:61616/hum",SERVICE_REG_LIFETIME);
			//call Node.removeService("humidity","cdac","service:hum:coap://fec0::5:61616/hum");	
		#else
		/*If he is a UA he will request for the location of the service*/
			printf("\n Iam UA");
			//call ServiceLocation.findServices("moisture12","cdac12");
			//call ServiceLocation.findAllServices("cdac");
			call ServiceLocation.findServices("humidity","");
		#endif
  	}

	


	event void SSLPControl.stopDone(error_t error)
 	{
  	}
	event void IPAddress.changed(bool valid)
	{

	}
	
	 event void SplitControl.startDone(error_t error)
  	{
  	}
		
	 event void SplitControl.stopDone(error_t error)
  	{
  	}
	event void ServiceLocation.receiveServices(servicelocation_entry *services,uint8_t count){

		/*uint8_t i;	
		if(count)	
			for(i=0;i<count;i++)
				printf("\n Service URL:%s",(services+i)->url);
		else
			printf("\n No service with the following service type found");
		printfflush();
	*/
	}

	event void ServiceLocation.receiveServiceTypes(char *services)
	{

		/*if(!stringlength(services))
			printf("\n No services are present");
		else
			printf("\n services received are %s",services);
		printfflush();
	*/
	}
	

	
}
