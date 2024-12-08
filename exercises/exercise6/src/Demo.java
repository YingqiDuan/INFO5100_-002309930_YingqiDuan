import javax.crypto.SecretKey;
import java.security.KeyPair;

public class Demo {
    public static void main(String[]args)throws Exception{
        SecretKey sharedAesKey=Utils.generateAesKey();
        KeyPair aliceRsaKeyPair=Utils.generateRsaKeyPair();
        KeyPair bobRsaKeyPair=Utils.generateRsaKeyPair();
        Alice alice=new Alice(sharedAesKey,aliceRsaKeyPair);
        Bob bob=new Bob(sharedAesKey,bobRsaKeyPair);

        String plaintext="Hi Bob, this is Alice.";
        byte[]encryptedMessage=Utils.encryptAesGcm(alice.getAesKey(),plaintext.getBytes());
        byte[]decryptedMessage=Utils.decryptAesGcm(bob.getAesKey(),encryptedMessage);
        System.out.println("Original plaintext: "+plaintext);
        System.out.println("Decrypted plaintext: "+new String(decryptedMessage));

        String secretMessage="This is a secret message from Bob.";
        byte[] rsaEncrypted=Utils.encryptRsa(bob.getReaKeyPair().getPublic(),secretMessage.getBytes());
        byte[]rsaDecrypted=Utils.decryptRsa(bob.getReaKeyPair().getPrivate(),rsaEncrypted);
        System.out.println("Original RSA message: "+secretMessage);
        System.out.println("RSA decrypted message: "+new String(rsaDecrypted));

        String messageToSign="This is from Alice.";
        byte[]signature=Utils.sign(alice.getReaKeyPair().getPrivate(),messageToSign.getBytes());
        boolean isValid=Utils.verify(alice.getReaKeyPair().getPublic(),messageToSign.getBytes(),signature);
        System.out.println("message: "+messageToSign);
        System.out.println("signature valid: "+isValid);
    }


}
