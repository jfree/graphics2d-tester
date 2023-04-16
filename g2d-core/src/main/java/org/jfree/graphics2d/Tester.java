/*
 * JFree Graphics2D Tester
 *
 * (C)opyright 2021-2023, by David Gilbert.
 */
package org.jfree.graphics2d;

import eu.hansolo.steelseries.gauges.Radial;

import java.awt.*;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.flow.FlowPlot;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart3d.Chart3D;
import org.jfree.chart3d.Chart3DFactory;
import org.jfree.chart3d.axis.ValueAxis3D;
import org.jfree.chart3d.data.Range;
import org.jfree.chart3d.data.function.Function3D;
import org.jfree.chart3d.graphics3d.Dimension3D;
import org.jfree.chart3d.graphics3d.ViewPoint3D;
import org.jfree.chart3d.plot.XYZPlot;
import org.jfree.chart3d.renderer.GradientColorScale;
import org.jfree.chart3d.renderer.xyz.SurfaceRenderer;
import org.jfree.data.flow.DefaultFlowDataset;
import org.jfree.data.flow.FlowDataset;

import javax.imageio.ImageIO;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

import static java.awt.Color.*;
import static java.awt.Font.BOLD;
import static java.awt.Font.SERIF;
import static java.awt.RenderingHints.*;
import static org.jfree.graphics2d.ClippingTests.drawArc2DWithRectangularClip;
import static org.jfree.graphics2d.ClippingTests.fillRectangularClippingRegions;
import static org.jfree.graphics2d.FontTests.*;
import static org.jfree.graphics2d.ImageTests.drawImage;
import static org.jfree.graphics2d.ImageTests.drawQRCodeImage;
import static org.jfree.graphics2d.ShapeTests.*;
import static org.jfree.graphics2d.TransformTests.*;

/**
 * Runs a visual testing setup against various Graphics2D implementations.  The idea
 * is that you can compare the output of a custom Graphics2D implementation against
 * the reference implementation (Java2D).
 */
public class Tester {

    private final static int REPEATS = 100;

    final static boolean DO_CLIP = true;

    private static final int TILE_COUNT_H = 11;

    private static final int TILE_COUNT_V = 34;

    private static final int TILE_WIDTH = 100;

    private static final int TILE_HEIGHT = 65;

    private static final int MARGIN = 5;

    private static final Stroke OUTLINE = new BasicStroke(1.0f);
    private static final Stroke OUTLINE_3 = new BasicStroke(3.0f);

