package com.abenezer.streambridge.util;

public class FileMetadata {
    private final long size;
    private final String mimeType;
    private final long modified;
    public FileMetadata(long size, String mimeType, long modified) {
        this.size = size;
        this.mimeType = mimeType;
        this.modified = modified;
    }
    public long getSize() { return size; }
    public String getMimeType() { return mimeType; }
    public long getModified() { return modified; }
}
