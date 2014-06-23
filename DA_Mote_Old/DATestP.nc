


module DATestP {
  uses {
    interface Boot;
    interface SplitControl as RadioControl;
    interface Leds;
    interface SplitControl as SSLPControl;
    interface SplitControl as NDControl;
  #ifdef RPL_ROUTING
    interface RootControl;
  #endif
  #ifdef LCD_DEBUG
	interface Lcd;
  #endif
  }

} 


implementation
{

	event void Boot.booted() {


		//call RadioControl.start();
	  #ifdef RPL_ROUTING
	      call RootControl.setRoot();
	  #endif
		call NDControl.start();

	#ifdef LCD_DEBUG
		call Lcd.lcdInit();
		call Lcd.lcdContrast(0x70);
	#endif

	}

	event void NDControl.stopDone(error_t error)
 	{
  	}

	event void NDControl.startDone(error_t error)
 	{
		call SSLPControl.start();
  	}


	 event void RadioControl.startDone(error_t e) {
		/*#ifdef LCD_DEBUG
			call Lcd.lcdClear();
			call Lcd.lcdGotoxy(1,2);
			call Lcd.lcdStr("Radio Started");
		#endif
		*/
		call SSLPControl.start();
	  }

	  event void RadioControl.stopDone(error_t e) {
	
	  }


	event void SSLPControl.startDone(error_t e) {

	  }

	  event void SSLPControl.stopDone(error_t e) {

	  }






}
