import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Date;
import java.sql.Timestamp;

public class SSLP_Listener implements Runnable {

	MulticastSocket sock,SLP_Sock,send_Socket;
	int send_Port = 7778;
	public SSLP_Listener(MulticastSocket sSLP_Socket,MulticastSocket SLP_Sock) {
		// TODO Auto-generated constructor stub
		this.sock=sSLP_Socket;
		this.SLP_Sock = SLP_Sock;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub


		
		byte[] buffer = new byte[200];
		DatagramPacket packet =new DatagramPacket(buffer,buffer.length);
		try {
					send_Socket = new MulticastSocket(send_Port);
			Thread t = new Thread(new OpenSLP_Listener(send_Socket,sock));
			t.start();
			while(true)
			{
				System.out.println("Waiting for SSLP Packet");
			
				sock.receive(packet);
				Date date = new Date();
				//System.out.println("Receive Packet TimeStamp"+new Timestamp(date.getTime()));
				System.out.println("Length of the packet is"+packet.getLength());
				System.out.println("SSLP Packet received with Message:"+new String(buffer)+"from "+packet.getAddress()+"On Port"+packet.getPort());
				convert_SSLPtoSLP(packet);
			}			
		}catch (Exception e) {
		// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	
	//this method converts the SSLP message to SLP Message
	//we have to handle all the messages which are present....First Step is try to convert the Service Request Message then we can do other things
	private void convert_SSLPtoSLP(DatagramPacket packet) {
		// TODO Auto-generated method stub
		//Generate a Function for converting each message example ServiceRequest,ServiceReply
		//ServiceTypeRequest,ServiceTypeReply,ServiceReg,ServiceDeReg
		
		//First convert the Packet into SSLP Message format and depending on the function
		//ID call the function 
		
		//System.out.println("IP:"+packet.getAddress());
		//System.out.println("Port:"+packet.getPort());
		Short SequenceNumber= SSLPMessage.getSequenceNumber(packet);
		//System.out.println("Before Conversion the sequence number is "+SequenceNumber);
		Sequencer.add(SequenceNumber,packet.getAddress(),packet.getPort());
		byte[] conversion=SSLPMessage.parse(packet.getAddress(), packet.getPort(), new DataInputStream(new ByteArrayInputStream(packet.getData())),packet);
		if(conversion!=null){
		try {
			//this will create a new class so make sure that only one class is present which will give the readings
		
			//try to get the sequence number here 
			final DataInputStream in = new DataInputStream(new ByteArrayInputStream(conversion));
			short sequencenumber= SLPMessage.getSequenceNumber(in);
		
		//	System.out.println("After conversion The sequence number is"+sequencenumber);
			InetAddress ip_address =Sequencer.getIP(sequencenumber);
			//System.out.println("The ip address is"+ip_address);
		//	System.out.println("port number is "+Sequencer.getPort(sequencenumber));
			InetAddress ip = InetAddress.getByName("fd00::1");

			//System.out.println("SSLP Message is Converted to SLP  Message");
			//System.out.println("Packet sent with the length"+conversion.length);
			DatagramPacket pack = new DatagramPacket(conversion,conversion.length,ip,427);
			//DatagramPacket pack = new DatagramPacket(conversion,conversion.length,ip,427);
			send_Socket.send(pack);
			//SLP_Sock.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		}else{
			System.out.println("SSLP_Listener:Conversion null");
		}	
	}
}


class OpenSLP_Listener implements Runnable{


	MulticastSocket socket,SSLP_Socket;
	public OpenSLP_Listener(MulticastSocket socket,MulticastSocket SSLP_Socket){
		this.socket = socket;
		this.SSLP_Socket = SSLP_Socket;
	}

	
	public void run(){
		byte[] buffer = new byte[200];
		DatagramPacket packet =new DatagramPacket(buffer,buffer.length);
		try{
			while(true){
				System.out.println("waiting for openslp packets");
				socket.receive(packet);
				System.out.println("Packet received from openslp");
				convert_SLPtoSSLP(packet);

				//convert SLP to SSLP Packet



			}
			
		}catch(Exception e){
			e.printStackTrace();
		}

	}

	//This method converts the SLP message to SSLP Message
	public void convert_SLPtoSSLP(DatagramPacket packet) {
		// TODO Auto-generated method stub
	//Parse Function will give back the SSLP Message
		try{
		Short SequenceNumber= SLPMessage.getSequenceNumber(new DataInputStream(new ByteArrayInputStream(packet.getData())));
		
	byte[] converted=SLPMessage.parse(packet.getAddress(), packet.getPort(), new DataInputStream(new ByteArrayInputStream(packet.getData())),packet.getLength());
	
	
	
			
		//Next we have to check which message it is .Create a Function Which Does that
	
	byte FunctionID=SLPMessage.getMsgID(new DataInputStream(new ByteArrayInputStream(packet.getData())));
		System.out.println("Message id is "+FunctionID);
		InetAddress ip_address =Sequencer.getIP(SequenceNumber);
		System.out.println("ip address is"+ip_address);
		DatagramPacket pack = new DatagramPacket(converted,converted.length,ip_address,4270);
		SSLP_Socket.send(pack);

	
	}catch(Exception e){
		e.printStackTrace();
	}
	
	}

}
