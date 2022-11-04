package com.lab3.laboration3moagrip.models;

import javafx.scene.paint.Color;

public class ShapeModelBuilder {

    private javafx.scene.paint.Color color;
    private Position position;
    private Size size;

    private String shape;

    private ShapeModelBuilder(String shape) {
        this.shape = shape;
    }

    public static ShapeModelBuilder newBuilder(String shape) {

        return new ShapeModelBuilder(shape);
    }

    public ShapeModel build() {
        switch (shape.toLowerCase()) {
            case "triangle":
                return new TriangleModel(this);
            case "circle":
                return new CircleModel(this);
            default:
                throw new Error("Something went wrong during Shape creation");
        }
    }

    public String getShape() {
        return shape;
    }

    public Color getColor() {
        return color;
    }

    public ShapeModelBuilder setColor(Color color) {
        this.color = color;

        return this;
    }

    public Position getPosition() {
        return position;
    }

    public ShapeModelBuilder setPosition(Position position) {
        this.position = position;

        return this;
    }

    public Size getSize() {
        return size;
    }

    public ShapeModelBuilder setSize(Size size) {
        this.size = size;

        return this;
    }
}
