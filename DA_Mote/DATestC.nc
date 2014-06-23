



configuration DATestC {

}

implementation
{

  components MainC, LedsC;

  components IPStackC;

  components DATestP;
  DATestP.Boot-> MainC;
  DATestP.Leds-> LedsC;
  DATestP.RadioControl -> IPStackC;




 

  components SSLC_DA;
  DATestP.SSLPControl -> SSLC_DA;

  components NDC;
  DATestP.NDControl->NDC;




  #ifdef RPL_ROUTING
  components RPLRoutingC,RPLRoutingEngineC;
    DATestP.RootControl -> RPLRoutingEngineC;
#endif

#ifdef LCD_DEBUG
	components LcdC;
	DATestP.Lcd -> LcdC;
#endif

  #ifdef PRINTFUART_ENABLED

	components PrintfC,SerialStartC;

  #endif


}
