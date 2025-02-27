import org.bouncycastle.bcpg.ArmoredInputStream;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcePGPDataEncryptorBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyKeyEncryptionMethodGenerator;

import java.io.*;
import java.security.Security;
import java.security.SecureRandom;
import java.util.Iterator;

public class PGPFileEncryptor {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static void encryptFile(String inputFilePath, String outputFilePath, String publicKeyPath) throws Exception {
        // Read the public key from the file
        PGPPublicKey publicKey = readPublicKey(publicKeyPath);

    //   try (FileOutputStream fos = new FileOutputStream("public_key.txt");
    //   ArmoredOutputStream aos = new ArmoredOutputStream(fos)) {
    //   secretKey.getPublicKey().encode(aos);
    //}
        // Encrypt the file
        try (FileOutputStream fos = new FileOutputStream(outputFilePath);
             BufferedOutputStream bos = new BufferedOutputStream(fos);
             FileInputStream fileInput = new FileInputStream(inputFilePath)) {

                 // Set up encryption generator
            PGPEncryptedDataGenerator encryptedDataGenerator = new PGPEncryptedDataGenerator(
                    new JcePGPDataEncryptorBuilder(SymmetricKeyAlgorithmTags.AES_256) // Using AES-256 encryption
                            .setWithIntegrityPacket(true)
                            .setSecureRandom(new SecureRandom())
                            .setProvider("BC")
            );

            // Add public key for encryption
            encryptedDataGenerator.addMethod(new JcePublicKeyKeyEncryptionMethodGenerator(publicKey).setProvider("BC"));

            try (OutputStream encryptedOut = encryptedDataGenerator.open(bos, new byte[4096])) {
                byte[] buffer = new byte[4096];
                int len;
                while ((len = fileInput.read(buffer)) > 0) {
                    encryptedOut.write(buffer, 0, len);
                }
            }
        }

        System.out.println("File encrypted successfully!");

        //     PGPCompressedDataGenerator compressedDataGenerator = new PGPCompressedDataGenerator(PGPCompressedData.ZIP);
        //     try (OutputStream cos = compressedDataGenerator.open(bos)) {
        //         PGPEncryptedDataGenerator encryptedDataGenerator = new PGPEncryptedDataGenerator(
        //                 new JcePublicKeyKeyEncryptionMethodGenerator(publicKey).setProvider("BC"));

        //         try (OutputStream eos = encryptedDataGenerator.open(cos, new byte[4096])) {
        //             byte[] buffer = new byte[4096];
        //             int len;
        //             while ((len = fileInput.read(buffer)) > 0) {
        //                 eos.write(buffer, 0, len);
        //             }
        //         }
        //     }
        // }

        // System.out.println("File encrypted successfully!");
    }

    private static PGPPublicKey readPublicKey(String filePath) throws IOException, PGPException {
        try (InputStream keyIn = new BufferedInputStream(new FileInputStream(filePath))) {
            return readPublicKeyFromStream(keyIn);
        }
    }

    // private static PGPPublicKey readPublicKeyFromStream(InputStream keyIn) throws IOException, PGPException {
    //     //InputStream decoderStream = PGPUtil.getDecoderStream(keyIn);
    //     //PGPObjectFactory pgpFactory = new PGPObjectFactory(decoderStream, null);
    //     //Object object = pgpFactory.nextObject();

    //     InputStream decoderStream = PGPUtil.getDecoderStream(keyIn); // Decode the key stream properly
    //     PGPObjectFactory pgpFactory = new PGPObjectFactory(decoderStream, new JcaKeyFingerprintCalculator()); // Ensure KeyFingerPrintCalculator is used
    //     Object object = pgpFactory.nextObject();
    //     // if (!(object instanceof PGPPublicKeyRingCollection)) {
    //     //     throw new IllegalArgumentException("Input does not contain a valid public key");
    //     // }

    //     if (object instanceof PGPPublicKeyRingCollection) {
    //         PGPPublicKeyRingCollection keyRingCollection = (PGPPublicKeyRingCollection) object;
    

    //         // Iterate over key rings to find encryption key
    //     for (Iterator<PGPPublicKeyRing> keyRingIterator = keyRingCollection.getKeyRings(); keyRingIterator.hasNext(); ) {
    //         PGPPublicKeyRing keyRing = keyRingIterator.next();
    //         for (Iterator<PGPPublicKey> keyIterator = keyRing.getPublicKeys(); keyIterator.hasNext(); ) {
    //             PGPPublicKey key = keyIterator.next();
    //             if (key.isEncryptionKey()) {
    //                 return key;
    //             }
    //         }
    //     }
    // }

    // throw new IllegalArgumentException("No encryption key found in the public key ring.");
    //     // PGPPublicKeyRingCollection keyRingCollection = (PGPPublicKeyRingCollection) object;
    //     // for (Iterator<PGPPublicKeyRing> keyRingIterator = keyRingCollection.getKeyRings(); keyRingIterator.hasNext(); ) {
    //     //     PGPPublicKeyRing keyRing = keyRingIterator.next();
    //     //     for (Iterator<PGPPublicKey> keyIterator = keyRing.getPublicKeys(); keyIterator.hasNext(); ) {
    //     //         PGPPublicKey key = keyIterator.next();
    //     //         if (key.isEncryptionKey()) {
    //     //             return key;
    //     //         }
    //     //     }
    //     // }
    //     // throw new IllegalArgumentException("No encryption key found in the public key ring.");
    // }


    private static PGPPublicKey readPublicKeyFromStream(InputStream keyIn) throws IOException, PGPException {
        InputStream decoderStream = PGPUtil.getDecoderStream(keyIn);
        PGPObjectFactory pgpFactory = new PGPObjectFactory(decoderStream, new JcaKeyFingerprintCalculator());
        Object object = pgpFactory.nextObject();


        // Debugging: Print the object type to see what is returned
        System.out.println("Object returned by PGPObjectFactory: " + object.getClass().getName());
        
        if (object instanceof PGPPublicKeyRingCollection) {
            PGPPublicKeyRingCollection keyRingCollection = (PGPPublicKeyRingCollection) object;

            // Iterate over key rings to find encryption key
            for (PGPPublicKeyRing keyRing : keyRingCollection) {
                for (PGPPublicKey key : keyRing) {
                    if (key.isEncryptionKey()) {
                        System.out.println("Encryption key found!");
                        return key;
                    }
                }
            }
        }
        else if (object instanceof PGPPublicKeyRing) {
            PGPPublicKeyRing keyRing = (PGPPublicKeyRing) object;
    
            // Iterate over public keys in the key ring to find an encryption key
            for (Iterator<PGPPublicKey> keyIterator = keyRing.getPublicKeys(); keyIterator.hasNext(); ) {
                PGPPublicKey key = keyIterator.next();
                if (key.isEncryptionKey()) {
                    return key;
                }
            }
        }

        throw new IllegalArgumentException("No encryption key found in the public key ring.");
    }

    public static void main(String[] args) throws Exception {
        String inputFilePath = "sample.txt";
        String outputFilePath = "encrypted_sample.txt";
        String publicKeyPath = "public_key.asc";

        encryptFile(inputFilePath, outputFilePath, publicKeyPath);
    }
}
