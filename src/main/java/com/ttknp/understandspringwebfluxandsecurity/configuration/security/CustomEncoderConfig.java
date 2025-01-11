package com.ttknp.understandspringwebfluxandsecurity.configuration.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

// ** create custom password encoder (for user’s password simulation)
@Component
public class CustomEncoderConfig implements PasswordEncoder {

    private String secret;
    private Integer iteration; // n. การวนซ้ำ
    private Integer keyLength;

    public CustomEncoderConfig(@Value("${springbootwebfluxjjwt.password.encoder.secret}")String secret,
                               @Value("${springbootwebfluxjjwt.password.encoder.iteration}") Integer iteration,
                               @Value("${springbootwebfluxjjwt.password.encoder.keyLength}") Integer keyLength) {
        this.secret = secret;
        this.iteration = iteration;
        this.keyLength = keyLength;
    }

    /**
     More info (https://www.owasp.org/index.php/Hashing_Java) 404 :(
     */
    @Override
    public String encode(CharSequence cs) {
        try {
            byte[] result = SecretKeyFactory
                    .getInstance("CUSTOMWithHmacSHA512")
                    .generateSecret(new PBEKeySpec(cs.toString().toCharArray(), secret.getBytes(), iteration, keyLength))
                    .getEncoded();
            return Base64
                    .getEncoder()
                    .encodeToString(result);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean matches(CharSequence cs, String string) {
        return encode(cs).equals(string);
    }
}