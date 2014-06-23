


generic configuration CoapCountResourceC(uint8_t uri_key)
{
	provides interface ReadResource;
	provides interface WriteResource;
}

implementation{

	components CoapCountResourceP(uri_key) as CoapCountResourceP;
	ReadResource= CoapCountResourceP;
	WriteResource=CoapCountResourceP;
}
