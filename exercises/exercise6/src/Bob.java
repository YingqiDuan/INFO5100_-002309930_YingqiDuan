import javax.crypto.SecretKey;
import java.security.KeyPair;

public class Bob {
    SecretKey aesKey;
    KeyPair reaKeyPair;

    public Bob(SecretKey aesKey,KeyPair reaKeyPair){
        this.aesKey=aesKey;
        this.reaKeyPair=reaKeyPair;
    }

    public SecretKey getAesKey(){
        return aesKey;
    }

    public KeyPair getReaKeyPair(){
        return reaKeyPair;
    }
}
