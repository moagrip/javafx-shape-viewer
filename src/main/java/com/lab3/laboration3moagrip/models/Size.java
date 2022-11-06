package com.lab3.laboration3moagrip.models;

import java.util.HashMap;
import java.util.Map;

public enum Size {
    SMALL(20),
    MEDIUM(40),
    LARGE(60);


    private static Map map = new HashMap<>();
    private final int value;
    private Size(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    static {
        for (Size pageType : Size.values()) {
            map.put(pageType.value, pageType);
        }
    }
    public static Size valueOf(int sizeInt) {
        Size size = (Size) map.get(sizeInt);
        return size == null ? Size.SMALL : size;
    }
}
