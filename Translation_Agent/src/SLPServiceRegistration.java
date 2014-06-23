import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Locale;

import ch.ethz.iks.slp.ServiceURL;

public class SLPServiceRegistration
{

	SLPHeader slp_header;
	Locale locale=new Locale("en");
	ServiceURL url;
	int lifetime;
	int Service_type_length;
	String Service_type;
	int Scope_Type_Length;
	String Scope;	
	Short attr_length=0;


	public SLPServiceRegistration(SLPHeader slp_header,ServiceURL url,int Service_type_length,String Service_type,int Scope_Type_Length,String Scope){

		this.slp_header = slp_header;
		this.url =url;
		this.Service_type_length = Service_type_length;
		this.Service_type = Service_type;
		this.Scope_Type_Length  = Scope_Type_Length;
		this.Scope = Scope;

	}

	public byte[] getBytes(){

		final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		final DataOutputStream out = new DataOutputStream(bytes);
		//Length = Length of the Header + Length of the Message
		int HeaderLength=14+locale.getLanguage().length();
		int MessageLength=2+2+url.getLength()+Service_type_length+Scope_Type_Length+2;
		int Length=HeaderLength+MessageLength;
		byte[] SLPHeader=slp_header.getHeader(Length);

		try{
			out.write(SLPHeader);		//Added the SLP Header
			url.writeTo(out);		//Added the URL
			out.writeShort(Service_type_length);
			out.write(Service_type.getBytes());
			out.writeShort(Scope_Type_Length);
			out.write(Scope.getBytes());
			out.writeShort(attr_length);
			


		}catch(Exception e){

			e.printStackTrace();
		}



		System.out.println("SSLP Service Registration --> SLP Service Registration");
		
		return bytes.toByteArray();
	}



}
