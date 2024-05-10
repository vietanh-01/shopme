package com.shopme.admin.security;

import org.springframework.security.crypto.bcrypt.BCrypt;

public class BcryptDecoder {

    public static String decodeBcrypt(String encodedString) {

        return BCrypt.hashpw(encodedString, BCrypt.gensalt());
    }

    public static void main(String[] args) {
        String encodedString = "$2a$12$m1m6js2jPaCVEGuag9mJ2.VTT0vXfsWoVAyXc.NrwjSq0a.MVTxte";
        String decodedString = decodeBcrypt(encodedString);
        System.out.println("Decoded string: " + decodedString);
    }
}
