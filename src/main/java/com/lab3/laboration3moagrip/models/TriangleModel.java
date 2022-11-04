package com.lab3.laboration3moagrip.models;

public class TriangleModel extends ShapeModel {

    private Position[] drawPositions;
    public TriangleModel(ShapeModelBuilder builder) {
        super(builder);
        this.drawPositions = new Position[] {
                this.position,
                this.position.increaseX(this.getSizeInt()),
                this.position.increaseY(this.getSizeInt())
        };
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

    public Position[] getDrawPositions() {
        return drawPositions;
    }
}
