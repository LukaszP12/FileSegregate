package pl.piwowarski.lukasz.project.filesegregator;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.logging.Logger;

import static java.nio.file.StandardWatchEventKinds.*;

public class FileSegregate {
    private static final Logger LOGGER = Logger.getLogger(FileSegregate.class.getName());

    private String homePath;
    private String devPath;
    private String testPath;

    public FileSegregate(String homePath, String devPath, String testPath) {
        this.homePath = homePath;
        this.devPath = devPath;
        this.testPath = testPath;
    }

    public void segregate() throws IOException {

        WatchService watcher = FileSystems.getDefault().newWatchService();

        File file = new File(homePath);
        LOGGER.info("file: " + file);
        Path dir = Paths.get(homePath);
        dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);

        while (true) {
            try {
                WatchKey key;
                try {
                    // wait for a key to be available
                    key = watcher.take();
                } catch (InterruptedException ex) {
                    return;
                }

                for (WatchEvent<?> event : key.pollEvents()) {
                    // get event type
                    WatchEvent.Kind<?> kind = event.kind();

                    // get file name
                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path filePath = ev.context();

                    System.out.println(kind.name() + ": " + filePath);
                    String extension = "";
                    int lastIndexOfDot = filePath.toString().lastIndexOf('.');
                    LOGGER.info("lastIndexOfDot: " + lastIndexOfDot);
                    if (lastIndexOfDot > 0) {
                        extension = filePath.toString().substring(lastIndexOfDot + 1);
                        LOGGER.info("IF extension: " + extension);
                    }
                    LOGGER.info("extension: " + extension);

                    String fileSeparator = System.getProperty("file.separator");

                    if (kind == OVERFLOW) {
                        continue;
                    } else if (kind == ENTRY_CREATE) {
                        // process create event
                        LOGGER.info("process create event");

                        if (extension.equalsIgnoreCase("xml")) {
                            LOGGER.info("Found XML file!");
                            Files.move(
                                    Paths.get(homePath + fileSeparator + filePath),
                                    Paths.get(devPath + fileSeparator + filePath),
                                    StandardCopyOption.REPLACE_EXISTING);
                        }

                        if (extension.equalsIgnoreCase("jar")) {
                            LOGGER.info("Found JAR file!");
                            BasicFileAttributes basicFileAttributes =
                                    Files.readAttributes(
                                            Paths.get(homePath + fileSeparator + filePath),
                                            BasicFileAttributes.class);
                            FileTime creationFileTime = basicFileAttributes.creationTime();
                            long creationMillis = creationFileTime.toMillis();
                            LocalDateTime creationLocalDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(creationMillis), ZoneId.systemDefault());
                            int creationHour = creationLocalDateTime.getHour();
                            LOGGER.info("creationHour: " + creationHour);
                            if (creationHour % 2 == 0) {
                                LOGGER.info("EVEN creationHour!");
                                Files.move(
                                        Paths.get(homePath + fileSeparator + filePath),
                                        Paths.get(devPath + fileSeparator + filePath),
                                        StandardCopyOption.REPLACE_EXISTING);
                            } else {
                                LOGGER.info("ODD creationHour!");
                                Files.move(
                                        Paths.get(homePath + fileSeparator + filePath),
                                        Paths.get(testPath + fileSeparator + filePath),
                                        StandardCopyOption.REPLACE_EXISTING);
                            }
                        }
                    } else if (kind == ENTRY_DELETE) {

                        // process delete event
                        LOGGER.info("process delete event");
                    } else if (kind == ENTRY_MODIFY) {

                        // process modify event
                        LOGGER.info("process modify event");
                    }
                }

                // IMPORTANT: The key must be reset after processed
                boolean valid = key.reset();
                if (!valid) {
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
