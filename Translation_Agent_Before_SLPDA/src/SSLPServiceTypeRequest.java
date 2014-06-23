import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;


public class SSLPServiceTypeRequest {
  int header;
  InetAddress SourceAddress;
  Short ScopeType_Length;
  String ScopeType;
  public final byte AM= (byte) 0x03;	//Always it will use the IP Addressing Mode
	
	public SSLPServiceTypeRequest(int serviceTypeReqheader,
			InetAddress senderAddress, short length_scopetype, String scope_type) {
		// TODO Auto-generated constructor stub
		
		this.header=serviceTypeReqheader;
		this.SourceAddress=senderAddress;
		this.ScopeType_Length=length_scopetype;
		this.ScopeType=scope_type;
		
		
	}
	
	
	public byte[] getBytes()
	{
		
		final ByteArrayOutputStream bytes= new ByteArrayOutputStream();
		final DataOutputStream out= new DataOutputStream(bytes);
		
		try {
			out.writeInt(header);
			out.writeByte(AM);
			byte[] ip=SourceAddress.getAddress();
			out.write(ip);
			out.writeShort(ScopeType_Length);
			out.write(ScopeType.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("length"+bytes.toByteArray().length);
		return bytes.toByteArray();
		
	}

}
