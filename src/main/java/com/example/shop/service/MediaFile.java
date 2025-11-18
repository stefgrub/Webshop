package com.example.shop.service;

import java.time.Instant;

public class MediaFile {

    private final String filename;
    private final String url;
    private final long size;
    private final Instant lastModified;
    private final Integer width;
    private final Integer height;

    public MediaFile(String filename, String url, long size, Instant lastModified, Integer width, Integer height) {
        this.filename = filename;
        this.url = url;
        this.size = size;
        this.lastModified = lastModified;
        this.width = width;
        this.height = height;
    }

    public String getFilename() {return filename;}
    public String getUrl() {return url;}
    public long getSize() {return size;}
    public Instant getLastModified() {return lastModified;}
    public Integer getWidth() {return width;}
    public Integer getHeight() {return height;}
}