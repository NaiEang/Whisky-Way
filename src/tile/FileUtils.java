package src.tile;

import java.io.File;

public class FileUtils {

    public static File findFileRecursive(File directory, String filename) {
        File[] files = directory.listFiles();
        if (files == null) return null;

        for (File file : files) {
            if (file.isDirectory()) {
                File found = findFileRecursive(file, filename);
                if (found != null) {
                    return found;
                }
            } else if (file.getName().equals(filename)) {
                return file;
            }
        }
        return null;
    }
}

