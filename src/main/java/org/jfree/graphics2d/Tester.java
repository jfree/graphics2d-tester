/**
 * Graphics2D Tester
 *
 * (C)opyright 2021, David Gilbert.
 */
package org.jfree.graphics2d;

import eu.hansolo.steelseries.gauges.Radial;
import org.jetbrains.skija.Data;
import org.jetbrains.skija.EncodedImageFormat;
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
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.jfree.graphics2d.svg.SVGUtils;
import org.jfree.skija.SkijaGraphics2D;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.font.LineMetrics;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Runs a visual testing setup against various Graphics2D implementations.  The idea
 * is that you can compare the output of a custom Graphics2D implementation against
 * the reference implementation (Java2D).
 */
public class Tester {

    private static int TILE_COUNT_H = 11;

    private static int TILE_COUNT_V = 32;

    private static int TILE_WIDTH = 100;

    private static int TILE_HEIGHT = 65;

    private static int MARGIN = 5;

    private static Stroke OUTLINE = new BasicStroke(1.0f);
    private static Stroke OUTLINE_3 = new BasicStroke(3.0f);

    /** A dashed line stroke. */
    private static Stroke DASHED = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_ROUND, 4f, new float[] { 2f, 2f }, 0f);

    /** A dashed line stroke with width 3. */
    private static Stroke DASHED_3 = new BasicStroke(3.0f, BasicStroke.CAP_ROUND,
            BasicStroke.JOIN_ROUND, 4f, new float[] { 4f, 8f }, 0f);

    /** An array containing rainbow colors, used in the gradient paint tests. */
    private static Color[] RAINBOW_COLORS = new Color[] {
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

    private static void drawSwingUI(Graphics2D g2, Rectangle2D bounds) {
        JComponent content = createContent();
        JFrame frame = new JFrame("Title");
        frame.getContentPane().add(content);
        frame.setBounds((int) bounds.getX(), (int) bounds.getY(), (int) bounds.getWidth(), (int) bounds.getHeight());
        frame.pack();
        content.paint(g2);
    }

    private static void drawOrsonChartSample(Graphics2D g2, Rectangle2D bounds) {
        Function3D function = (double x, double z) -> Math.cos(x) * Math.sin(z);

        Chart3D chart = Chart3DFactory.createSurfaceChart(
                "SurfaceRendererDemo1",
                "y = cos(x) * sin(z)",
                function, "X", "Y", "Z");

        XYZPlot plot = (XYZPlot) chart.getPlot();
        plot.setDimensions(new Dimension3D(10, 5, 10));
        ValueAxis3D xAxis = plot.getXAxis();
        xAxis.setRange(-Math.PI, Math.PI);
        ValueAxis3D zAxis = plot.getZAxis();
        zAxis.setRange(-Math.PI, Math.PI);
        SurfaceRenderer renderer = (SurfaceRenderer) plot.getRenderer();
        renderer.setDrawFaceOutlines(false);
        renderer.setColorScale(new GradientColorScale(new Range(-1.0, 1.0),
                Color.RED, Color.YELLOW));
        chart.setViewPoint(ViewPoint3D.createAboveLeftViewPoint(70.0));
        chart.draw(g2, bounds);
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
     * Creates a JFreeChart sample chart.
     *
     * @param dataset  the dataset.
     *
     * @return A sample chart.
     */
    private static JFreeChart createChart(FlowDataset dataset) {
        FlowPlot plot = new FlowPlot(dataset);
        plot.setBackgroundPaint(Color.BLACK);
        plot.setDefaultNodeLabelPaint(Color.WHITE);
        plot.setNodeColorSwatch(createPastelColors());
        JFreeChart chart = new JFreeChart("Selected NZ Exports Sept 2020", plot);
        chart.addSubtitle(new TextTitle("Source: https://statisticsnz.shinyapps.io/trade_dashboard/"));
        chart.setBackgroundPaint(Color.WHITE);
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

    private static void drawJFreeChartSample(Graphics2D g2, Rectangle2D bounds) {
        JFreeChart chart = createChart(createDataset());
        chart.draw(g2, bounds);
    }

    /**
     * Draws a test sheet consisting of a number of tiles, each one testing one or
     * more features of Java2D and Graphics2D.
     *
     * @param g2  the graphics target.
     * @param qrLink  the link text to put in the QR code
     */
    private static void drawTestSheet(Graphics2D g2, String g2UnderTest, String qrLink) {
        int row = -1;
        Rectangle2D bounds = new Rectangle2D.Double(0.0, 0.0, TILE_WIDTH, TILE_HEIGHT);

        row++; // ***** HEADER
        moveTo(0, row, g2);
        g2.setPaint(Color.WHITE);
        g2.fill(new Rectangle2D.Double(2, 2, TILE_WIDTH * TILE_COUNT_H, TILE_HEIGHT - 2));
        g2.setPaint(Color.BLACK);
        g2.setStroke(new BasicStroke(2.0f));
        g2.drawLine(0, 1, TILE_WIDTH * TILE_COUNT_H, 1);
        g2.drawLine(0, TILE_HEIGHT, TILE_WIDTH * TILE_COUNT_H, TILE_HEIGHT);

        String str = "Graphics2D Tester";
        g2.setFont(new Font(Font.SERIF, Font.BOLD, 32));
        FontMetrics fm = g2.getFontMetrics();
        Rectangle2D strBounds = fm.getStringBounds(str, g2);
        LineMetrics lm = fm.getLineMetrics(str, g2);
        float x = 5f;
        float y = (float) (bounds.getCenterY() + (lm.getAscent() / 2));
        g2.drawString(str, x, y);

        row ++;
        moveTo(7, row, g2);
        drawTestProperties(g2, g2UnderTest);
        row--;

        // QR CODE AT RIGHT SIDE
        row += 4;
        moveTo(TILE_COUNT_H - 4, row, g2);
        try {
            ImageTests.drawQRCodeImage(g2, new Rectangle2D.Double(0, 0, TILE_WIDTH * 2, TILE_HEIGHT * 2), 5, qrLink);
        } catch (Exception e) {
            e.printStackTrace();
        }
        row -= 4;

        // JFREECHART AT RIGHT SIDE
        row += 7;
        moveTo(TILE_COUNT_H - 4, row, g2);
        drawJFreeChartSample(g2, new Rectangle2D.Double(0, 0, TILE_WIDTH * 4, TILE_HEIGHT * 4));
        row -= 7;

        // ORSON CHARTS AT RIGHT SIDE
        row += 12;
        moveTo(TILE_COUNT_H - 4, row, g2);
        drawOrsonChartSample(g2, new Rectangle2D.Double(0, 0, TILE_WIDTH * 4, TILE_HEIGHT * 4));
        row -= 12;

        row++;  // ***** LINES SPECIAL
        moveTo(0, row, g2);
        ShapeTests.drawLineCaps(g2, bounds, 5.0,0.0f, Color.RED);
        moveTo(1, row, g2);
        ShapeTests.drawLineCaps(g2, bounds, 5.0,1.0f, Color.RED);
        moveTo(2, row, g2);
        ShapeTests.drawLineCaps(g2, bounds, 5.0f,1.0f, Color.RED);
        moveTo(3, row, g2);
        ShapeTests.drawLineCapAndDash(g2, bounds, 5.0,1.0f, new float[] {2f, 2f}, Color.BLACK);
        moveTo(4, row, g2);
        ShapeTests.drawLineCapAndDash(g2, bounds, 5.0, 3.0f, new float[] {4f, 8f}, Color.BLACK);
        moveTo(5, row, g2);
        ShapeTests.drawLineCaps(g2, bounds, 5.0f,5.0f, Color.BLACK);

        row++; // ***** LINE2D
        moveTo(0, row, g2);
        ShapeTests.drawLines(g2, bounds, 5.0, new BasicStroke(0.0f), Color.RED);
        moveTo(1, row, g2);
        ShapeTests.drawLines(g2, bounds, 5.0, OUTLINE, Color.RED);
        moveTo(2, row, g2);
        ShapeTests.drawLines(g2, bounds, 5.0, OUTLINE, Color.RED);
        moveTo(3, row, g2);
        ShapeTests.drawLines(g2, bounds, 5.0, DASHED, Color.BLACK);
        moveTo(4, row, g2);
        ShapeTests.drawLines(g2, bounds, 5.0, DASHED_3, Color.BLACK);
        moveTo(5, row, g2);
        ShapeTests.drawLines(g2, bounds, 5.0, OUTLINE_3, Color.BLACK);

        row++; // ***** RECTANGLE2D
        Rectangle2D rect = new Rectangle2D.Double(5, 5, TILE_WIDTH - 10, TILE_HEIGHT - 10);
        moveTo(0, row, g2);
        ShapeTests.fillAndStrokeShape(g2, rect, Color.BLUE, null, null);
        moveTo(1, row, g2);
        ShapeTests.fillAndStrokeShape(g2, rect, null, OUTLINE, Color.BLUE);
        moveTo(2, row, g2);
        ShapeTests.fillAndStrokeShape(g2, rect, Color.LIGHT_GRAY, OUTLINE, Color.BLUE);
        moveTo(3, row, g2);
        ShapeTests.fillAndStrokeShape(g2, rect, Color.LIGHT_GRAY, DASHED, Color.BLACK);
        moveTo(4, row, g2);
        ShapeTests.fillAndStrokeShape(g2, rect, Color.LIGHT_GRAY, DASHED_3, Color.BLACK);
        moveTo(5, row, g2);
        ShapeTests.fillAndStrokeShape(g2, rect, Color.LIGHT_GRAY, OUTLINE_3, Color.BLACK);

        row++;  // ***** ROUNDRECTANGLE2D
        RoundRectangle2D roundRect = new RoundRectangle2D.Double(5, 5, TILE_WIDTH - 10, TILE_HEIGHT - 10, 8.0, 12.0);
        moveTo(0, row, g2);
        ShapeTests.fillAndStrokeShape(g2, roundRect, Color.BLUE, null, null);
        moveTo(1, row, g2);
        ShapeTests.fillAndStrokeShape(g2, roundRect, null, OUTLINE, Color.BLUE);
        moveTo(2, row, g2);
        ShapeTests.fillAndStrokeShape(g2, roundRect, Color.LIGHT_GRAY, OUTLINE, Color.BLUE);
        moveTo(3, row, g2);
        ShapeTests.fillAndStrokeShape(g2, roundRect, Color.LIGHT_GRAY, DASHED, Color.BLACK);
        moveTo(4, row, g2);
        ShapeTests.fillAndStrokeShape(g2, roundRect, Color.LIGHT_GRAY, DASHED_3, Color.BLACK);
        moveTo(5, row, g2);
        ShapeTests.fillAndStrokeShape(g2, roundRect, Color.LIGHT_GRAY, OUTLINE_3, Color.BLACK);

        row++; // ***** QUADCURVE2D
        moveTo(0, row, g2);
        ShapeTests.fillAndStrokeQuadCurve2D(g2, bounds, 5.0, Color.RED, null, null);
        moveTo(1, row, g2);
        ShapeTests.fillAndStrokeQuadCurve2D(g2, bounds, 5.0, null, OUTLINE, Color.RED);
        moveTo(2, row, g2);
        ShapeTests.fillAndStrokeQuadCurve2D(g2, bounds, 5.0, Color.LIGHT_GRAY, OUTLINE, Color.RED);
        moveTo(3, row, g2);
        ShapeTests.fillAndStrokeQuadCurve2D(g2, bounds, 5.0, Color.LIGHT_GRAY, DASHED, Color.BLACK);
        moveTo(4, row, g2);
        ShapeTests.fillAndStrokeQuadCurve2D(g2, bounds, 5.0, Color.LIGHT_GRAY, DASHED_3, Color.BLACK);
        moveTo(5, row, g2);
        ShapeTests.fillAndStrokeQuadCurve2D(g2, bounds, 5.0, Color.LIGHT_GRAY, OUTLINE_3, Color.BLACK);

        row++; // ***** CUBICCURVE2D
        moveTo(0, row, g2);
        ShapeTests.fillAndStrokeCubicCurve2D(g2, bounds, 5.0, Color.RED, null, null);
        moveTo(1, row, g2);
        ShapeTests.fillAndStrokeCubicCurve2D(g2, bounds, 5.0, null, OUTLINE, Color.RED);
        moveTo(2, row, g2);
        ShapeTests.fillAndStrokeCubicCurve2D(g2, bounds, 5.0, Color.LIGHT_GRAY, OUTLINE, Color.RED);
        moveTo(3, row, g2);
        ShapeTests.fillAndStrokeCubicCurve2D(g2, bounds, 5.0, Color.LIGHT_GRAY, DASHED, Color.BLACK);
        moveTo(4, row, g2);
        ShapeTests.fillAndStrokeCubicCurve2D(g2, bounds, 5.0, Color.LIGHT_GRAY, DASHED_3, Color.BLACK);
        moveTo(5, row, g2);
        ShapeTests.fillAndStrokeCubicCurve2D(g2, bounds, 5.0, Color.LIGHT_GRAY, OUTLINE_3, Color.BLACK);

        row++;  // ***** ELLIPSE2D
        Ellipse2D ellipse = new Ellipse2D.Double(MARGIN, MARGIN, bounds.getWidth() - 2 * MARGIN, bounds.getHeight() - 2 * MARGIN);
        moveTo(0, row, g2);
        ShapeTests.fillAndStrokeShape(g2, ellipse, Color.BLUE, null, null);
        moveTo(1, row, g2);
        ShapeTests.fillAndStrokeShape(g2, ellipse, null, OUTLINE, Color.BLUE);
        moveTo(2, row, g2);
        ShapeTests.fillAndStrokeShape(g2, ellipse, Color.LIGHT_GRAY, OUTLINE, Color.BLUE);
        moveTo(3, row, g2);
        ShapeTests.fillAndStrokeShape(g2, ellipse, Color.LIGHT_GRAY, DASHED, Color.BLACK);
        moveTo(4, row, g2);
        ShapeTests.fillAndStrokeShape(g2, ellipse, Color.LIGHT_GRAY, DASHED_3, Color.BLACK);
        moveTo(5, row, g2);
        ShapeTests.fillAndStrokeShape(g2, ellipse, Color.LIGHT_GRAY, OUTLINE_3, Color.BLACK);

        row++;  // ***** ARC2D PIE
        Arc2D arc = ShapeTests.createArc2D(Arc2D.PIE, 45, 270, bounds, 5);
        moveTo(0, row, g2);
        ShapeTests.fillAndStrokeShape(g2, arc, Color.BLUE, null, null);
        moveTo(1, row, g2);
        ShapeTests.fillAndStrokeShape(g2, arc, null, OUTLINE, Color.BLUE);
        moveTo(2, row, g2);
        ShapeTests.fillAndStrokeShape(g2, arc, Color.LIGHT_GRAY, OUTLINE, Color.BLUE);
        moveTo(3, row, g2);
        ShapeTests.fillAndStrokeShape(g2, arc, Color.LIGHT_GRAY, DASHED, Color.BLACK);
        moveTo(4, row, g2);
        ShapeTests.fillAndStrokeShape(g2, arc, Color.LIGHT_GRAY, DASHED_3, Color.BLACK);
        moveTo(5, row, g2);
        ShapeTests.fillAndStrokeShape(g2, arc, Color.LIGHT_GRAY, OUTLINE_3, Color.BLACK);

        row++;  // ***** ARC2D CHORD
        Arc2D arc2 = ShapeTests.createArc2D(Arc2D.CHORD, 210, 300, bounds, 5);
        moveTo(0, row, g2);
        ShapeTests.fillAndStrokeShape(g2, arc2, Color.BLUE, null, null);
        moveTo(1, row, g2);
        ShapeTests.fillAndStrokeShape(g2, arc2, null, OUTLINE, Color.BLUE);
        moveTo(2, row, g2);
        ShapeTests.fillAndStrokeShape(g2, arc2, Color.LIGHT_GRAY, OUTLINE, Color.BLUE);
        moveTo(3, row, g2);
        ShapeTests.fillAndStrokeShape(g2, arc2, Color.LIGHT_GRAY, DASHED, Color.BLACK);
        moveTo(4, row, g2);
        ShapeTests.fillAndStrokeShape(g2, arc2, Color.LIGHT_GRAY, DASHED_3, Color.BLACK);
        moveTo(5, row, g2);
        ShapeTests.fillAndStrokeShape(g2, arc2, Color.LIGHT_GRAY, OUTLINE_3, Color.BLACK);

        row++;  // ***** ARC2D OPEN
        Arc2D arc3 = ShapeTests.createArc2D(Arc2D.OPEN, -45, 270, bounds, 5);
        moveTo(0, row, g2);
        ShapeTests.fillAndStrokeShape(g2, arc3, Color.BLUE, null, null);
        moveTo(1, row, g2);
        ShapeTests.fillAndStrokeShape(g2, arc3, null, OUTLINE, Color.BLUE);
        moveTo(2, row, g2);
        ShapeTests.fillAndStrokeShape(g2, arc3, Color.LIGHT_GRAY, OUTLINE, Color.BLUE);
        moveTo(3, row, g2);
        ShapeTests.fillAndStrokeShape(g2, arc3, Color.LIGHT_GRAY, DASHED, Color.BLACK);
        moveTo(4, row, g2);
        ShapeTests.fillAndStrokeShape(g2, arc3, Color.LIGHT_GRAY, DASHED_3, Color.BLACK);
        moveTo(5, row, g2);
        ShapeTests.fillAndStrokeShape(g2, arc3, Color.LIGHT_GRAY, OUTLINE_3, Color.BLACK);

        row++; // ***** GeneralPATH
        Path2D path = ShapeTests.createPath2D(bounds, MARGIN);
        moveTo(0, row, g2);
        ShapeTests.fillAndStrokeShape(g2, path, Color.RED, null, null);
        moveTo(1, row, g2);
        ShapeTests.fillAndStrokeShape(g2, path, null, OUTLINE, Color.RED);
        moveTo(2, row, g2);
        ShapeTests.fillAndStrokeShape(g2, path, Color.LIGHT_GRAY, OUTLINE, Color.RED);
        moveTo(3, row, g2);
        ShapeTests.fillAndStrokeShape(g2, path, Color.LIGHT_GRAY, DASHED, Color.BLACK);
        moveTo(4, row, g2);
        ShapeTests.fillAndStrokeShape(g2, path, Color.LIGHT_GRAY, DASHED_3, Color.BLACK);
        moveTo(5, row, g2);
        ShapeTests.fillAndStrokeShape(g2, path, Color.LIGHT_GRAY, OUTLINE_3, Color.BLACK);

        row++; // ***** GeneralPATH WIND_NON_ZERO FILL
        path.setWindingRule(Path2D.WIND_NON_ZERO);
        moveTo(0, row, g2);
        ShapeTests.fillAndStrokeShape(g2, path, Color.RED, null, null);
        moveTo(1, row, g2);
        ShapeTests.fillAndStrokeShape(g2, path, null, OUTLINE, Color.RED);
        moveTo(2, row, g2);
        ShapeTests.fillAndStrokeShape(g2, path, Color.LIGHT_GRAY, OUTLINE, Color.RED);
        moveTo(3, row, g2);
        ShapeTests.fillAndStrokeShape(g2, path, Color.LIGHT_GRAY, DASHED, Color.BLACK);
        moveTo(4, row, g2);
        ShapeTests.fillAndStrokeShape(g2, path, Color.LIGHT_GRAY, DASHED_3, Color.BLACK);
        moveTo(5, row, g2);
        ShapeTests.fillAndStrokeShape(g2, path, Color.LIGHT_GRAY, OUTLINE_3, Color.BLACK);

        row++; // ***** Area - add
        Area areaAdd = ShapeTests.createCombinedArea("add", new Rectangle2D.Double(0.0, 0.0, TILE_WIDTH, TILE_HEIGHT), 5.0);
        moveTo(0, row, g2);
        ShapeTests.fillAndStrokeShape(g2, areaAdd, Color.BLUE, null, null);
        moveTo(1, row, g2);
        ShapeTests.fillAndStrokeShape(g2, areaAdd, null, OUTLINE, Color.BLUE);
        moveTo(2, row, g2);
        ShapeTests.fillAndStrokeShape(g2, areaAdd, Color.LIGHT_GRAY, OUTLINE, Color.BLUE);
        moveTo(3, row, g2);
        ShapeTests.fillAndStrokeShape(g2, areaAdd, Color.LIGHT_GRAY, DASHED, Color.BLACK);
        moveTo(4, row, g2);
        ShapeTests.fillAndStrokeShape(g2, areaAdd, Color.LIGHT_GRAY, DASHED_3, Color.BLACK);
        moveTo(5, row, g2);
        ShapeTests.fillAndStrokeShape(g2, areaAdd, Color.LIGHT_GRAY, OUTLINE_3, Color.BLACK);

        row++; // ***** Area - intersect
        Area areaIntersect = ShapeTests.createCombinedArea("intersect", new Rectangle2D.Double(0.0, 0.0, TILE_WIDTH, TILE_HEIGHT), 5.0);
        moveTo(0, row, g2);
        ShapeTests.fillAndStrokeShape(g2, areaIntersect, Color.BLUE, null, null);
        moveTo(1, row, g2);
        ShapeTests.fillAndStrokeShape(g2, areaIntersect, null, OUTLINE, Color.BLUE);
        moveTo(2, row, g2);
        ShapeTests.fillAndStrokeShape(g2, areaIntersect, Color.LIGHT_GRAY, OUTLINE, Color.BLUE);
        moveTo(3, row, g2);
        ShapeTests.fillAndStrokeShape(g2, areaIntersect, Color.LIGHT_GRAY, DASHED, Color.BLACK);
        moveTo(4, row, g2);
        ShapeTests.fillAndStrokeShape(g2, areaIntersect, Color.LIGHT_GRAY, DASHED_3, Color.BLACK);
        moveTo(5, row, g2);
        ShapeTests.fillAndStrokeShape(g2, areaIntersect, Color.LIGHT_GRAY, OUTLINE_3, Color.BLACK);

        row++; // ***** Area - intersect
        Area areaSubtract = ShapeTests.createCombinedArea("subtract", new Rectangle2D.Double(0.0, 0.0, TILE_WIDTH, TILE_HEIGHT), 5.0);
        moveTo(0, row, g2);
        ShapeTests.fillAndStrokeShape(g2, areaSubtract, Color.BLUE, null, null);
        moveTo(1, row, g2);
        ShapeTests.fillAndStrokeShape(g2, areaSubtract, null, OUTLINE, Color.BLUE);
        moveTo(2, row, g2);
        ShapeTests.fillAndStrokeShape(g2, areaSubtract, Color.LIGHT_GRAY, OUTLINE, Color.BLUE);
        moveTo(3, row, g2);
        ShapeTests.fillAndStrokeShape(g2, areaSubtract, Color.LIGHT_GRAY, DASHED, Color.BLACK);
        moveTo(4, row, g2);
        ShapeTests.fillAndStrokeShape(g2, areaSubtract, Color.LIGHT_GRAY, DASHED_3, Color.BLACK);
        moveTo(5, row, g2);
        ShapeTests.fillAndStrokeShape(g2, areaSubtract, Color.LIGHT_GRAY, OUTLINE_3, Color.BLACK);

        row++; // ***** Area - intersect
        Area areaXOR = ShapeTests.createCombinedArea("exclusiveOr", new Rectangle2D.Double(0.0, 0.0, TILE_WIDTH, TILE_HEIGHT), 5.0);
        moveTo(0, row, g2);
        ShapeTests.fillAndStrokeShape(g2, areaXOR, Color.BLUE, null, null);
        moveTo(1, row, g2);
        ShapeTests.fillAndStrokeShape(g2, areaXOR, null, OUTLINE, Color.BLUE);
        moveTo(2, row, g2);
        ShapeTests.fillAndStrokeShape(g2, areaXOR, Color.LIGHT_GRAY, OUTLINE, Color.BLUE);
        moveTo(3, row, g2);
        ShapeTests.fillAndStrokeShape(g2, areaXOR, Color.LIGHT_GRAY, DASHED, Color.BLACK);
        moveTo(4, row, g2);
        ShapeTests.fillAndStrokeShape(g2, areaXOR, Color.LIGHT_GRAY, DASHED_3, Color.BLACK);
        moveTo(5, row, g2);
        ShapeTests.fillAndStrokeShape(g2, areaXOR, Color.LIGHT_GRAY, OUTLINE_3, Color.BLACK);

        row++;  // ***** ALPHACOMPOSITE
        // show a set of tiles with standard AlphaComposite settings
        moveTo(0, row, g2);
        ShapeTests.drawShapesWithAlphaComposite(g2, AlphaComposite.Clear, new Rectangle2D.Double(0.0, 0.0, TILE_WIDTH, TILE_HEIGHT), 5.0);
        moveTo(1, row, g2);
        ShapeTests.drawShapesWithAlphaComposite(g2, AlphaComposite.Src, new Rectangle2D.Double(0.0, 0.0, TILE_WIDTH, TILE_HEIGHT), 5.0);
        moveTo(2, row, g2);
        ShapeTests.drawShapesWithAlphaComposite(g2, AlphaComposite.SrcOver, new Rectangle2D.Double(0.0, 0.0, TILE_WIDTH, TILE_HEIGHT), 5.0);
        moveTo(3, row, g2);
        ShapeTests.drawShapesWithAlphaComposite(g2, AlphaComposite.DstOver, new Rectangle2D.Double(0.0, 0.0, TILE_WIDTH, TILE_HEIGHT), 5.0);
        moveTo(4, row, g2);
        ShapeTests.drawShapesWithAlphaComposite(g2, AlphaComposite.SrcIn, new Rectangle2D.Double(0.0, 0.0, TILE_WIDTH, TILE_HEIGHT), 5.0);
        moveTo(5, row, g2);
        ShapeTests.drawShapesWithAlphaComposite(g2, AlphaComposite.DstIn, new Rectangle2D.Double(0.0, 0.0, TILE_WIDTH, TILE_HEIGHT), 5.0);
        moveTo(6, row, g2);
        ShapeTests.drawShapesWithAlphaComposite(g2, AlphaComposite.SrcOut, new Rectangle2D.Double(0.0, 0.0, TILE_WIDTH, TILE_HEIGHT), 5.0);
        moveTo(7, row, g2);
        ShapeTests.drawShapesWithAlphaComposite(g2, AlphaComposite.DstOut, new Rectangle2D.Double(0.0, 0.0, TILE_WIDTH, TILE_HEIGHT), 5.0);
        moveTo(8, row, g2);
        ShapeTests.drawShapesWithAlphaComposite(g2, AlphaComposite.Dst, new Rectangle2D.Double(0.0, 0.0, TILE_WIDTH, TILE_HEIGHT), 5.0);
        moveTo(9, row, g2);
        ShapeTests.drawShapesWithAlphaComposite(g2, AlphaComposite.SrcAtop, new Rectangle2D.Double(0.0, 0.0, TILE_WIDTH, TILE_HEIGHT), 5.0);
        moveTo(10, row, g2);
        ShapeTests.drawShapesWithAlphaComposite(g2, AlphaComposite.DstAtop, new Rectangle2D.Double(0.0, 0.0, TILE_WIDTH, TILE_HEIGHT), 5.0);

        row++;  // ***** ALPHACOMPOSITE
        // show a set of tiles with standard AlphaComposite settings
        moveTo(0, row, g2);
        ShapeTests.drawShapesWithAlphaComposite(g2, AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.6f), new Rectangle2D.Double(0.0, 0.0, TILE_WIDTH, TILE_HEIGHT), 5.0);
        moveTo(1, row, g2);
        ShapeTests.drawShapesWithAlphaComposite(g2, AlphaComposite.getInstance(AlphaComposite.SRC, 0.6f), new Rectangle2D.Double(0.0, 0.0, TILE_WIDTH, TILE_HEIGHT), 5.0);
        moveTo(2, row, g2);
        ShapeTests.drawShapesWithAlphaComposite(g2, AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f), new Rectangle2D.Double(0.0, 0.0, TILE_WIDTH, TILE_HEIGHT), 5.0);
        moveTo(3, row, g2);
        ShapeTests.drawShapesWithAlphaComposite(g2, AlphaComposite.getInstance(AlphaComposite.DST_OVER, 0.6f), new Rectangle2D.Double(0.0, 0.0, TILE_WIDTH, TILE_HEIGHT), 5.0);
        moveTo(4, row, g2);
        ShapeTests.drawShapesWithAlphaComposite(g2, AlphaComposite.getInstance(AlphaComposite.SRC_IN, 0.6f), new Rectangle2D.Double(0.0, 0.0, TILE_WIDTH, TILE_HEIGHT), 5.0);
        moveTo(5, row, g2);
        ShapeTests.drawShapesWithAlphaComposite(g2, AlphaComposite.getInstance(AlphaComposite.DST_IN, 0.6f), new Rectangle2D.Double(0.0, 0.0, TILE_WIDTH, TILE_HEIGHT), 5.0);
        moveTo(6, row, g2);
        ShapeTests.drawShapesWithAlphaComposite(g2, AlphaComposite.getInstance(AlphaComposite.SRC_OUT, 0.6f), new Rectangle2D.Double(0.0, 0.0, TILE_WIDTH, TILE_HEIGHT), 5.0);
        moveTo(7, row, g2);
        ShapeTests.drawShapesWithAlphaComposite(g2, AlphaComposite.getInstance(AlphaComposite.DST_OUT, 0.6f), new Rectangle2D.Double(0.0, 0.0, TILE_WIDTH, TILE_HEIGHT), 5.0);
        moveTo(8, row, g2);
        ShapeTests.drawShapesWithAlphaComposite(g2, AlphaComposite.getInstance(AlphaComposite.DST, 0.6f), new Rectangle2D.Double(0.0, 0.0, TILE_WIDTH, TILE_HEIGHT), 5.0);
        moveTo(9, row, g2);
        ShapeTests.drawShapesWithAlphaComposite(g2, AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.6f), new Rectangle2D.Double(0.0, 0.0, TILE_WIDTH, TILE_HEIGHT), 5.0);
        moveTo(10, row, g2);
        ShapeTests.drawShapesWithAlphaComposite(g2, AlphaComposite.getInstance(AlphaComposite.DST_ATOP, 0.6f), new Rectangle2D.Double(0.0, 0.0, TILE_WIDTH, TILE_HEIGHT), 5.0);

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

        row++;  // ***** GRADIENTPAINT
        moveTo(0, row, g2);
        GradientPaint gp = new GradientPaint(0f, 0f, Color.YELLOW, TILE_WIDTH, 0f, Color.RED);
        ShapeTests.fillAndStrokeShape(g2, roundRect, gp, null, null);

        // here we change the gradient to start one quarter of the way across the shape
        // and finish at the three quarter mark - the default should be non-cyclic
        moveTo(1, row, g2);
        float p = TILE_WIDTH / 4.0f;
        GradientPaint gp2 = new GradientPaint(p, 0f, Color.YELLOW, p * 3, 0f, Color.RED);
        ShapeTests.fillAndStrokeShape(g2, roundRect, gp2, null, null);

        moveTo(2, row, g2);
        GradientPaint gp3 = new GradientPaint(p, 0f, Color.YELLOW, p * 3, 0f, Color.RED, true);
        ShapeTests.fillAndStrokeShape(g2, roundRect, gp3, null, null);

        moveTo(3, row, g2);
        float delta = (TILE_WIDTH - 10) / 6f;
        LinearGradientPaint lgp1 = new LinearGradientPaint(10f, 0f, TILE_WIDTH - 10 , 0f, new float[] { 0f, 1/6f, 2/6f, 3/6f, 4/6f, 5/6f, 1f}, RAINBOW_COLORS);
        ShapeTests.fillAndStrokeShape(g2, roundRect, lgp1, null, null);

        moveTo(4, row, g2);
        GradientPaint gp4 = new GradientPaint(0f, 0f, Color.YELLOW, TILE_WIDTH, TILE_HEIGHT, Color.RED);
        ShapeTests.fillAndStrokeShape(g2, roundRect, gp4, null, null);

        // here we change the gradient to start one quarter of the way across the shape
        // and finish at the three quarter mark - the default should be non-cyclic
        moveTo(5, row, g2);
        GradientPaint gp5 = new GradientPaint(p, 0f, Color.YELLOW, p * 3, TILE_HEIGHT, Color.RED);
        ShapeTests.fillAndStrokeShape(g2, roundRect, gp5, null, null);

        moveTo(6, row, g2);
        GradientPaint gp6 = new GradientPaint(p, 0f, Color.YELLOW, p * 3, TILE_HEIGHT, Color.RED, true);
        ShapeTests.fillAndStrokeShape(g2, roundRect, gp6, null, null);

        moveTo(7, row, g2);
        LinearGradientPaint lgp2 = new LinearGradientPaint(10f, 0f, TILE_WIDTH - 10 , TILE_HEIGHT, new float[] { 0f, 1/6f, 2/6f, 3/6f, 4/6f, 5/6f, 1f}, RAINBOW_COLORS);
        ShapeTests.fillAndStrokeShape(g2, roundRect, lgp2, null, null);

        row++;  // ***** LINES WITH GRADIENT PAINT
        moveTo(0, row, g2);
        ShapeTests.drawLineCaps(g2, bounds, 5.0,5.0f, gp);
        moveTo(1, row, g2);
        ShapeTests.drawLineCaps(g2, bounds, 5.0,5.0f, gp2);
        moveTo(2, row, g2);
        ShapeTests.drawLineCaps(g2, bounds, 5.0,5.0f, gp3);
        moveTo(3, row, g2);
        ShapeTests.drawLineCaps(g2, bounds, 5.0,5.0f, lgp1);
        moveTo(4, row, g2);
        ShapeTests.drawLineCaps(g2, bounds, 5.0,5.0f, gp4);
        moveTo(5, row, g2);
        ShapeTests.drawLineCaps(g2, bounds, 5.0,5.0f, gp5);
        moveTo(6, row, g2);
        ShapeTests.drawLineCaps(g2, bounds, 5.0,5.0f, gp6);
        moveTo(7, row, g2);
        ShapeTests.drawLineCaps(g2, bounds, 5.0,5.0f, lgp2);

        row++;  // ***** RADIAL GRADIENT PAINT
        moveTo(0, row, g2);
        Point2D center = new Point2D.Double(TILE_WIDTH / 2, TILE_HEIGHT / 2);
        RadialGradientPaint rgp = new RadialGradientPaint(center, (float) (TILE_HEIGHT / 2.0 - 5), new float[] {0.0f, 0.75f, 1.0f}, new Color[] {Color.YELLOW, Color.RED, Color.LIGHT_GRAY});
        ShapeTests.fillAndStrokeShape(g2, roundRect, rgp, null, null);

        moveTo(1, row, g2);
        RadialGradientPaint rgp2 = new RadialGradientPaint(center, (float) (TILE_HEIGHT / 2.0 - 5), new float[] {0.0f, 0.75f, 1.0f}, new Color[] {Color.YELLOW, Color.RED, Color.LIGHT_GRAY}, MultipleGradientPaint.CycleMethod.REPEAT);
        ShapeTests.fillAndStrokeShape(g2, roundRect, rgp2, null, null);

        moveTo(2, row, g2);
        RadialGradientPaint rgp3 = new RadialGradientPaint(center, (float) (TILE_HEIGHT / 2.0 - 5), new float[] {0.0f, 0.75f, 1.0f}, new Color[] {Color.YELLOW, Color.RED, Color.LIGHT_GRAY}, MultipleGradientPaint.CycleMethod.REFLECT);
        ShapeTests.fillAndStrokeShape(g2, roundRect, rgp3, null, null);

        moveTo(3, row, g2);
        Point2D focus = new Point2D.Double(TILE_WIDTH / 3, TILE_HEIGHT / 3);
        RadialGradientPaint rgp4 = new RadialGradientPaint(center, (float) (TILE_HEIGHT / 2.0 - 5), focus, new float[] {0.0f, 0.75f, 1.0f}, new Color[] {Color.YELLOW, Color.RED, Color.LIGHT_GRAY}, MultipleGradientPaint.CycleMethod.NO_CYCLE);
        ShapeTests.fillAndStrokeShape(g2, roundRect, rgp4, null, null);

        moveTo(4, row, g2);
        RadialGradientPaint rgp5 = new RadialGradientPaint(center, (float) (TILE_HEIGHT / 2.0 - 5), focus, new float[] {0.0f, 0.75f, 1.0f}, new Color[] {Color.YELLOW, Color.RED, Color.LIGHT_GRAY}, MultipleGradientPaint.CycleMethod.REPEAT);
        ShapeTests.fillAndStrokeShape(g2, roundRect, rgp5, null, null);

        moveTo(5, row, g2);
        RadialGradientPaint rgp6 = new RadialGradientPaint(center, (float) (TILE_HEIGHT / 2.0 - 5), focus, new float[] {0.0f, 0.75f, 1.0f}, new Color[] {Color.YELLOW, Color.RED, Color.LIGHT_GRAY}, MultipleGradientPaint.CycleMethod.REFLECT);
        ShapeTests.fillAndStrokeShape(g2, roundRect, rgp6, null, null);

        row++; // ***** TRANSLATION
        moveTo(0, row, g2);
        Rectangle2D rectToTranslate = new Rectangle2D.Double(5, 5, TILE_WIDTH / 2 - 10, TILE_HEIGHT / 2 - 10);
        TransformTests.translateShape(g2, bounds, rectToTranslate, Color.YELLOW, new BasicStroke(1.0f), Color.BLACK);
        moveTo(1, row, g2);
        RoundRectangle2D roundRectToTranslate = new RoundRectangle2D.Double(5, 5, TILE_WIDTH / 2 - 10, TILE_HEIGHT / 2 - 10, 8, 8);
        TransformTests.translateShape(g2, bounds, roundRectToTranslate, Color.ORANGE, new BasicStroke(1.0f), Color.BLACK);
        moveTo(2, row, g2);
        QuadCurve2D quadCurveToTranslate = ShapeTests.createQuadCurve2D1(new Rectangle2D.Double(0, 0, TILE_WIDTH / 2, TILE_HEIGHT / 2), 3);
        TransformTests.translateShape(g2, bounds, quadCurveToTranslate, Color.YELLOW, new BasicStroke(1.0f), Color.BLACK);
        moveTo(3, row, g2);
        CubicCurve2D cubicCurveToTranslate = ShapeTests.createCubicCurve2D(new Rectangle2D.Double(0, 0, TILE_WIDTH / 2, TILE_HEIGHT / 2), 3);
        TransformTests.translateShape(g2, bounds, cubicCurveToTranslate, Color.ORANGE, new BasicStroke(1.0f), Color.BLACK);
        moveTo(4, row, g2);
        Ellipse2D ellipseToTranslate = new Ellipse2D.Double(5, 5, TILE_WIDTH / 2 - 10, TILE_HEIGHT / 2 - 10);
        TransformTests.translateShape(g2, bounds, ellipseToTranslate, Color.YELLOW, new BasicStroke(1.0f), Color.BLACK);
        moveTo(5, row, g2);
        Arc2D arcToTranslate = new Arc2D.Double(new Rectangle2D.Double(5, 5, TILE_WIDTH / 2 - 10, TILE_HEIGHT / 2 - 10), 45, 290, Arc2D.PIE);
        TransformTests.translateShape(g2, bounds, arcToTranslate, Color.ORANGE, new BasicStroke(1.0f), Color.BLACK);
        moveTo(6, row, g2);
        Area areaToTranslate = ShapeTests.createCombinedArea("exclusiveOr", new Rectangle2D.Double(0.0, 0.0, TILE_WIDTH / 2, TILE_HEIGHT / 2), 2.5);
        TransformTests.translateShape(g2, bounds, areaToTranslate, Color.YELLOW, new BasicStroke(1.0f), Color.BLACK);
        moveTo(7, row, g2);
        Path2D pathToTranslate = ShapeTests.createPath2D(new Rectangle2D.Double(0.0, 0.0, TILE_WIDTH / 2, TILE_HEIGHT / 2), 2.5);
        TransformTests.translateShape(g2, bounds, pathToTranslate, Color.ORANGE, new BasicStroke(1.0f), Color.BLACK);

        row++; // ***** ROTATION
        double m = 0.33 * TILE_HEIGHT;
        moveTo(0, row, g2);
        Rectangle2D rectToRotate = new Rectangle2D.Double(m, m, TILE_WIDTH - m*2, TILE_HEIGHT - m*2);
        TransformTests.rotateShape(g2, bounds, rectToRotate, Math.PI / 4, Color.BLUE, OUTLINE, Color.BLACK);
        moveTo(1, row, g2);
        RoundRectangle2D roundRectToRotate = new RoundRectangle2D.Double(m, m, TILE_WIDTH - m*2, TILE_HEIGHT - m*2, 8, 8);
        TransformTests.rotateShape(g2, bounds, roundRectToRotate, Math.PI / 4, Color.BLUE, OUTLINE, Color.BLACK);
        moveTo(2, row, g2);
        QuadCurve2D quadCurveToRotate = ShapeTests.createQuadCurve2D2(new Rectangle2D.Double(0, 0, TILE_WIDTH, TILE_HEIGHT),15);
        TransformTests.rotateShape(g2, bounds, quadCurveToRotate, Math.PI / 4, Color.BLUE, OUTLINE, Color.BLACK);
        moveTo(3, row, g2);
        CubicCurve2D cubicCurveToRotate = ShapeTests.createCubicCurve2D(new Rectangle2D.Double(0, 0, TILE_WIDTH, TILE_HEIGHT), 15);
        TransformTests.rotateShape(g2, bounds, cubicCurveToRotate, Math.PI / 4, Color.BLUE, OUTLINE, Color.BLACK);
        moveTo(4, row, g2);
        Ellipse2D ellipseToRotate = new Ellipse2D.Double(m, m, TILE_WIDTH - m*2, TILE_HEIGHT - m*2);
        TransformTests.rotateShape(g2, bounds, ellipseToRotate, Math.PI / 4, Color.BLUE, OUTLINE, Color.BLACK);
        moveTo(5, row, g2);
        Arc2D arcToRotate = new Arc2D.Double(new Rectangle2D.Double(m, m, TILE_WIDTH - m*2, TILE_HEIGHT - m*2), 45, 290, Arc2D.PIE);
        TransformTests.rotateShape(g2, bounds, arcToRotate, Math.PI / 4, Color.BLUE, OUTLINE, Color.BLACK);
        moveTo(6, row, g2);
        Area areaToRotate = ShapeTests.createCombinedArea("add", new Rectangle2D.Double(0, 0, TILE_WIDTH, TILE_HEIGHT), m);
        TransformTests.rotateShape(g2, bounds, areaToRotate, Math.PI / 4, Color.BLUE, OUTLINE, Color.BLACK);
        moveTo(7, row, g2);
        double mmm = 0.50;
        Path2D pathToRotate = ShapeTests.createPath2D(new Rectangle2D.Double(TILE_WIDTH * (mmm / 2.0), TILE_HEIGHT * (mmm / 2.0), TILE_WIDTH * (1 - mmm), TILE_HEIGHT * (1 - mmm)), 0.0);
        TransformTests.rotateShape(g2, bounds, pathToRotate, Math.PI / 4, Color.BLUE, OUTLINE, Color.BLACK);

        row++; // ***** SHEAR X & Y
        moveTo(0, row, g2);
        double shx = -2.0;
        double shy = -0.5;
        double mm = 0.33 * TILE_HEIGHT;
        Rectangle2D rectToSkew = new Rectangle2D.Double(m, m, TILE_WIDTH - mm*2, TILE_HEIGHT - mm*2);
        TransformTests.shearShape(g2, bounds, rectToSkew, 0.0, shy, Color.BLUE, OUTLINE, Color.BLACK);
        moveTo(0, row + 1, g2);
        TransformTests.shearShape(g2, bounds, rectToSkew, shx, 0.0, Color.BLUE, OUTLINE, Color.BLACK);
        moveTo(1, row, g2);
        RoundRectangle2D roundRectToShear = new RoundRectangle2D.Double(m, m, TILE_WIDTH - m*2, TILE_HEIGHT - m*2, 8, 8);
        TransformTests.shearShape(g2, bounds, roundRectToShear, 0.0, shy, Color.BLUE, OUTLINE, Color.BLACK);
        moveTo(1, row + 1, g2);
        TransformTests.shearShape(g2, bounds, roundRectToShear, shx, 0.0, Color.BLUE, OUTLINE, Color.BLACK);

        moveTo(2, row, g2);
        QuadCurve2D quadCurveToShear = ShapeTests.createQuadCurve2D2(new Rectangle2D.Double(0, 0, TILE_WIDTH, TILE_HEIGHT),15);
        TransformTests.shearShape(g2, bounds, quadCurveToShear, 0.0, shy, Color.BLUE, OUTLINE, Color.BLACK);
        moveTo(2, row + 1, g2);
        TransformTests.shearShape(g2, bounds, quadCurveToShear, shx, 0.0, Color.BLUE, OUTLINE, Color.BLACK);

        moveTo(3, row, g2);
        CubicCurve2D cubicCurveToShear = ShapeTests.createCubicCurve2D(new Rectangle2D.Double(0, 0, TILE_WIDTH, TILE_HEIGHT), 15);
        TransformTests.shearShape(g2, bounds, cubicCurveToShear, 0.0, shy, Color.BLUE, OUTLINE, Color.BLACK);
        moveTo(3, row + 1, g2);
        TransformTests.shearShape(g2, bounds, cubicCurveToShear, shx, 0.0, Color.BLUE, OUTLINE, Color.BLACK);

        moveTo(4, row, g2);
        Ellipse2D ellipseToSkew = new Ellipse2D.Double(m, m, TILE_WIDTH - m*2, TILE_HEIGHT - m*2);
        TransformTests.shearShape(g2, bounds, ellipseToSkew, 0.0, shy, Color.BLUE, OUTLINE, Color.BLACK);
        moveTo(4, row + 1, g2);
        TransformTests.shearShape(g2, bounds, ellipseToSkew, shx, 0.0, Color.BLUE, OUTLINE, Color.BLACK);

        moveTo(5, row, g2);
        Arc2D arcToShear = new Arc2D.Double(new Rectangle2D.Double(m, m, TILE_WIDTH - m*2, TILE_HEIGHT - m*2), 45, 290, Arc2D.PIE);
        TransformTests.shearShape(g2, bounds, arcToShear, 0.0, shy, Color.BLUE, OUTLINE, Color.BLACK);
        moveTo(5, row + 1, g2);
        TransformTests.shearShape(g2, bounds, arcToShear, shx, 0.0, Color.BLUE, OUTLINE, Color.BLACK);

        moveTo(6, row, g2);
        Area areaToShear = ShapeTests.createCombinedArea("add", new Rectangle2D.Double(0, 0, TILE_WIDTH, TILE_HEIGHT), m);
        TransformTests.shearShape(g2, bounds, areaToShear, 0.0, shy, Color.BLUE, OUTLINE, Color.BLACK);
        moveTo(6, row + 1, g2);
        TransformTests.shearShape(g2, bounds, areaToShear, shx, 0.0, Color.BLUE, OUTLINE, Color.BLACK);

        moveTo(7, row, g2);
        Path2D pathToShear = ShapeTests.createPath2D(new Rectangle2D.Double(TILE_WIDTH * (mmm / 2.0), TILE_HEIGHT * (mmm / 2.0), TILE_WIDTH * (1 - mmm), TILE_HEIGHT * (1 - mmm)), 0.0);
        TransformTests.shearShape(g2, bounds, pathToShear, 0.0, shy, Color.BLUE, OUTLINE, Color.BLACK);
        moveTo(7, row + 1, g2);
        TransformTests.shearShape(g2, bounds, pathToShear, shx, 0.0, Color.BLUE, OUTLINE, Color.BLACK);

        row++; // to cover the shearing above taking up two rows

        row++;  // ***** STRINGS & FONTS
        moveTo(0, row, g2);
        FontTests.drawString(g2, new Rectangle2D.Double(0.0, 0.0, TILE_WIDTH * 2, TILE_HEIGHT));
        moveTo(2, row, g2);
        FontTests.drawStringBounds(g2, bounds);
        moveTo(4, row, g2);
        FontTests.drawTextMetrics(g2, bounds);

        row++;  // ***** CLIPPING
        moveTo(0, row, g2);
        ClippingTests.fillRectangularClippingRegions(g2, bounds);

        moveTo(1, row, g2);
        ClippingTests.drawArc2DWithRectangularClip(g2, bounds, 5);

        row++;  // ***** IMAGE
        moveTo(0, row, g2);
        Rectangle2D imageBounds = new Rectangle2D.Double(0, 0, TILE_WIDTH * 3, TILE_WIDTH * 2);
        ImageTests.drawImage(g2, imageBounds, 5);

        moveTo(4, row, g2);
        imageBounds = new Rectangle2D.Double(0, 0, TILE_WIDTH * 3, TILE_WIDTH * 2);
        Shape saved = g2.getClip();
        g2.clip(new Ellipse2D.Double(15, 15, TILE_WIDTH * 3 - 30, TILE_WIDTH * 2  - 30));
        ImageTests.drawImage(g2, imageBounds, 5);
        g2.setClip(saved);

        moveTo(8, row, g2);
        imageBounds = new Rectangle2D.Double(0, 0, TILE_WIDTH * 3, TILE_WIDTH * 2);
        AffineTransform savedTransform = g2.getTransform();
        Shape savedClip = g2.getClip();
        g2.clip(imageBounds);
        g2.translate(TILE_WIDTH * 1.5, TILE_WIDTH);
        g2.rotate(Math.PI / 4);
        g2.translate(-TILE_WIDTH * 1.5, -TILE_WIDTH);
        ImageTests.drawImage(g2, imageBounds, 5);
        g2.setClip(savedClip);
        g2.setTransform(savedTransform);

        row ++;  // skip a row because the images are covering two rows

        moveTo(TILE_COUNT_H - 2, 20, g2);
        drawSwingUI(g2, new Rectangle2D.Double(0, 0, TILE_WIDTH * 4, TILE_HEIGHT * 4));

    }

    /**
     * Writes out the relevant properties for the test.
     *
     * @param g2  the graphics target.
     * @param g2Implementation  a description of the Graphics2D implementation under test.
     */
    public static void drawTestProperties(Graphics2D g2, String g2Implementation) {
        g2.setPaint(Color.BLACK);
        g2.setFont(new Font("Courier New", Font.PLAIN, 14));
        int y = 20;
        g2.drawString("target -> " + g2Implementation, 10, y += 16);
        g2.drawString("timestamp -> " + LocalDateTime.now(), 10, y += 16);
        g2.drawString("os.name -> " + System.getProperty("os.name"), 10, y += 16);
        g2.drawString("os.version -> " + System.getProperty("os.version"), 10, y += 16);
        g2.drawString("os.arch -> " + System.getProperty("os.arch"), 10, y += 16);
        g2.drawString("java.runtime.version -> " + System.getProperty("java.runtime.version"), 10, y += 16);
        g2.drawString("java.vm.name -> " + System.getProperty("java.vm.name"), 10, y += 16);
        g2.drawString("java.vendor.version -> " + System.getProperty("java.vendor.version"), 10, y += 16);// Graphics2D Implementation : SkijaGraphics2D 1.0.1 (-> Skija 0.92.18)
    }

    /**
     * Draws a single tile - useful for testing just one feature.
     *
     * @param g2  the graphics target.
     */
    private static void drawTestSingle(Graphics2D g2) {
        moveTo(0, 0, g2);
        Rectangle2D bounds = new Rectangle2D.Double(0, 0, TILE_WIDTH, TILE_HEIGHT);
        drawSwingUI(g2, new Rectangle2D.Double(0, 0, TILE_WIDTH * 4, TILE_HEIGHT * 4));
    }

    /**
     * Renders the test output (checks whether generating the whole test
     * sheet or just one single test).
     *
     * @param g2  the graphics target.
     * @param single  set to true if just generating a single test
     */
    private static void drawTestOutput(Graphics2D g2, String g2UnderTest, String qrLink, boolean single) {
        if (single) {
            drawTestSingle(g2);
        } else {
            drawTestSheet(g2, g2UnderTest, qrLink);
        }
    }

    /**
     * Run the tests with SkijaGraphics2D.
     *
     * @param fileName  the base filename.
     * @param single  run the current single test?
     */
    public static void testSkijaGraphics2D(String fileName, boolean single) {
        SkijaGraphics2D g2 = new SkijaGraphics2D(TILE_COUNT_H * TILE_WIDTH, TILE_COUNT_V * TILE_HEIGHT);
        drawTestOutput(g2, "SkijaGraphics2d 1.0.2-SNAPSHOT", "https://github.com/jfree/skijagraphics2d", single);
        org.jetbrains.skija.Image image = g2.getSurface().makeImageSnapshot();
        Data pngData = image.encodeToData(EncodedImageFormat.PNG);
        byte [] pngBytes = pngData.getBytes();
        try {
            if (single) {
                fileName += "-single.png";
            } else {
                fileName += ".png";
            }
            java.nio.file.Path path = java.nio.file.Path.of(fileName);
            java.nio.file.Files.write(path, pngBytes);
        }
        catch (IOException e) {
            System.out.println(e);
        }
    }

    /**
     * Run the tests with a Graphics2D from a Java2D BufferedImage and save
     * the results to the specified file.
     *
     * @param fileName  the PNG file name.
     *
     * @throws IOException
     */
    public static void testJava2D(String fileName, boolean single) throws IOException {
        BufferedImage image = new BufferedImage(TILE_WIDTH * TILE_COUNT_H, TILE_HEIGHT * TILE_COUNT_V, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        drawTestOutput(g2, "Java2D/BufferedImage", "https://github.com/jfree", single);
        if (single) {
            fileName += "-single.png";
        } else {
            fileName += ".png";
        }
        ImageIO.write(image, "png", new File(fileName));
    }

    public static void testJFreeSVG(String filename, boolean single) throws IOException {
        SVGGraphics2D g2 = new SVGGraphics2D(TILE_WIDTH * TILE_COUNT_H, TILE_HEIGHT * TILE_COUNT_V);
        drawTestOutput(g2, "JFree/SVGGraphics2D", "https://github.com/jfree/jfreesvg", single);
        if (single) {
            filename += "-single.svg";
        } else {
            filename += ".svg";
        }
        SVGUtils.writeToSVG(new File(filename), g2.getSVGElement());
    }

    /**
     * Creates Java2D output that exercises many features of the API.
     *
     * @param args
     */
    public static void main(String[] args) throws IOException {
        boolean single = false;
        testJava2D("java2D", single);
        //testJFreeSVG("jfreesvg", single);
        testSkijaGraphics2D("skija", single);
        System.exit(0);
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

}
