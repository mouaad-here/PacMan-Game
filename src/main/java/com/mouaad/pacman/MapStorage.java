package com.mouaad.pacman;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class MapStorage {
    private static final String FOLDER_NAME = "maps";

    private static Path getBaseDir() {
        try {
            // This gets the location of the classes (e.g., target/classes)
            Path codePath = Paths.get(MapStorage.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            // If we are in target/classes, go up two levels to get to the project root
            if (Files.isDirectory(codePath)
                    && codePath.toString().endsWith(Paths.get("target", "classes").toString())) {
                return codePath.getParent().getParent();
            }
            // Otherwise just use the parent of where the code is (e.g., if in a JAR)
            return codePath.getParent();
        } catch (Exception e) {
            // Fallback to current directory if anything fails
            return Paths.get(".");
        }
    }

    public static void saveMap(String name, char[][] grid) throws IOException {
        Path folder = getBaseDir().resolve(FOLDER_NAME);
        if (!Files.exists(folder)) {
            Files.createDirectories(folder);
        }

        Path file = folder.resolve(name + ".txt");
        List<String> lines = new ArrayList<>();
        for (char[] row : grid) {
            lines.add(new String(row));
        }
        Files.write(file, lines);
    }

    public static char[][] loadMap(String name, int rows, int cols) throws IOException {
        Path folder = getBaseDir().resolve(FOLDER_NAME);
        Path file = folder.resolve(name + ".txt");
        if (!Files.exists(file)) {
            throw new IOException("Map not found: " + name);
        }

        List<String> lines = Files.readAllLines(file);
        char[][] grid = new char[rows][cols];
        for (int r = 0; r < rows && r < lines.size(); r++) {
            String line = lines.get(r);
            for (int c = 0; c < cols && c < line.length(); c++) {
                grid[r][c] = line.charAt(c);
            }
        }
        return grid;
    }

    public static String[] getRegisteredMapNames() {
        Path folder = getBaseDir().resolve(FOLDER_NAME);
        if (!Files.exists(folder)) {
            return new String[0];
        }

        try (Stream<Path> stream = Files.list(folder)) {
            return stream
                    .filter(path -> !Files.isDirectory(path))
                    .map(path -> path.getFileName().toString())
                    .filter(name -> name.endsWith(".txt"))
                    .map(name -> name.substring(0, name.lastIndexOf('.')))
                    .toArray(String[]::new);
        } catch (IOException e) {
            return new String[0];
        }
    }
}
