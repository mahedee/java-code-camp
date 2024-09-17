package com.mahedee;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class PGPDecryptorMain {
    public static void main( String[] args )
    {
        // Default directory values
        String defaultSourcePath = "/opt/app/source";
        String defaultDestPath = "/opt/app/dest";
        String defaultPvtKeyPath = "/opt/app/pgp";
        String defaultPathForBadFiles = "/opt/app/bad";
        //String defaultPassPhrase = "strong-passphrase";

        // If arguments are provided, override the defaults
        String sourcePath = (args.length > 0 && !args[0].isEmpty()) ? args[0] : defaultSourcePath;
        String destPath = (args.length > 1 && !args[1].isEmpty()) ? args[1] : defaultDestPath;
        String pvtKeyPath = (args.length > 2 && !args[2].isEmpty()) ? args[2] : defaultPvtKeyPath;
        // String passPhrase = (args.length > 3 && !args[3].isEmpty()) ? args[3] : defaultPassPhrase;
        String pathForBadFiles = (args.length > 3 && !args[3].isEmpty()) ? args[3] : defaultPathForBadFiles;
        // Print the directories
        System.out.println("Source Directory: " + sourcePath);
        System.out.println("Destination Directory: " + destPath);
        System.out.println("Private key File Directory: " + pvtKeyPath);
        System.out.println("Path for bad files: " + pathForBadFiles);

        try {
            // Specify the file paths for the encrypted file and private key
            String encryptedFilePath = "encryptedtext.txt";
            String privateKeyFilePath = "private_key.pgp";
            String outputFilePath = "decryptedtext.txt";

            // Open file streams for the encrypted file and private key
            InputStream encryptedFileStream = new FileInputStream(encryptedFilePath);
            InputStream privateKeyStream = new FileInputStream(privateKeyFilePath);

            // Open an output stream where decrypted data will be written
            OutputStream decryptedOutputStream = new FileOutputStream(outputFilePath);

            // Initialize the PgpDecryptionUtil class with the private key input stream
            PGPDecryptor decryptionUtil = new PGPDecryptor(privateKeyStream);

            // Perform the decryption
            decryptionUtil.decrypt(encryptedFileStream, decryptedOutputStream);

            // Close the streams after the process is done
            encryptedFileStream.close();
            privateKeyStream.close();
            decryptedOutputStream.close();

            System.out.println("Decryption completed. Decrypted data written to " + outputFilePath);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("An error occurred during decryption: " + e.getMessage());
        }
    }
}
