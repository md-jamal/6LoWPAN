import java.net.InetAddress;


public class Sequencer {

	static int MAX_SEQUENCES = 100;
	static short[] sequences= new short[MAX_SEQUENCES];
	static InetAddress[] IPs= new InetAddress[MAX_SEQUENCES];
	static int[] ports= new int[MAX_SEQUENCES];

	static int index=0;
	public static void add(short sequence,InetAddress IP,int port)
	{
		if(index == MAX_SEQUENCES)	//then loop back
			index = 0;
		sequences[index]=sequence;
		IPs[index]=IP;
		ports[index]=port;
		//System.out.println("The index is"+index);
		index++;
		
	}

		
	public static InetAddress getIP(short sequence)
	{
		int ind=0;
		for(ind=0;ind<IPs.length;ind++)
		{
			if(sequences[ind]==sequence)
				break;
		}	
		
		return IPs[ind];
	}
	
	public static int getPort(short sequence)
	{
		int ind=0;
		for(ind=0;ind<ports.length;ind++)
		{
			if(sequences[ind]==sequence)
				break;
		}	
		return ports[ind];
	}
}
