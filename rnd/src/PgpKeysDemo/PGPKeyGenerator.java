import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.operator.PGPDigestCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPDigestCalculatorProviderBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPKeyPair;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyEncryptorBuilder;

import java.io.FileOutputStream;
import java.security.*;
import java.util.Date;

public class PGPKeyGenerator {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static void generateKeyPair(String identity, String passphrase) throws Exception {
        
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA", "BC");
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.generateKeyPair();

        PGPDigestCalculator sha1Calc = new JcaPGPDigestCalculatorProviderBuilder().build().get(HashAlgorithmTags.SHA1);
        PGPKeyPair pgpKeyPair = new JcaPGPKeyPair(PGPPublicKey.RSA_GENERAL, keyPair, new Date());
        
        PGPSecretKey secretKey = new PGPSecretKey(
                PGPSignature.DEFAULT_CERTIFICATION,
                pgpKeyPair,
                identity,
                sha1Calc,
                null,
                null,
                new JcaPGPContentSignerBuilder(pgpKeyPair.getPublicKey().getAlgorithm(), HashAlgorithmTags.SHA1),
                new JcePBESecretKeyEncryptorBuilder(PGPEncryptedData.CAST5, sha1Calc)
                        .setProvider("BC")
                        .build(passphrase.toCharArray())
        );

        // Write public key
        try (FileOutputStream fos = new FileOutputStream("public_key.asc");
             ArmoredOutputStream aos = new ArmoredOutputStream(fos)) {
            secretKey.getPublicKey().encode(aos);
        }

        // Write private key
        try (FileOutputStream fos = new FileOutputStream("private_key.asc");
             ArmoredOutputStream aos = new ArmoredOutputStream(fos)) {
            secretKey.encode(aos);
        }

        System.out.println("Keys generated and saved to files.");
    }

    public static void main(String[] args) throws Exception {
        generateKeyPair("test@example.com", "strong-passphrase");
    }
}
