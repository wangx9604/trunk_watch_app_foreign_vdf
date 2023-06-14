package com.xiaoxun.xun.utils;

import android.util.Base64;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class RSAUtils
{
    public static final String KEY_ALGORITHM = "RSA";
    public static final String SIGNATURE_ALGORITHM = "SHA1WithRSA";
    public static final String ENCODING = "utf-8";
    public static final String X509 = "X.509";
    public static final String private_key = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIeVDV4wU5YUcAXR3bvyWqZC+yS8iOc6CT1Ckyh511XRmwTIR7/wSM2mn7HMtrpJx+1ksEnLUog9SS7WCppwC/UKv3W9LkY+s8ZISjAffgSsYcKYnq/5NXQJf4YDmX5xewgHqqOyp4PCy4v2rLr2/huAPHDBvgFOqPjKu0LqIiEjAgMBAAECgYBXEKNN+dCjTqqFQjiWqdIGdHrcI2nyunUWbrsbQerDBF/82TQiEIZf8YEZZzFsPVbynjcKnFN6g4iMWAzlEdDK8nh11NiNQPjavCKE1J4SLZtPWDOoJgati8S6PKcSZ3lqEdLyiUMJho7uyKNUw5ZuuAAFLze/ZLO5d6v8c+5n6QJBALznUOUjH951EFHwNaKpx3/QCY+tZsn3+MWN7L5+LWGvL1VhGEmHOUO0s11UW07bnF0Uz0sqxK4r+WwQw1ACDb8CQQC3vVD98BeqDGghnif4BWcFQJbI8axNOuSNVSVSKjUSIhrreQgjyhji9LmRB3ZSf4PP/Zi4d0Brf8nfxfyHEw2dAkB6vPlsyFOuoPVHqAHRfOro+u0bc5lz5TC4e6y1YRpve/oPSZUUQU5N1Z0tKOBi7oI8YIcH2fQNDvly47ljxNHVAkAZx3MUndoEHseBZ/o9nyxsHTrKqLW/BDNK7RZWZ9uYVramMQ4GT3Wcai+a/IwKWR+6T99cika8jmpZfiLHJNI1AkEAt0B3rtJRkswG71ezpHdvLdMQfMPuZ5bdpcZEl6d9AJqxPzPKR9gMJSi2/JEE/pP3mwks9f1i117ZibEgRqaojQ==";
    public static final String private2_key = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBANcA+/Eip49SLyoisFv9b9Jm2YPTBZ9QGUgPkmJtXtT2Ruf4BEa9zmC+NU6Ld3bCchV1Rp+BVtcy/g4DrZIoyvF21sM/TxDK/QG0EiZMO93XN+Hx8mEQpAgcjdp9K2i48CZOwo04HFxZL1RON/HdQZpJjBXAcUu5iSY0K5vJ5aCVAgMBAAECgYEAjkxRcxv7iEjdfGavEIMhMkDt72iHzRQnV2EvAkPewtYowbiNpABVigP5pf16WLiC5x7SWhkxT4apTL80borfHVA4bUVa9bBpm/o+yDPQUZfJX/dh9NO5h1UK9p3dWmmwXV1onRzshmoGqH+ZqmnOdwzX2SGaPQstqBtQBh4+MUECQQD0e0t1zCVj4P7sH/0/jGAbPpng0OSQu/DtRqg8WyyOvpQOT4QaFnuKrwEwg3wFxpolFCvBE94nF22fpp+/IOylAkEA4SIoC9lFjcud/8G/1jBplAj1bygDQaHfudgLrbk4rrB4YnHyUg0nQXpg1K8ngeJdCdPKeOEgsiDF7kJFgL3xMQJBALPLvNob0V+bxz5V5EQI82D17+qjFYTPgsatEOqaTrGHvJCtr8QTxrVeMKzB9cK1pQUhyW/MMbNkCm2/93lzODUCQQCZzD61UXmdk4NdbBvElBAcLD6W8CvBU3dGO1xHEqixVib3gcY73XZ62cJ6qSmBJoXOp9vC1nECnzb4PFfEyn2RAkABJav91Koft9AywQtRf7SJKSFxVROfivDIMMH/S2+y0Xx56jSCttUihAuTDyvv2TgmyciviUb4iMIOe+dbYrrf";
    public static final String PUBLISH_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCgNZI/OH2hn++sy89YdqLJma/GEP+OPHfZnhf0LFKX/S2BXbILOwDQeFHmGXUp4sBgFVYsHGdvIr5PSOEkiUWmL/QeUaWU4NFFF7Vtpv1NrATNRipbtNEfkQ7lDeR8cTHHWIiu+NWLSjVDX/iFaanD9Ehksm+l/76DxohHfchdKwIDAQAB";

    public static PublicKey getPublicKey(String key)
            throws Exception
    {
        byte[] keyBytes = Base64.decode(key,Base64.DEFAULT);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    public static PrivateKey getPrivateKey(String key)
            throws Exception
    {
        byte[] keyBytes = Base64.decode(key.getBytes("utf-8"),Base64.DEFAULT);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }

    public static String signByPrivateKey(String content, String privateKey)
    {
        try
        {
            PrivateKey priKey = getPrivateKey(privateKey);
            Signature signature = Signature.getInstance("SHA1WithRSA");
            signature.initSign(priKey);
            signature.update(content.getBytes("utf-8"));
            byte[] signed = signature.sign();
            return new String(Base64.encode(signed,Base64.URL_SAFE), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean verifySignByPublicKey(String content, String sign, String publicKey) {
        try {
            PublicKey pubKey = getPublicKey(publicKey);

            Signature signature = Signature.getInstance("SHA1WithRSA");
            signature.initVerify(pubKey);
            signature.update(content.getBytes("utf-8"));

            return signature.verify(Base64.decode(sign.getBytes("utf-8"),Base64.DEFAULT));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String encryptByPublicKey(String plainText, String publicKey) {
        try {
            PublicKey pubKey = getPublicKey(publicKey);
            Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            byte[] enBytes = cipher.doFinal(plainText.getBytes("utf-8"));
            return new String(Base64.encode(enBytes,Base64.DEFAULT));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decryptByPrivateKey(String enStr, String privateKey) {
        try {
            PrivateKey priKey = getPrivateKey(privateKey);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(2, priKey);
            byte[] deBytes = cipher.doFinal(Base64.decode(enStr,Base64.DEFAULT));
            return new String(deBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) throws Exception {

//	  String key = RSAUtils.decryptByPrivateKey("M0ua5NQNw7UNdCtWoTGYIpdaZbSJJgSKCiReozvI5le1LfK26wS2nccZ/GpMtOWoRpTQVmArKCT3d/C16To4n6WiZpo5btX0Up+goNuYP/yfhA9YHrfH7Um39ilIlrhv8Og/NkWH03cb8iIPYYwwglSIzCzv8ydD+rYq1btyBQ4=", private2_key);
//	  String pt2 = "KK2rNvNXWrjZQZY4JWbdYaLTfvmXJCAK7ZFE5LndxpXwoBDoh9F8YjgZZ4nM3ep+Tnr/GOLc3gHZWf+cJezSqTI/mTVdr1hO7sksi+z72Cc2A3MScWQ+4fHwJmwFc4gwp6dvpckGfNnLyKHlWFS+VdM5jfN9Sg0o7H4Ugz7CvacN";
//	  System.out.println(key+":"+key.length());
//	  byte[] data = AesCBC.decrypt(dx.common.util.Base64.decode(pt2.getBytes()), key.getBytes(), key.getBytes());
//	  System.out.println(new String(data));

    }



}