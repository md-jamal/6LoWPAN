
import java.io.*;
import java.net.*;
public class SSLP
{
	
	public static void main(String args[])
	{
		
		//This is a Test Program which just sends the sslp packet for suppose 

		//lets take the sslp port as 4270
		try{
	
			int SSLP_PORT=4270;

			InetAddress SSLP_Address = InetAddress.getByName("239.255.255.253"); 
	
			//lets join this group so you may receive some Directory Advertisement
	
			MulticastSocket SSLP_Socket = new MulticastSocket();
	
			SSLP_Socket.joinGroup(SSLP_Address);
	
			//Lets send a SSLP packet to this address
	
			String msg= "Hai Iam from SSLP";
			DatagramPacket SSLP_Packet = new DatagramPacket(msg.getBytes(),msg.length(),SSLP_Address,SSLP_PORT);
		
			SSLP_Socket.send(SSLP_Packet);
	
			SSLP_Socket.close();

		}catch(Exception e)
		{
			e.printStackTrace();
		}


		

	}



}
