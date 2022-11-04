package com.lab3.laboration3moagrip;

import com.lab3.laboration3moagrip.models.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

public class ShapeController {
    public AnchorPane anchorPane;
    public Canvas canvas;
    public GraphicsContext context;
    public HBox hBox;
    public ChoiceBox<Size> choiceBox;
    public ColorPicker colorPicker;
    public ToggleGroup radioButtons;
    public RadioButton triangle;
    public RadioButton circle;
    public CheckBox selectMode;
    private ObservableList<Size> sizes;
    private Group shapes;
    private Shape selectedShape;
    private double applicationHeight;
    private double applicationWidth;

    public void initialize() {
        shapes = new Group();
        anchorPane.getChildren().add(shapes);
        applicationWidth = 800;
        applicationHeight = 400;
        anchorPane.setPrefHeight(applicationHeight);
        anchorPane.setPrefWidth(applicationWidth);
        prepareBackground();
        prepareHBox();
    }

    private void prepareBackground() {
        hBox.setPrefWidth(applicationWidth);
        hBox.setBackground(
                new Background(
                        new BackgroundFill(
                                Color.LIGHTGRAY,
                                CornerRadii.EMPTY,
                                Insets.EMPTY)
                )
        );
        canvas.setHeight(applicationHeight);
        canvas.setWidth(applicationWidth);
        context = canvas.getGraphicsContext2D();
        context.setFill(Color.WHITE);
        context.fillRect(0, 0, applicationWidth, applicationHeight);
    }

    private void prepareHBox() {
        prepareChoiceBox();
        prepareRadioButtons();
        prepareSelectMode();
        prepareColorPicker();
    }

    private void prepareColorPicker() {
        colorPicker.setValue(Color.LIGHTBLUE);
        colorPicker.valueProperty().addListener(
                new ChangeListener<Color>() {
                    @Override
                    public void changed(ObservableValue<? extends Color> observableValue, Color color, Color t1) {
                        onChange();
                    }
                }
        );
    }

    private void prepareSelectMode() {
        selectMode.selectedProperty().addListener(
                new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        if (observable.getValue() == false) {
                            deselectSelectedShape();
                        }
                    }
                });
    }

    private void prepareRadioButtons() {
        circle.setSelected(true);
        radioButtons = new ToggleGroup();
        circle.setToggleGroup(radioButtons);
        triangle.setToggleGroup(radioButtons);
        radioButtons.selectedToggleProperty().addListener(
                new ChangeListener<Toggle>() {
                    @Override
                    public void changed(ObservableValue<? extends Toggle> observableValue, Toggle toggle, Toggle t1) {
                        onChange();
                    }
                }
        );
    }

    private void prepareChoiceBox() {
        sizes = FXCollections.observableArrayList(Size.values());
        choiceBox.setItems(sizes);
        choiceBox.setValue(Size.SMALL);
        choiceBox.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<Size>() {
                    @Override
                    public void changed(ObservableValue<? extends Size> observable, Size size, Size t1) {
                        onChange();
                    }
                }
        );
    }

    private void onChange() {
        if (!selectMode.isSelected()) return;
        if (selectedShape == null) return;
        shapes.getChildren().remove(selectedShape);
        drawShape(selectedShape);
    }

    private void render(ShapeModel shapeModel) {
        if (shapeModel instanceof TriangleModel) {
            renderTriangle((TriangleModel) shapeModel);
        } else if (shapeModel instanceof CircleModel) {
            renderCircle((CircleModel) shapeModel);
        }
    }


    private void renderTriangle(TriangleModel triangleModel) {
        Position p1 = triangleModel.getPosition().increaseX(triangleModel.getSizeInt());
        Position p2 = triangleModel.getPosition().increaseY(triangleModel.getSizeInt());
        Double[] positions = new Double[]{
                triangleModel.getPosition().x(),
                triangleModel.getPosition().y(),
                p1.x(),
                p1.y(),
                p2.x(),
                p2.y()
        };
        Polygon polygon = new Polygon();
        polygon.getPoints().addAll(positions);
        polygon.setFill(triangleModel.getColor());
        polygon.setStroke(Color.BLACK);
        polygon.setOnMouseClicked(this::selectShape);
        finishShape(polygon);
    }

    private void renderCircle(CircleModel circleModel) {
        Circle c = new Circle(
                circleModel.getPosition().x(),
                circleModel.getPosition().y(),
                circleModel.getSizeInt(),
                circleModel.getColor());
        c.setStroke(Color.BLACK);
        c.setOnMouseClicked(this::selectShape);
        finishShape(c);
    }

    private void finishShape(Shape shape) {
        shapes.getChildren().add(shape);
        if (selectedShape != null) {
            deselectSelectedShape();
            setSelectedShape(shape);
        }
    }

    public void drawShape(MouseEvent mouseEvent) {
        if (selectMode.isSelected()) return;
        drawShape(new Position(
                mouseEvent.getSceneX(),
                mouseEvent.getSceneY()
        ));
    }

    private void drawShape(Shape shape) {
        if (shape instanceof Polygon) {
            drawShape(
                    new Position(
                            ((Polygon) shape).getPoints().get(0),
                            ((Polygon) shape).getPoints().get(1)
                    )
            );
        } else if (shape instanceof Circle) {
            drawShape(
                    new Position(
                            ((Circle) shape).getCenterX(),
                            ((Circle) shape).getCenterY()
                    )
            );
        }
    }

    private void drawShape(Position position) {
        RadioButton selectedShapeType = (RadioButton) radioButtons.getSelectedToggle();
        ShapeModel shapeModel = ShapeModelBuilder
                .newBuilder(selectedShapeType.getText())
                .setSize(choiceBox.getValue())
                .setPosition(position)
                .setColor(colorPicker.getValue())
                .build();
        render(shapeModel);
    }

    public void selectShape(MouseEvent mouseEvent) {
        if (!selectMode.isSelected()) return;
        deselectSelectedShape();
        Shape shape = (Shape) mouseEvent.getSource();
        setSelectedShape(shape);
        colorPicker.setValue((Color) shape.getFill());
        if (shape instanceof Polygon) {
            triangle.setSelected(true);
        } else if (shape instanceof Circle) {
            circle.setSelected(true);
        }
    }

    private void setSelectedShape(Shape shape) {
        selectedShape = shape;
        shape.setStroke(Color.YELLOW);
    }

    private void deselectSelectedShape() {
        if (selectedShape != null) {
            selectedShape.setStroke(Color.BLACK);
        }
    }
}