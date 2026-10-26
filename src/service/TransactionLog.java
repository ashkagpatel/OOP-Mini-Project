
package service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class TransactionLog {

    public static void append(Path file, String line)
            throws IOException {

        Path parent = file.getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }

        Files.write(
                file,
                (line + System.lineSeparator()).getBytes(),
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
        );
    }
}
