

configuration TestAppC {


}


implementation {

	components TestC as TC;
    	components MainC;
   	TC.Boot->MainC;
	components LedsC;
	TC.Leds->LedsC;

	components SSLPNodeC;
	TC.SSLPNode->SSLPNodeC;

	#ifdef PRINTFUART_ENABLED	
	components PrintfC,SerialStartC;
	#endif
	components IPAddressC;
	TC.IPAddress->IPAddressC;
	
	components new TimerMilliC() as Timer;
	TC.Timer->Timer;	

	components IPStackC;
	TC.SplitControl->IPStackC;
	
	 components NDC;
	 TC.NDControl->NDC;



	components SSLC;
	TC.SSLPControl->SSLC;
	TC.ServiceLocation -> SSLC;


}
