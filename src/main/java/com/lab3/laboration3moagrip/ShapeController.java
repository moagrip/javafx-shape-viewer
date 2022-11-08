package com.lab3.laboration3moagrip;

import com.lab3.laboration3moagrip.models.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ShapeController {
    public AnchorPane anchorPane;
    public Canvas canvas;
    public GraphicsContext context;
    public HBox hBox;
    public ChoiceBox<Size> choiceBox;
    public ColorPicker colorPicker;
    public ToggleGroup radioButtons;
    public RadioButton triangleButton;
    public RadioButton circleButton;
    public CheckBox selectMode;
    public Button saveButton;
    public Button undoButton;
    private Group shapes;
    private Shape selectedShape;
    private double applicationHeight;
    private double applicationWidth;
    private boolean selectInProgress;
    private Shape previousState;
    private Shape currentState;

    public void initialize() {
        shapes = new Group();
        anchorPane.getChildren().add(shapes);
        applicationWidth = 800;
        applicationHeight = 400;
        selectInProgress = false;
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
        prepareSaveButton();
        prepareUndoButton();
    }

    private void updateState(Shape shape) {
        previousState = currentState;
        currentState = shape;
    }

    private void prepareUndoButton() {
        undoButton.setOnAction(this::undoLastAction);
    }

    private void undoLastAction(ActionEvent e) {
        shapes.getChildren().remove(currentState);
        if (previousState == null) return;

        String typeString;
        Size size;
        Position position;
        Color color;
        if (previousState instanceof Circle circle) {
            typeString = "circle";
            size = Size.valueOf((int) circle.getRadius());
            position = getPositionFromShape(circle);
            color = (Color) circle.getFill();
        } else if (previousState instanceof Polygon polygon) {
            typeString = "triangle";
            size = Size.valueOf(polygon.getPoints().get(2).intValue() - polygon.getPoints().get(0).intValue());
            position = getPositionFromShape(polygon);
            color = (Color) polygon.getFill();
        } else {
            return;
        }

        ShapeModel shapeModel = ShapeModelBuilder
                .newBuilder(typeString)
                .setSize(size)
                .setPosition(position)
                .setColor(color)
                .build();
        render(shapeModel);

    }

    private void prepareSaveButton() {
        saveButton.setOnAction(this::saveToSvg);
    }

    private String polygonToPointsString(Polygon polygon) {
        return polygon.getPoints().get(0) +
                "," +
                polygon.getPoints().get(1) +
                " " +
                polygon.getPoints().get(2) +
                "," +
                polygon.getPoints().get(3) +
                " " +
                polygon.getPoints().get(4) +
                "," +
                polygon.getPoints().get(5);
    }

    private void saveToSvg(ActionEvent e) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save as");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SVG", "*.svg"));
        File file = fileChooser.showSaveDialog(anchorPane.getScene().getWindow());
        if (file == null) return;

        StringBuilder svgData = new StringBuilder();
        svgData.append("<svg  version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\">");
        for (Node node : shapes.getChildren()) {
            Shape shape = (Shape) node;
            Color color = (Color) shape.getFill();
            if (shape instanceof Circle circle) {
                svgData.append("<circle cx=\"")
                        .append(circle.getCenterX())
                        .append("\" cy=\"")
                        .append(circle.getCenterY())
                        .append("\" r=\"")
                        .append(circle.getRadius())
                        .append("\" fill=\"")
                        .append(toHexString(color))
                        .append("\" stroke=\"")
                        .append(toHexString(Color.BLACK))
                        .append("\"/>");
            } else if (shape instanceof Polygon) {
                String points = polygonToPointsString((Polygon) shape);
                svgData.append("<polygon points=\"")
                        .append(points)
                        .append("\" fill=\"")
                        .append(toHexString(color))
                        .append("\" stroke=\"")
                        .append(toHexString(Color.BLACK))
                        .append("\"/>");
            } else {
                continue;
            }
            svgData.append("\n");
        }
        svgData.append("</svg>");

        try {
            Files.writeString(file.toPath(), svgData.toString());
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    private String format(double val) {
        String in = Integer.toHexString((int) Math.round(val * 255));
        return in.length() == 1 ? "0" + in : in;
    }

    public String toHexString(Color value) {
        return "#" + (format(value.getRed()) + format(value.getGreen()) + format(value.getBlue()) + format(value.getOpacity()))
                .toUpperCase();
    }

    private void prepareColorPicker() {
        colorPicker.setValue(Color.LIGHTBLUE);
        colorPicker.valueProperty().addListener(
                (observableValue, color, t1) -> onChange()
        );
    }

    private void prepareSelectMode() {
        selectMode.selectedProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (observable.getValue() == false) {
                        deselectSelectedShape();
                    }
                });
    }

    private void prepareRadioButtons() {
        circleButton.setSelected(true);
        radioButtons = new ToggleGroup();
        circleButton.setToggleGroup(radioButtons);
        triangleButton.setToggleGroup(radioButtons);
        radioButtons.selectedToggleProperty().addListener(
                (observableValue, toggle, t1) -> onChange()
        );
    }

    private void prepareChoiceBox() {
        ObservableList<Size> sizes = FXCollections.observableArrayList(Size.values());
        choiceBox.setItems(sizes);
        choiceBox.setValue(Size.SMALL);
        choiceBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, size, t1) -> onChange()
        );
    }

    private void onChange() {
        if (!selectMode.isSelected()) return;
        if (selectedShape == null) return;
        if (selectInProgress) return;
        shapes.getChildren().remove(selectedShape);
        currentState = selectedShape;
        drawShape(getPositionFromShape(selectedShape));
    }

    private void render(ShapeModel shapeModel) {
        if (shapeModel instanceof TriangleModel) {
            renderTriangle((TriangleModel) shapeModel);
        } else if (shapeModel instanceof CircleModel) {
            renderCircle((CircleModel) shapeModel);
        }
    }

    private void renderTriangle(TriangleModel triangleModel) {
        Position p1 = triangleModel.getPosition().increaseX(triangleModel.getSize().getValue());
        Position p2 = triangleModel.getPosition().increaseY(triangleModel.getSize().getValue());
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
                circleModel.getSize().getValue(),
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
        updateState(shape);
    }

    public void drawShape(MouseEvent mouseEvent) {
        if (selectMode.isSelected()) return;
        drawShape(new Position(
                mouseEvent.getSceneX(),
                mouseEvent.getSceneY()
        ));
    }

    private Position getPositionFromShape(Shape shape) {
        if (shape instanceof Polygon) {
            return new Position(
                    ((Polygon) shape).getPoints().get(0),
                    ((Polygon) shape).getPoints().get(1)
            );
        } else if (shape instanceof Circle) {
            return new Position(
                    ((Circle) shape).getCenterX(),
                    ((Circle) shape).getCenterY()
            );
        }
        return new Position(0.0, 0.0);
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
        selectInProgress = true;
        deselectSelectedShape();
        Shape shape = (Shape) mouseEvent.getSource();
        setSelectedShape(shape);
        colorPicker.setValue((Color) shape.getFill());
        if (shape instanceof Polygon polygon) {
            triangleButton.setSelected(true);
            choiceBox.setValue(
                    Size.valueOf(
                            polygon.getPoints().get(2).intValue() - polygon.getPoints().get(0).intValue()
                    )
            );
        } else if (shape instanceof Circle circle) {
            circleButton.setSelected(true);
            choiceBox.setValue(
                    Size.valueOf((int) circle.getRadius())
            );
        }
        selectInProgress = false;
    }

    private void setSelectedShape(Shape shape) {
        selectedShape = shape;
        shape.setStroke(Color.YELLOW);
    }

    private void deselectSelectedShape() {
        if (selectedShape != null) {
            selectedShape.setStroke(Color.BLACK);
            selectedShape = null;
        }
    }
}