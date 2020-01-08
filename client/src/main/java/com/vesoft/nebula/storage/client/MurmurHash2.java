package com.vesoft.nebula.storage.client;

public class MurmurHash2 {
    private MurmurHash2() {
    }

    public static int hash32(byte[] data, int length, int seed) {
        int m = 1540483477;
        boolean r = true;
        int h = seed ^ length;
        int length4 = length / 4;

        for(int i = 0; i < length4; ++i) {
            int i4 = i * 4;
            int k = (data[i4 + 0] & 255) + ((data[i4 + 1] & 255) << 8) + ((data[i4 + 2] & 255) << 16) + ((data[i4 + 3] & 255) << 24);
            k *= 1540483477;
            k ^= k >>> 24;
            k *= 1540483477;
            h *= 1540483477;
            h ^= k;
        }

        switch(length % 4) {
            case 3:
                h ^= (data[(length & -4) + 2] & 255) << 16;
            case 2:
                h ^= (data[(length & -4) + 1] & 255) << 8;
            case 1:
                h ^= data[length & -4] & 255;
                h *= 1540483477;
            default:
                h ^= h >>> 13;
                h *= 1540483477;
                h ^= h >>> 15;
                return h;
        }
    }

    public static int hash32(byte[] data, int length) {
        return hash32(data, length, -1756908916);
    }

    public static int hash32(String text) {
        byte[] bytes = text.getBytes();
        return hash32(bytes, bytes.length);
    }

    public static int hash32(String text, int from, int length) {
        return hash32(text.substring(from, from + length));
    }

    public static long hash64(byte[] data, int length, int seed) {
        long m = -4132994306676758123L;
        boolean r = true;
        long h = (long)seed & 4294967295L ^ (long)length * -4132994306676758123L;
        int length8 = length / 8;

        for(int i = 0; i < length8; ++i) {
            int i8 = i * 8;
            long k = ((long)data[i8 + 0] & 255L) + (((long)data[i8 + 1] & 255L) << 8) + (((long)data[i8 + 2] & 255L) << 16) + (((long)data[i8 + 3] & 255L) << 24) + (((long)data[i8 + 4] & 255L) << 32) + (((long)data[i8 + 5] & 255L) << 40) + (((long)data[i8 + 6] & 255L) << 48) + (((long)data[i8 + 7] & 255L) << 56);
            k *= -4132994306676758123L;
            k ^= k >>> 47;
            k *= -4132994306676758123L;
            h ^= k;
            h *= -4132994306676758123L;
        }

        switch(length % 8) {
            case 7:
                h ^= (long)(data[(length & -8) + 6] & 255) << 48;
            case 6:
                h ^= (long)(data[(length & -8) + 5] & 255) << 40;
            case 5:
                h ^= (long)(data[(length & -8) + 4] & 255) << 32;
            case 4:
                h ^= (long)(data[(length & -8) + 3] & 255) << 24;
            case 3:
                h ^= (long)(data[(length & -8) + 2] & 255) << 16;
            case 2:
                h ^= (long)(data[(length & -8) + 1] & 255) << 8;
            case 1:
                h ^= (long)(data[length & -8] & 255);
                h *= -4132994306676758123L;
            default:
                h ^= h >>> 47;
                h *= -4132994306676758123L;
                h ^= h >>> 47;
                return h;
        }
    }

    public static long hash64(byte[] data, int length) {
        return hash64(data, length, -512093083);
    }

    public static long hash64(String text) {
        byte[] bytes = text.getBytes();
        return hash64(bytes, bytes.length);
    }

    public static long hash64(String text, int from, int length) {
        return hash64(text.substring(from, from + length));
    }
}
