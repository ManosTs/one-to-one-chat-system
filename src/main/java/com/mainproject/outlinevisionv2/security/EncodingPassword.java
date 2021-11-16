package com.mainproject.outlinevisionv2.security;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class EncodingPassword {
    public static String passwordEncoder(String originalPassword){
        return BCrypt.hashpw(originalPassword, BCrypt.gensalt(12));
    }

    public static boolean checkIfPwsMatch(String originalPassword, String passwordToCheck){
        return BCrypt.checkpw(originalPassword,passwordToCheck);
    }
}

