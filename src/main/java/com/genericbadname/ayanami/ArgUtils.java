package com.genericbadname.ayanami;

public class ArgUtils {
    public static double range(double val, double min, double max) {
        if (val >= min && val <= max) {
            return val;
        } else {
            throw new IllegalArgumentException("Value must be within ["+min+","+max+"]");
        }
    }

    public static double greater(double val, double greater) {
        if (val >= greater) {
            return val;
        } else {
            throw new IllegalArgumentException("Value must be greater than "+greater);
        }
    }

    public static double less(double val, double less) {
        if (val <= less) {
            return val;
        } else {
            throw new IllegalArgumentException("Value must be less than "+less);
        }
    }

    public static Number[] ensureLength(Number[] array, int length) {
        if (array == null) return null;

        if (array.length == length) {
            return array;
        } else {
            throw new IllegalArgumentException("Array length must be "+length);
        }
    }

    // maximum laziness
    public static int range(int val, int min, int max) {
        return range(val, min, max);
    }

    public static int greater(int val, int greater) {
        return greater(val, greater);
    }

    public static int less(int val, int less) {
        return less(val, less);
    }
}
