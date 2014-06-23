import java.util.List;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Deque;
import java.util.StringTokenizer;

import ch.ethz.iks.slp.ServiceLocationException;
import ch.ethz.iks.slp.ServiceURL;


public class SLPMessage {

	//FUNCTION ID'S of the SLP Messages
	public static final byte SERVICE_REQUEST=1;
	
	public static final byte SERVICE_REPLY=2;
	
	public static final byte SERVICE_REGISTRATION=3;
	
	public static final byte SERVICE_DEREGISTRATION=4;
	
	public static final byte SERVICE_ACKNOWLEDGE=5;
	
	public static final byte ATTRIBUTE_REQUEST=6;
	
	public static final byte ATTRIBUTE_REPLY=7;
	
	public static final byte DA_ADVERTISEMENT=8;
	
	public static final byte SERVICE_TYPEREQUEST=9;
	
	public static final byte SERVICE_TYPEREPLY=10;
	
	public static final byte SERVICE_ADVERTISEMENT=11;
	
	public static final int O_MASK=0x00008000;
	
	public static final short F_MASK=0x4000;
	
	
	static byte[] parse(final InetAddress senderAddress,final int senderPort,final DataInputStream in, int len )
	{
		
	//	System.out.println("The sender address is "+senderAddress);
		//System.out.println("The Sender Port is"+senderPort);
		//First Read the Version Number
		
		try {
			
			final byte version = in.readByte();
			System.out.println("The version of the SLP Message received is"+version);
			//We are supporting slp version 2
			if(version==2)
			{
				//Next Step is to read the Type of the SLP Message i.e., Function ID
				final byte Func_Id=in.readByte();
				final short length=in.readShort();
				byte[] prlist;
				byte[] servicetype = null;
				byte[] namingAuthority;
				char[] ServiceType;
				byte[] scopetype = null;
				byte[] URL_Entry ;
				short  length_prlist;
				short length_scopetype=0;
				String serv_type="";
				String scope_type="";
				final byte length_contd=in.readByte();
				final short flags=in.readShort();
				final byte offset=in.readByte();
				final short offset_contd=in.readShort();
				final short sequence_no=in.readShort();
				final short language= in.readShort();	
				byte[] language_tag;
	
				if(language>0)
				{
					System.out.println("Length of the language_tag_length"+language);

					language_tag=new byte[language];
					in.readFully(language_tag);
					String lang=new String(language_tag);
					System.out.println("Language_Tag:"+lang);

				}
				//we have the read the total header till here now we have to read the message information
				switch(Func_Id)
				{
				
				case SERVICE_REQUEST:
					//convert this SLP Service Request Message into SSLP Service Request Message
					//we have to fill the SSLP Header First
					
					length_prlist = in.readShort();
					short length_servicetype=0;					
					if(length_prlist==0)
					{
						length_servicetype=in.readShort();
					}					
					else{
						prlist	= new byte[length_prlist];
						in.readFully(prlist);
						length_servicetype=in.readShort();
					}
					if(length_servicetype<=0)		//this condition<=0 is for when the servicetype request are rece
													//received in those we dont have any service types
						length_scopetype=in.readShort();
					else
					{
						servicetype=new byte[length_servicetype];								
						in.readFully(servicetype);
						serv_type=new String(servicetype);					
						length_scopetype=in.readShort();
					}
					
					if(length_scopetype>0)
					{
						scopetype=new byte[length_scopetype];
						in.readFully(scopetype);
						scope_type=new String(scopetype);
					}
					SSLP_Header Servicereq_header=new SSLP_Header(version,SSLPMessage.SERVICE_REQUEST,isOSet(flags),isFSet(flags),sequence_no);
					int Servicereqheader=Servicereq_header.getSSLPHeader();
					SSLPServiceRequest  req = new SSLPServiceRequest(Servicereqheader, senderAddress, length_servicetype, serv_type, length_scopetype, scope_type);
					//create a Service Request Message Now
					System.out.println("SLP Service Request Message is Received");
					return req.getBytes();					
				case SERVICE_REPLY://not completed
					short Error_code = in.readShort(); 
					System.out.println("Error Code is "+Error_code);
					short Url_EntryCount = in.readShort();
					System.out.println("URL Entry Count is"+Url_EntryCount);
				
					
					SSLP_Header Servicerep_header=new SSLP_Header(version,SSLPMessage.SERVICE_REPLY,isOSet(flags),isFSet(flags),sequence_no);
					int Servicerepheader= Servicerep_header.getSSLPHeader();
					System.out.println("The Message id is"+Servicerep_header.getMsg_id());
					SSLPServiceReply sreply = new SSLPServiceReply(Servicerepheader,Error_code,Url_EntryCount,in);
					
					System.out.println("SLP Service Reply Message is Received");
					return sreply.getBytes();
				
				case SERVICE_REGISTRATION://not required for SSLP
					
					try {
						ServiceURL url =ServiceURL.fromBytes(in);
						System.out.println("URL :"+url);
						System.out.println("Lifetime:"+url.getLifetime());
						System.out.println("URL Length:"+url.toString().length());
						short length_service_type= in.readShort() ;
						System.out.println("Length_Service_type"+length_service_type);
						if(length_service_type>0)
						{
							servicetype=new byte[length_service_type];
							in.readFully(servicetype);
							serv_type=new String(servicetype);
							System.out.println("The service type is "+serv_type);
						}
						length_scopetype=in.readShort();
						if(length_scopetype>0)
						{
							scopetype=new byte[length_scopetype];
							in.readFully(scopetype);
							scope_type=new String(scopetype);
							System.out.println("The scope type is"+scope_type);
						}
						SSLP_Header Servicereg_header=new SSLP_Header(version,SSLPMessage.SERVICE_REGISTRATION,isOSet(flags),isFSet(flags),sequence_no);
						int Serviceregheader= Servicereg_header.getSSLPHeader();
						System.out.println("Message id"+Servicereg_header.getMsg_id());
						SSLPServiceRegistration sreg = new SSLPServiceRegistration(Serviceregheader,url,length_service_type,serv_type,length_scopetype,scope_type);;
						System.out.println(" SLP Service Registration Message is Received");
						return sreg.getBytes();	
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
							
					
				
				case SERVICE_DEREGISTRATION://not required for SSLP
					
					try{
						SSLP_Header ServiceDereg_header=new SSLP_Header(version,SSLPMessage.SERVICE_DEREGISTRATION,isOSet(flags),isFSet(flags),sequence_no);
					
					int Servicederegheader= ServiceDereg_header.getSSLPHeader();

					length_scopetype=in.readShort();
					System.out.println("the length of the scope is "+length_scopetype);
					if(length_scopetype>0)
					{
						scopetype=new byte[length_scopetype];
						in.readFully(scopetype);
						scope_type=new String(scopetype);
					}
					ServiceURL url  = ServiceURL.fromBytes(in);
					System.out.println("URL :"+url);
					System.out.println("Lifetime:"+url.getLifetime());
					System.out.println("URL Length:"+url.toString().length());
					
					System.out.println("SLP Service DeRegistration Message is Received");
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
					break;
				
				case SERVICE_ACKNOWLEDGE://As the service acknowledge is sent in response to sreg message this is not required
					SSLP_Header Serviceack_header=new SSLP_Header(version,SSLPMessage.SERVICE_ACKNOWLEDGE,isOSet(flags),isFSet(flags),sequence_no);
					short error_code=in.readShort();
					System.out.println("Error code is "+error_code);
					System.out.println("Service Acknowledge Message is Received");
					
					break;
					
				case ATTRIBUTE_REQUEST://this is not required as we dont have any attribute request message in sslp
					SSLP_Header AttrReq_header=new SSLP_Header(version,Func_Id,isOSet(flags),isFSet(flags),sequence_no);

					System.out.println("Attribute Request Message is Received");
					break;
					
				case ATTRIBUTE_REPLY://this is not required as we dont have any attribute reply message in sslp
					SSLP_Header AttrRep_header=new SSLP_Header(version,Func_Id,isOSet(flags),isFSet(flags),sequence_no);
					System.out.println("Attribute Reply Message is Received");
					break;
					
				case DA_ADVERTISEMENT:
					SSLP_Header DAAdvt_header=new SSLP_Header(version,SSLPMessage.DA_ADVERTISEMENT,isOSet(flags),isFSet(flags),sequence_no);
					System.out.println("DA Advertisement Message is Received");
					break;
					
				case SERVICE_TYPEREQUEST:
					//change the function id here
					//read the length of the prlist
					length_prlist=in.readShort();
					short length_namingauthority;
					if(length_prlist==0)
					{
						length_namingauthority=in.readShort();
					}
					else{
						prlist	= new byte[length_prlist];
						in.readFully(prlist);
						length_namingauthority=in.readShort();
					}
					if(length_namingauthority<=0)
					{
						length_scopetype=in.readShort();
					}
					else{
						namingAuthority= new byte[length_namingauthority];
						in.readFully(namingAuthority);
						length_scopetype=in.readShort();
					}
					if(length_scopetype>0)
					{
						scopetype=new byte[length_scopetype];
						in.readFully(scopetype);
						scope_type=new String(scopetype);
					}
					SSLP_Header ServiceTypeReq_header=new SSLP_Header(version,SSLPMessage.SERVICE_TYPEREQUEST,isOSet(flags),isFSet(flags),sequence_no);
					int ServiceTypeReqheader=ServiceTypeReq_header.getSSLPHeader();
					SSLPServiceTypeRequest ServiceType_req=new SSLPServiceTypeRequest(ServiceTypeReqheader, senderAddress,length_scopetype, scope_type);
					System.out.println("Service Type Request Message is Received");
					return ServiceType_req.getBytes();
					
					
				case SERVICE_TYPEREPLY:
					System.out.println("Service Type Reply Message is Received");
					error_code = in.readShort(); 
					System.out.println("Error Code is"+error_code);
					length_servicetype=in.readShort();
					System.out.println("The length of the service type is"+length_servicetype);
					if(length_servicetype>0)
					{
						servicetype=new byte[length_servicetype];
						in.readFully(servicetype);
						System.out.println("The service type is "+new String(servicetype));
					}
					SSLP_Header ServiceTypeRep_header=new SSLP_Header(version,SSLPMessage.SERVICE_TYPEREPLY,isOSet(flags),isFSet(flags),sequence_no);
					int ServiceTypeRepheader= ServiceTypeRep_header.getSSLPHeader();
					SSLPServiceTypeReply servicetype_rep = new SSLPServiceTypeReply(ServiceTypeRepheader,error_code,length_servicetype,servicetype);
					return servicetype_rep.getBytes();
				
				case SERVICE_ADVERTISEMENT://as jslp does not have Service Advertisement
					SSLP_Header ServiceAdvt_header=new SSLP_Header(version,SSLPMessage.SERVICE_ADVERTISEMENT,isOSet(flags),isFSet(flags),sequence_no);
					System.out.println("Service Advertisement is Received");
					break;			
				
				
				}
				
				
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
		
	}
	
	
	private static boolean isFSet(short flags) {
		// TODO Auto-generated method stub
		
		if((flags&F_MASK)==flags)
			return true;
		return false;
	}


	public static boolean isOSet(short flags)
	{
		
		if((flags&O_MASK)==flags)
			return true;
		return false;
	}
	static List stringToList(final String str, final String delim) {
		List result = new ArrayList();
		StringTokenizer tokenizer = new StringTokenizer(str, delim);
		while (tokenizer.hasMoreTokens()) {
			result.add(tokenizer.nextToken());
		}
		return result;
	}

	public static short getSequenceNumber(DataInputStream in)
	{
		try{
			//read the version
			in.readByte();
			//read the Messageid
			in.readByte();
			//read the length
			in.readShort();
			//continue with the length;
			in.readByte();
			//read the flags
			in.readShort();
			//read the offset
			in.readByte();
			//offset continued
			in.readShort();
			//return the sequence number
			return in.readShort();
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return 0;
		
	}

	public static byte getMsgID(DataInputStream in) {
		// TODO Auto-generated method stub
		
		//first read the version
		try {
			short version=in.readByte();
				return in.readByte();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
}
