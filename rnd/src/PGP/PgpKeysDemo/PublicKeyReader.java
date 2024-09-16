import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPObjectFactory;
import org.bouncycastle.bcpg.ArmoredInputStream;
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;

import java.io.FileInputStream;
import java.io.InputStream;

public class PublicKeyReader {

    public static void readPublicKey(String publicKeyPath) throws Exception {
        try (InputStream keyIn = new FileInputStream(publicKeyPath)) {
            ArmoredInputStream armoredInputStream = new ArmoredInputStream(keyIn);
            PGPObjectFactory pgpObjectFactory = new PGPObjectFactory(armoredInputStream, new JcaKeyFingerprintCalculator());
            PGPPublicKeyRing publicKeyRing = (PGPPublicKeyRing) pgpObjectFactory.nextObject();
            PGPPublicKey publicKey = publicKeyRing.getPublicKey();
            
            System.out.println("Public Key ID: " + Long.toHexString(publicKey.getKeyID()));
            System.out.println("Public Key Fingerprint: " + bytesToHex(publicKey.getFingerprint()));
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static void main(String[] args) throws Exception {
        readPublicKey("public_key.asc"); // Path to your public key file
    }
}
