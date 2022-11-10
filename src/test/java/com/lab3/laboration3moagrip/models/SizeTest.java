package com.lab3.laboration3moagrip.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class SizeTest {
    private static final int LARGE = 60;
    private static final int MEDIUM = 40;

    @Test
    void sizeToInt() {
        var expected = LARGE;
        var actual = Size.LARGE.getValue();
        assertEquals(expected, actual);
    }

    @Test
    void intToSize() {
        var expected = Size.MEDIUM;
        var actual = Size.valueOf(MEDIUM);
        assertEquals(expected, actual);
    }
}
