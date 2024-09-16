import org.bouncycastle.bcpg.ArmoredInputStream;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.jcajce.JcaPGPObjectFactory;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyDecryptorBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyDataDecryptorFactoryBuilder;
import org.bouncycastle.openpgp.operator.PBESecretKeyDecryptor;
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;
import org.bouncycastle.util.io.Streams;

import java.io.*;
import java.security.Security;
import java.util.Iterator;

public class PGPFileDecryptorV2 {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static void decryptFile(String inputFilePath, String outputFilePath, String privateKeyPath, String passphrase) throws Exception {
        // Read the private key from the file
        PGPPrivateKey privateKey = readPrivateKey(privateKeyPath, passphrase);

        // Decrypt the file
        try (InputStream fileInput = new BufferedInputStream(new FileInputStream(inputFilePath));
             InputStream decoderStream = PGPUtil.getDecoderStream(fileInput);
             FileOutputStream fos = new FileOutputStream(outputFilePath)) {

            // Use JcaPGPObjectFactory for parsing PGP objects
            JcaPGPObjectFactory pgpFactory = new JcaPGPObjectFactory(decoderStream);
            Object object = pgpFactory.nextObject();

            // Handle PGPEncryptedDataList
            if (object instanceof PGPEncryptedDataList) {
                PGPEncryptedDataList encryptedDataList = (PGPEncryptedDataList) object;
                decryptData(encryptedDataList, privateKey, fos);
            } else {
                throw new PGPException("Invalid input file format.");
            }
        }

        System.out.println("File decrypted successfully!");
    }

    private static PGPPrivateKey readPrivateKey(String privateKeyFilePath, String passphrase) throws IOException, PGPException {
        try (InputStream keyIn = new BufferedInputStream(new FileInputStream(privateKeyFilePath))) {
            InputStream decoderStream = PGPUtil.getDecoderStream(keyIn);
            PGPObjectFactory pgpFactory = new PGPObjectFactory(decoderStream, new JcaKeyFingerprintCalculator());
            Object object = pgpFactory.nextObject();

            if (object instanceof PGPSecretKeyRingCollection) {
                PGPSecretKeyRingCollection secretKeyRingCollection = (PGPSecretKeyRingCollection) object;

                // Iterate over key rings to find the private key
                for (Iterator<PGPSecretKeyRing> keyRingIterator = secretKeyRingCollection.getKeyRings(); keyRingIterator.hasNext(); ) {
                    PGPSecretKeyRing keyRing = keyRingIterator.next();
                    PGPSecretKey secretKey = keyRing.getSecretKey();
                    return decryptPrivateKey(secretKey, passphrase);
                }
            } else if (object instanceof PGPSecretKeyRing) {
                PGPSecretKeyRing keyRing = (PGPSecretKeyRing) object;
                PGPSecretKey secretKey = keyRing.getSecretKey();
                return decryptPrivateKey(secretKey, passphrase);
            }

            throw new IllegalArgumentException("No private key found in the private key ring.");
        }
    }

    private static PGPPrivateKey decryptPrivateKey(PGPSecretKey secretKey, String passphrase) throws PGPException {
        if (secretKey == null) {
            throw new IllegalArgumentException("Secret key is null");
        }

        PBESecretKeyDecryptor decryptorFactory = new JcePBESecretKeyDecryptorBuilder()
                .setProvider("BC")
                .build(passphrase.toCharArray());

        return secretKey.extractPrivateKey(decryptorFactory);
    }

    private static void decryptData(PGPEncryptedDataList encryptedDataList, PGPPrivateKey privateKey, OutputStream outStream) throws IOException, PGPException {
        PGPPublicKeyEncryptedData encryptedData = null;

        // Find the encrypted data for the correct private key
        for (Iterator<PGPEncryptedData> it = encryptedDataList.getEncryptedDataObjects(); it.hasNext(); ) {
            PGPEncryptedData pgpEncryptedData = it.next();

            if (pgpEncryptedData instanceof PGPPublicKeyEncryptedData) {
                PGPPublicKeyEncryptedData publicKeyEncryptedData = (PGPPublicKeyEncryptedData) pgpEncryptedData;

                if (publicKeyEncryptedData.getKeyID() == privateKey.getKeyID()) {
                    encryptedData = publicKeyEncryptedData;
                    break;
                }
            }
        }

        if (encryptedData == null) {
            throw new IllegalArgumentException("No encrypted data found for the provided private key.");
        }

        try {
            // Decrypt the file
            InputStream clearData = encryptedData.getDataStream(new JcePublicKeyDataDecryptorFactoryBuilder()
                    .setProvider("BC")
                    .build(privateKey));

            JcaPGPObjectFactory plainFactory = new JcaPGPObjectFactory(clearData);
            Object message = plainFactory.nextObject();

            // Handle PGPCompressedData
            if (message instanceof PGPCompressedData) {
                PGPCompressedData compressedData = (PGPCompressedData) message;
                plainFactory = new JcaPGPObjectFactory(compressedData.getDataStream());
                message = plainFactory.nextObject();
            }

            // Dealing with literal data
            if (message instanceof PGPLiteralData) {
                PGPLiteralData literalData = (PGPLiteralData) message;
                try (InputStream literalInput = literalData.getInputStream()) {
                    Streams.pipeAll(literalInput, outStream);
                }
            } else {
                throw new PGPException("Message is not a literal data packet.");
            }
        } catch (Exception e) {
            throw new PGPException("Error during decryption: " + e.getMessage(), e);
        }
    }

    public static void main(String[] args) throws Exception {
        String inputFilePath = "encrypted_sample.txt";
        String outputFilePath = "decrypted_sample.txt";
        String privateKeyPath = "private_key.asc";
        String passphrase = "strong-passphrase";

        decryptFile(inputFilePath, outputFilePath, privateKeyPath, passphrase);
    }
}
