import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPObjectFactory;
import org.bouncycastle.bcpg.ArmoredInputStream;
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;

import java.io.FileInputStream;
import java.io.InputStream;

public class PrivateKeyReader {

    public static void readPrivateKey(String privateKeyPath) throws Exception {
        try (InputStream keyIn = new FileInputStream(privateKeyPath)) {
            ArmoredInputStream armoredInputStream = new ArmoredInputStream(keyIn);
            PGPObjectFactory pgpObjectFactory = new PGPObjectFactory(armoredInputStream, new JcaKeyFingerprintCalculator());
            PGPSecretKeyRing secretKeyRing = (PGPSecretKeyRing) pgpObjectFactory.nextObject();
            PGPSecretKey privateKey = secretKeyRing.getSecretKey();

            System.out.println("Private Key ID: " + Long.toHexString(privateKey.getKeyID()));
            System.out.println("Private Key Fingerprint: " + bytesToHex(privateKey.getPublicKey().getFingerprint()));
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
        readPrivateKey("private_key.asc"); // Path to your private key file
    }
}
