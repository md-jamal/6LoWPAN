

//i think version should be removed as this is fixed
public class SSLP_Header {
 int  ver;				//contains the version of the SSLP being used
 int Msg_id;			//determines the message type
 boolean O;				//o(overflow) flag is set when the message type exceeds what can 
 						//be fit in the message datagram
 boolean F;				//F(Fresh) flag is set on every SREG(Service Registration)
 int seq_no;			//Set to a unique for every SREQ Message
 int SSLP_Header;		//This contains the header to be sent
 private final int O_BITMASK=0x00200000;
 private final int F_BITMASK=0x00100000;
 private final int SEQ_BITMASK=0x0000ffff;
 private final int MSGID_BITMASK=0x0fc00000;
public SSLP_Header(int ver, int Msg_id, boolean O, boolean F, int seq_no) {
	super();
	this.ver = ver; //cannot give  a version number more than 7 here because it overwrites the
					//signed bit as there are no unsigned types in java
	
	this.Msg_id = Msg_id;
	this.O = O;
	this.F=F;
	this.seq_no = seq_no;
	create_SSLP_Header(ver, Msg_id, O, F, seq_no);
	
}
 
 public void create_SSLP_Header(int ver,int msg_id,boolean O,boolean F,int seq_no){
	
	 SSLP_Header=0;
	 //first lets add the version number in the header which will be in the starting
	 //four bits
	 //clearing the starting four bits
	 SSLP_Header=SSLP_Header&0x0fffffff;
	 ver=ver<<28;
	 SSLP_Header=SSLP_Header|ver;
	 int temp=0;
	 //getting the 8 bits into the starting position
	 //storing the four bits into temp
	 temp=msg_id&0x000000f0;
	//>>>--->unsigned right shift
	 temp=temp>>>4;
	 temp=temp<<26; 
 //storing those four bits in the SSLP Header
	SSLP_Header=SSLP_Header|(temp);
	 //lets add the next 4 bits of the msg id to the sslp header
	temp=msg_id&0x0000000f;	
	temp=temp<<22;		 
	 //storing those four bits in the SSLP Header
	SSLP_Header=SSLP_Header|(temp);
	
	 //lets the add O flag which is one bit(it may be one or zero)
	 if(O==true)//it means the we have to clear that bit
	 {
		 temp=1<<21;
		 SSLP_Header=SSLP_Header|temp;
	 }
	 if(F==true)
	 {
		 temp=1<<20;
		 SSLP_Header=SSLP_Header|temp;
	 }

	 //next 4 bits will be any how zeroes so no need to add them
	 
	 //next add the sequence number which is 16-bit
	 
	 SSLP_Header=SSLP_Header|seq_no;
	 
	// System.out.println("SSLP Header after adding starting two bits of the message id"+Integer.toBinaryString(SSLP_Header));
	 //System.out.println("The sequence number is "+getSeq_no());
	
}
 
 public int getSSLPHeader()
 {
	 return SSLP_Header;
 }

 //Return the first 4 bits of the header which is the version field
public int getVer() {
	return SSLP_Header>>>28;
}
//return the next 6 bits
public int getMsg_id() {
	int temp=SSLP_Header&MSGID_BITMASK;
	temp=temp>>22;
	return temp;
}

public boolean isO() {
	int temp=SSLP_Header&O_BITMASK;
	temp=temp>>>21;
	if(temp==1)
	return true;
	else 
		return false;
}

public boolean isF() {
	int temp=SSLP_Header&F_BITMASK;
	temp=temp>>>20;
	if(temp==1)
	return true;
	else 
		return false;
}

public int getSeq_no() {
	return (SSLP_Header&SEQ_BITMASK);
}


 
 
}
