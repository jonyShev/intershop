package com.jonyshev.intershop.util;

public final class CacheKeys {
    private CacheKeys() {}
    public static String item(long id, String prefix) {
        return prefix + "item:" + id;
    }
    public static String catalog(String search, String sort, int page, int size, String prefix) {
        String s = (search == null || search.isBlank()) ? "_" : search.trim().toLowerCase();
        String so = (sort == null || sort.isBlank()) ? "NONE" : sort.toUpperCase();
        return String.format("%scatalog:s=%s:so=%s:p=%d:z=%d", prefix, s, so, page, size);
    }
}
