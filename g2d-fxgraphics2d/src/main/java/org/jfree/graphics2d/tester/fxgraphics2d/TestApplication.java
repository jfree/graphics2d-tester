package org.jfree.graphics2d.tester.fxgraphics2d;

import java.awt.RenderingHints;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.jfree.fx.FXGraphics2D;
import org.jfree.graphics2d.Tester;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import javafx.animation.AnimationTimer;
import javafx.scene.SnapshotParameters;
import javafx.scene.transform.Transform;

public class TestApplication extends Application {

    static final class MyCanvas extends Canvas {

        private final boolean single = false;

        private final Tester.TesterContext tc;
        private boolean saveFirst = true;

        private final FXGraphics2D g2;

        MyCanvas() {
            super(Tester.getTestSheetWidth(), Tester.getTestSheetHeight());
            this.g2 = new FXGraphics2D(getGraphicsContext2D());
            this.g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            // Prepare context:
            this.tc = Tester.prepareTestOutput(
                    "JFree/FXGraphics2D (2.1.3)",
                    "https://github.com/jfree/fxgraphics2d", single);
        }

        int nFrame = 0;

        void draw() {
            final long startTime = System.nanoTime();

            final GraphicsContext gc = getGraphicsContext2D();
            gc.save();

            if (true) {
                final int width = (int) Math.ceil(getWidth());
                final int height = (int) Math.ceil(getHeight());

                gc.setFill(((nFrame++) % 2 == 0) ? Color.WHITE : Color.GREEN);
                gc.fillRect(0, 0, width, height);
            }

            Tester.drawTestOutput(tc, g2);

            gc.restore();

            // TODO: sync ?
            // image is ready
            final double elapsedTime = 1e-6d * (System.nanoTime() - startTime);
            System.out.println("drawTestOutput(FXGraphics2D) duration = " + elapsedTime + " ms.");

            if (saveFirst) {
                saveFirst = false;

                final WritableImage writableImage = pixelScaleAwareCanvasSnapshot(this, 1.0);

                final RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
                try {
                    ImageIO.write(renderedImage, "png", new File("fxgraphics2d.png"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public static WritableImage pixelScaleAwareCanvasSnapshot(Canvas canvas, double pixelScale) {
            WritableImage writableImage = new WritableImage(
                    (int) Math.rint(pixelScale * canvas.getWidth()),
                    (int) Math.rint(pixelScale * canvas.getHeight()));
            final SnapshotParameters spa = new SnapshotParameters();
            spa.setTransform(Transform.scale(pixelScale, pixelScale));
            return canvas.snapshot(spa, writableImage);
        }

        @Override
        public boolean isResizable() {
            return false;
        }

        @Override
        public double prefWidth(double height) {
            return getWidth();
        }

        @Override
        public double prefHeight(double width) {
            return getHeight();
        }
    }

    private AnimationTimer timer = null;

    @Override
    public void start(Stage stage) throws Exception {
        StackPane stackPane = new StackPane();
        MyCanvas canvas = new MyCanvas();
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setCache(false);
        scrollPane.setContent(canvas);
        stackPane.getChildren().add(scrollPane);
        // Bind canvas size to stack pane size.
        //scrollPane.widthProperty().bind( stackPane.widthProperty());
        //scrollPane.heightProperty().bind( stackPane.heightProperty());
        stage.setScene(new Scene(stackPane));
        stage.setTitle("TestApplication.java");
        stage.setWidth(Tester.getTestSheetWidth());
        stage.setHeight(Tester.getTestSheetHeight());
        stage.show();

        timer = new AnimationTimer() {
            private int nbFrames = 0;
            private long lastTime = System.nanoTime();
            private long lastInstant = lastTime;
            private long nextInstant = lastTime + 1000_000_000L;

            @Override
            public void handle(long startNanos) {
                final long elapsed = startNanos - lastTime;
                lastTime = startNanos;

                canvas.draw();

                nbFrames++;

                if (elapsed > 0L) {
                    System.out.println(String.format("Elapsed: %.3f ms", 1e-6 * elapsed));
                }
                if (startNanos > nextInstant) {
                    System.out.println(String.format(">>> FPS: %.3f", 5e8 * nbFrames / (startNanos - lastInstant)));

                    // reset
                    nbFrames = 0;
                    lastInstant = startNanos;
                    nextInstant = startNanos + 1000_000_000L;
                }
            }
        };

        timer.start();
    }

    @Override
    public void stop() {
        System.out.println("Stop application ...");
        timer.stop();
        Platform.runLater(() -> System.exit(0));
        Platform.exit();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Set the default locale to en-US locale (for Numerical Fields "." ",")
        Locale.setDefault(Locale.US);

        // ensure no hi-dpi to ensure scale = 1.0:
        System.out.println("Use 'java -Dprism.verbose=true -Dprism.allowhidpi=false -Dprism.order=sw -Dglass.gtk.uiScale=1.0 -Dsun.java2d.uiScale=1.0 ...' ");
        // -Dprism.order=es2
        // -Dprism.marlin.log=true

        launch(args);
    }
}
