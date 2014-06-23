generic configuration CoapBuzzerResourceC(uint8_t uri_key) {
  provides interface ReadResource;
  provides interface WriteResource;

} implementation {
  components new CoapBuzzerResourceP(uri_key) as CoapBuzzerResourceP;

  ReadResource = CoapBuzzerResourceP;
  WriteResource = CoapBuzzerResourceP;

   components HplMsp430GeneralIOC as HplGeneralIOC;
   components new Msp430GpioC() as Buzzer;

   Buzzer -> HplGeneralIOC.Port23;
   CoapBuzzerResourceP.Buzzer -> Buzzer;

 
 }
