
package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.stream.Stream;

public class ReportGenerator {

    public static void generate(Path logsFolder, Path reportFile)
            throws IOException {

        long totalDeposits = 0;
        long totalWithdrawals = 0;

        StringBuilder report = new StringBuilder();

        report.append("MINIBANK END-OF-DAY REPORT\n");
        report.append("==========================\n");

        try (DirectoryStream<Path> files =
                     Files.newDirectoryStream(logsFolder)) {

            for (Path file : files) {

                if (!Files.isRegularFile(file)) {
                    continue;
                }

                BasicFileAttributes attributes =
                        Files.readAttributes(
                                file, BasicFileAttributes.class);

                report.append("\nFile: ")
                        .append(file.getFileName())
                        .append("\nSize: ")
                        .append(attributes.size())
                        .append(" bytes\nLast modified: ")
                        .append(attributes.lastModifiedTime())
                        .append("\n");

                try (BufferedReader reader =
                             Files.newBufferedReader(file)) {

                    String line;

                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.trim().split("\\s+");

                        if (parts.length < 2) {
                            continue;
                        }

                        try {
                            long amount = Long.parseLong(parts[1]);

                            if (parts[0].equalsIgnoreCase("DEPOSIT")) {
                                totalDeposits += amount;
                            } else if (
                                    parts[0].equalsIgnoreCase("WITHDRAW")) {
                                totalWithdrawals += amount;
                            }
                        } catch (NumberFormatException e) {
                            // Ignore lines without a valid amount.
                        }
                    }
                }
            }
        }

        report.append("\nTotal deposits: ")
                .append(totalDeposits)
                .append("\nTotal withdrawals: ")
                .append(totalWithdrawals)
                .append("\nNet amount: ")
                .append(totalDeposits - totalWithdrawals)
                .append("\n");

        Path parent = reportFile.getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }

        Files.write(reportFile, report.toString().getBytes());

        System.out.println(report);
    }
}
