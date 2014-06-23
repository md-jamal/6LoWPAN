import java.net.InetAddress;


public class Sequencer {

	static short[] sequences= new short[100];
	static InetAddress[] IPs= new InetAddress[100];
	static int[] ports= new int[100];
	
	static int index=0;
	public static void add(short sequence,InetAddress IP,int port)
	{
		sequences[index]=sequence;
		IPs[index]=IP;
		ports[index]=port;
		System.out.println("The index is"+index);
		index++;
		
	}
	
	public static InetAddress getIP(short sequence)
	{
		int ind=0;
		for(ind=0;ind<100;ind++)
		{
			if(sequences[ind]==sequence)
				break;
		}	
		
		return IPs[ind];
	}
	
	public static int getPort(short sequence)
	{
		int ind=0;
		for(ind=0;ind<100;ind++)
		{
			if(sequences[ind]==sequence)
				break;
		}	
		return ports[ind];
	}
}
