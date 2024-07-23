package com.shopme.admin.security;

import org.springframework.security.crypto.bcrypt.BCrypt;

public class BcryptDecoder {

    public static String decodeBcrypt(String encodedString) {

        return BCrypt.hashpw(encodedString, BCrypt.gensalt());
    }

    public static void main(String[] args) {
        String encodedString = "$2a$10$zyNQbQgXJai5aUyc932o1eI.3WwZaONFkEkoYQebz2N2f8pDbfYZ.";
        String decodedString = decodeBcrypt(encodedString);
        System.out.println("Decoded string: " + decodedString);
    }
}
