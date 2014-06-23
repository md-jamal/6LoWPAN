import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import ch.ethz.iks.slp.ServiceURL;


public class SSLPServiceReply {

	int header;
	short error_code;
	short url_EntryCount;
	DataInputStream in ;
	final byte LT = 0x03;
	public SSLPServiceReply(int header, short error_code, short url_EntryCount, DataInputStream in ) {
		// TODO Auto-generated constructor stub
		this.header=header;
		this.error_code=error_code;
		this.url_EntryCount= url_EntryCount;
		this.in=in;
	}

	
	public byte[] getBytes()
	{
		
		
		final ByteArrayOutputStream bytes= new ByteArrayOutputStream();
		final DataOutputStream out= new DataOutputStream(bytes);
		
		try{
			out.writeInt(header);
			out.writeShort(error_code);
			out.writeShort(url_EntryCount);
			
			ServiceURL[] url=new ServiceURL[url_EntryCount];
			
			for(int i=0;i<url_EntryCount;i++)
			{
				url[i]=ServiceURL.fromBytes(in);
			}
			
			for(int i=0;i<url_EntryCount;i++)
			{
				out.writeShort(url[i].getLifetime());
				out.writeByte(LT);
				out.writeShort(url[i].getLength());
				String ServiceURL=url[i].toString();
				if(url[i].getLength()<80)
				{
//					System.out.println("Appending *");appending because no malloc in tinyos and it will overwrite the other elements data
					for(int j=0;j<80-(url[i].getLength()-6);j++)
						ServiceURL	=ServiceURL+"*";
					
				}
				byte[] Serviceurl = ServiceURL.getBytes();
				out.write(Serviceurl);
			}
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("SLP Service Reply --> SSLP Service Reply");
		return bytes.toByteArray();
		
		
		
		
	}
}
