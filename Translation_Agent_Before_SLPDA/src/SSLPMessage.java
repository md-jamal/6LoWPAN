import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Locale;

import javax.print.attribute.standard.MediaSize.ISO;

import ch.ethz.iks.slp.Locator;
import ch.ethz.iks.slp.ServiceLocationEnumeration;
import ch.ethz.iks.slp.ServiceLocationManager;
import ch.ethz.iks.slp.ServiceType;
import ch.ethz.iks.slp.ServiceURL;


public class SSLPMessage {

	//Message ID'S of the SSLP Messages
	
	public static final int SERVICE_REQUEST=1;
	
	public static final int SERVICE_REPLY=2;
	
	public static final int SERVICE_REGISTRATION=3;
	
	public static final int SERVICE_ACKNOWLEDGE=4;
	
	public static final int DA_ADVERTISEMENT=5;
	
	public static final int SERVICE_ADVERTISEMENT=6;
	
	public static final int SERVICE_TYPEREQUEST=7;
	
	public static final int SERVICE_TYPEREPLY=8;
	
	public static final int SERVICE_DEREGISTRATION=9;
	
	
	public static final byte O_MASK=0x0020;
	
	public static final byte F_MASK=0x0001;


	public static byte[] parse(InetAddress address, int port,
			DataInputStream in, DatagramPacket packet) {
		// TODO Auto-generated method stub
		
		System.out.println("Packet received from"+address+"at port"+port);
		
		//converting Big Endian which is the Network order to Little Endian which is the mote endianess
		ByteBuffer buf = ByteBuffer.wrap(packet.getData());
		buf.order(ByteOrder.LITTLE_ENDIAN);
		
		//Reading the SSLP Header
		short flags=buf.getShort();
		System.out.println("flags"+Integer.toBinaryString(flags));
		byte version = (byte) (flags&(0x000f));
		byte MessageID=(byte) ((short) (flags&0x03f0)>>4);
		short seq_no = buf.getShort();
		System.out.println("Version "+version);
		System.out.println("Message id"+MessageID);
		System.out.println("sequence number is"+seq_no);
		//we are supporting only SSLP with version 2
		if(version==2)
		{
			
			
			
			switch(MessageID)
			{
				case SERVICE_REQUEST:
					
					try{
						System.out.println("SSLP Service Request Message is received");
					//Read the Message Parameters here
						buf.order(ByteOrder.BIG_ENDIAN);
						String service="";
						String scope="";
					byte AM = buf.get();
					System.out.print("AM is"+Integer.toBinaryString(AM));
					InetAddress ip=getIP(buf);
					System.out.println("The Source Address is"+ip);
					short length_service_type= buf.getShort();

					System.out.println("Length of the service type is "+length_service_type);
					byte[] service_type;
					if(length_service_type>0)
					{
						service_type=new byte[length_service_type];		//as we dont have malloc at the tinyos we will read 16 bytes from the buffer which is fixed at the tinyos side
						buf.get(service_type);
					service=new String(service_type);
						System.out.println("The service is"+service);	
						byte[] rem= new byte[16-length_service_type];
						buf.get(rem);
						String remaining = new String(rem);
						System.out.println("The remaining buffer is"+remaining);
					}
					
					Short length_scope=buf.getShort();
					System.out.println("The length of the scope is"+length_scope);
					byte[] scope_type;
					if(length_scope>0)
					{
						scope_type = new byte[length_scope];
						buf.get(scope_type);
						scope = new String(scope_type);
						System.out.println("The scope type is"+scope);
					}
					Locator locator = ServiceLocationManager.getLocator(new Locale("en"));
					ServiceLocationEnumeration sle;
					if(scope.equals("default"))
					sle=locator.findServices(new ServiceType(service), null,null);
					else
					sle=locator.findServices(new ServiceType(service), null,scope);
					System.out.println("Searching up ...");
					while (sle.hasMoreElements()) {
					System.out.println("Found");
					ServiceURL foundService = (ServiceURL) sle.nextElement();
					 	System.out.println(foundService);
					//System.out.println(sle.nextElement());
					}
					}catch(Exception e){
						e.printStackTrace();
					}
					
				
					break;
				
				case SERVICE_REPLY:
					String URL="" ;
					System.out.println("SSLP Service Reply Message is Received");
					//TRY to extract the message details
					short error_code= buf.getShort();
					System.out.println("the error code is"+error_code);
					short location_entry_count=buf.getShort();
					System.out.println("the count is "+location_entry_count);
					
					short lifetime = buf.getShort();
					System.out.println("The lifetime is"+lifetime);
					
					byte LT=buf.get();
					System.out.println("The value of LT is"+Integer.toBinaryString(LT));
							
					short url_length=buf.getShort();
					System.out.println("The url length is "+url_length);	
					
					byte[] url;
					if(url_length>0)//TODO:has to do for more than one entries
					{
						url = new byte[url_length];
						buf.get(url);
						URL = new String(url);
						System.out.println("The url received is"+URL);
						//has to send it to the 427 port of the SLP and convert it into SLP here we have just read the values
					}
					
					//Its time to create a SLPServiceReply Message
					//First Create a SLP Header
					SLPHeader header = new SLPHeader(SLPMessage.SERVICE_REPLY,isOset(flags),isFset(flags),seq_no);
					try{
						ServiceURL Serviceurl = new ServiceURL(URL, lifetime);
						//you have to call getBytes function to get the Bytes along with the length
						SLPServiceReply srep = new SLPServiceReply(header,error_code,location_entry_count,Serviceurl);
						return srep.getBytes();
					}catch(Exception e){
						e.printStackTrace();
					}
					
					break;
					
				case SERVICE_REGISTRATION:
					
					System.out.println("SSLP Service Registration Message is Recieved");
					
					break;
					
					
				case SERVICE_ACKNOWLEDGE:
					
					System.out.println("SSLP Service Acknowledge Message is Received");
					
					break;
					
				case DA_ADVERTISEMENT:
					
					System.out.println("SSLP Directory Advertisement Message is Received");
					
					break;
					
					
				case SERVICE_ADVERTISEMENT:
					
					System.out.println("SSLP Service Advertisement Message is Received");
					
					break;
					
				
				case SERVICE_TYPEREQUEST :

					try{
						System.out.println("SSLP Service Type Request Message is Received");
						String scope="";
						buf.order(ByteOrder.BIG_ENDIAN);
						byte AM= buf.get();
						System.out.print("AM is"+Integer.toBinaryString(AM));
						InetAddress ip=getIP(buf);
						System.out.println("The Source Address is"+ip);
						Short length_scope=buf.getShort();
						System.out.println("The length of the scope is"+length_scope);
						byte[] scope_type;
						if(length_scope>0)
						{
							scope_type = new byte[length_scope];
							buf.get(scope_type);
							scope = new String(scope_type);
							System.out.println("The scope type is"+scope);
						}
						Locator locator = ServiceLocationManager.getLocator(new Locale("en"));
						ServiceLocationEnumeration sle;
						if(scope.equals("default"))
							sle = locator.findServiceTypes(null,null);
						else
							sle= locator.findServiceTypes(scope, null);
						while (sle.hasMoreElements()) {
							System.out.println("Found");
							System.out.println(sle.nextElement());
						}
					}
					catch(Exception e){
						e.printStackTrace();
					}
					
					
					
					break;
					
				case SERVICE_TYPEREPLY :
					String ServiceType="";
					System.out.println("SSLP Service Type Reply Message is Received");
					short Error_code = buf.getShort();
					System.out.println("The error code is"+Error_code);
					
					
					short length_service_type=buf.getShort();
					System.out.println("The service type length is"+length_service_type);
					byte [] Service_type;
					if(length_service_type>0)
					{
						Service_type=new byte[length_service_type];
						buf.get(Service_type);
						ServiceType = new String(Service_type);
						System.out.println("The service type is"+ServiceType);
					}
					
					//Now here we have to create a SLP ServiceTypeReply
					
					SLPHeader slp_header = new SLPHeader(SLPMessage.SERVICE_TYPEREPLY,isOset(flags),isFset(flags),seq_no);
					//Only Supporting one Service now will try to support multiple services
					SLPServiceTypeReply strep =new SLPServiceTypeReply(slp_header,Error_code,length_service_type,ServiceType);
					
					return strep.getbytes();
					
					
				case SERVICE_DEREGISTRATION:
					
					System.out.println("SSLP Service Deregistration Message is received");
					break;
		
					
					
					
					
					
				default:
					
					System.out.println("Undefined SSLP Message is received with message id "+MessageID);
				
			
			
			
			}
			
			
			
			
			
			
			
		
		}
				
		
		return null;
	}
	
	
	static InetAddress getIP(ByteBuffer buf)
	{
		
		String ipv6="";
		for(int  i=0;i<16;i++)
		{
			int ip = buf.get();
			String ip_address= Integer.toHexString(ip);
			if(i%2==0&&i!=0&&i!=16)
				ipv6=ipv6+":";
			if(ip_address.length()==8)
				ipv6=ipv6+ip_address.substring(6);
			else
				ipv6=ipv6+ip_address;
			
		}
		InetAddress ip;
		try {
			ip =InetAddress.getByName(ipv6);
			return ip;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
		
		
	}
	

	static boolean isOset(short flags)
	{
		if((flags&O_MASK)==flags)
			return true;
		return false;
	
	}
	
	static boolean isFset(short flags)
	{
		if((flags&F_MASK)==flags)
			return true;
		return false;
		
	}
	
}
