#include <pdu.h>
#include "printf.h"
generic module CoapBuzzerResourceP(uint8_t uri_key) {
  provides interface ReadResource;
  provides interface WriteResource;
  uses interface GeneralIO as Buzzer;

} implementation {

  bool lock = FALSE;
  coap_tid_t temp_id;

  void task getBuzzer() {
    uint8_t val = call Buzzer.get();
    lock = FALSE;
    printf("\nCoapLedResourceP:Leds.get");
    signal ReadResource.getDone(SUCCESS, temp_id, 0,
				(uint8_t*)&val, sizeof(uint8_t));
  };

  command int ReadResource.get(coap_tid_t id) {
    if (lock == FALSE) {
      lock = TRUE;

      temp_id = id;
      post getBuzzer();
      return COAP_SPLITPHASE;
    } else {
      return COAP_RESPONSE_503;
    }
  }

  void task setBuzzerDone() {
    lock = FALSE;
    signal WriteResource.putDone(SUCCESS, temp_id, 0);
  };

  command int WriteResource.put(uint8_t *val, size_t buflen, coap_tid_t id) {
    if (*val < 8) {
      if (lock == FALSE) {
	lock = TRUE;
	temp_id = id;
	
	if(*val)
		call Buzzer.set();
	else
		call Buzzer.clr();
	post setBuzzerDone();
	return COAP_SPLITPHASE;
      } else {
	return COAP_RESPONSE_503;
      }
    } else {
      return COAP_RESPONSE_500;
    }
  }
}
