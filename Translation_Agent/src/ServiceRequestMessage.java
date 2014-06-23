import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;




//send function should be implemented here only while sending write everything to dataoutstream and convert it into
//bytes
public class ServiceRequestMessage {

	private final int IP_ADDRESSING=0x3;
	int SSLP_Header;		//contains the SSLP_Header
	
	//Here we are going to fix the AM Mode so that source address field will always be using the IPV6 Address;
	
	short AM=IP_ADDRESSING<<6;	//2bits for the AM field and remaining bits are for reserved
	
	byte[] IP_Address=new byte[16];	//it contains the IPAddress
	
	byte ServiceTypeLength; //contains the length of the Service Type
	
	String ServiceType;		//Contains the Service Type
	
	byte ScopeLength; //contains the length of the Scope Type
	
	String Scope;	//Contains the Scope Type
	
	
	
	//constructor for ServiceRequest Message
	
	public ServiceRequestMessage(int SSLP_Header,byte[] IP_Address,byte ServiceTypeLength,
			String ServiceType,byte ScopeLength,String Scope)
	{
		this.SSLP_Header=SSLP_Header;
		this.IP_Address=IP_Address;
		this.ServiceTypeLength=ServiceTypeLength;
		this.ServiceType=ServiceType;
		this.ScopeLength=ScopeLength;
		this.Scope=Scope;		
		
	}
	
	//TO Whom we have to send ??
	
	public void send()
	{
		final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		final DataOutputStream out = new DataOutputStream(bytes);
		try {
			//Adding the SSLP Header
			out.write(SSLP_Header);
			
			//Adding the message fields now;
			out.write(AM);			
			out.write(IP_Address);
			out.write(ServiceTypeLength);
			out.writeChars(ServiceType);
			out.write(ScopeLength);
			out.writeChars(Scope);
			
			byte[] data=bytes.toByteArray();
			
			//Create a Datagram Packet and send it
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
		
		
		
		
		
	
	
}

