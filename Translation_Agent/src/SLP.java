

import java.io.*;
import java.net.*;
public class SLP
{
	public static void main(String args[])
	{
		//This is a Test Program which just sends the sslp packet for suppose 

		//lets take the sslp port as 4270
		try{
	
			int SLP_PORT=427;

			InetAddress SLP_Address = InetAddress.getByName("239.255.255.253"); 
	
			//lets join this group so you may receive some Directory Advertisement
	
			MulticastSocket SLP_Socket = new MulticastSocket();
	
			SLP_Socket.joinGroup(SLP_Address);
	
			//Lets send a SSLP packet to this address
	
			String msg= "Hai Iam from SLP";
			DatagramPacket SSLP_Packet = new DatagramPacket(msg.getBytes(),msg.length(),SLP_Address,SLP_PORT);
		
			SLP_Socket.send(SSLP_Packet);
	
			SLP_Socket.close();

		}catch(Exception e)
		{
			e.printStackTrace();
		}



	}


}
