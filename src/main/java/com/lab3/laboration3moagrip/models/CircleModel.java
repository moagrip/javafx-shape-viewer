package com.lab3.laboration3moagrip.models;

public class CircleModel extends ShapeModel {
    public CircleModel(ShapeModelBuilder builder) {
        super(builder);
    }
    public int getSizeInt() {
        switch (this.size) {
            case SMALL:
                return 20;
            case MEDIUM:
                return 40;
            case LARGE:
                return 60;
        }
        return 20;
    }
}