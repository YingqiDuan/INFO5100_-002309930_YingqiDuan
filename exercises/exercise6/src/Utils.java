import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.security.*;
import java.security.spec.RSAKeyGenParameterSpec;
import java.util.Arrays;

public class Utils {
    public static SecretKey generateAesKey() throws Exception{
        KeyGenerator keyGen=KeyGenerator.getInstance(("AES"));
        keyGen.init(256);
        return keyGen.generateKey();
    }

    public static KeyPair generateRsaKeyPair() throws Exception{
        KeyPairGenerator reaKeyGen=KeyPairGenerator.getInstance(("RSA"));
        reaKeyGen.initialize(new RSAKeyGenParameterSpec(2048,RSAKeyGenParameterSpec.F4));
        return reaKeyGen.generateKeyPair();
    }

    public static byte[] encryptAesGcm(SecretKey key,byte[] plaintext) throws Exception{
        Cipher cipher=Cipher.getInstance("AES/GCM/NoPadding");
        SecureRandom random=new SecureRandom();
        byte[] iv=new byte[12];
        random.nextBytes(iv);
        GCMParameterSpec gcmSpec=new GCMParameterSpec(128,iv);
        cipher.init(Cipher.ENCRYPT_MODE,key,gcmSpec);
        byte[] ciphertext=cipher.doFinal(plaintext);
        byte[] output=new byte[iv.length+ciphertext.length];
        System.arraycopy(iv,0,output,0,iv.length);
        System.arraycopy(ciphertext,0,output,iv.length,ciphertext.length);
        return output;
    }

    public static byte[] decryptAesGcm(SecretKey key,byte[]ivAndCiphertext) throws Exception{
        Cipher cipher=Cipher.getInstance("AES/GCM/NoPadding");
        byte[] iv= Arrays.copyOfRange(ivAndCiphertext,0,12);
        byte[]ciphertext=Arrays.copyOfRange(ivAndCiphertext,12,ivAndCiphertext.length);
        GCMParameterSpec gcmSpec=new GCMParameterSpec(128,iv);
        cipher.init(Cipher.DECRYPT_MODE,key,gcmSpec);
        return cipher.doFinal(ciphertext);
    }

    public static byte[]encryptRsa(PublicKey publicKey,byte[] plaintext) throws Exception{
        Cipher cipher=Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE,publicKey);
        return cipher.doFinal(plaintext);
    }

    public static byte[]decryptRsa(PrivateKey privateKey,byte[]ciphertext)throws Exception{
        Cipher cipher=Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE,privateKey);
        return cipher.doFinal(ciphertext);
    }

    public static byte[] sign(PrivateKey privateKey,byte[]message)throws Exception{
        Signature signature=Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(message);
        return signature.sign();
    }

    public static boolean verify(PublicKey publicKey,byte[]message,byte[]signatureBytes)throws Exception{
        Signature signature=Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(message);
        return signature.verify(signatureBytes);
    }
}


