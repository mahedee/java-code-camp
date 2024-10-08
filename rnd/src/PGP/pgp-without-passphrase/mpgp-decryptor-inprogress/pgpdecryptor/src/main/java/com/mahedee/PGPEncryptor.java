package com.mahedee;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.CompressionAlgorithmTags;
import org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPCompressedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedDataGenerator;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.operator.jcajce.JcePGPDataEncryptorBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyKeyEncryptionMethodGenerator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Objects;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PGPEncryptor {

    static {
        // Add Bouncy castle to JVM
        if (Objects.isNull(Security.getProvider(BouncyCastleProvider.PROVIDER_NAME))) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    // Fields with default values
    private int compressionAlgorithm = CompressionAlgorithmTags.ZIP;
    private int symmetricKeyAlgorithm = SymmetricKeyAlgorithmTags.AES_128;
    private boolean armor = true;
    private boolean withIntegrityCheck = true;
    private int bufferSize = 1 << 16;

    // No-args constructor setting default values
    public PGPEncryptor() {}

    // Constructor with parameters
    public PGPEncryptor(int compressionAlgorithm, int symmetricKeyAlgorithm, boolean armor, boolean withIntegrityCheck, int bufferSize) {
        this.compressionAlgorithm = compressionAlgorithm;
        this.symmetricKeyAlgorithm = symmetricKeyAlgorithm;
        this.armor = armor;
        this.withIntegrityCheck = withIntegrityCheck;
        this.bufferSize = bufferSize;
    }

    // Getters for all fields
    public int getCompressionAlgorithm() {
        return compressionAlgorithm;
    }

    public int getSymmetricKeyAlgorithm() {
        return symmetricKeyAlgorithm;
    }

    public boolean isArmor() {
        return armor;
    }

    public boolean isWithIntegrityCheck() {
        return withIntegrityCheck;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    // Encryption method
    public void encrypt(OutputStream encryptOut, InputStream clearIn, long length, InputStream publicKeyIn)
            throws IOException, PGPException {
        PGPCompressedDataGenerator compressedDataGenerator =
                new PGPCompressedDataGenerator(compressionAlgorithm);
        PGPEncryptedDataGenerator pgpEncryptedDataGenerator = new PGPEncryptedDataGenerator(
                new JcePGPDataEncryptorBuilder(symmetricKeyAlgorithm)
                        .setWithIntegrityPacket(withIntegrityCheck)
                        .setSecureRandom(new SecureRandom())
                        .setProvider(BouncyCastleProvider.PROVIDER_NAME)
        );
        // Adding public key
        pgpEncryptedDataGenerator.addMethod(new JcePublicKeyKeyEncryptionMethodGenerator(
                CommonUtils.getPublicKey(publicKeyIn)));
        if (armor) {
            encryptOut = new ArmoredOutputStream(encryptOut);
        }
        OutputStream cipherOutStream = pgpEncryptedDataGenerator.open(encryptOut, new byte[bufferSize]);
        CommonUtils.copyAsLiteralData(compressedDataGenerator.open(cipherOutStream), clearIn, length, bufferSize);
        // Closing all output streams in sequence
        compressedDataGenerator.close();
        cipherOutStream.close();
        encryptOut.close();
    }

    // Encryption methods for byte arrays
    public byte[] encrypt(byte[] clearData, InputStream publicKeyIn) throws PGPException, IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(clearData);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        encrypt(outputStream, inputStream, clearData.length, publicKeyIn);
        return outputStream.toByteArray();
    }

    public InputStream encrypt(InputStream clearIn, long length, InputStream publicKeyIn)
            throws IOException, PGPException {
        File tempFile = File.createTempFile("pgp-", "-encrypted");
        encrypt(Files.newOutputStream(tempFile.toPath()), clearIn, length, publicKeyIn);
        return Files.newInputStream(tempFile.toPath());
    }

    // Encryption methods for public key as a string
    public byte[] encrypt(byte[] clearData, String publicKeyStr) throws PGPException, IOException {
        return encrypt(clearData, IOUtils.toInputStream(publicKeyStr, Charset.defaultCharset()));
    }

    public InputStream encrypt(InputStream clearIn, long length, String publicKeyStr) throws IOException, PGPException {
        return encrypt(clearIn, length, IOUtils.toInputStream(publicKeyStr, Charset.defaultCharset()));
    }

    // Main method
    public static void main(String[] args) {
        try {
            // File paths
            String plainTextFile = "plaintext.txt";
            String publicKeyFile = "public_key.pgp";
            String encryptedFile = "encryptedtext.txt";

            // Read the plaintext file
            byte[] plainTextBytes = Files.readAllBytes(Paths.get(plainTextFile));

            // Create an instance of PgpEncryptionUtil
            PGPEncryptor pgpEncryptionUtil = new PGPEncryptor();

            // Encrypt the file
            try (FileInputStream publicKeyIn = new FileInputStream(publicKeyFile);
                 FileOutputStream encryptedOut = new FileOutputStream(encryptedFile)) {

                // Encrypt the plaintext and write to the output file
                byte[] encryptedBytes = pgpEncryptionUtil.encrypt(plainTextBytes, publicKeyIn);
                encryptedOut.write(encryptedBytes);

                System.out.println("File encrypted successfully: " + encryptedFile);
            }

        } catch (IOException | PGPException e) {
            System.err.println("Error during encryption: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
