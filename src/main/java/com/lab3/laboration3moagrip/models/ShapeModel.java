package com.lab3.laboration3moagrip.models;

import javafx.scene.paint.Color;


public class ShapeModel {
    protected Color color;
    protected Position position;
    protected Size size;

    public ShapeModel(ShapeModelBuilder builder) {
        this.color = builder.getColor();
        this.position = builder.getPosition();
        this.size = builder.getSize();
    }

    public Color getColor() {
        return color;
    }

    public Position getPosition() {
        return position;
    }

    public Size getSize() {
        return size;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "color=" + color +
                ", position=" + position +
                ", size=" + size +
                '}';
    }
}
