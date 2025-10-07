package com.abenezer.streambridge.util;

public class Range {
    private final long start;
    private final long end;
    public Range(long start, long end) {
        this.start = start;
        this.end = end;
    }
    public long getStart() { return start; }
    public long getEnd() { return end; }

    public static Range parse(String header, long fileSize) {
        if (header == null || !header.startsWith("bytes=")) return null;
        String rangeSpec = header.substring(6).trim();
        if (rangeSpec.isEmpty()) return null;
        
        String[] parts = rangeSpec.split("-", 2);
        try {
            if (parts.length == 2) {
                if (parts[0].isEmpty()) {
                    // Suffix range: bytes=-500
                    long suffix = Long.parseLong(parts[1]);
                    if (suffix <= 0 || suffix > fileSize) return null;
                    return new Range(Math.max(0, fileSize - suffix), fileSize - 1);
                } else if (parts[1].isEmpty()) {
                    // Open range: bytes=100-
                    long start = Long.parseLong(parts[0]);
                    if (start < 0 || start >= fileSize) return null;
                    return new Range(start, fileSize - 1);
                } else {
                    // Normal range: bytes=100-200
                    long start = Long.parseLong(parts[0]);
                    long end = Long.parseLong(parts[1]);
                    if (start < 0 || end < 0 || start > end || start >= fileSize) return null;
                    return new Range(start, Math.min(end, fileSize - 1));
                }
            }
        } catch (NumberFormatException e) {
            return null;
        }
        return null;
    }
}
