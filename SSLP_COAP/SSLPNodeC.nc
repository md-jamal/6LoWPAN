
configuration SSLPNodeC{
	provides interface SSLPNode;
}

implementation{

	components SSLPNodeP;
	components SSLC;
	SSLPNode=SSLPNodeP;
	SSLPNodeP.ServiceLocation->SSLC;

	components new TimerMilliC() as Timer;
	SSLPNodeP.Timer -> Timer;

}
