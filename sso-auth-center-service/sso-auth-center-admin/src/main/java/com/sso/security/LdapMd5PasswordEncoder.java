package com.sso.security;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class LdapMd5PasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(rawPassword.toString().getBytes(StandardCharsets.UTF_8));
            return "{MD5}" + Base64.getEncoder().encodeToString(digest);
        } catch (Exception e) {
            throw new RuntimeException("MD5 encoding failed", e);
        }
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (!encodedPassword.startsWith("{MD5}")) {
            return false;
        }
        return this.encode(rawPassword).equals(encodedPassword);
    }
}
