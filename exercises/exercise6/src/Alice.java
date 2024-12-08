import javax.crypto.SecretKey;
import java.security.KeyPair;

public class Alice {
    SecretKey aesKey;
    KeyPair reaKeyPair;

    public Alice(SecretKey aesKey,KeyPair reaKeyPair){
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
