COMPONENT=DATestC
#CFLAGS += -I$(TOSDIR)/lib/printf
# uncomment this for network programming support
# BOOTLOADER=tosboot

# radio options
 CFLAGS += -DCC2420_DEF_CHANNEL=15
 CFLAGS += -DCC2520_DEF_CHANNEL=15
PFLAGS += -DNODE_HOST
# CFLAGS += -DRF230_DEF_CHANNEL=26
# CFLAGS += -DCC2420_DEF_RFPOWER=4 -DENABLE_SPI0_DMA

# enable dma on the radio
# PFLAGS += -DENABLE_SPI0_DMA

# you can compile with or without a routing protocol... of course,
# without it, you will only be able to use link-local communication.
#CFLAGS+=-DRPL_ROUTING -DRPL_STORING_MODE -I$(LOWPAN_ROOT)/tos/lib/net/rpl
# PFLAGS += -DRPL_OF_MRHOF

# tell the 6lowpan layer to not generate hc-compressed headers
# PFLAGS += -DLIB6LOWPAN_HC_VERSION=-1

# if this is set, motes will send debugging information to the address
# listed.  BLIP_STATS causes blip to record statistics.
# you can log this information using the util/Listener.py script
# PFLAGS += -DREPORT_DEST=\"fec0::100\" -DBLIP_STATS

# if you're using DHCP, set this to try and derive a 16-bit address
# from the IA received from the server.  This will work if the server
# gives out addresses from a /112 prefix.  If this is not set, blip
# will only use EUI64-based link addresses.  If not using DHCP, this
# causes blip to use TOS_NODE_ID as short address.  Otherwise the
# EUI will be used in either case.
#PFLAGS += -DBLIP_DERIVE_SHORTADDRS

# Set this when you want to use statically assigned addresses. The stack will
# not use DHCP in that case.
PFLAGS += -DIN6_PREFIX=\"2001::\"

CFLAGS += -DNEW_PRINTF_SEMANTICS -DPRINTFUART_ENABLED
CFLAGS += -DPRINTF_BUFFER_SIZE=1024
CFLAGS += -DNEIGHBOR_DISCOVERY
#CFLAGS += -DCC2520_NO_ADDRESS_RECOGNITION
#CFLAGS += -DLCD_DEBUG
include $(MAKERULES)

