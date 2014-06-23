import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Locale;


public class SLPServiceTypeReply {

	SLPHeader header;
	short error_code;
	short length_service_type;
	String serviceType;
	Locale locale=new Locale("en");
	public SLPServiceTypeReply(SLPHeader slp_header, short error_code,
			short length_service_type, String serviceType) {
		// TODO Auto-generated constructor stub
		
		this.header=slp_header;
		this.error_code=error_code;
		this.length_service_type=length_service_type;
		this.serviceType=serviceType;
	}
	
	
	
	byte[] getbytes()
	{
		final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		final DataOutputStream out = new DataOutputStream(bytes);
		
		//Length = Length of the Header + Length of the Message
		int HeaderLength=14+locale.getLanguage().length();
		int MessageLength=2+2+serviceType.length();
		int Length= HeaderLength + MessageLength;
		byte[] SLPHeader=header.getHeader(Length);
		
		try{
			out.write(SLPHeader);	//Added the SLPHeader
			out.writeShort(error_code);
			out.writeShort(length_service_type);
			out.write(serviceType.getBytes());
			
		}catch(Exception e){
			e.printStackTrace();
		}
		

		System.out.println("SSLP Service Type Reply --> SLP Service Type Reply");
		
		return bytes.toByteArray();
		
		
		
	}

}
