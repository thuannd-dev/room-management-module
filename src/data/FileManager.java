package data;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;

public class FileManager {

    private String fileName;

    public FileManager() {
    }

    public FileManager(String fileName) {
        this.fileName = fileName;
    }

    public List<String> readDataFromFile() throws IOException {
        List<String> result;
        result = Files.readAllLines(
                new File(fileName).toPath(),
                Charset.forName("utf-8")
        );
        return result;
    }

    public void saveDataToFile(String data) throws IOException {
        Files.writeString(
                new File(fileName).toPath(), data,
                Charset.forName("utf-8")
        );
    }
}
