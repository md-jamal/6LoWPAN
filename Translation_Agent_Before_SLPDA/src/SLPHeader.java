import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Locale;


public class SLPHeader {

	byte Version;
	byte FunctionID;
	int Length;
	boolean OSet;
	boolean FSet;
	byte offset;
	short offset_cntd;
	short xid;
	short language_tag_length;
	String language_tag;
	Locale locale=new Locale("en");
	//the length is wrong here you have to get the length of the whole message not the length of the SSLP Message
	
	public SLPHeader(byte messageID, boolean oset, boolean fset,
			short seq_no) {
		// TODO Auto-generated constructor stub
		
		this.Version=2;
		this.FunctionID=messageID;
		this.OSet=oset;
		this.FSet=fset;
		this.xid=seq_no;		
	}
	
	
	byte[] getHeader(int Length)
	{
		final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		final DataOutputStream out = new DataOutputStream(bytes);
		try{
	
		out.write(2);		//Added the version
		out.write(FunctionID);//Added the Function ID
		out.write((byte) ((Length) >> 16));
		out.write((byte) (((Length) >> 8) & 0xFF));
		out.write((byte) ((Length) & 0xFF));
		if(OSet&&FSet)
			out.write(0xc0);
		else if(OSet&&!FSet)
			out.write(0x80);
		else if(!OSet&&FSet)
			out.write(0x40);
		else
			out.write(0x00);
		out.write(0x00);		//Reserved
		out.write(0x00);		//Offset
		out.write(0x00);		//Offset
		out.write(0x00);		//Offset
		out.writeShort(xid);
		out.writeUTF(locale.getLanguage());		//Language Tag
		
				
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return bytes.toByteArray();
		
		
	}
	
	
	
	
	
}
