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

import java.io.FileWriter;
import java.io.IOException;

public class PGPKeyGenerator {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }


    public static class PGPKeys {
        private String publicKey;
        private String privateKey;

        public PGPKeys(String publicKey, String privateKey) {
            this.publicKey = publicKey;
            this.privateKey = privateKey;
        }

        public String getPublicKey() {
            return publicKey;
        }

        public String getPrivateKey() {
            return privateKey;
        }
    }

   // public static void generateKeyPair(String identity, String passphrase) throws Exception {
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

        //PGPPublicKey skey = secretKey.getPublicKey();

             try (FileOutputStream fos = new FileOutputStream("public_key.asc");
             ArmoredOutputStream aos = new ArmoredOutputStream(fos)) {
            secretKey.getPublicKey().encode(aos);
        }

        // Capture public key in string
        // ByteArrayOutputStream publicKeyOut = new ByteArrayOutputStream();
        // try (ArmoredOutputStream aos = new ArmoredOutputStream(publicKeyOut)) {
        //     secretKey.getPublicKey().encode(aos);
        // }

        //String publicKeyString = publicKeyOut.toString("UTF-8");


          // Capture private key in string
        //   ByteArrayOutputStream privateKeyOut = new ByteArrayOutputStream();
        //   try (ArmoredOutputStream aos = new ArmoredOutputStream(privateKeyOut)) {
        //       secretKey.encode(aos);
        //   }

   


        //   String privateKeyString = privateKeyOut.toString("UTF-8");
  
        //   System.out.println("Keys generated.");
  
        //   return new PGPKeys(publicKeyString, privateKeyString);

        // // Write public key
  
        
        // // Write private key
        try (FileOutputStream fos = new FileOutputStream("private_key.asc");
             ArmoredOutputStream aos = new ArmoredOutputStream(fos)) {
            secretKey.encode(aos);
        }

        System.out.println("Keys generated and saved to files.");
    }

    public static void writeKeysToFile(String publicKey, String privateKey) throws IOException {
        // Write public key to public_key.txt
        try (FileWriter publicKeyWriter = new FileWriter("public_key.txt")) {
            publicKeyWriter.write(publicKey);
        }

        // Write private key to private_key.txt
        try (FileWriter privateKeyWriter = new FileWriter("private_key.txt")) {
            privateKeyWriter.write(privateKey);
        }

        System.out.println("Public and Private keys saved to files.");
    }


    public static void main(String[] args) throws Exception {
        //generateKeyPair("test@example.com", "strong-passphrase");

        generateKeyPair("test@example.com", "strong-passphrase");
        //System.out.println("Public Key:\n" + pgpKeys.getPublicKey());
       // System.out.println("Private Key:\n" + pgpKeys.getPrivateKey());

        // Write the keys to text files
        //writeKeysToFile(pgpKeys.getPublicKey(), pgpKeys.getPrivateKey());

        System.out.println("Public Key and Private Key written to public_key.txt and private_key.txt.");
            
    }
}
