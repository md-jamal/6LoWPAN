	

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Date;
import java.sql.Timestamp;

import ch.ethz.iks.slp.ServiceType;
import ch.ethz.iks.slp.ServiceURL;

public class Translation
{

	

	public static void main(String args[])
	{

		
		try
		{
			//It has to bind to a default port of SLP:427 and let us think the port of SSLP:4270 so we require two 				sockets each 	socket will bind to one port

			int SLP_PORT=427;
			
			int SSLP_PORT=4270;
		
			//Multicast Address of SLP is 239.255.255.253 Lets both the socket join the multicast group
		
		//	InetAddress MULTICAST_IP= InetAddress.getByName("239.255.255.253");
	
			//With this Multicast address you can receive DA Advertisements
			InetAddress SVRLOC_DA= InetAddress.getByName("FF05::123"); 
			
			MulticastSocket SLP_Socket= new MulticastSocket(SLP_PORT);
	
			MulticastSocket SSLP_Socket= new MulticastSocket(SSLP_PORT);

			SLP_Socket.setInterface(InetAddress.getByName("fd00::1"));
		
			SSLP_Socket.setInterface(InetAddress.getByName("2001::1"));
	
			SLP_Socket.joinGroup(SVRLOC_DA);
	
			SSLP_Socket.joinGroup(SVRLOC_DA);


			//lets start two threads one will receive for SLP Packets and another will receive for SSLP Packets
			Thread SLP_Thread = new Thread(new SLP_Listener(SLP_Socket,SSLP_Socket));
			
			Thread SSLP_Thread = new Thread(new SSLP_Listener(SSLP_Socket,SLP_Socket));
			
			SLP_Thread.start();
			SSLP_Thread.start();
			
			

		}catch(Exception e){
			e.printStackTrace();
		}

	}

	
	

}


class SLP_Listener implements Runnable
{
	MulticastSocket sock,SSLP_Socket;
	public SLP_Listener(MulticastSocket sock,MulticastSocket SSLP_Socket)
	{
		this.sock = sock;
		this.SSLP_Socket = SSLP_Socket;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		//Create a Datagram Packet in which u will receive the packet
		
		byte[] buffer = new byte[200];
		DatagramPacket packet =new DatagramPacket(buffer,buffer.length);
		try {

			while(true)
			{

				
				System.out.println("Waiting for  Slp Packet");
			
				sock.receive(packet);
				System.out.println("Length of the packet received"+packet.getLength());
				Date date = new Date();
				//System.out.println(new Timestamp(date.getTime()));
				//System.out.println("SLP Packet received with Message:"+new String(buffer)+"from "+packet.getAddress()+"On Port"+packet.getPort());
				convert_SLPtoSSLP(packet);
				
			}			
		}catch (IOException e) {
		// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	
	//This method converts the SLP message to SSLP Message
	public void convert_SLPtoSSLP(DatagramPacket packet) {
		// TODO Auto-generated method stub
	//Parse Function will give back the SSLP Message
		
		System.out.println("IP"+packet.getAddress());
		System.out.println("Port"+packet.getPort());
		Short SequenceNumber= SLPMessage.getSequenceNumber(new DataInputStream(new ByteArrayInputStream(packet.getData())));
		//System.out.println("The sequence number is"+SequenceNumber);
		Sequencer.add(SequenceNumber,packet.getAddress(),packet.getPort());
		//System.out.println("The port number for the sequencenumber is"+Sequencer.getPort(SequenceNumber));
	byte[] converted=SLPMessage.parse(packet.getAddress(), packet.getPort(), new DataInputStream(new ByteArrayInputStream(packet.getData())),packet.getLength());
	
	
	
			
		//Next we have to check which message it is .Create a Function Which Does that
	
	byte FunctionID=SLPMessage.getMsgID(new DataInputStream(new ByteArrayInputStream(packet.getData())));
	
	
	//We have to send the packet after this
	switch(FunctionID)
	{
	case SLPMessage.SERVICE_REQUEST:
		//WE have to Multicast the packet
		try {
			InetAddress ip = InetAddress.getByName("FF02::123");//We will multicast this
			//DatagramSocket sock = new DatagramSocket();
			//System.out.println("SLP Request Message is Converted to SSLP Request Message");
			DatagramPacket pack = new DatagramPacket(converted,converted.length,ip,4270);
			//System.out.println("Packet sent with the length"+converted.length);
			SSLP_Socket.send(pack);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		break;
	
	case SLPMessage.SERVICE_TYPEREQUEST:
		
		InetAddress ip;
		try {
			ip = InetAddress.getByName("FEC0::3");
			DatagramSocket sock = new DatagramSocket();
			//System.out.println("SLP Service Type Request Message is Converted to SSLP Service Type Request Message");
			DatagramPacket pack = new DatagramPacket(converted,converted.length,ip,4270);
			//System.out.println("Packet sent with the length"+converted.length);
			sock.send(pack);
			sock.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		break;
		
	case SLPMessage.SERVICE_REPLY:
		try {
			ip = InetAddress.getByName("FEC0::3");
			DatagramSocket sock = new DatagramSocket();
			//System.out.println("SLP Service Reply Message is Converted to SSLP Service Reply Message");
			DatagramPacket pack = new DatagramPacket(converted,converted.length,ip,4270);
			//System.out.println("Packet sent with the length"+converted.length);
			sock.send(pack);
			sock.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		break;
	case SLPMessage.SERVICE_REGISTRATION:
		try{
			ip = InetAddress.getByName("FEC0::3");
			DatagramSocket sock = new DatagramSocket();
			DatagramPacket pack = new DatagramPacket(converted,converted.length,ip,4270);
			sock.send(pack);
			sock.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		//System.out.println("\nSLP Service Registration Message is received and converted to SSLP Service Registration Message");
	
	case SLPMessage.SERVICE_TYPEREPLY:
		try{
			ip = InetAddress.getByName("FEC0::3");
			DatagramSocket sock = new DatagramSocket();
			DatagramPacket pack = new DatagramPacket(converted,converted.length,ip,4270);
			sock.send(pack);
			sock.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		//System.out.println("\nSLP Service Type Reply Message is received and converted to SSLP Service Reply Message");


	case SLPMessage.DA_ADVERTISEMENT:
		try{
			ip = InetAddress.getByName("ff02::123");
			DatagramPacket pack = new DatagramPacket(converted,converted.length,ip,4270);
			SSLP_Socket.send(pack);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		//System.out.println("\nSLP Directory Advertisement Message is received and converted to SSLP Directory Advertisement Message");

	
	}	
	}
	
}
