package unet.jbep.bep42;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Test {

    public static void main(String[] args)throws UnknownHostException {
        System.out.println("Testing BEP42");
        System.out.println();

        //CHECKING KNOWN GOOD IDS
        System.out.println("GOOD: "+new Bep42(InetAddress.getByName("124.31.75.21"), 1, "5fbfbff10c5d6a4ec8a88e4c6ab4c28b95eee401").hasSecureID());
        System.out.println("GOOD: "+new Bep42(InetAddress.getByName("21.75.31.124"), 1, "5a3ce9c14e7a08645677bbd1cfe7d8f956d53256").hasSecureID());
        System.out.println("GOOD: "+new Bep42(InetAddress.getByName("65.23.51.170"), 1, "a5d43220bc8f112a3d426c84764f8c2a1150e616").hasSecureID());
        System.out.println("GOOD: "+new Bep42(InetAddress.getByName("84.124.73.14"), 1, "1b0321dd1bb1fe518101ceef99462b947a01ff41").hasSecureID());
        System.out.println("GOOD: "+new Bep42(InetAddress.getByName("43.213.53.83"), 1, "e56f6cbf5b7c4be0237986d5243b87aa6d51305a").hasSecureID());
        System.out.println();

        //CHECKING KNOWN BAD IDS
        System.out.println("BAD:  "+new Bep42(InetAddress.getByName("124.31.75.21"), 1, "5cbfbff10c5d6a4ec8a88e4c6ab4c28b95eee401").hasSecureID());
        System.out.println("BAD:  "+new Bep42(InetAddress.getByName("21.75.31.124"), 1, "5c3ce9c14e7a08645677bbd1cfe7d8f956d53256").hasSecureID());
        System.out.println("BAD:  "+new Bep42(InetAddress.getByName("65.23.51.170"), 1, "acd43220bc8f112a3d426c84764f8c2a1150e616").hasSecureID());
        System.out.println("BAD:  "+new Bep42(InetAddress.getByName("84.124.73.14"), 1, "1c0321dd1bb1fe518101ceef99462b947a01ff41").hasSecureID());
        System.out.println("BAD:  "+new Bep42(InetAddress.getByName("43.213.53.83"), 1, "ec6f6cbf5b7c4be0237986d5243b87aa6d51305a").hasSecureID());
        System.out.println();

        //CREATING IDS
        System.out.println("TEST: "+new Bep42(InetAddress.getByName("124.31.75.21"), 1).hasSecureID());
        System.out.println("TEST: "+new Bep42(InetAddress.getByName("21.75.31.124"), 1).hasSecureID());
        System.out.println("TEST: "+new Bep42(InetAddress.getByName("65.23.51.170"), 1).hasSecureID());
        System.out.println("TEST: "+new Bep42(InetAddress.getByName("84.124.73.14"), 1).hasSecureID());
        System.out.println("TEST: "+new Bep42(InetAddress.getByName("43.213.53.83"), 1).hasSecureID());
        System.out.println();
    }
}
