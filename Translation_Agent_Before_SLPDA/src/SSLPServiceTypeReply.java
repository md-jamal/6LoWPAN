import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;


public class SSLPServiceTypeReply {

	
	int header;
	short error_code;
	short length_servicetype;
	byte[] servicetype;
	public SSLPServiceTypeReply(int serviceTypeRepheader, short error_code,
			short length_servicetype, byte[] servicetype) {
		// TODO Auto-generated constructor stub
		
		this.header=serviceTypeRepheader;
		this.error_code=error_code;
		this.length_servicetype=length_servicetype;
		this.servicetype=servicetype;
	}

	public byte[] getBytes()
	{
		final ByteArrayOutputStream bytes= new ByteArrayOutputStream();
		final DataOutputStream out= new DataOutputStream(bytes);
		
		
		try{
			out.writeInt(header);
			out.writeShort(error_code);
			
			out.writeShort(length_servicetype);
			out.write(servicetype);			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		return bytes.toByteArray();
	}
	
}
