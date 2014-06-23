import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;


public class SSLPServiceRequest {

	int header;
	InetAddress SourceAddress;
	Short ServiceType_Length;
	String ServiceType;
	Short ScopeType_Length;
	String ScopeType;
	public final byte AM= (byte) 0x03;	//Always it will use the IP Addressing Mode(2 bits for AM and 6bits are reserved)
	
	public SSLPServiceRequest(int header,InetAddress SourceAddress,short ServiceType_Length,
			String ServiceType,short ScopeType_Length,String ScopeType)
	{
		this.header=header;
		this.SourceAddress=SourceAddress;
		this.ServiceType_Length=ServiceType_Length;
		this.ServiceType=ServiceType;
		this.ScopeType_Length=ScopeType_Length;
		this.ScopeType=ScopeType;	
		
		System.out.println("We have to create a Service Request Message "
				+ "With Service Type:" + ServiceType 
				+ "With Scope:"+ScopeType);
	}
	
	
	public byte[] getBytes()
	{
		
		final ByteArrayOutputStream bytes= new ByteArrayOutputStream();
		final DataOutputStream out= new DataOutputStream(bytes);
		
		
		try {
			//First Add the Header Then the Message Fields			
			out.writeInt(header);
			
			out.writeByte(AM);
				System.out.println("AM:"+Integer.toBinaryString(AM));
			//TODO:Here its IPv4 we have to use IPv6
			byte[] ip=SourceAddress.getAddress();
			System.out.println("ip:"+ip);
			out.write(ip);
			out.writeShort(ServiceType_Length);
			if(ServiceType_Length<24)
			{
				//	System.out.println("Appending *");appending because no malloc in tinyos and it will overwrite the other elements data
					for(int i=0;i<24-ServiceType_Length;i++)
						ServiceType=ServiceType+"*";
			}
				
			System.out.println("Length_service_type:"+ServiceType_Length);
			
			byte[] Service=ServiceType.getBytes();
			out.write(Service);
			System.out.println("ServiceType"+ServiceType);
			out.writeShort(ScopeType_Length);
			System.out.println("ScopeTypeLength:"+ScopeType_Length);
			byte[] Scope=ScopeType.getBytes();
			out.write(Scope);
			System.out.println("ScopeType"+ScopeType);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("length"+bytes.toByteArray().length);
		return bytes.toByteArray();

		
	}
	
	
}
