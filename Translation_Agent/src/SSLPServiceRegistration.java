import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import ch.ethz.iks.slp.ServiceURL;


public class SSLPServiceRegistration {

	int header;
	ServiceURL url;
	short length_service_type;
	String servicetype;
	short length_scopetype;
	String scopetype;
	
	final byte LT=0x03;
	
	
	
	public SSLPServiceRegistration(int serviceregheader, ServiceURL url2,
			short length_service_type2, String serv_type,
			short length_scopetype2, String scope_type) {
		// TODO Auto-generated constructor stub
		this.header=serviceregheader;
		this.url=url2;
		this.length_service_type=length_service_type2;
		this.servicetype=serv_type;
		this.length_scopetype=length_scopetype2;
		this.scopetype=scope_type;
	}



	public byte[] getBytes()
	{
		
		final ByteArrayOutputStream bytes= new ByteArrayOutputStream();
		final DataOutputStream out= new DataOutputStream(bytes);
		
		try
		{
		
			out.writeInt(header);
			out.writeShort(url.getLifetime());
			//System.out.println("The lifetime is "+url.getLifetime());
			//setting the LT field to 11
			out.writeByte(LT);
			out.writeShort(url.getLength()-6);
			//System.out.println("Length in binary"+Integer.toBinaryString(url.getLength()));
			String ServiceURL=url.toString();
			if(url.getLength()<48)
			{
//				System.out.println("Appending *");appending because no malloc in tinyos and it will overwrite the other elements data
				for(int i=0;i<48-(url.getLength()-6);i++)
					ServiceURL	=ServiceURL+"*";
				//System.out.println("The service url is" +ServiceURL);
			}
			byte[] Serviceurl = ServiceURL.getBytes();
			out.write(Serviceurl);
			
			out.writeShort(length_service_type);
		
			if(length_service_type>0)
			{
				for(int i=0;i<48-length_service_type;i++)
					servicetype= servicetype + "*";
			}
					
				
			out.write(servicetype.getBytes());
			out.writeShort(length_scopetype);
			//System.out.println("The length of the scope type is"+length_scopetype);
			out.write(scopetype.getBytes());		
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		
		}
		
		//System.out.println("length"+bytes.toByteArray().length);
		byte[] array=bytes.toByteArray();
		System.out.println("SLP Service Registration -> SSLP Service Registration");
		return bytes.toByteArray();
	
	}
	
	
}
