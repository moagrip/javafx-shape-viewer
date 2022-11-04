package com.lab3.laboration3moagrip.models;

public record Position(double x, double y) {

    public Position increaseX(double x) {
        return new Position(this.x + x, this.y);
    }

    public Position increaseY(double y) {
        return new Position(this.x, this.y + y);
    }
}
