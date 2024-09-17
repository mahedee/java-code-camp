package com.mahedee;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class PGPDecryptorMain {

    /* 
    public static void main( String[] args )
    {
        // Default directory values
        String defaultSourcePath = "files/encrypted-files";
        String defaultDestPath = "files/decrypted-files";
        String defaultPvtKeyPath = "pgp-keys/private_key.pgp";
        String defaultPathForBadFiles = "files/bad-files";
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
            String privateKeyFilePath = "pgp-keys//private_key.pgp";
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
    
    */


    public static void main(String[] args) {
        // Default directory values
        String defaultSourcePath = "files/encrypted-files";
        String defaultDestPath = "files/decrypted-files";
        String defaultPvtKeyPath = "pgp-keys/private_key.pgp";
        String defaultPathForBadFiles = "files/bad-files";

        // Override the defaults with provided arguments
        String sourcePath = (args.length > 0 && !args[0].isEmpty()) ? args[0] : defaultSourcePath;
        String destPath = (args.length > 1 && !args[1].isEmpty()) ? args[1] : defaultDestPath;
        String pvtKeyPath = (args.length > 2 && !args[2].isEmpty()) ? args[2] : defaultPvtKeyPath;
        String pathForBadFiles = (args.length > 3 && !args[3].isEmpty()) ? args[3] : defaultPathForBadFiles;

        // Print the directories
        System.out.println("Source Directory: " + sourcePath);
        System.out.println("Destination Directory: " + destPath);
        System.out.println("Private Key File: " + pvtKeyPath);
        System.out.println("Path for bad files: " + pathForBadFiles);

        // Get the private key input stream
        try (InputStream privateKeyStream = new FileInputStream(pvtKeyPath)) {

            // Initialize the PgpDecryptionUtil with the private key
            PGPDecryptor decryptionUtil = new PGPDecryptor(privateKeyStream);

            // List all files in the source directory
            File sourceDir = new File(sourcePath);
            File[] encryptedFiles = sourceDir.listFiles();

            if (encryptedFiles == null || encryptedFiles.length == 0) {
                System.out.println("No files found in the source directory.");
                return;
            }

            // Iterate over each file in the source directory
            for (File encryptedFile : encryptedFiles) {
                if (!encryptedFile.isFile()) {
                    continue;
                }

                try {
                    // Prepare input stream for the encrypted file
                    InputStream encryptedFileStream = new FileInputStream(encryptedFile);

                    // Prepare output stream for the decrypted file
                    String decryptedFilePath = destPath + File.separator + encryptedFile.getName();
                    OutputStream decryptedOutputStream = new FileOutputStream(decryptedFilePath);

                    // Perform decryption
                    decryptionUtil.decrypt(encryptedFileStream, decryptedOutputStream);

                    // Close streams after processing
                    encryptedFileStream.close();
                    decryptedOutputStream.close();

                    // Delete the original file from sourcePath after decryption
                    Files.delete(encryptedFile.toPath());
                    System.out.println("Decrypted and removed: " + encryptedFile.getName());

                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Failed to decrypt file: " + encryptedFile.getName() + ". Moving to bad files folder.");

                    // Move the file to pathForBadFiles if decryption fails
                    File badFile = new File(pathForBadFiles + File.separator + encryptedFile.getName());
                    Files.move(encryptedFile.toPath(), badFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("An error occurred during decryption: " + e.getMessage());
        }
    }
    
}
