package com.mahedee;

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
import java.io.IOException;
import java.security.*;
import java.util.Date;

public class PGPKeyGenerator {

    static {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
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

    public static void generateKeyPair(String identity, String passphrase) throws Exception {
        // Generate RSA key pair
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA", BouncyCastleProvider.PROVIDER_NAME);
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.generateKeyPair();

        // Create PGP key pair
        PGPDigestCalculator sha1Calc = new JcaPGPDigestCalculatorProviderBuilder().build().get(HashAlgorithmTags.SHA1);
        PGPKeyPair pgpKeyPair = new JcaPGPKeyPair(PGPPublicKey.RSA_GENERAL, keyPair, new Date());

        // Create secret key with passphrase encryption
        PGPSecretKey secretKey = new PGPSecretKey(
                PGPSignature.DEFAULT_CERTIFICATION,
                pgpKeyPair,
                identity,
                sha1Calc,
                null,
                null,
                new JcaPGPContentSignerBuilder(pgpKeyPair.getPublicKey().getAlgorithm(), HashAlgorithmTags.SHA1),
                new JcePBESecretKeyEncryptorBuilder(PGPEncryptedData.CAST5, sha1Calc)
                        .setProvider(BouncyCastleProvider.PROVIDER_NAME)
                        .build(passphrase.toCharArray())  // Encrypt private key with passphrase
        );

        // Save public key to file
        try (FileOutputStream fos = new FileOutputStream("public_key.pgp");
             ArmoredOutputStream aos = new ArmoredOutputStream(fos)) {
            secretKey.getPublicKey().encode(aos);
        }

        // Save private key (encrypted with passphrase) to file
        try (FileOutputStream fos = new FileOutputStream("private_key.pgp");
             ArmoredOutputStream aos = new ArmoredOutputStream(fos)) {
            secretKey.encode(aos);
        }

        System.out.println("Keys generated and saved to files.");
    }

    public static void main(String[] args) throws Exception {
        // Print current date and time
        System.out.println("Current Date and Time: " + new Date().toString());

        // Generate keys with passphrase protection
        String passphrase = "mySecurePassphrase"; // Set your passphrase here
        generateKeyPair("test@example.com", passphrase);

        System.out.println("Public Key and Private Key written to public_key.pgp and private_key.pgp.");
    }
}
