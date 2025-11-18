package com.example.shop.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class ImageStorageService {

    private final Path rootLocation;
    private final List<String> allowedExtensions = List.of("png", "jpg", "jpeg", "webp", "gif");

    public ImageStorageService(@Value("${app.media-dir}") String mediaDir) {
        this.rootLocation = Paths.get(mediaDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(rootLocation);
            log.info("Media directory: {}", rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Konnte Upload-Verzeichnis nicht erstellen: " + rootLocation, e);
        }
    }

    // ---------- Upload ----------

    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Datei ist leer.");
        }

        String originalName = StringUtils.cleanPath(
                Objects.requireNonNull(file.getOriginalFilename())
        );

        String ext = getExtension(originalName);
        if (!allowedExtensions.contains(ext.toLowerCase())) {
            throw new RuntimeException("Nur Bild-Dateien erlaubt: " + allowedExtensions);
        }

        String filename = UUID.randomUUID() + "." + ext;
        Path target = rootLocation.resolve(filename);

        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Fehler beim Speichern der Datei.", e);
        }

        return "/media/" + filename;
    }

    private String getExtension(String filename) {
        int idx = filename.lastIndexOf('.');
        if (idx == -1) {
            throw new RuntimeException("Datei hat keine Endung: " + filename);
        }
        return filename.substring(idx + 1);
    }

    // ---------- Listing ----------

    public List<MediaFile> listAll() {
        if (!Files.exists(rootLocation)) {
            return List.of();
        }

        try (Stream<Path> stream = Files.list(rootLocation)) {
            return stream
                    .filter(Files::isRegularFile)
                    .sorted(Comparator.comparing(this::getLastModifiedSafe).reversed())
                    .map(this::toMediaFile)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Fehler beim Auflisten der Medien-Dateien", e);
        }
    }

    private MediaFile toMediaFile(Path path) {
        String filename = path.getFileName().toString();
        long size = getSizeSafe(path);
        Instant lastModified = getLastModifiedSafe(path);
        String url = "/media/" + filename;

        Integer width = null;
        Integer height = null;

        try {
            BufferedImage img = ImageIO.read(path.toFile());
            if (img != null) {
                width = img.getWidth();
                height = img.getHeight();
            }
        } catch (IOException e){
            log.debug("Konnte Dimensionen von {} nicht lesen: {}", filename, e.getMessage());
        }

        return new MediaFile(filename, url, size, lastModified, width, height);
    }

    private long getSizeSafe(Path path) {
        try {
            return Files.size(path);
        } catch (IOException e) {
            return -1L;
        }
    }

    private Instant getLastModifiedSafe(Path path) {
        try {
            return Files.getLastModifiedTime(path).toInstant();
        } catch (IOException e) {
            return Instant.EPOCH;
        }
    }

    // ---------- Einzel-Löschen ----------

    public void deleteByFilename(String filename) {
        Path target = resolveSafe(filename);
        try {
            boolean deleted = Files.deleteIfExists(target);
            if (!deleted) {
                throw new RuntimeException("Datei nicht gefunden: " + filename);
            }
        } catch (IOException e) {
            throw new RuntimeException("Fehler beim Löschen der Datei: " + filename, e);
        }
    }


    // ---------- Bulk-Delete ----------

    public void deleteMultiple(List<String> filenames) {
        if (filenames == null || filenames.isEmpty()) {
            return;
        }
        for (String fn : filenames) {
            deleteByFilename(fn);
        }
    }

    // ---------- Rename ----------

    public void rename(String oldFilename, String newFilename) {
        if (oldFilename == null || oldFilename.isBlank() ||
                newFilename == null || newFilename.isBlank()) {
            throw new IllegalArgumentException("Dateinamen dürfen nicht leer sein.");
        }

        Path source = resolveSafe(oldFilename);
        Path target = resolveSafe(newFilename);

        if (!Files.exists(source)) {
            throw new RuntimeException("Quelle existiert nicht: " + oldFilename);
        }
        if (Files.exists(target)) {
            throw new RuntimeException("Zieldatei existiert bereits: " + newFilename);
        }

        try {
            Files.move(source, target);
        } catch (IOException e) {
            throw new RuntimeException("Fehler beim Umbenennen von " + oldFilename + " nach " + newFilename, e);
        }
    }

    // ---------- Pfad-Check ----------

    private Path resolveSafe(String filename) {
        if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            throw new IllegalArgumentException("Ungültiger Dateiname.");
        }
        return rootLocation.resolve(filename);
    }
}