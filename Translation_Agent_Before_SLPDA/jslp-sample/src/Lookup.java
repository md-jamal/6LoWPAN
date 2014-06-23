import ch.ethz.iks.slp.Locator;
import ch.ethz.iks.slp.ServiceLocationManager;
import ch.ethz.iks.slp.ServiceLocationEnumeration;
import ch.ethz.iks.slp.ServiceType;
import ch.ethz.iks.slp.ServiceURL;
import java.lang.Exception;
import java.util.Locale;

public class Lookup {

public static void main(String[] args) throws Exception {
	// get Locator instance
	Locator locator = ServiceLocationManager.getLocator(new Locale("en"));
	//
	// find all services of type "test" that have attribute "cool=yes"
	ServiceLocationEnumeration sle = locator.findServices(new ServiceType("service:leds"), null, null);
	
	//ServiceLocationEnumeration sle = locator.findServiceTypes(null,null);
	// iterate over the results
	System.out.println("Searching up ....");
	while (sle.hasMoreElements()) {
	System.out.println("Found");
	ServiceURL foundService = (ServiceURL) sle.nextElement();
	 System.out.println(foundService);
	//System.out.println(sle.nextElement());
	}
	System.out.println("Finished.");

}

}
