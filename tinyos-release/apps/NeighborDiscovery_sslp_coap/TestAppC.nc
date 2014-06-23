

configuration TestAppC {


}


implementation {

	components TestC as TC;
	components NDC;
  	TC.NDControl->NDC;
    	components MainC;
   	TC.Boot->MainC;
	components LedsC;
	TC.Leds->LedsC;
	#ifdef PRINTFUART_ENABLED	
		components PrintfC,SerialStartC;
	#endif
	components IPAddressC;
	TC.IPAddress->IPAddressC;
	
	components IPStackC;
	TC.IP->IPStackC.IP[IPV6_NONEXT];

	components new TimerMilliC() as Timer;
	TC.Timer->Timer;
	components SSLC;
	TC.SSLPControl->SSLC;
	components CoapUdpServerC;
	  components new UdpSocketC() as UdpServerSocket;
  CoapBlipP.CoAPServer -> CoapUdpServerC;
  CoapUdpServerC.LibCoapServer -> LibCoapAdapterC.LibCoapServer;
  CoapUdpServerC.Init <- MainC.SoftwareInit;
  LibCoapAdapterC.UDPServer -> UdpServerSocket;
	
}
