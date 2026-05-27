package com.mips.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class HS256Example {

    private static final String ALGORITHM = "HmacSHA256";

    public static void main(String[] args) {
        try {
            // 1. 안전한 256비트(32바이트) 비밀키 생성 및 Base64 인코딩
            String base64SecretKey = generateSafeBase64Key();
            System.out.println("생성된 Secret Key (Base64): " + base64SecretKey);

            // 2. 서명할 데이터 (JWT의 경우 Header + "." + Payload 부분에 해당)
            String dataToSign = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIn0";

            // 3. HS256 알고리즘으로 서명(해싱) 생성
            String signature = createHS256Signature(dataToSign, base64SecretKey);
            System.out.println("생성된 서명 (Base64 URL Safe): " + signature);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 32바이트(256비트) 이상의 난수를 발생시켜 Base64로 인코딩한 문자열을 반환합니다.
     */
    public static String generateSafeBase64Key() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] keyBytes = new byte[32]; // 32 bytes = 256 bits
        secureRandom.nextBytes(keyBytes);
        return Base64.getEncoder().encodeToString(keyBytes);
    }

    /**
     * 평문 데이터와 Base64로 인코딩된 비밀키를 받아 HS256 서명을 생성합니다.
     */
    public static String createHS256Signature(String data, String base64SecretKey) throws Exception {
        // 1. Base64로 인코딩된 키를 다시 바이트 배열로 디코딩
        byte[] decodedKey = Base64.getDecoder().decode(base64SecretKey);

        // 2. 알고리즘과 바이트 배열을 이용해 SecretKeySpec 객체 생성
        SecretKeySpec secretKeySpec = new SecretKeySpec(decodedKey, ALGORITHM);

        // 3. Mac (Message Authentication Code) 객체를 HmacSHA256 알고리즘으로 초기화
        Mac mac = Mac.getInstance(ALGORITHM);
        mac.init(secretKeySpec);

        // 4. 데이터를 바이트 배열로 변환하여 암호화(해싱) 수행
        byte[] hashBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

        // 5. 생성된 해시값을 Base64 URL Safe 형태로 인코딩 (JWT 표준 방식)
        // JWT는 URL에 포함될 수 있으므로 '+' 나 '/' 같은 문자가 포함되지 않는 UrlEncoder를 사용합니다.
        return Base64.getUrlEncoder().withoutPadding().encodeToString(hashBytes);
    }
}