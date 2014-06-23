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

	MulticastSocket sock;
	public SSLP_Listener(MulticastSocket sSLP_Socket) {
		// TODO Auto-generated constructor stub
		this.sock=sSLP_Socket;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		byte[] buffer = new byte[100];
		DatagramPacket packet =new DatagramPacket(buffer,buffer.length);
		try {

			while(true)
			{
				System.out.println("Waiting for SSLP Packet");
			
				sock.receive(packet);
				Date date = new Date();
				System.out.println("Receive Packet TimeStamp"+new Timestamp(date.getTime()));
				System.out.println("Length of the packet is"+packet.getLength());
				System.out.println("SSLP Packet received with Message:"+new String(buffer)+"from "+packet.getAddress()+"On Port"+packet.getPort());
				convert_SSLPtoSLP(packet);
			}			
		}catch (IOException e) {
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
		byte[] conversion=SSLPMessage.parse(packet.getAddress(), packet.getPort(), new DataInputStream(new ByteArrayInputStream(packet.getData())),packet);

		try {
			//this will create a new class so make sure that only one class is present which will give the readings
		
			//try to get the sequence number here 
			final DataInputStream in = new DataInputStream(new ByteArrayInputStream(conversion));
			short sequencenumber= SLPMessage.getSequenceNumber(in);
		
			System.out.println("After conversion The sequence number is"+sequencenumber);
			InetAddress ip_address =Sequencer.getIP(sequencenumber);
			System.out.println("The ip address is"+ip_address);
			System.out.println("port number is "+Sequencer.getPort(sequencenumber));
			InetAddress ip = InetAddress.getByName("fd00::11");
			DatagramSocket sock = new DatagramSocket();
			System.out.println("SSLP Message is Converted to SLP  Message");
			System.out.println("Packet sent with the length"+conversion.length);
			DatagramPacket pack = new DatagramPacket(conversion,conversion.length,ip_address,Sequencer.getPort(sequencenumber));
			sock.send(pack);
			sock.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
}
