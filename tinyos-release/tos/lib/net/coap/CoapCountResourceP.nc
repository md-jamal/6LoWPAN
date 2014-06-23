
#include <pdu.h>
generic module CoapCountResourceP(uint8_t uri_key)
{

	provides interface ReadResource;
	provides interface WriteResource;

}


implementation
{
	
	uint8_t count=0;
	bool lock=FALSE;
	coap_tid_t temp_id;

	void task getCount()
	{	
		lock=FALSE;
		signal ReadResource.getDone(SUCCESS,temp_id,0,(uint8_t *)&count,sizeof(uint8_t));


	}
	command int ReadResource.get(coap_tid_t id)
	{
		if(lock==FALSE){
			lock=TRUE;
			temp_id = id;
			post getCount();
			return COAP_SPLITPHASE;
		}
		else{
			return COAP_RESPONSE_503;
		    }
	}

	void task setCount()
	{
		lock=FALSE;
		count=*val;
		signal WriteResource.putDone(SUCCESS,temp_id,0);

	}


	command int ReadResource.put(uint8_t *val,ssize_t buflen,coap_tid_t id)
	{
		if(lock==FALSE){
			lock=TRUE;	
			temp_id = id;
			post setCount();
			return COAP_SPLITPHASE;
		}else{
			return COAP_RESPONSE_503;
		}


	}





}
