package com.lab3.laboration3moagrip.models;

import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class ShapeModelBuilderTest {
    private static final String TRIANGLE = "triangle";
    private static final String CIRCLE = "circle";
    private static final Size SIZE_MEDIUM = Size.MEDIUM;
    private static final Color COLOR_BLACK = Color.BLACK;
    private static final Position POSITION = new Position(10, 20);


    @Test
    void createNewTriangleModelUsingBuilderAndAssertValues() {
        ShapeModel shapeModel = ShapeModelBuilder.newBuilder(TRIANGLE)
                .setColor(COLOR_BLACK)
                .setSize(SIZE_MEDIUM)
                .setPosition(POSITION)
                .build();
        assertEquals(Color.BLACK, shapeModel.getColor());
        assertEquals(POSITION, shapeModel.getPosition());
        assertEquals(SIZE_MEDIUM, shapeModel.getSize());
        assertInstanceOf(TriangleModel.class, shapeModel);
    }

    @Test
    void createNewCircleModelUsingBuilder() {
        ShapeModel shapeModel = ShapeModelBuilder.newBuilder(CIRCLE).build();
        assertInstanceOf(CircleModel.class, shapeModel);
    }
}
