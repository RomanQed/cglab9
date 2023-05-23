package com.github.romanqed.cglab9;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.function.Function;


public class MainController implements Initializable {
    private final ActionPool pool = new ActionPool();
    private final List<Point> buffer = new LinkedList<>();
    @FXML
    protected Canvas canvas;
    @FXML
    protected TextField x;
    @FXML
    protected TextField y;
    private GraphicsContext context;
    private Figure cutter;
    private Figure figure;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.context = canvas.getGraphicsContext2D();
        this.clear();
    }

    private void clear() {
        context.setFill(Color.WHITE);
        context.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private void drawLine(Line line, Color color) {
        context.setStroke(color);
        context.strokeLine(line.start.x, line.start.y, line.end.x, line.end.y);
    }

    private void drawPoints(List<Point> body, Color color) {
        for (int i = 0; i < body.size() - 1; ++i) {
            Line toDraw = new Line(body.get(i), body.get(i + 1));
            this.drawLine(toDraw, color);
        }
    }

    private void drawFigure(Figure figure, Color color) {
        List<Point> body = figure.body;
        drawPoints(body, color);
        this.drawLine(new Line(body.get(body.size() - 1), body.get(0)), color);
    }

    private void refresh() {
        this.clear();
        this.drawPoints(buffer, Color.RED);
        if (cutter != null) {
            this.drawFigure(cutter, Color.RED);
        }
        if (figure != null) {
            this.drawFigure(figure, Color.BLACK);
        }
        if (cutter != null && figure != null) {
            Figure clipped = cutter.cut(figure);
            if (clipped != null) {
                this.context.setLineWidth(3);
                this.drawFigure(clipped, Color.GREEN);
                this.context.setLineWidth(1);
            }
        }
    }

    private void commitCutter(Figure figure) {
        this.pool.add(new FigureImpl(figure, this.cutter, value -> this.cutter = value));
    }

    private void commitFigure(Figure figure) {
        this.pool.add(new FigureImpl(figure, this.figure, value -> this.figure = value));
    }

    private <T> T parse(TextField textField, Function<String, T> parser) {
        try {
            return parser.apply(textField.getText());
        } catch (Throwable e) {
            return null;
        }
    }

    private Point getPoint(TextField xField, TextField yField) {
        Double x = parse(xField, Double::parseDouble);
        Double y = parse(yField, Double::parseDouble);
        if (x == null || y == null) {
            return null;
        }
        return new Point(x, y);
    }

    @FXML
    protected void onExitAction() {
        System.exit(0);
    }

    @FXML
    protected void onAboutAction() throws IOException {
        Util.showInfo("О программе", Util.readResourceFile("about.txt"));
    }

    @FXML
    protected void onAuthorAction() {
        Util.showInfo("Автор", "Бакалдин Роман ИУ7-45Б");
    }

    @FXML
    protected void onCancelAction() {
        this.pool.undo();
    }

    @FXML
    protected void onRedoAction() {
        this.pool.redo();
    }

    @FXML
    protected void onResetAction() {
        this.pool.clear();
        this.cutter = null;
        this.figure = null;
        this.buffer.clear();
        this.refresh();
    }

    @FXML
    protected void onPointsReset() {
        this.buffer.clear();
        this.refresh();
    }

    private Figure makeFigure() {
        if (buffer.size() < 3) {
            Util.showError("Ошибка", "Невозможно задать отсекатель, если количество вершин меньше 3");
            return null;
        }
        Figure ret = new Figure(buffer);
        buffer.clear();
        if (ret.getConvexity() < 0) {
            Collections.reverse(ret.body);
        }
        return ret;
    }

    @FXML
    protected void onSetCutter() {
        Figure toAdd = makeFigure();
        if (toAdd == null) {
            return;
        }
        if (!toAdd.isConvex()) {
            Util.showError("Ошибка", "Отсекатель должен быть выпуклым");
            this.refresh();
            return;
        }
        this.commitCutter(toAdd);
    }

    @FXML
    protected void onSetFigure() {
        Figure toAdd = makeFigure();
        if (toAdd == null) {
            return;
        }
        this.commitFigure(toAdd);
    }

    @FXML
    protected void onResetCutter() {
        this.commitCutter(null);
    }

    @FXML
    protected void onResetFigure() {
        this.commitFigure(null);
    }

    @FXML
    protected void onAddPoint() {
        Point toAdd = getPoint(x, y);
        if (toAdd == null) {
            Util.showError("Ошибка", "Введенные координаты должны быть числом");
            return;
        }
        buffer.add(toAdd);
        refresh();
    }

    @FXML
    protected void onCanvasMouseClicked(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();
        MouseButton button = event.getButton();
        if (button == MouseButton.SECONDARY) {
            this.x.setText(Double.toString(x));
            this.y.setText(Double.toString(y));
            return;
        }
        Point toAdd = new Point(x, y);
        buffer.add(toAdd);
        this.refresh();
    }

    private final class FigureImpl implements Action {
        private final Figure figure;
        private final Figure previous;
        private final Consumer<Figure> setter;

        private FigureImpl(Figure figure, Figure previous, Consumer<Figure> setter) {
            this.figure = figure;
            this.previous = previous;
            this.setter = setter;
        }

        @Override
        public void perform() {
            setter.accept(figure);
            refresh();
        }

        @Override
        public void undo() {
            setter.accept(previous);
            refresh();
        }
    }
}
