package org.jfree.graphics2d.tester.fxgraphics2d;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataset;
import org.jfree.fx.FXGraphics2D;
import org.jfree.graphics2d.Tester;

import javax.imageio.ImageIO;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

public class TestApplication extends Application {

    static class MyCanvas extends Canvas {

        private final FXGraphics2D g2;

        public MyCanvas() {
            super(Tester.getTestSheetWidth(), Tester.getTestSheetHeight());
            this.g2 = new FXGraphics2D(getGraphicsContext2D());
            // Redraw canvas when size changes.
            draw();
        }

        private void draw() {
            double width = getWidth();
            double height = getHeight();
            getGraphicsContext2D().clearRect(0, 0, width, height);
            Tester.drawTestSheet(this.g2, "FXGraphics2D", "https://github.com/jfree/fxgraphics2d");
            WritableImage writableImage = new WritableImage((int) getWidth(), (int) getHeight());
            snapshot(null, writableImage);
            RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
            try {
                ImageIO.write(renderedImage, "png", new File("fxgraphics2d.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public boolean isResizable() {
            return true;
        }

        @Override
        public double prefWidth(double height) { return getWidth(); }

        @Override
        public double prefHeight(double width) { return getHeight(); }
    }

    @Override
    public void start(Stage stage) throws Exception {
        StackPane stackPane = new StackPane();
        MyCanvas canvas = new MyCanvas();
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(canvas);
        stackPane.getChildren().add(scrollPane);
        // Bind canvas size to stack pane size.
        //scrollPane.widthProperty().bind( stackPane.widthProperty());
        //scrollPane.heightProperty().bind( stackPane.heightProperty());
        stage.setScene(new Scene(stackPane));
        stage.setTitle("TestApplication.java");
        stage.setWidth(700);
        stage.setHeight(390);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
