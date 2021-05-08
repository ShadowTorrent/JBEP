package unet.jbep.bep5;

import unet.jbep.libs.bencode.variables.BencodeObject;
import unet.jbep.bep42.Bep42;

import java.net.*;
import java.nio.ByteBuffer;
import java.util.Random;

public class Bep5 {

    //POTENTIAL DHT ENTRIES
    //  router.utorrent.com:6881
    //  router.bittorrent.com:6881
    //  dht.transmissionbt.com:6881
    //  router.bitcomet.com:6881
    //  dht.aelitis.com:6881

    private DatagramSocket socket;

    private InetAddress myAddress;
    private InetAddress peerAddress;
    private int peerPort;

    public Bep5(InetAddress myAddress, int myPort){
        this.myAddress = myAddress;

        try{
            peerAddress = InetAddress.getByName("router.bittorrent.com");
            peerPort = 6881;

            socket = new DatagramSocket(myPort);
            socket.setSoTimeout(5000);
        }catch(UnknownHostException | SocketException e){
            e.printStackTrace();
        }
    }

    public void ping(){
        try{
            System.out.println("TESTING PING");

            new Thread(new Runnable(){
                @Override
                public void run(){
                    try{
                        DatagramPacket packet = new DatagramPacket(new byte[65535], 65535);
                        socket.receive(packet);

                        BencodeObject ben = new BencodeObject(packet.getData());
                        Bep42 b42 = new Bep42(packet.getAddress(), packet.getPort(), ben.getBencodeObject("r").getBytes("id"));
                        System.out.println("THEIR-ID: "+b42);
                        System.out.println();
                        System.out.println(ben);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }).start();

            Bep42 b42 = new Bep42(myAddress, socket.getLocalPort());
            System.out.println("MY-ID:    "+b42);

            BencodeObject ben = new BencodeObject();
            ben.put("t", "aa");
            ben.put("y", "q");
            ben.put("q", "ping");
            ben.put("a", new BencodeObject());
            ben.getBencodeObject("a").put("id", b42.getByteKey());

            byte[] buf = ben.encode();
            DatagramPacket packet = new DatagramPacket(buf, 0, buf.length, peerAddress, peerPort);

            socket.send(packet);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void findNode(){
        try{
            System.out.println("TESTING FIND_NODE");
            Random random = new Random();
            byte[] randomPeer = new byte[20];
            random.nextBytes(randomPeer);

            new Thread(new Runnable(){
                @Override
                public void run(){
                    try{
                        DatagramPacket packet = new DatagramPacket(new byte[65535], 65535);
                        socket.receive(packet);

                        BencodeObject ben = new BencodeObject(packet.getData());
                        Bep42 b42 = new Bep42(packet.getAddress(), packet.getPort(), ben.getBencodeObject("r").getBytes("id"));
                        System.out.println("THEIR-ID: "+b42);
                        System.out.println();

                        ByteBuffer buf;
                        byte[] bid = new byte[20];
                        byte[] addr;
                        int port;

                        if(ben.getBencodeObject("r").containsKey("nodes")){
                            buf = ByteBuffer.wrap(ben.getBencodeObject("r").getBytes("nodes"));
                            addr = new byte[4];

                        }else if(ben.getBencodeObject("r").containsKey("nodes6")){
                            buf = ByteBuffer.wrap(ben.getBencodeObject("r").getBytes("nodes6"));
                            addr = new byte[16];

                        }else{
                            throw new NullPointerException("No nodes received");
                        }

                        while(buf.position() != buf.limit()){
                            buf.get(bid);
                            buf.get(addr);
                            port = ((buf.get() & 0xff) << 8) | (buf.get() & 0xff);

                            Bep42 peer = new Bep42(InetAddress.getByAddress(addr), port, bid);
                            System.out.println("PEER:     "+peer);
                        }

                        System.out.println();
                        System.out.println(ben);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }).start();


            Bep42 b42 = new Bep42(myAddress, socket.getLocalPort());
            System.out.println("MY-ID:    "+b42);

            BencodeObject ben = new BencodeObject();
            ben.put("t", "aa");
            ben.put("y", "q");
            ben.put("q", "find_node");
            ben.put("a", new BencodeObject());
            ben.getBencodeObject("a").put("id", b42.getByteKey());
            ben.getBencodeObject("a").put("target", randomPeer);

            byte[] buf = ben.encode();
            DatagramPacket packet = new DatagramPacket(buf, 0, buf.length, peerAddress, peerPort);

            socket.send(packet);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void getPeers(){
        System.out.println("TESTING GET_PEERS");

    }

    public void announcePeer(){
        System.out.println("TESTING ANNOUNCE_PEER");

    }

    public void close(){
        socket.close();
    }
}
