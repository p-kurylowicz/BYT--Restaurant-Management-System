import java.io.File;


public class PersistenceConfig {

    /**
     * Base directory
     */
    public static final String DATA_DIRECTORY = "data";


    static {
        createDataDirectory();
    }

    /**
     * Creates the data directory if it doesn't exist
     */
    private static void createDataDirectory() {
        File dir = new File(DATA_DIRECTORY);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }


    public static String getDataFilePath(String filename) {
        return DATA_DIRECTORY + File.separator + filename;
    }



    public static void deleteDataFile(String filename) {
        File file = new File(getDataFilePath(filename));
        if (file.exists()) {
            file.delete();
        }
    }


}
