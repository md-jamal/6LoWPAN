import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Locale;

import ch.ethz.iks.slp.ServiceURL;


public class SLPServiceReply {

	SLPHeader header;
	Locale locale=new Locale("en");
	short error_code;
	short url_entrycount;
	ServiceURL url;
	public SLPServiceReply(SLPHeader header, short error_code,
			short location_entry_count,ServiceURL url) {
		// TODO Auto-generated constructor stub
		
		this.header=header;
		this.error_code=error_code;
		this.url_entrycount=location_entry_count;
		this.url=url;
		
		
	}
	public byte[] getBytes() {
		final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		final DataOutputStream out = new DataOutputStream(bytes);
		// TODO Auto-generated method stub
		//Length = Length of the Header + Length of the Message
		int HeaderLength=14+locale.getLanguage().length();
		int MessageLength=2+2+url.getLength();
		int Length=HeaderLength+MessageLength;
		byte[] SLPHeader=header.getHeader(Length);
	try{
		
		out.write(SLPHeader);		//Added the SLP Header
		out.writeShort(error_code);
		out.writeShort(url_entrycount);
		url.writeTo(out);
		
	}catch(Exception e){
		e.printStackTrace();
	}
	System.out.println("length"+bytes.toByteArray().length);
	
		return bytes.toByteArray();
	}

	
	
	
	
	
}
