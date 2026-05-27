package com.mips;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.jupiter.api.Test;

public class MipsDBConnectionTests {
    @Test
    void encryptTest() {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setAlgorithm("PBEWithMD5AndDES");
        encryptor.setPassword("finance_key_2026"); // 절대 유출되면 안 되는 키!

        // 우리가 DB 세팅할 때 썼던 실제 비밀번호 입력
        String plainText = "RT4h3r3efLVg+GmDlR5gUsEZE3RPaVqW";

        String encryptedText = encryptor.encrypt(plainText);
        System.out.println("암호화된 문자열: " + encryptedText); // 콘솔에 찍힌 값을 복사!
    }

}