    /** A dashed line stroke. */
    private static final Stroke DASHED = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_ROUND, 4f, new float[]{2f, 2f}, 0f);

    /** A dashed line stroke with width 3. */
    private static final Stroke DASHED_3 = new BasicStroke(3.0f, BasicStroke.CAP_ROUND,
            BasicStroke.JOIN_ROUND, 4f, new float[]{4f, 8f}, 0f);

    /** An array containing rainbow colors, used in the gradient paint tests. */
    private static final Color[] RAINBOW_COLORS = new Color[]{
        new Color(255, 0, 0), // RED
        new Color(255, 165, 0), // ORANGE
        new Color(255, 255, 0), // YELLOW
        new Color(0, 128, 0), // GREEN
        new Color(0, 0, 255), // BLUE
        new Color(75, 0, 130), // INDIGO
        new Color(238, 130, 238) // VIOLET
    };

    private static void moveTo(int tileX, int tileY, Graphics2D g2) {
        AffineTransform t = AffineTransform.getTranslateInstance(tileX * TILE_WIDTH, tileY * TILE_HEIGHT);
        g2.setTransform(t);
    }

    private static void prepareSwingUI(final TesterContext tc, final Rectangle2D bounds) {
        final JComponent content = createContent();
        final JFrame frame = new JFrame("Title");
        frame.getContentPane().add(content);
        frame.setBounds((int) bounds.getX(), (int) bounds.getY(), (int) bounds.getWidth(), (int) bounds.getHeight());
        frame.pack();
        tc.frame = frame;
    }

    private static void drawSwingUI(final JFrame frame, final Graphics2D g2) {
        frame.getContentPane().paint(g2);
    }

    private static void prepareOrsonChartSample(final TesterContext tc) {
        final Function3D function = (double x, double z) -> Math.cos(x) * Math.sin(z);

        final Chart3D chart = Chart3DFactory.createSurfaceChart(
                "SurfaceRendererDemo1",
                "y = cos(x) * sin(z)",
                function, "X", "Y", "Z");

        final XYZPlot plot = (XYZPlot) chart.getPlot();
        plot.setDimensions(new Dimension3D(10, 5, 10));

        final ValueAxis3D xAxis = plot.getXAxis();
        xAxis.setRange(-Math.PI, Math.PI);
        final ValueAxis3D zAxis = plot.getZAxis();
        zAxis.setRange(-Math.PI, Math.PI);

        final SurfaceRenderer renderer = (SurfaceRenderer) plot.getRenderer();
        renderer.setDrawFaceOutlines(false);
        renderer.setColorScale(new GradientColorScale(new Range(-1.0, 1.0),
                RED, YELLOW));
        chart.setViewPoint(ViewPoint3D.createAboveLeftViewPoint(70.0));
        tc.orsonChart = chart;
    }

    private static void drawOrsonChartSample(final TesterContext tc, Graphics2D g2, Rectangle2D bounds) {
        tc.orsonChart.draw(g2, bounds);
    }

    /**
     * Creates a dataset (source https://statisticsnz.shinyapps.io/trade_dashboard/).
     *
     * @return a dataset.
     */
    private static DefaultFlowDataset<String> createDataset() {
        DefaultFlowDataset<String> dataset = new DefaultFlowDataset<>();
        dataset.setFlow(0, "Goods", "Australia", 2101);
        dataset.setFlow(0, "Services", "Australia", 714);
        dataset.setFlow(0, "Goods", "China", 3397);
        dataset.setFlow(0, "Services", "China", 391);
        dataset.setFlow(0, "Goods", "USA", 1748);
        dataset.setFlow(0, "Services", "USA", 583);
        dataset.setFlow(0, "Goods", "United Kingdom", 363);
        dataset.setFlow(0, "Services", "United Kingdom", 178);

        // dairy, meat, travel, fruits & nuts, wood, beverages
        dataset.setFlow(1, "Australia", "Dairy", 165);
        dataset.setFlow(1, "Australia", "Travel", 198);
        dataset.setFlow(1, "Australia", "Beverages", 170);
        dataset.setFlow(1, "Australia", "Other Goods", 2815 - 165 - 198 - 170);

        dataset.setFlow(1, "China", "Dairy", 848);
        dataset.setFlow(1, "China", "Meat", 463);
        dataset.setFlow(1, "China", "Travel", 343);
        dataset.setFlow(1, "China", "Fruit & Nuts", 296);
        dataset.setFlow(1, "China", "Wood", 706);
        dataset.setFlow(1, "China", "Other Goods", 3788 - 848 - 463 - 343 - 296 - 706);

        dataset.setFlow(1, "United Kingdom", "Dairy", 18);
        dataset.setFlow(1, "United Kingdom", "Meat", 71);
        dataset.setFlow(1, "United Kingdom", "Travel", 59);
        dataset.setFlow(1, "United Kingdom", "Fruit & Nuts", 13);
        dataset.setFlow(1, "United Kingdom", "Beverages", 154);
        dataset.setFlow(1, "United Kingdom", "Other Goods", 541 - 18 - 71 - 59 - 13 - 154);

        dataset.setFlow(1, "USA", "Dairy", 95);
        dataset.setFlow(1, "USA", "Meat", 367);
        dataset.setFlow(1, "USA", "Travel", 90);
        dataset.setFlow(1, "USA", "Wood", 83);
        dataset.setFlow(1, "USA", "Beverages", 157);
        dataset.setFlow(1, "USA", "Other Goods", 2331 - 95 - 367 - 90 - 83 - 157);
        return dataset;
    }

    /**
     * Creates a JFreeChart instance to draw as test output.
     *
     * @param dataset the dataset.
     *
     * @return A JFreeChart instance.
     */
    private static JFreeChart createChart(FlowDataset<String> dataset) {
        FlowPlot plot = new FlowPlot(dataset);
        plot.setBackgroundPaint(BLACK);
        plot.setDefaultNodeLabelPaint(WHITE);
        plot.setNodeColorSwatch(createPastelColors());
        JFreeChart chart = new JFreeChart("Selected NZ Exports Sept 2020", plot);
        chart.addSubtitle(new TextTitle("Source: https://statisticsnz.shinyapps.io/trade_dashboard/"));
        chart.setBackgroundPaint(WHITE);
        return chart;
    }

    private static java.util.List<Color> createPastelColors() {
        List<Color> result = new ArrayList<>();
        result.add(new Color(232, 177, 165));
        result.add(new Color(207, 235, 142));
        result.add(new Color(142, 220, 220));
        result.add(new Color(228, 186, 115));
        result.add(new Color(187, 200, 230));
        result.add(new Color(157, 222, 177));
        result.add(new Color(234, 183, 210));
        result.add(new Color(213, 206, 169));
        result.add(new Color(202, 214, 205));
        result.add(new Color(195, 204, 133));
        return result;
    }

    private static void prepareJFreeChartSample(final TesterContext tc) {
        tc.jfreeChart = createChart(createDataset());
    }

    private static void drawJFreeChartSample(final TesterContext tc, Graphics2D g2, Rectangle2D bounds) {
        tc.jfreeChart.draw(g2, bounds);
    }

    /**
     * Prepare test sheets
     * @param tc  the tester context.
     */
    public static void prepareTestSheet(final TesterContext tc) {
        try {
            ImageTests.prepareQRCodeImage(tc);

            prepareJFreeChartSample(tc);

            prepareOrsonChartSample(tc);

            ImageTests.prepareImage(tc);

            prepareSwingUI(tc, new Rectangle2D.Double(0, 0, TILE_WIDTH * 4, TILE_HEIGHT * 4));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Draws a tile in a fail-safe manner.  If the Graphics2D implementation under test fails,
     * a fail tile is shown instead.
     *
     * @param g2 the graphics target
     * @param row the row number for the tile (zero-based)
     * @param column the column number for the tile (zero-based)
     * @param drawer the tile drawer (receives the tile bounds)
     */
    private static void drawTile(Graphics2D g2, int row, int column, Rectangle2D bounds, Consumer<Rectangle2D> drawer) {
        try {
            moveTo(column, row, g2);
            drawer.accept(bounds);
        } catch (Exception e) {
            Rectangle2D tile = new Rectangle2D.Double(bounds.getX() + MARGIN, bounds.getY() + MARGIN, bounds.getWidth() - MARGIN * 2, bounds.getHeight() - MARGIN * 2);
            g2.setPaint(WHITE);
            g2.fill(bounds);
            g2.setPaint(LIGHT_GRAY);
            g2.fill(tile);
            String message = "FAILED";
            g2.setFont(new Font(Font.SANS_SERIF, BOLD, 12));
            g2.setPaint(BLACK);
            int w = g2.getFontMetrics().stringWidth(message);
            int h = g2.getFontMetrics().getAscent();
            g2.drawString(message, (int) (bounds.getCenterX() - w / 2), (int) bounds.getCenterY() + h / 2);
        }
    }

    /**
     * Draws a test sheet consisting of a number of tiles, each one testing one or
     * more features of Java2D and Graphics2D.
     *
     * @param tc the test context.
     * @param g2 the graphics target.
     */
    private static void drawTestSheet(final TesterContext tc, final Graphics2D g2) {
        int row = -1;
        Rectangle2D bounds = new Rectangle2D.Double(0.0, 0.0, TILE_WIDTH, TILE_HEIGHT);

        row++; // *** HEADER
        moveTo(0, row, g2);
        g2.setPaint(WHITE);
        g2.fill(new Rectangle2D.Double(2, 2, TILE_WIDTH * TILE_COUNT_H, TILE_HEIGHT - 2));
        g2.setPaint(BLACK);
        g2.setStroke(new BasicStroke(2.0f));
        g2.drawLine(0, 1, TILE_WIDTH * TILE_COUNT_H, 1);
        g2.drawLine(0, TILE_HEIGHT, TILE_WIDTH * TILE_COUNT_H, TILE_HEIGHT);

        String str = "JFree Graphics2D Tester";
        g2.setFont(new Font(SERIF, BOLD, 32));
        FontMetrics fm = g2.getFontMetrics();
        LineMetrics lm = fm.getLineMetrics(str, g2);
        float x = 5f;
        float y = (float) (bounds.getCenterY() + (lm.getAscent() / 2));
        g2.drawString(str, x, y);

        row++;
        Rectangle2D propertyBounds = new Rectangle2D.Double(0, 0, TILE_WIDTH * 4, TILE_HEIGHT * 3);
        drawTile(g2, row, 7, propertyBounds, t -> drawTestProperties(g2, tc.g2UnderTest));
        row--;

        // QR CODE AT RIGHT SIDE
        row += 4;
        Rectangle2D qrCodeBounds = new Rectangle2D.Double(0, 0, TILE_WIDTH * 2, TILE_HEIGHT * 2);
        drawTile(g2, row, TILE_COUNT_H - 4, qrCodeBounds, t -> drawQRCodeImage(tc, g2, t, 0));
        row -= 4;

        // JFREECHART AT RIGHT SIDE
        row += 7;
        Rectangle2D chartBounds = new Rectangle2D.Double(0, 0, TILE_WIDTH * 4, TILE_HEIGHT * 4);
        drawTile(g2, row, TILE_COUNT_H - 4, chartBounds, t -> drawJFreeChartSample(tc, g2, t));
        row -= 7;

        // ORSON CHARTS AT RIGHT SIDE
        row += 12;
        drawTile(g2, row, TILE_COUNT_H - 4, chartBounds, t -> drawOrsonChartSample(tc, g2, t));
        row -= 12;

        row++; // *** lines with cap and dash
        drawTile(g2, row, 0, bounds, t -> drawLineCaps(g2, t, MARGIN, 0.0f, RED));
        drawTile(g2, row, 1, bounds, t -> drawLineCaps(g2, t, MARGIN, 1.0f, RED));
        drawTile(g2, row, 2, bounds, t -> drawLineCaps(g2, t, MARGIN, 1.0f, RED));
        drawTile(g2, row, 3, bounds, t -> drawLineCapAndDash(g2, t, MARGIN, 1.0f, new float[]{2f, 2f}, BLACK));
        drawTile(g2, row, 4, bounds, t -> drawLineCapAndDash(g2, t, MARGIN, 3.0f, new float[]{4f, 8f}, BLACK));
        drawTile(g2, row, 5, bounds, t -> drawLineCaps(g2, t, MARGIN, 5.0f, BLACK));

        row++; // *** Line2D variations
        drawTile(g2, row, 0, bounds, t -> drawLines(g2, t, MARGIN, new BasicStroke(0.0f), RED));
        drawTile(g2, row, 1, bounds, t -> drawLines(g2, t, MARGIN, OUTLINE, RED));
        drawTile(g2, row, 2, bounds, t -> drawLines(g2, t, MARGIN, OUTLINE, RED));
        drawTile(g2, row, 3, bounds, t -> drawLines(g2, t, MARGIN, DASHED, BLACK));
        drawTile(g2, row, 4, bounds, t -> drawLines(g2, t, MARGIN, DASHED_3, BLACK));
        drawTile(g2, row, 5, bounds, t -> drawLines(g2, t, MARGIN, OUTLINE_3, BLACK));

        row++; // *** Rectangle2D variations
        Rectangle2D rect = new Rectangle2D.Double(MARGIN, MARGIN, TILE_WIDTH - MARGIN * 2, TILE_HEIGHT - MARGIN * 2);
        drawTile(g2, row, 0, bounds, t -> fillAndStrokeShape(g2, rect, BLUE, null, null));
        drawTile(g2, row, 1, bounds, t -> fillAndStrokeShape(g2, rect, null, OUTLINE, BLUE));
        drawTile(g2, row, 2, bounds, t -> fillAndStrokeShape(g2, rect, LIGHT_GRAY, OUTLINE, BLUE));
        drawTile(g2, row, 3, bounds, t -> fillAndStrokeShape(g2, rect, LIGHT_GRAY, DASHED, BLACK));
        drawTile(g2, row, 4, bounds, t -> fillAndStrokeShape(g2, rect, LIGHT_GRAY, DASHED_3, BLACK));
        drawTile(g2, row, 5, bounds, t -> fillAndStrokeShape(g2, rect, LIGHT_GRAY, OUTLINE_3, BLACK));

        row++;  // *** RoundRectangle2D variations
        RoundRectangle2D roundRect = new RoundRectangle2D.Double(MARGIN, MARGIN, TILE_WIDTH - MARGIN * 2, TILE_HEIGHT - MARGIN * 2, 8.0, 12.0);
        drawTile(g2, row, 0, bounds, t -> fillAndStrokeShape(g2, roundRect, BLUE, null, null));
        drawTile(g2, row, 1, bounds, t -> fillAndStrokeShape(g2, roundRect, null, OUTLINE, BLUE));
        drawTile(g2, row, 2, bounds, t -> fillAndStrokeShape(g2, roundRect, LIGHT_GRAY, OUTLINE, BLUE));
        drawTile(g2, row, 3, bounds, t -> fillAndStrokeShape(g2, roundRect, LIGHT_GRAY, DASHED, BLACK));
        drawTile(g2, row, 4, bounds, t -> fillAndStrokeShape(g2, roundRect, LIGHT_GRAY, DASHED_3, BLACK));
        drawTile(g2, row, 5, bounds, t -> fillAndStrokeShape(g2, roundRect, LIGHT_GRAY, OUTLINE_3, BLACK));

        row++; // *** QuadCurve2D variations
        drawTile(g2, row, 0, bounds, t -> fillAndStrokeQuadCurve2D(g2, t, MARGIN, RED, null, null));
        drawTile(g2, row, 1, bounds, t -> fillAndStrokeQuadCurve2D(g2, t, MARGIN, null, OUTLINE, RED));
        drawTile(g2, row, 2, bounds, t -> fillAndStrokeQuadCurve2D(g2, t, MARGIN, LIGHT_GRAY, OUTLINE, RED));
        drawTile(g2, row, 3, bounds, t -> fillAndStrokeQuadCurve2D(g2, t, MARGIN, LIGHT_GRAY, DASHED, BLACK));
        drawTile(g2, row, 4, bounds, t -> fillAndStrokeQuadCurve2D(g2, t, MARGIN, LIGHT_GRAY, DASHED_3, BLACK));
        drawTile(g2, row, 5, bounds, t -> fillAndStrokeQuadCurve2D(g2, t, MARGIN, LIGHT_GRAY, OUTLINE_3, BLACK));

        row++; // *** CubicCurve2D variations
        drawTile(g2, row, 0, bounds, t -> fillAndStrokeCubicCurve2D(g2, t, MARGIN, RED, null, null));
        drawTile(g2, row, 1, bounds, t -> fillAndStrokeCubicCurve2D(g2, t, MARGIN, null, OUTLINE, RED));
        drawTile(g2, row, 2, bounds, t -> fillAndStrokeCubicCurve2D(g2, t, MARGIN, LIGHT_GRAY, OUTLINE, RED));
        drawTile(g2, row, 3, bounds, t -> fillAndStrokeCubicCurve2D(g2, t, MARGIN, LIGHT_GRAY, DASHED, BLACK));
        drawTile(g2, row, 4, bounds, t -> fillAndStrokeCubicCurve2D(g2, t, MARGIN, LIGHT_GRAY, DASHED_3, BLACK));
        drawTile(g2, row, 5, bounds, t -> fillAndStrokeCubicCurve2D(g2, t, MARGIN, LIGHT_GRAY, OUTLINE_3, BLACK));

        row++;  // *** Ellipse2D variations
        Ellipse2D ellipse = new Ellipse2D.Double(MARGIN, MARGIN, bounds.getWidth() - MARGIN * 2, bounds.getHeight() - MARGIN * 2);
        drawTile(g2, row, 0, bounds, t -> fillAndStrokeShape(g2, ellipse, BLUE, null, null));
        drawTile(g2, row, 1, bounds, t -> fillAndStrokeShape(g2, ellipse, null, OUTLINE, BLUE));
        drawTile(g2, row, 2, bounds, t -> fillAndStrokeShape(g2, ellipse, LIGHT_GRAY, OUTLINE, BLUE));
        drawTile(g2, row, 3, bounds, t -> fillAndStrokeShape(g2, ellipse, LIGHT_GRAY, DASHED, BLACK));
        drawTile(g2, row, 4, bounds, t -> fillAndStrokeShape(g2, ellipse, LIGHT_GRAY, DASHED_3, BLACK));
        drawTile(g2, row, 5, bounds, t -> fillAndStrokeShape(g2, ellipse, LIGHT_GRAY, OUTLINE_3, BLACK));

        row++;  // *** Arc2D PIE variations
        Arc2D arc = createArc2D(Arc2D.PIE, 45, 270, bounds, MARGIN);
        drawTile(g2, row, 0, bounds, t -> fillAndStrokeShape(g2, arc, BLUE, null, null));
        drawTile(g2, row, 1, bounds, t -> fillAndStrokeShape(g2, arc, null, OUTLINE, BLUE));
        drawTile(g2, row, 2, bounds, t -> fillAndStrokeShape(g2, arc, LIGHT_GRAY, OUTLINE, BLUE));
        drawTile(g2, row, 3, bounds, t -> fillAndStrokeShape(g2, arc, LIGHT_GRAY, DASHED, BLACK));
        drawTile(g2, row, 4, bounds, t -> fillAndStrokeShape(g2, arc, LIGHT_GRAY, DASHED_3, BLACK));
        drawTile(g2, row, 5, bounds, t -> fillAndStrokeShape(g2, arc, LIGHT_GRAY, OUTLINE_3, BLACK));

        row++;  // *** Arc2D CHORD variations
        Arc2D arc2 = createArc2D(Arc2D.CHORD, 210, 300, bounds, MARGIN);
        drawTile(g2, row, 0, bounds, t -> fillAndStrokeShape(g2, arc2, BLUE, null, null));
        drawTile(g2, row, 1, bounds, t -> fillAndStrokeShape(g2, arc2, null, OUTLINE, BLUE));
        drawTile(g2, row, 2, bounds, t -> fillAndStrokeShape(g2, arc2, LIGHT_GRAY, OUTLINE, BLUE));
        drawTile(g2, row, 3, bounds, t -> fillAndStrokeShape(g2, arc2, LIGHT_GRAY, DASHED, BLACK));
        drawTile(g2, row, 4, bounds, t -> fillAndStrokeShape(g2, arc2, LIGHT_GRAY, DASHED_3, BLACK));
        drawTile(g2, row, 5, bounds, t -> fillAndStrokeShape(g2, arc2, LIGHT_GRAY, OUTLINE_3, BLACK));

        row++;  // *** Arc2D OPEN variations
        Arc2D arc3 = createArc2D(Arc2D.OPEN, -45, 270, bounds, MARGIN);
        drawTile(g2, row, 0, bounds, t -> fillAndStrokeShape(g2, arc3, BLUE, null, null));
        drawTile(g2, row, 1, bounds, t -> fillAndStrokeShape(g2, arc3, null, OUTLINE, BLUE));
        drawTile(g2, row, 2, bounds, t -> fillAndStrokeShape(g2, arc3, LIGHT_GRAY, OUTLINE, BLUE));
        drawTile(g2, row, 3, bounds, t -> fillAndStrokeShape(g2, arc3, LIGHT_GRAY, DASHED, BLACK));
        drawTile(g2, row, 4, bounds, t -> fillAndStrokeShape(g2, arc3, LIGHT_GRAY, DASHED_3, BLACK));
        drawTile(g2, row, 5, bounds, t -> fillAndStrokeShape(g2, arc3, LIGHT_GRAY, OUTLINE_3, BLACK));

        row++; // ***** Path2D variations
        Path2D path = createPath2D(bounds, MARGIN);
        drawTile(g2, row, 0, bounds, t -> fillAndStrokeShape(g2, path, RED, null, null));
        drawTile(g2, row, 1, bounds, t -> fillAndStrokeShape(g2, path, null, OUTLINE, RED));
        drawTile(g2, row, 2, bounds, t -> fillAndStrokeShape(g2, path, LIGHT_GRAY, OUTLINE, RED));
        drawTile(g2, row, 3, bounds, t -> fillAndStrokeShape(g2, path, LIGHT_GRAY, DASHED, BLACK));
        drawTile(g2, row, 4, bounds, t -> fillAndStrokeShape(g2, path, LIGHT_GRAY, DASHED_3, BLACK));
        drawTile(g2, row, 5, bounds, t -> fillAndStrokeShape(g2, path, LIGHT_GRAY, OUTLINE_3, BLACK));

        row++; // *** Path2D WIND_NON_ZERO fill
        path.setWindingRule(Path2D.WIND_NON_ZERO);
        drawTile(g2, row, 0, bounds, t -> fillAndStrokeShape(g2, path, RED, null, null));
        drawTile(g2, row, 1, bounds, t -> fillAndStrokeShape(g2, path, null, OUTLINE, RED));
        drawTile(g2, row, 2, bounds, t -> fillAndStrokeShape(g2, path, LIGHT_GRAY, OUTLINE, RED));
        drawTile(g2, row, 3, bounds, t -> fillAndStrokeShape(g2, path, LIGHT_GRAY, DASHED, BLACK));
        drawTile(g2, row, 4, bounds, t -> fillAndStrokeShape(g2, path, LIGHT_GRAY, DASHED_3, BLACK));
        drawTile(g2, row, 5, bounds, t -> fillAndStrokeShape(g2, path, LIGHT_GRAY, OUTLINE_3, BLACK));

        row++; // *** Area - add
        Area areaAdd = createCombinedArea("add", bounds, MARGIN);
        drawTile(g2, row, 0, bounds, t -> fillAndStrokeShape(g2, areaAdd, BLUE, null, null));
        drawTile(g2, row, 1, bounds, t -> fillAndStrokeShape(g2, areaAdd, null, OUTLINE, BLUE));
        drawTile(g2, row, 2, bounds, t -> fillAndStrokeShape(g2, areaAdd, LIGHT_GRAY, OUTLINE, BLUE));
        drawTile(g2, row, 3, bounds, t -> fillAndStrokeShape(g2, areaAdd, LIGHT_GRAY, DASHED, BLACK));
        drawTile(g2, row, 4, bounds, t -> fillAndStrokeShape(g2, areaAdd, LIGHT_GRAY, DASHED_3, BLACK));
        drawTile(g2, row, 5, bounds, t -> fillAndStrokeShape(g2, areaAdd, LIGHT_GRAY, OUTLINE_3, BLACK));

        row++; // *** Area - intersect
        Area areaIntersect = createCombinedArea("intersect", bounds, MARGIN);
        drawTile(g2, row, 0, bounds, t -> fillAndStrokeShape(g2, areaIntersect, BLUE, null, null));
        drawTile(g2, row, 1, bounds, t -> fillAndStrokeShape(g2, areaIntersect, null, OUTLINE, BLUE));
        drawTile(g2, row, 2, bounds, t -> fillAndStrokeShape(g2, areaIntersect, LIGHT_GRAY, OUTLINE, BLUE));
        drawTile(g2, row, 3, bounds, t -> fillAndStrokeShape(g2, areaIntersect, LIGHT_GRAY, DASHED, BLACK));
        drawTile(g2, row, 4, bounds, t -> fillAndStrokeShape(g2, areaIntersect, LIGHT_GRAY, DASHED_3, BLACK));
        drawTile(g2, row, 5, bounds, t -> fillAndStrokeShape(g2, areaIntersect, LIGHT_GRAY, OUTLINE_3, BLACK));

        row++; // *** Area - subtract
        Area areaSubtract = createCombinedArea("subtract", bounds, MARGIN);
        drawTile(g2, row, 0, bounds, t -> fillAndStrokeShape(g2, areaSubtract, BLUE, null, null));
        drawTile(g2, row, 1, bounds, t -> fillAndStrokeShape(g2, areaSubtract, null, OUTLINE, BLUE));
        drawTile(g2, row, 2, bounds, t -> fillAndStrokeShape(g2, areaSubtract, LIGHT_GRAY, OUTLINE, BLUE));
        drawTile(g2, row, 3, bounds, t -> fillAndStrokeShape(g2, areaSubtract, LIGHT_GRAY, DASHED, BLACK));
        drawTile(g2, row, 4, bounds, t -> fillAndStrokeShape(g2, areaSubtract, LIGHT_GRAY, DASHED_3, BLACK));
        drawTile(g2, row, 5, bounds, t -> fillAndStrokeShape(g2, areaSubtract, LIGHT_GRAY, OUTLINE_3, BLACK));

        row++; // *** Area - XOR
        Area areaXOR = createCombinedArea("exclusiveOr", bounds, MARGIN);
        drawTile(g2, row, 0, bounds, t -> fillAndStrokeShape(g2, areaXOR, BLUE, null, null));
        drawTile(g2, row, 1, bounds, t -> fillAndStrokeShape(g2, areaXOR, null, OUTLINE, BLUE));
        drawTile(g2, row, 2, bounds, t -> fillAndStrokeShape(g2, areaXOR, LIGHT_GRAY, OUTLINE, BLUE));
        drawTile(g2, row, 3, bounds, t -> fillAndStrokeShape(g2, areaXOR, LIGHT_GRAY, DASHED, BLACK));
        drawTile(g2, row, 4, bounds, t -> fillAndStrokeShape(g2, areaXOR, LIGHT_GRAY, DASHED_3, BLACK));
        drawTile(g2, row, 5, bounds, t -> fillAndStrokeShape(g2, areaXOR, LIGHT_GRAY, OUTLINE_3, BLACK));

        row++;  // *** AlphaComposite variations
        drawTile(g2, row, 0, bounds, t -> drawShapesWithAlphaComposite(g2, AlphaComposite.Clear, t, MARGIN));
        drawTile(g2, row, 1, bounds, t -> drawShapesWithAlphaComposite(g2, AlphaComposite.Src, t, MARGIN));
        drawTile(g2, row, 2, bounds, t -> drawShapesWithAlphaComposite(g2, AlphaComposite.SrcOver, t, MARGIN));
        drawTile(g2, row, 3, bounds, t -> drawShapesWithAlphaComposite(g2, AlphaComposite.DstOver, t, MARGIN));
        drawTile(g2, row, 4, bounds, t -> drawShapesWithAlphaComposite(g2, AlphaComposite.SrcIn, t, MARGIN));
        drawTile(g2, row, 5, bounds, t -> drawShapesWithAlphaComposite(g2, AlphaComposite.DstIn, t, MARGIN));
        drawTile(g2, row, 6, bounds, t -> drawShapesWithAlphaComposite(g2, AlphaComposite.SrcOut, t, MARGIN));
        drawTile(g2, row, 7, bounds, t -> drawShapesWithAlphaComposite(g2, AlphaComposite.DstOut, t, MARGIN));
        drawTile(g2, row, 8, bounds, t -> drawShapesWithAlphaComposite(g2, AlphaComposite.Dst, t, MARGIN));
        drawTile(g2, row, 9, bounds, t -> drawShapesWithAlphaComposite(g2, AlphaComposite.SrcAtop, t, MARGIN));
        drawTile(g2, row, 10, bounds, t -> drawShapesWithAlphaComposite(g2, AlphaComposite.DstAtop, t, MARGIN));

        row++;  // ***** AlphaComposite variations 2
        drawTile(g2, row, 0, bounds, t -> drawShapesWithAlphaComposite(g2, AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.6f), t, MARGIN));
        drawTile(g2, row, 1, bounds, t -> drawShapesWithAlphaComposite(g2, AlphaComposite.getInstance(AlphaComposite.SRC, 0.6f), t, MARGIN));
        drawTile(g2, row, 2, bounds, t -> drawShapesWithAlphaComposite(g2, AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f), t, MARGIN));
        drawTile(g2, row, 3, bounds, t -> drawShapesWithAlphaComposite(g2, AlphaComposite.getInstance(AlphaComposite.DST_OVER, 0.6f), t, MARGIN));
        drawTile(g2, row, 4, bounds, t -> drawShapesWithAlphaComposite(g2, AlphaComposite.getInstance(AlphaComposite.SRC_IN, 0.6f), t, MARGIN));
        drawTile(g2, row, 5, bounds, t -> drawShapesWithAlphaComposite(g2, AlphaComposite.getInstance(AlphaComposite.DST_IN, 0.6f), t, MARGIN));
        drawTile(g2, row, 6, bounds, t -> drawShapesWithAlphaComposite(g2, AlphaComposite.getInstance(AlphaComposite.SRC_OUT, 0.6f), t, MARGIN));
        drawTile(g2, row, 7, bounds, t -> drawShapesWithAlphaComposite(g2, AlphaComposite.getInstance(AlphaComposite.DST_OUT, 0.6f), t, MARGIN));
        drawTile(g2, row, 8, bounds, t -> drawShapesWithAlphaComposite(g2, AlphaComposite.getInstance(AlphaComposite.DST, 0.6f), t, MARGIN));
        drawTile(g2, row, 9, bounds, t -> drawShapesWithAlphaComposite(g2, AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.6f), t, MARGIN));
        drawTile(g2, row, 10, bounds, t -> drawShapesWithAlphaComposite(g2, AlphaComposite.getInstance(AlphaComposite.DST_ATOP, 0.6f), t, MARGIN));
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

        row++;  // *** GradientPaint
        GradientPaint gp = new GradientPaint(0f, 0f, YELLOW, TILE_WIDTH, 0f, RED);
        drawTile(g2, row, 0, bounds, t -> fillAndStrokeShape(g2, roundRect, gp, null, null));

        // here we change the gradient to start one quarter of the way across the shape
        // and finish at the three-quarter mark - the default should be non-cyclic
        float p = TILE_WIDTH / 4.0f;
        GradientPaint gp2 = new GradientPaint(p, 0f, YELLOW, p * 3, 0f, RED);
        drawTile(g2, row, 1, bounds, t -> fillAndStrokeShape(g2, roundRect, gp2, null, null));

        GradientPaint gp3 = new GradientPaint(p, 0f, YELLOW, p * 3, 0f, RED, true);
        drawTile(g2, row, 2, bounds, t -> fillAndStrokeShape(g2, roundRect, gp3, null, null));

        LinearGradientPaint lgp1 = new LinearGradientPaint(10f, 0f, TILE_WIDTH - 10, 0f, new float[]{0f, 1 / 6f, 2 / 6f, 3 / 6f, 4 / 6f, 5 / 6f, 1f}, RAINBOW_COLORS);
        drawTile(g2, row, 3, bounds, t -> fillAndStrokeShape(g2, roundRect, lgp1, null, null));

        GradientPaint gp4 = new GradientPaint(0f, 0f, YELLOW, TILE_WIDTH, TILE_HEIGHT, RED);
        drawTile(g2, row, 4, bounds, t -> fillAndStrokeShape(g2, roundRect, gp4, null, null));

        // here we change the gradient to start one quarter of the way across the shape
        // and finish at the three-quarter mark - the default should be non-cyclic
        GradientPaint gp5 = new GradientPaint(p, 0f, YELLOW, p * 3, TILE_HEIGHT, RED);
        drawTile(g2, row, 5, bounds, t -> fillAndStrokeShape(g2, roundRect, gp5, null, null));

        GradientPaint gp6 = new GradientPaint(p, 0f, YELLOW, p * 3, TILE_HEIGHT, RED, true);
        drawTile(g2, row, 6, bounds, t -> fillAndStrokeShape(g2, roundRect, gp6, null, null));

        LinearGradientPaint lgp2 = new LinearGradientPaint(10f, 0f, TILE_WIDTH - 10, TILE_HEIGHT, new float[]{0f, 1 / 6f, 2 / 6f, 3 / 6f, 4 / 6f, 5 / 6f, 1f}, RAINBOW_COLORS);
        drawTile(g2, row, 7, bounds, t -> fillAndStrokeShape(g2, roundRect, lgp2, null, null));

        row++;  // *** Lines with GradientPaint variations
        drawTile(g2, row, 0, bounds, t -> drawLineCaps(g2, t, MARGIN, 5.0f, gp));
        drawTile(g2, row, 1, bounds, t -> drawLineCaps(g2, t, MARGIN, 5.0f, gp2));
        drawTile(g2, row, 2, bounds, t -> drawLineCaps(g2, t, MARGIN, 5.0f, gp3));
        drawTile(g2, row, 3, bounds, t -> drawLineCaps(g2, t, MARGIN, 5.0f, lgp1));
        drawTile(g2, row, 4, bounds, t -> drawLineCaps(g2, t, MARGIN, 5.0f, gp4));
        drawTile(g2, row, 5, bounds, t -> drawLineCaps(g2, t, MARGIN, 5.0f, gp5));
        drawTile(g2, row, 6, bounds, t -> drawLineCaps(g2, t, MARGIN, 5.0f, gp6));
        drawTile(g2, row, 7, bounds, t -> drawLineCaps(g2, t, MARGIN, 5.0f, lgp2));

        row++;  // *** RadialGradientPaint
        Point2D center = new Point2D.Double(TILE_WIDTH / 2.0, TILE_HEIGHT / 2.0);
        RadialGradientPaint rgp = new RadialGradientPaint(center, (float) (TILE_HEIGHT / 2.0 - 5), new float[]{0.0f, 0.75f, 1.0f}, new Color[]{YELLOW, RED, LIGHT_GRAY});
        drawTile(g2, row, 0, bounds, t -> fillAndStrokeShape(g2, roundRect, rgp, null, null));

        RadialGradientPaint rgp2 = new RadialGradientPaint(center, (float) (TILE_HEIGHT / 2.0 - 5), new float[]{0.0f, 0.75f, 1.0f}, new Color[]{YELLOW, RED, LIGHT_GRAY}, MultipleGradientPaint.CycleMethod.REPEAT);
        drawTile(g2, row, 1, bounds, t -> fillAndStrokeShape(g2, roundRect, rgp2, null, null));

        RadialGradientPaint rgp3 = new RadialGradientPaint(center, (float) (TILE_HEIGHT / 2.0 - 5), new float[]{0.0f, 0.75f, 1.0f}, new Color[]{YELLOW, RED, LIGHT_GRAY}, MultipleGradientPaint.CycleMethod.REFLECT);
        drawTile(g2, row, 2, bounds, t -> fillAndStrokeShape(g2, roundRect, rgp3, null, null));

        Point2D focus = new Point2D.Double(TILE_WIDTH / 3.0, TILE_HEIGHT / 3.0);
        RadialGradientPaint rgp4 = new RadialGradientPaint(center, (float) (TILE_HEIGHT / 2.0 - 5), focus, new float[]{0.0f, 0.75f, 1.0f}, new Color[]{YELLOW, RED, LIGHT_GRAY}, MultipleGradientPaint.CycleMethod.NO_CYCLE);
        drawTile(g2, row, 3, bounds, t -> fillAndStrokeShape(g2, roundRect, rgp4, null, null));

        RadialGradientPaint rgp5 = new RadialGradientPaint(center, (float) (TILE_HEIGHT / 2.0 - 5), focus, new float[]{0.0f, 0.75f, 1.0f}, new Color[]{YELLOW, RED, LIGHT_GRAY}, MultipleGradientPaint.CycleMethod.REPEAT);
        drawTile(g2, row, 4, bounds, t -> fillAndStrokeShape(g2, roundRect, rgp5, null, null));

        RadialGradientPaint rgp6 = new RadialGradientPaint(center, (float) (TILE_HEIGHT / 2.0 - 5), focus, new float[]{0.0f, 0.75f, 1.0f}, new Color[]{YELLOW, RED, LIGHT_GRAY}, MultipleGradientPaint.CycleMethod.REFLECT);
        drawTile(g2, row, 5, bounds, t -> fillAndStrokeShape(g2, roundRect, rgp6, null, null));

        row++;  // *** TexturePaint
        int w = 5;
        int h = 3;
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D tpg = image.createGraphics();
        tpg.setColor(YELLOW);
        tpg.drawLine(0, 0, w - 1, 0);
        tpg.drawLine(0, 0, 0, h - 1);
        tpg.setColor(RED);
        tpg.drawLine(1, h - 1, w - 1, h - 1);
        tpg.drawLine(w - 1, 1, w - 1, h - 1);

        final Paint tp = new TexturePaint(image, new Rectangle(5, 5, w, h));
        drawTile(g2, row, 0, bounds, t -> fillAndStrokeShape(g2, rect, tp, null, null));
        drawTile(g2, row, 1, bounds, t -> fillAndStrokeShape(g2, roundRect, tp, null, null));
        drawTile(g2, row, 2, bounds, t -> fillAndStrokeShape(g2, ellipse, tp, new BasicStroke(1.0f), BLACK));

        final Paint tp2 = new TexturePaint(image, new Rectangle(0, 0, w * 2, h * 2));
        drawTile(g2, row, 3, bounds, t -> fillAndStrokeShape(g2, rect, tp2, null, null));
        drawTile(g2, row, 4, bounds, t -> fillAndStrokeShape(g2, roundRect, tp, null, null));
        drawTile(g2, row, 5, bounds, t -> fillAndStrokeShape(g2, ellipse, tp, new BasicStroke(2.0f), BLACK));

        row++; // ***** TRANSLATION
        Rectangle2D quarterBounds = new Rectangle2D.Double(0.0, 0.0, TILE_WIDTH / 2.0, TILE_HEIGHT / 2.0);
        drawTile(g2, row, 0, bounds, t -> {
            Rectangle2D rectToTranslate = new Rectangle2D.Double(MARGIN, MARGIN, TILE_WIDTH / 2.0 - MARGIN * 2, TILE_HEIGHT / 2.0 - MARGIN * 2.0);
            translateShape(g2, t, rectToTranslate, YELLOW, OUTLINE, BLACK);
        });
        drawTile(g2, row, 1, bounds, t -> {
            RoundRectangle2D roundRectToTranslate = new RoundRectangle2D.Double(5, 5, TILE_WIDTH / 2.0 - 10, TILE_HEIGHT / 2.0 - 10, 8, 8);
            translateShape(g2, t, roundRectToTranslate, ORANGE, OUTLINE, BLACK);
        });
        drawTile(g2, row, 2, bounds, t -> {
            QuadCurve2D quadCurveToTranslate = createQuadCurve2D1(quarterBounds, 3);
            translateShape(g2, t, quadCurveToTranslate, YELLOW, OUTLINE, BLACK);
        });
        drawTile(g2, row, 3, bounds, t -> {
            CubicCurve2D cubicCurveToTranslate = createCubicCurve2D(quarterBounds, 3);
            translateShape(g2, t, cubicCurveToTranslate, ORANGE, OUTLINE, BLACK);
        });
        drawTile(g2, row, 4, bounds, t -> {
            Ellipse2D ellipseToTranslate = new Ellipse2D.Double(MARGIN, MARGIN, TILE_WIDTH / 2.0 - MARGIN * 2, TILE_HEIGHT / 2.0 - MARGIN * 2);
            translateShape(g2, t, ellipseToTranslate, YELLOW, OUTLINE, BLACK);
        });
        drawTile(g2, row, 5, bounds, t -> {
            Arc2D arcToTranslate = new Arc2D.Double(new Rectangle2D.Double(MARGIN, MARGIN, TILE_WIDTH / 2.0 - MARGIN * 2, TILE_HEIGHT / 2.0 - MARGIN * 2), 45, 290, Arc2D.PIE);
            translateShape(g2, t, arcToTranslate, ORANGE, OUTLINE, BLACK);
        });
        drawTile(g2, row, 6, bounds, t -> {
            Area areaToTranslate = createCombinedArea("exclusiveOr", new Rectangle2D.Double(0.0, 0.0, TILE_WIDTH / 2.0, TILE_HEIGHT / 2.0), 2.5);
            translateShape(g2, bounds, areaToTranslate, YELLOW, OUTLINE, BLACK);
        });
        drawTile(g2, row, 7, bounds, t -> {
            Path2D pathToTranslate = createPath2D(quarterBounds, 2.5);
            translateShape(g2, bounds, pathToTranslate, ORANGE, OUTLINE, BLACK);
        });

        row++; // ***** ROTATION
        double m = 0.33 * TILE_HEIGHT;
        Rectangle2D rectToRotate = new Rectangle2D.Double(m, m, TILE_WIDTH - m * 2, TILE_HEIGHT - m * 2);
        drawTile(g2, row, 0, bounds, t -> rotateShape(g2, t, rectToRotate, Math.PI / 4, BLUE, OUTLINE, BLACK));
        RoundRectangle2D roundRectToRotate = new RoundRectangle2D.Double(m, m, TILE_WIDTH - m * 2, TILE_HEIGHT - m * 2, 8, 8);
        drawTile(g2, row, 1, bounds, t -> rotateShape(g2, t, roundRectToRotate, Math.PI / 4, BLUE, OUTLINE, BLACK));
        QuadCurve2D quadCurveToRotate = ShapeTests.createQuadCurve2D2(bounds, 15);
        drawTile(g2, row, 2, bounds, t -> rotateShape(g2, t, quadCurveToRotate, Math.PI / 4, BLUE, OUTLINE, BLACK));
        CubicCurve2D cubicCurveToRotate = createCubicCurve2D(bounds, 15);
        drawTile(g2, row, 3, bounds, t -> rotateShape(g2, t, cubicCurveToRotate, Math.PI / 4, BLUE, OUTLINE, BLACK));
        Ellipse2D ellipseToRotate = new Ellipse2D.Double(m, m, TILE_WIDTH - m * 2, TILE_HEIGHT - m * 2);
        drawTile(g2, row, 4, bounds, t -> rotateShape(g2, t, ellipseToRotate, Math.PI / 4, BLUE, OUTLINE, BLACK));
        Arc2D arcToRotate = new Arc2D.Double(new Rectangle2D.Double(m, m, TILE_WIDTH - m * 2, TILE_HEIGHT - m * 2), 45, 290, Arc2D.PIE);
        drawTile(g2, row, 5, bounds, t -> rotateShape(g2, t, arcToRotate, Math.PI / 4, BLUE, OUTLINE, BLACK));
        Area areaToRotate = createCombinedArea("add", bounds, m);
        drawTile(g2, row, 6, bounds, t -> rotateShape(g2, t, areaToRotate, Math.PI / 4, BLUE, OUTLINE, BLACK));
        double mmm = 0.50;
        Path2D pathToRotate = createPath2D(new Rectangle2D.Double(TILE_WIDTH * (mmm / 2.0), TILE_HEIGHT * (mmm / 2.0), TILE_WIDTH * (1 - mmm), TILE_HEIGHT * (1 - mmm)), 0.0);
        drawTile(g2, row, 7, bounds, t -> rotateShape(g2, t, pathToRotate, Math.PI / 4, BLUE, OUTLINE, BLACK));

        row++; // *** SHEAR X & Y
        double shx = -2.0;
        double shy = -0.5;
        double mm = 0.33 * TILE_HEIGHT;

        Rectangle2D rectToSkew = new Rectangle2D.Double(m, m, TILE_WIDTH - mm * 2, TILE_HEIGHT - mm * 2);
        drawTile(g2, row, 0, bounds, t -> shearShape(g2, t, rectToSkew, 0.0, shy, BLUE, OUTLINE, BLACK));
        drawTile(g2, row + 1, 0, bounds, t -> shearShape(g2, t, rectToSkew, shx, 0.0, BLUE, OUTLINE, BLACK));

        RoundRectangle2D roundRectToShear = new RoundRectangle2D.Double(m, m, TILE_WIDTH - m * 2, TILE_HEIGHT - m * 2, 8, 8);
        drawTile(g2, row, 1, bounds, t -> shearShape(g2, t, roundRectToShear, 0.0, shy, BLUE, OUTLINE, BLACK));
        drawTile(g2, row + 1, 1, bounds, t -> shearShape(g2, t, roundRectToShear, shx, 0.0, BLUE, OUTLINE, BLACK));

        QuadCurve2D quadCurveToShear = ShapeTests.createQuadCurve2D2(bounds, 15);
        drawTile(g2, row, 2, bounds, t -> shearShape(g2, t, quadCurveToShear, 0.0, shy, BLUE, OUTLINE, BLACK));
        drawTile(g2, row + 1, 2, bounds, t -> shearShape(g2, t, quadCurveToShear, shx, 0.0, BLUE, OUTLINE, BLACK));

        CubicCurve2D cubicCurveToShear = createCubicCurve2D(new Rectangle2D.Double(0, 0, TILE_WIDTH, TILE_HEIGHT), 15);
        drawTile(g2, row, 3, bounds, t -> shearShape(g2, t, cubicCurveToShear, 0.0, shy, BLUE, OUTLINE, BLACK));
        drawTile(g2, row + 1, 3, bounds, t -> shearShape(g2, t, cubicCurveToShear, shx, 0.0, BLUE, OUTLINE, BLACK));

        Ellipse2D ellipseToSkew = new Ellipse2D.Double(m, m, TILE_WIDTH - m * 2, TILE_HEIGHT - m * 2);
        drawTile(g2, row, 4, bounds, t -> shearShape(g2, t, ellipseToSkew, 0.0, shy, BLUE, OUTLINE, BLACK));
        drawTile(g2, row + 1, 4, bounds, t -> shearShape(g2, t, ellipseToSkew, shx, 0.0, BLUE, OUTLINE, BLACK));

        Arc2D arcToShear = new Arc2D.Double(new Rectangle2D.Double(m, m, TILE_WIDTH - m * 2, TILE_HEIGHT - m * 2), 45, 290, Arc2D.PIE);
        drawTile(g2, row, 5, bounds, t -> shearShape(g2, t, arcToShear, 0.0, shy, BLUE, OUTLINE, BLACK));
        drawTile(g2, row + 1, 5, bounds, t -> shearShape(g2, t, arcToShear, shx, 0.0, BLUE, OUTLINE, BLACK));

        Area areaToShear = createCombinedArea("add", bounds, m);
        drawTile(g2, row, 6, bounds, t -> shearShape(g2, t, areaToShear, 0.0, shy, BLUE, OUTLINE, BLACK));
        drawTile(g2, row + 1, 6, bounds, t -> shearShape(g2, t, areaToShear, shx, 0.0, BLUE, OUTLINE, BLACK));

        Path2D pathToShear = createPath2D(new Rectangle2D.Double(TILE_WIDTH * (mmm / 2.0), TILE_HEIGHT * (mmm / 2.0), TILE_WIDTH * (1 - mmm), TILE_HEIGHT * (1 - mmm)), 0.0);
        drawTile(g2, row, 7, bounds, t -> shearShape(g2, t, pathToShear, 0.0, shy, BLUE, OUTLINE, BLACK));
        drawTile(g2, row + 1, 7, bounds, t -> shearShape(g2, t, pathToShear, shx, 0.0, BLUE, OUTLINE, BLACK));

        row++; // to cover the shearing above taking up two rows

        row++;  // drawArc() / fillArc()
        final int xx = MARGIN;
        final int yy = MARGIN;
        final int ww = TILE_WIDTH - MARGIN * 2;
        final int hh = TILE_HEIGHT - MARGIN * 2;
        g2.setColor(BLUE);
        drawTile(g2, row, 0, bounds, t -> g2.fillArc(xx, yy, ww, hh, 45, 270));
        drawTile(g2, row, 1, bounds, t -> g2.fillArc(xx, yy, ww, hh, 90, 180));
        drawTile(g2, row, 2, bounds, t -> g2.fillArc(xx, yy, ww, hh, 135, 90));

        g2.setColor(RED);
        drawTile(g2, row, 3, bounds, t -> g2.drawArc(xx, yy, ww, hh, 45, 270));
        drawTile(g2, row, 4, bounds, t -> g2.drawArc(xx, yy, ww, hh, 90, 180));
        drawTile(g2, row, 5, bounds, t -> g2.drawArc(xx, yy, ww, hh, 135, 90));

        row++;  // ** Strings & Fonts
        Rectangle2D bounds2 = new Rectangle2D.Double(0.0, 0.0, TILE_WIDTH * 2, TILE_HEIGHT);
        drawTile(g2, row, 0, bounds2, t -> drawString(g2, t));
        drawTile(g2, row, 2, bounds2, t -> drawAttributedString(g2, t));
        drawTile(g2, row, 4, bounds2, t -> drawAttributedString2(g2, t));
        drawTile(g2, row, 6, bounds2, t -> drawUnicodeCharacters(g2, t));

        row++;  // *** More Strings & Fonts
        drawTile(g2, row, 0, bounds2, t -> drawAttributedStringWithKerning(g2, t));
        drawTile(g2, row, 2, bounds2, t -> drawAttributedStringWithLigatures(g2, t));
        drawTile(g2, row, 4, bounds2, t -> drawTextWithTracking(g2, t));

        row++;
        drawTile(g2, row, 0, bounds2, t -> drawStringBounds(g2, t));
        drawTile(g2, row, 2, bounds2, t -> drawTextMetrics(g2, t));
        drawTile(g2, row, 4, bounds, t -> fillRectangularClippingRegions(g2, t));
        drawTile(g2, row, 5, bounds, t -> drawArc2DWithRectangularClip(g2, t, MARGIN));

        row++;  // *** IMAGE
        Rectangle2D imageBounds = new Rectangle2D.Double(0, 0, TILE_WIDTH * 3, TILE_WIDTH * 2);
        drawTile(g2, row, 0, imageBounds, t -> drawImage(tc, g2, t, MARGIN));

        if (DO_CLIP) {
            Shape savedClip = g2.getClip();
            drawTile(g2, row, 4, imageBounds, t -> {
                g2.clip(new Ellipse2D.Double(15, 15, TILE_WIDTH * 3 - 30, TILE_WIDTH * 2 - 30));
                drawImage(tc, g2, t, MARGIN);
            });
            g2.setClip(savedClip);

            AffineTransform savedTransform = g2.getTransform();
            savedClip = g2.getClip();
            drawTile(g2, row, 8, imageBounds, t -> {
                g2.clip(imageBounds);
                g2.translate(TILE_WIDTH * 1.5, TILE_WIDTH);
                g2.rotate(Math.PI / 4);
                g2.translate(-TILE_WIDTH * 1.5, -TILE_WIDTH);
                drawImage(tc, g2, t, MARGIN);
            });
            g2.setClip(savedClip);
            g2.setTransform(savedTransform);
        }

        drawTile(g2, 20, TILE_COUNT_H - 2, bounds, t -> drawSwingUI(tc.frame, g2));
    }

    /**
     * Writes out the relevant properties for the test.
     *
     * @param g2  the graphics target.
     * @param g2Implementation  a description of the Graphics2D implementation under test.
     */
    private static void drawTestProperties(Graphics2D g2, String g2Implementation) {
        g2.setPaint(BLACK);
        g2.setFont(new Font("Courier New", Font.PLAIN, 14));
        int y = 20;
        g2.drawString("target -> " + g2Implementation, 10, y += 16);
        g2.drawString("timestamp -> " + LocalDateTime.now(), 10, y += 16);
        g2.drawString("os.name -> " + System.getProperty("os.name"), 10, y += 16);
        g2.drawString("os.version -> " + System.getProperty("os.version"), 10, y += 16);
        g2.drawString("os.arch -> " + System.getProperty("os.arch"), 10, y += 16);
        g2.drawString("java.runtime.version -> " + System.getProperty("java.runtime.version"), 10, y += 16);
        g2.drawString("java.vm.name -> " + System.getProperty("java.vm.name"), 10, y += 16);
        g2.drawString("java.vendor.version -> " + System.getProperty("java.vendor.version"), 10, y += 16);
    }

    private static void prepareTestSingle(final TesterContext tc) {
        prepareSwingUI(tc, new Rectangle2D.Double(0, 0, TILE_WIDTH * 4, TILE_HEIGHT * 4));
    }

    /**
     * Draws a single tile - useful for testing just one feature.
     *
     * @param tc  the tester context.
     * @param g2  the graphics target.
     */
    private static void drawTestSingle(final TesterContext tc, Graphics2D g2) {
        moveTo(0, 0, g2);
        drawSwingUI(tc.frame, g2); // change this line to call the tile function under test
    }

    /**
     * Renders the test output (checks whether generating the whole test
     * sheet or just one single test).
     *
     * @param g2UnderTest  a description of the Graphics2D implementation under test.
     * @param qrLink  the link text to put in the QR code
     * @param single  set to true if just generating a single test
     * @return TesterContext instance
     */
    public static TesterContext prepareTestOutput(final String g2UnderTest, final String qrLink, final boolean single) {
        final TesterContext tc = new TesterContext(g2UnderTest, qrLink, single);
        if (tc.single) {
            prepareTestSingle(tc);
        } else {
            prepareTestSheet(tc);
        }
        System.out.println("DO_CLIP: " + DO_CLIP);
        return tc;
    }

    /**
     * Renders the test output (checks whether generating the whole test
     * sheet or just one single test).
     *
     * @param tc  the tester context.
     * @param g2  the graphics target.
     */
    public static void drawTestOutput(final TesterContext tc, final Graphics2D g2) {
        if (tc.single) {
            drawTestSingle(tc, g2);
        } else {
            drawTestSheet(tc, g2);
        }
    }

    /**
     * Run the tests with a Graphics2D from a Java2D BufferedImage and save
     * the results to the specified file.
     *
     * @param fileName  the PNG file name.
     * @param single  set to true if just generating a single test
     *
     * @throws IOException if there is an I/O problem.
     */
    private static void testJava2D(String fileName, boolean single) throws IOException {
        if (single) {
            fileName += "-single.png";
        } else {
            fileName += ".png";
        }
        // Prepare context:
        final TesterContext tc = prepareTestOutput(
                "Java2D/BufferedImage",
                "https://github.com/jfree", single);

        final int width = Tester.getTestSheetWidth();
        final int height = Tester.getTestSheetHeight();

        final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int i = 0; i < REPEATS; i++) {
            final Graphics2D g2 = image.createGraphics();
            g2.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_OFF);
            g2.setBackground(WHITE);
            g2.clearRect(0, 0, width, height);

            g2.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
            final long startTime = System.nanoTime();

            try {
                drawTestOutput(tc, g2);

                // Sync CPU / GPU:
                Toolkit.getDefaultToolkit().sync();
                // image is ready

                final double elapsedTime = 1e-6d * (System.nanoTime() - startTime);
                System.out.println("drawTestOutput(Java2D) duration = " + elapsedTime + " ms.");

                if (i == 0) {
                    try {
                        ImageIO.write(image, "png", new File(fileName));
                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                        e.printStackTrace(System.err);
                    }
                }
            } finally {
                g2.dispose();
            }
        }
    }

    private static JComponent createContent() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // just take the default look and feel
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
        }

        JPanel content = new JPanel(new BorderLayout());
        JTabbedPane tabs = new JTabbedPane();
        final Radial gauge = new Radial();
        gauge.setTitle("SteelSeries");
        gauge.setUnitString("Units");
        gauge.setDigitalFont(true);
        gauge.setValue(45.0);
        gauge.setPreferredSize(new Dimension(300, 200));
        JPanel panel1 = new JPanel(new BorderLayout());
        panel1.add(gauge, BorderLayout.CENTER);
        //panel1.add(new JButton("Click ME!"), BorderLayout.CENTER);
        tabs.add("Tab 1", panel1);
        tabs.add("Tab 2", new JButton("Second Tab"));
        JButton button = new JButton("OK");
        content.add(tabs);
        content.add(button, BorderLayout.SOUTH);
        return content;
    }

    /**
     * Returns the width of the test sheet in Java2D units.
     *
     * @return The width of the test sheet in Java2D units.
     */
    public static int getTestSheetWidth() {
        return TILE_WIDTH * TILE_COUNT_H;
    }

    /**
     * Returns the height of the test sheet in Java2D units.
     *
     * @return The height of the test sheet in Java2D units.
     */
    public static int getTestSheetHeight() {
        return TILE_HEIGHT * TILE_COUNT_V;
    }

    private Tester() {
        // no-op
    }

    /**
     * Creates Java2D output that exercises many features of the API.
     *
     * @param args  ignored
     */
    public static void main(String[] args) throws IOException {
        // ensure no hi-dpi to ensure scale = 1.0:
        System.out.println("Use 'java -Dsun.java2d.uiScale=1.0 ...' ");

        boolean single = false;
        testJava2D("java2D", single);
        System.exit(0);
    }

    /**
     * Basic container class to hold the test preparation state
     */
    public final static class TesterContext {

        // test case:
        final String g2UnderTest;
        String qrLink;
        boolean single;

        // preparation state
        JFrame frame;
        BufferedImage qrCodeImage;
        BufferedImage TRIUMPH_IMAGE;
        JFreeChart jfreeChart;
        Chart3D orsonChart;

        TesterContext(final String g2UnderTest, final String qrLink, final boolean single) {
            this.g2UnderTest = g2UnderTest;
            this.qrLink = qrLink;
            this.single = single;
        }
    }
}