import java.io.File;
import java.io.InputStream;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.ProviderNotFoundException;

public class Hello {

    static {
        String LIB_NAME = "libhello";
        String LIB_EXT = ".so";

        try {
            File file = File.createTempFile(LIB_NAME, LIB_EXT);

            String JNI_LOCAL_TEMP_PATH = file.getAbsolutePath().toString();

            InputStream link = (new Hello().getClass().getResourceAsStream(LIB_NAME + LIB_EXT));

            System.out.println("Instantiating JNI lib: " + JNI_LOCAL_TEMP_PATH);
            Files.copy(link, file.getAbsoluteFile().toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            try {
                System.load(file.getAbsolutePath());
                System.out.println("Loaded JNI lib: " + JNI_LOCAL_TEMP_PATH);
            } finally {
                if (isPosixCompliant()) {
                    System.out.println("Removing JNI lib: " + JNI_LOCAL_TEMP_PATH);
                    file.delete();
                } else {
                    file.deleteOnExit();
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public static void main(String[] args) {
        System.out.println("Hello from Java");

        new Hello().sayHello();

    }

    public native String sayHello();

    private static boolean isPosixCompliant() {
        try {
            return FileSystems.getDefault().supportedFileAttributeViews().contains("posix");
        } catch (FileSystemNotFoundException | ProviderNotFoundException | SecurityException e) {
            return false;
        }
    }
}