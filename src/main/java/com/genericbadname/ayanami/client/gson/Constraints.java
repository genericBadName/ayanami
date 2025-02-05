package com.genericbadname.ayanami.client.gson;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class Constraints {
    public static final Predicate<Integer> nonZero = i -> i >= 0;
    public static final Predicate<Integer[]> allNonZero = arr -> {
        for (Integer integer : arr) {
            if (integer < 0) return false;
        }
        return true;
    };
    public static final Predicate<Integer[]> allUnique = arr -> {
        Set<Integer> set = new HashSet<>(Arrays.asList(arr));
        return set.size() == arr.length;
    };
}
