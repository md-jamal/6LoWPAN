import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.io.DataInputStream;
import ch.ethz.iks.slp.ServiceURL;

public class SSLPServiceAcknowledge{


	int header;
	short error_code;

	public SSLPServiceAcknowledge(int header,short error_code)
	{
		this.header = header;
		this.error_code = error_code;
	}


	public byte[] getBytes()
	{
		final ByteArrayOutputStream bytes= new ByteArrayOutputStream();
		final DataOutputStream out= new DataOutputStream(bytes);
		

		try{

			out.writeInt(header);
			out.writeShort(error_code);
		

		}catch(Exception e){
			e.printStackTrace();
		}
		
		System.out.println("SLP Service Ack --> SSLP Service Ack");

		return bytes.toByteArray();


	}






}



