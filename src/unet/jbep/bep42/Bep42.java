package unet.jbep.bep42;

import unet.jbep.libs.hash.CRC32C;

import java.net.InetAddress;
import java.util.Random;

public class Bep42 {

    private static final int NODE_LENGTH = 20;
    private final byte[] v4_mask = { 0x03, 0x0f, 0x3f, (byte) 0xff };
    private final byte[] v6_mask = { 0x01, 0x03, 0x07, 0x0f, 0x1f, 0x3f, 0x7f, (byte) 0xff };

    private InetAddress address;
    private int port;
    private byte[] bid;

    public Bep42(InetAddress address, int port, byte[] bid){
        if(bid.length != NODE_LENGTH){
            throw new IllegalArgumentException("Key must have 20 bytes");
        }

        this.address = address;
        this.port = port;
        this.bid = bid;
    }

    public Bep42(InetAddress address, int port, String key){
        if(key.length() != NODE_LENGTH*2){
            throw new IllegalArgumentException("Hex String must have 40 bytes");
        }

        this.address = address;
        this.port = port;
        bid = new byte[NODE_LENGTH];

        for(int i = 0; i < key.length(); i += 2){
            bid[i/2] = (byte) ((Character.digit(key.charAt(i), 16) << 4)+Character.digit(key.charAt(i+1), 16));
        }
    }

    public Bep42(InetAddress address, int port){
        this.address = address;
        this.port = port;

        byte[] ip = address.getAddress();
        byte[] mask = ip.length == 4 ? v4_mask : v6_mask;

        for(int i = 0; i < ip.length; i++){
            ip[i] &= mask[i];
        }

        final Random random = new Random();
        int rand = random.nextInt() & 0xFF;
        int r = rand & 0x7;

        ip[0] |= r << 5;

        CRC32C c = new CRC32C();
        c.update(ip, 0, ip.length);
        int crc = (int) c.getValue();

        // idk about this stuff below
        bid = new byte[NODE_LENGTH];
        bid[0] = (byte) ((crc >> 24) & 0xFF);
        bid[1] = (byte) ((crc >> 16) & 0xFF);
        bid[2] = (byte) (((crc >> 8) & 0xF8) | (random.nextInt() & 0x7));

        for(int i = 3; i < 19; i++){
            bid[i] = (byte) (random.nextInt() & 0xFF);
        }

        bid[19] = (byte) (rand & 0xFF);
    }

    public boolean hasSecureID(){
        byte[] ip = address.getAddress();
        byte[] mask = ip.length == 4 ? v4_mask : v6_mask;

        for(int i = 0; i < mask.length; i++){
            ip[i] &= mask[i];
        }

        int r = bid[19] & 0x7;

        ip[0] |= r << 5;

        CRC32C c = new CRC32C();
        c.update(ip, 0, Math.min(ip.length, 8));
        int crc = (int) c.getValue();

        return ((getInt(0) ^ crc) & 0xff_ff_f8_00) == 0;

		/*
		uint8_t* ip; // our external IPv4 or IPv6 address (network byte order)
		int num_octets; // the number of octets to consider in ip (4 or 8)
		uint8_t node_id[20]; // resulting node ID


		uint8_t* mask = num_octets == 4 ? v4_mask : v6_mask;

		for (int i = 0; i < num_octets; ++i)
		        ip[i] &= mask[i];

		uint32_t rand = std::rand() & 0xff;
		uint8_t r = rand & 0x7;
		ip[0] |= r << 5;

		uint32_t crc = 0;
		crc = crc32c(crc, ip, num_octets);

		// only take the top 21 bits from crc
		node_id[0] = (crc >> 24) & 0xff;
		node_id[1] = (crc >> 16) & 0xff;
		node_id[2] = ((crc >> 8) & 0xf8) | (std::rand() & 0x7);
		for (int i = 3; i < 19; ++i) node_id[i] = std::rand();
		node_id[19] = rand;
		*/
    }

    public byte[] getByteKey(){
        return bid;
    }

    public String getKey(){
        StringBuilder sb = new StringBuilder(NODE_LENGTH*2);

        sb.append(String.format("%02x", bid[0])+String.format("%02x", bid[1])+String.format("%02x", bid[2])+" ");

        for(int i = 3; i < 19; i++){
            sb.append(String.format("%02x", bid[i]));
        }

        sb.append(" "+String.format("%02x", bid[19]));
        return sb.toString();
    }

    public InetAddress getAddress(){
        return address;
    }

    public int getPort(){
        return port;
    }

    private int getInt(int offset){
        return Byte.toUnsignedInt(bid[offset]) << 24 | Byte.toUnsignedInt(bid[offset+1]) << 16 | Byte.toUnsignedInt(bid[offset+2]) << 8 | Byte.toUnsignedInt(bid[offset+3]);
    }

    @Override
    public String toString(){
        return getKey()+" V: "+hasSecureID()+"  "+address.getHostAddress()+":"+port;
    }
}
