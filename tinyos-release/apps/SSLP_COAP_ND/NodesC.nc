
configuration NodesC{
	provides interface Nodes;
}

implementation{

	components NodesP;
	components SSLC;
	Nodes=NodesP;
	NodesP.ServiceLocation->SSLC;

	components new TimerMilliC() as Timer;
	NodesP.Timer -> Timer;

	components LedsC;
	NodesP.Leds -> LedsC;

}
