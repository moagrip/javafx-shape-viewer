package com.lab3.laboration3moagrip;

import com.lab3.laboration3moagrip.models.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
        prepareSaveButton();
    }

    private void prepareSaveButton() {
        saveButton.setOnAction(actionEvent -> saveToSvg(actionEvent));
    }

    private String polygonToPointsString(Polygon polygon) {
        StringBuffer sb = new StringBuffer();
        sb.append(polygon.getPoints().get(0));
        sb.append(",");
        sb.append(polygon.getPoints().get(1));
        sb.append(" ");
        sb.append(polygon.getPoints().get(2));
        sb.append(",");
        sb.append(polygon.getPoints().get(3));
        sb.append(" ");
        sb.append(polygon.getPoints().get(4));
        sb.append(",");
        sb.append(polygon.getPoints().get(5));
        return sb.toString();
    }

    private void saveToSvg(ActionEvent e) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save as");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SVG", "*.svg"));
        File file = fileChooser.showSaveDialog(anchorPane.getScene().getWindow());

        if (file == null) return;

        StringBuffer outPut = new StringBuffer();
        outPut.append("<svg  version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\">");
        for (Node node : shapes.getChildren()) {
            Shape shape = (Shape) node;
            Color color = (Color) shape.getFill();
            if (shape instanceof Circle) {
                Circle circle = (Circle) shape;
                outPut.append(
                        "<circle cx=\"" + circle.getCenterX() +
                                "\" cy=\"" + circle.getCenterY() +
                                "\" r=\"" + circle.getRadius() +
                                "\" fill=\"" + toHexString(color) +
                                "\" stroke=\"" + toHexString(Color.BLACK) +
                                "\"/>"
                );
            } else if (shape instanceof Polygon) {
                String points = polygonToPointsString((Polygon) shape);
                outPut.append(
                        "<polygon points=\"" + points +
                                "\" fill=\"" + toHexString(color) +
                                "\" stroke=\"" + toHexString(Color.BLACK) +
                                "\"/>"
                );
            } else {
                continue;
            }
            outPut.append("\n");
        }
        outPut.append("</svg>");

        try {
            Files.writeString(file.toPath(), outPut.toString());
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
        circleButton.setSelected(true);
        radioButtons = new ToggleGroup();
        circleButton.setToggleGroup(radioButtons);
        triangleButton.setToggleGroup(radioButtons);
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
            triangleButton.setSelected(true);
        } else if (shape instanceof Circle) {
            circleButton.setSelected(true);
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