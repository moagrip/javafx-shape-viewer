package com.lab3.laboration3moagrip;

import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ShapeControllerTest {

    private static final Color COLOR_BLACK = Color.BLACK;
    private static final String HEXADECIMAL_STRING_BLACK = "#000000FF";

    @Test
    void colorBlackToHexadecimalString() {
        var expected = HEXADECIMAL_STRING_BLACK;
        var actual = ShapeController.toHexString(COLOR_BLACK);
        assertEquals(expected, actual);
    }
}
