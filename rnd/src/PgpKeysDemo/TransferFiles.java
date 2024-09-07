import java.io.File;

public class TransferFiles {
    public static void main(String[] args) {
        System.out.println("Hello world!");

        // Get absolute path from relative path
        String sourcePath = "src/ext_alert";
        String absoluteSource = new File(sourcePath).getAbsolutePath();

        String destPath = "dest/ext_alert";
        String absoluteDest = new File(destPath).getAbsolutePath();

        moveFiles(absoluteSource, absoluteDest);
    }

    // Write a method which will move files from source directory to destination
    // directory
    public static void moveFiles(String sourceDir, String destDir) {
        File source = new File(sourceDir);
        File dest = new File(destDir);
        // Check if source and destination directories exist
        if (!source.exists() || !dest.exists()) {
            System.out.println("Source or destination directory does not exist.");
            return;
            }

        // Check if source directory is a directory
        if (!source.isDirectory()) {
            System.out.println("Source is not a directory.");
            return;
            }

        // Check if destination directory is a directory
        if (!dest.isDirectory()) {
            System.out.println("Destination is not a directory.");
            return;
            }
        
        // Get all files in source directory
        File[] files = source.listFiles();
        for (File file : files) {
            // Check if file is a file (not a directory)
            if (file.isFile()) {
                // Move file to destination directory
                if (file.renameTo(new File(dest, file.getName()))) {
                System.out.println("File " + file.getName() + " moved to " + dest.getAbsolutePath());
                } else {
                    System.out.println("Failed to move file " + file.getName());
                }
            }
        }
    }

}