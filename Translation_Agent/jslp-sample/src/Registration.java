import ch.ethz.iks.slp.Advertiser;
import ch.ethz.iks.slp.ServiceLocationManager;
import ch.ethz.iks.slp.ServiceLocationException;
import ch.ethz.iks.slp.ServiceURL;
import java.util.Locale;
import java.util.Hashtable;


public class Registration {

public static void main(String[] args) throws ServiceLocationException {
	// get Advertiser instance

	Advertiser advertiser = ServiceLocationManager.getAdvertiser(new Locale("en"));
//public static final Context ctx=this;
	// the service has lifetime 60, that means it will only persist for one minute
	ServiceURL myService = new ServiceURL("service:temp:myService://my.host.ch", 600);
	ServiceURL myService1 = new ServiceURL("service:leds:coap://[fec0::5]:61616/l", 600);
	// some attributes for the service
	Hashtable attributes = new Hashtable();
	attributes.put("persistent", Boolean.TRUE);
	attributes.put("cool", "yes");
	attributes.put("max-connections", new Integer(5));

//	advertiser.register(myService1, attributes);
advertiser.register(myService,attributes);

	//advertiser.deregister(myService,null);
 	Hashtable attributes1 = new Hashtable();
	attributes1.put("persistent", Boolean.TRUE);
	attributes1.put("room2", "yes");
	attributes1.put("max-connections", new Integer(5));
	advertiser.register(myService1,attributes1);
	//System.out.println("Registered service:test:myService");*/
}

}
