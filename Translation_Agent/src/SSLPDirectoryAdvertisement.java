import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.io.DataInputStream;
import ch.ethz.iks.slp.ServiceURL;
public class SSLPDirectoryAdvertisement {

	int header;
	short error_code;
	DataInputStream in ;
	Short ScopeType_Length;
	byte[] ScopeType;
	byte[] service_URL;
	final byte LT = 0x03;
	final short Lifetime = 0x0fff;	//TODO:Change to automatic lifetime.this is manual lifetime

	public SSLPDirectoryAdvertisement(int header, short error_code,
			DataInputStream in) {

		this.header               = header;
		this.error_code           = error_code;
		this.in                   = in;
	}


	public byte[] getBytes()
	{
		
		final ByteArrayOutputStream bytes= new ByteArrayOutputStream();
		final DataOutputStream out= new DataOutputStream(bytes);
		
		
		try {
			

			
			//stateless bootstamp:4 bytes
			in.readInt();
			short serviceurl_length = in.readShort();
			if(serviceurl_length>0)
			{
				service_URL = new byte[serviceurl_length];
				in.readFully(service_URL);

			}			
			ScopeType_Length = in.readShort();
			if(ScopeType_Length>0)
			{
				ScopeType= new byte[ScopeType_Length];
				in.readFully(ScopeType);

			}

			//First Add the Header Then the Message Fields			
			out.writeInt(header);
			out.writeShort(0);			

			out.writeShort(Lifetime);

			out.writeByte(LT);
			out.writeShort(service_URL.length);
			out.write(service_URL);
			if(service_URL.length<80)
			{
//					System.out.println("Appending *");appending because no malloc in tinyos and it will overwrite the other elements data
				byte[] rem_bytes= new byte[80-service_URL.length];
				out.write(rem_bytes);
					
			}
		
			out.writeShort(ScopeType_Length);
			out.write(ScopeType);
			


	             }catch(Exception e){

				e.printStackTrace();

		    }

			System.out.println("SLP Directory Advertisement --> SSLP Directory Advertisement");
		   return bytes.toByteArray();
	}


}
