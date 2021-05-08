package unet.jbep.bep5;

import unet.jbep.libs.upnp.UPnP;

import java.net.InetAddress;

public class Test {

    public static void main(String[] args){
        int port = 6969;

        if(UPnP.isUPnPAvailable()){
            System.out.println("UPnP is available.");
            InetAddress externalAddress = UPnP.getExternalIP();
            System.out.println("UPnP found your external IP address as: "+externalAddress.getHostAddress());

            if(!UPnP.isMappedUDP(port)){
                UPnP.openPortUDP(port);
                System.out.println("UPnP opened port: "+port);
                System.out.println("Testing BEP5");
                System.out.println();

                Bep5 bep = new Bep5(externalAddress, port);

                Runtime.getRuntime().addShutdownHook(new Thread(new Runnable(){
                    @Override
                    public void run(){
                        System.out.println();
                        System.out.println("Closing UPnP port: "+port);
                        UPnP.closePortUDP(port);
                        bep.close();
                    }
                }));

                try{
                    bep.ping();
                    Thread.sleep(500);
                    System.out.println();
                    System.out.println();
                    System.out.println();

                    bep.findNode();
                    Thread.sleep(500);
                    System.out.println();
                    System.out.println();
                    System.out.println();

                    bep.getPeers();
                    Thread.sleep(500);
                    System.out.println();
                    System.out.println();
                    System.out.println();

                    bep.announcePeer();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }else{
                System.err.println("Port specified is in use: "+port);
            }
        }else{
            System.err.println("UPnP is not available.");
        }
    }
}
