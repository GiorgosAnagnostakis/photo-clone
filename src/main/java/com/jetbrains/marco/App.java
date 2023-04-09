package com.jetbrains.marco;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * Hello world!
 */
public class App {

    static String userHome = System.getProperty("user.home");
    static Path thumbnailsDir = Path.of(userHome).resolve(".photos");

    public static void main(String[] args) throws IOException {

        Files.createDirectories((thumbnailsDir));

        String directory = args.length == 1 ? args[0] : ".";

        Path sourceDir = Path.of("c:\\Users\\ganagnostakis\\Desktop\\personnalProjects\\google_photos_clone_marco_behler\\sample-images\\100-100-color");
        AtomicInteger counter = new AtomicInteger();
        long start = System.currentTimeMillis();
        try (Stream<Path> files = Files.walk(sourceDir)
                .filter(Files::isRegularFile)
                .filter(App::isImage)) {
            files.forEach(f -> {
                counter.incrementAndGet();
                createThumbnail(f, thumbnailsDir.resolve(f.getFileName()));
            });
        }

        long end = System.currentTimeMillis();
        System.out.println("Converted " + counter + " image to thumbnails" + ((end - start) * 0.001) + " seconds");
    }

    private static boolean isImage(Path path) {
        String mimeTpye = null;
        try {
            mimeTpye = Files.probeContentType(path);
            return mimeTpye != null && mimeTpye.contains("image");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean createThumbnail(Path source, Path target) {
        //        magick convert
        try {
            System.out.println("creating thumbnail: " + source.normalize().toAbsolutePath());
            List<String> cmd = List.of("magick", "convert", "-resize", "300x", source.toString(), target.toString());
            ProcessBuilder builder = new ProcessBuilder(cmd);
            builder.inheritIO();
            Process process = builder.start();
            boolean finished = process.waitFor(3, TimeUnit.SECONDS);
            if (!finished) {
                process.destroy();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }


}
