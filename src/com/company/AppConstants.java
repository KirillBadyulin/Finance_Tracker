package com.company;


import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//CATEGORIES, MONTHS, AND SHORT MONTHS;
public class AppConstants {

    public static final List<String> CATEGORIES = Stream.of(Category.values())
            .map(Enum::name)
            .collect(Collectors.toList());

    public static final List<String> MONTHS_FULL = new ArrayList<>();
    public static final List<String> MONTHS_SHORT = new ArrayList<>();
    public static final Map<String, String> MONTHS_DICTIONARY = new HashMap<>();

    static {
        for (int i = 1; i <= 12; i++) {
            MONTHS_FULL.add(Month.of(i).getDisplayName(TextStyle.FULL, Locale.ENGLISH).toUpperCase());
        }
        for (int i = 1; i <= 12; i++) {
            MONTHS_SHORT.add(Month.of(i).getDisplayName(TextStyle.SHORT, Locale.ENGLISH).toUpperCase());
        }
        int i = 0;
        for (String shortMonth : MONTHS_SHORT) {
            MONTHS_DICTIONARY.put(shortMonth, MONTHS_FULL.get(i));
            i++;
        }
        i = 0;
        for (String fullMonth : MONTHS_FULL) {
            MONTHS_DICTIONARY.put(fullMonth, MONTHS_SHORT.get(i));
            i++;
        }
    }

    public static void printDictionary() {
        for (Map.Entry<String, String> entry : MONTHS_DICTIONARY.entrySet()) {
            System.out.println("Key : " + entry.getKey() + ". Value : " + entry.getValue());
        }
    }

    public static final Map<String, Integer> MONTH_MAP = new HashMap<>();
    static {
        MONTH_MAP.put("JAN", 1);
        MONTH_MAP.put("FEB", 2);
        MONTH_MAP.put("MAR", 3);
        MONTH_MAP.put("APR", 4);
        MONTH_MAP.put("MAY", 5);
        MONTH_MAP.put("JUN", 6);
        MONTH_MAP.put("JUL", 7);
        MONTH_MAP.put("AUG", 8);
        MONTH_MAP.put("SEP", 9);
        MONTH_MAP.put("OCT", 10);
        MONTH_MAP.put("NOV", 11);
        MONTH_MAP.put("DEC", 12);
    }

    public static void printMap(Map<String, Integer> map) {
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            System.out.println("category: " + entry.getKey() + " value " + entry.getValue());
        }
    }

    public static Map<String, Number> convertIntegerIntoNumber(Map<String, Integer> temp) {
        Map<String, Number> numberMap = new HashMap<>();
        for (Map.Entry<String, Integer> entry : temp.entrySet()) {
            numberMap.put(entry.getKey(), entry.getValue());
        }
        return numberMap;
    }
}
