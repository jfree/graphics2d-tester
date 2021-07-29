package org.jfree.graphics2d;

import org.jetbrains.skija.Data;
import org.jetbrains.skija.EncodedImageFormat;
import org.jfree.skija.SkijaGraphics2D;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Runs a visual testing setup against various Graphics2D implementations.  The idea
 * is that you can compare the output of a custom Graphics2D implementation against
 * the reference implementation (Java2D).
 */
public class Test {

    private static int TILE_COUNT_H = 12;

    private static int TILE_COUNT_V = 12;

    private static int TILE_WIDTH = 100;

    private static int TILE_HEIGHT = 80;

    private static void moveTo(int tileX, int tileY, Graphics2D g2) {
        AffineTransform t = AffineTransform.getTranslateInstance(tileX * TILE_WIDTH, tileY * TILE_HEIGHT);
        g2.setTransform(t);
    }

    private static void drawTileLineCaps(Graphics2D g2) {
        g2.setPaint(Color.BLACK);
        double delta = TILE_HEIGHT / 4.0;
        Line2D line = new Line2D.Double(5.0, delta, TILE_WIDTH - 10.0, delta);
        g2.setStroke(new BasicStroke(5.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
        g2.draw(line);
        line = new Line2D.Double(5.0, delta * 2, TILE_WIDTH - 10.0, delta * 2);
        g2.setStroke(new BasicStroke(5.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.draw(line);
        g2.setStroke(new BasicStroke(5.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));
        line = new Line2D.Double(5.0, delta * 3, TILE_WIDTH - 10.0, delta * 3);
        g2.draw(line);
    }

    private static void drawTileLineCapAndDash(Graphics2D g2) {
        g2.setPaint(Color.RED);
        double delta = TILE_HEIGHT / 4.0;
        Line2D line = new Line2D.Double(5.0, delta, TILE_WIDTH * 2 - 10.0, delta);
        g2.setStroke(new BasicStroke(5.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 10f, new float[] { 10.0f, 5.0f, 20.0f, 10.0f }, 0f));
        g2.draw(line);
        line = new Line2D.Double(5.0, delta * 2, TILE_WIDTH * 2 - 10.0, delta * 2);
        g2.setStroke(new BasicStroke(5.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10f, new float[] { 10.0f, 5.0f, 20.0f, 10.0f }, 0f));
        g2.draw(line);
        g2.setStroke(new BasicStroke(5.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND, 10f, new float[] { 10.0f, 5.0f, 20.0f, 10.0f }, 0f));
        line = new Line2D.Double(5.0, delta * 3, TILE_WIDTH  * 2 - 10.0, delta * 3);
        g2.draw(line);
    }

    private static void drawTileString(Graphics2D g2) {
        g2.setPaint(Color.BLACK);
        //g2.setStroke(new BasicStroke(1.0f));
        g2.setFont(new Font(Font.SERIF, Font.PLAIN, 14));
        g2.setFont(new Font("Times New Roman", Font.PLAIN, 14));
        g2.drawString("Serif Font 1234567890", 15, 20);
//        g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        g2.setFont(new Font("Helvetica", Font.PLAIN, 14));
        g2.drawString("Sans Serif Font 1234567890", 15, 40);
//        g2.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        g2.setFont(new Font("Courier New", Font.PLAIN, 14));
        g2.drawString("Monospaced Font 1234567890", 15, 60);
    }

    /**
     * Fills a rectangle with the specified paint.
     *
     * @param g2  the graphics target.
     * @param paint  the paint ({code null} not permitted).
     */
    private static void fillRectangle(Graphics2D g2, Paint paint) {
        Rectangle2D rect = new Rectangle2D.Double(5, 5, TILE_WIDTH - 10, TILE_HEIGHT - 10);
        g2.setPaint(paint);
        g2.fill(rect);
    }

    /**
     * Draws a rectangle with the specified stroke.
     *
     * @param g2
     * @param stroke
     */
    private static void drawTileRectangleStroked(Graphics2D g2, Stroke stroke) {
        Rectangle2D rect = new Rectangle2D.Double(5, 5, TILE_WIDTH - 10, TILE_HEIGHT - 10);
        g2.setStroke(stroke);
        g2.draw(rect);
    }

    /**
     * Draws a rectangle with the specified stroke and fill.
     *
     * @param g2
     * @param stroke
     */
    private static void drawTileRectangleFilledAndStroked(Graphics2D g2, Paint paint, Stroke stroke, Paint outlinePaint) {
        Rectangle2D rect = new Rectangle2D.Double(5, 5, TILE_WIDTH - 10, TILE_HEIGHT - 10);
        g2.setPaint(paint);
        g2.setStroke(stroke);
        g2.fill(rect);
        g2.setPaint(outlinePaint);
        g2.draw(rect);
    }

    /**
     * Draws a rectangle that is filled with the specified paint.
     *
     * @param g2
     * @param paint
     */
    private static void drawTileRoundRectangleFilled(Graphics2D g2, Paint paint) {
        RoundRectangle2D rect = new RoundRectangle2D.Double(5, 5, TILE_WIDTH - 10, TILE_HEIGHT - 10, 4.0, 4.0);
        g2.setPaint(paint);
        g2.fill(rect);
    }

    /**
     * Draws a rectangle with the specified stroke.
     *
     * @param g2
     * @param stroke
     */
    private static void drawTileRoundRectangleStroked(Graphics2D g2, Stroke stroke) {
        RoundRectangle2D rect = new RoundRectangle2D.Double(5, 5, TILE_WIDTH - 10, TILE_HEIGHT - 10, 4.0, 4.0);
        g2.setStroke(stroke);
        g2.draw(rect);
    }

    /**
     * Draws a rectangle with the specified stroke and fill.
     *
     * @param g2
     * @param stroke
     */
    private static void drawTileRoundRectangleFilledAndStroked(Graphics2D g2, Paint paint, Stroke stroke, Paint outlinePaint) {
        RoundRectangle2D rect = new RoundRectangle2D.Double(5, 5, TILE_WIDTH - 10, TILE_HEIGHT - 10, 4.0, 4.0);
        g2.setPaint(paint);
        g2.setStroke(stroke);
        g2.fill(rect);
        g2.setPaint(outlinePaint);
        g2.draw(rect);
    }

    /**
     * Draws a shape with the specified stroke and fill.
     *
     * @param g2
     * @param shape  the shape (null not permitted)
     * @param stroke
     */
    private static void drawTileShapeFilledAndStroked(Graphics2D g2, Shape shape, Paint paint, Stroke stroke, Paint outlinePaint) {
        g2.setPaint(paint);
        g2.setStroke(stroke);
        g2.fill(shape);
        g2.setPaint(outlinePaint);
        g2.draw(shape);
    }

    private static Arc2D createArc2D(int style) {
        Arc2D arc = new Arc2D.Double(5, 5, TILE_WIDTH - 10, TILE_HEIGHT - 10, 45, 270, style);
        return arc;
    }

    private static void drawTileImage(Graphics2D g2) {
        g2.drawImage(BUG_IMAGE, 0, 0, null);
    }

    private static void drawTileArc2DWithRectangularClip(Graphics2D g2) {
        Shape savedClip = g2.getClip();
        g2.clipRect(15, 15, TILE_WIDTH - 30, TILE_HEIGHT - 30);
        Arc2D arc = createArc2D(Arc2D.PIE);
        g2.setPaint(Color.DARK_GRAY);
        g2.fill(arc);
        g2.setPaint(Color.RED);
        g2.draw(arc);
        g2.setClip(savedClip);
    }

    private static void drawTilePolygon(Graphics2D g2) {
        g2.setPaint(Color.BLUE);
        g2.setStroke(new BasicStroke(1.0f));
        g2.drawPolygon(xCoordsForCross(TILE_WIDTH / 2, 36, 18), yCoordsForCross(TILE_HEIGHT / 2, 36, 18), 12);
    }

    private static int[] xCoordsForCross(int centerX, int radius, int thickness) {
        int[] xcoords = new int[12];
        xcoords[0] = centerX + thickness / 2;
        xcoords[1] = xcoords[0];
        xcoords[2] = centerX + radius;
        xcoords[3] = xcoords[2];
        xcoords[4] = xcoords[0];
        xcoords[5] = xcoords[0];
        xcoords[6] = centerX - thickness / 2;
        xcoords[7] = xcoords[6];
        xcoords[8] = centerX - radius;
        xcoords[9] = xcoords[8];
        xcoords[10] = xcoords[6];
        xcoords[11] = xcoords[6];
        return xcoords;
    }

    private static int[] yCoordsForCross(int centerY, int radius, int thickness) {
        int[] ycoords = new int[12];
        ycoords[0] = centerY + radius;
        ycoords[1] = centerY + thickness / 2;
        ycoords[2] = ycoords[1];
        ycoords[3] = centerY - thickness / 2;
        ycoords[4] = ycoords[3];
        ycoords[5] = centerY - radius;
        ycoords[6] = ycoords[5];
        ycoords[7] = ycoords[3];
        ycoords[8] = ycoords[3];
        ycoords[9] = ycoords[1];
        ycoords[10] = ycoords[1];
        ycoords[11] = ycoords[0];
        return ycoords;
    }

    /**
     * Fills the specified area and then draws its outline.  This tile is used to
     * show various combinations of Constructive Area Geometry (CAG) operations.
     *
     * @param g2  the graphics target.
     * @param area  the shape.
     */
    private static void drawAndFillArea(Graphics2D g2, Area area) {
        g2.setPaint(Color.LIGHT_GRAY);
        g2.fill(area);

        g2.setPaint(Color.BLACK);
        g2.setStroke(new BasicStroke(1.0f));
        g2.draw(area);
    }

    /**
     * Creates an area from two shapes.
     *
     * @param operation  the operation ("add", "intersect", "subtract" or "exclusiveOr")
     * @param bounds  the bounds (controls the size of the shapes).
     * @param margin  the margin.
     *
     * @return An area.
     */
    private static Area createCombinedArea(String operation, Rectangle2D bounds, double margin) {
        double w = bounds.getWidth() / 1.5;
        double h = bounds.getHeight() / 1.5;

        // create an ellipse in the top left
        Ellipse2D ellipse = new Ellipse2D.Double(margin, margin, bounds.getWidth() / 1.5, bounds.getHeight() / 1.5);

        // create a rectangle in the bottom right
        Rectangle2D rectangle = new Rectangle2D.Double(bounds.getWidth() - margin - w, bounds.getHeight() - margin - h, w, h);

        Area a1 = new Area(ellipse);
        Area a2 = new Area(rectangle);

        // perform operation on two areas
        switch (operation) {
            case "add":
                a1.add(a2);
                break;
            case "intersect":
                a1.intersect(a2);
                break;
            case "subtract":
                a1.subtract(a2);
                break;
            case "exclusiveOr":
                a1.exclusiveOr(a2);
                break;
            default:
                // do nothing
        }
        return a1;
    }

    private static void drawShapesWithAlphaComposite(Graphics2D g2, AlphaComposite ac, Rectangle2D bounds, double margin) {
        double w = bounds.getWidth() / 1.5;
        double h = bounds.getHeight() / 1.5;

        // create an ellipse in the top left
        Ellipse2D ellipse = new Ellipse2D.Double(margin, margin, bounds.getWidth() / 1.5, bounds.getHeight() / 1.5);

        // create a rectangle in the bottom right
        Rectangle2D rectangle = new Rectangle2D.Double(bounds.getWidth() - margin - w, bounds.getHeight() - margin - h, w, h);

        g2.setComposite(AlphaComposite.SrcOver);
        g2.setPaint(Color.RED);
        g2.fill(ellipse);

        g2.setComposite(ac);
        g2.setPaint(Color.BLUE);
        g2.fill(rectangle);
    }

    /**
     * Draws a test sheet consisting of a number of tiles, each one testing one or
     * more features of Java2D.
     *
     * @param g2  the graphics target.
     */
    private static void drawTestSheet(Graphics2D g2) {
        int row = 0;
        Rectangle2D bounds = new Rectangle2D.Double(0.0, 0.0, TILE_WIDTH, TILE_HEIGHT);

        moveTo(0, row, g2);
        drawTileLineCaps(g2);

        moveTo(1, row, g2);
        drawTileLineCapAndDash(g2);

        row++;  // *****
        moveTo(0, row, g2);
        drawTileString(g2);

        row++;  // *****
        moveTo(0, row, g2);
        ShapeTests.drawEllipse2D(g2, bounds, 5, Color.RED, null);
        moveTo(1, row, g2);
        ShapeTests.drawEllipse2D(g2, bounds, 5, null, new BasicStroke(3.0f));
        moveTo(2, row, g2);
        ShapeTests.drawEllipse2D(g2, bounds, 5, Color.LIGHT_GRAY, new BasicStroke(3.0f, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND, 4f, new float[] { 8f, 8f }, 0f));

        row++;  // *****
        moveTo(0, row, g2);
        fillRectangle(g2, Color.DARK_GRAY);

        moveTo(1, row, g2);
        g2.setPaint(Color.BLUE);
        drawTileRectangleStroked(g2, new BasicStroke(3.0f));

        moveTo(2, row, g2);
        g2.setPaint(Color.BLUE);
        drawTileRectangleFilledAndStroked(g2, Color.YELLOW, new BasicStroke(3.0f), Color.GRAY);

        moveTo(3, row, g2);
        drawTileRoundRectangleFilled(g2, Color.DARK_GRAY);

        moveTo(4, row, g2);
        g2.setPaint(Color.BLUE);
        drawTileRoundRectangleStroked(g2, new BasicStroke(3.0f));

        moveTo(5, row, g2);
        g2.setPaint(Color.BLUE);
        drawTileRoundRectangleFilledAndStroked(g2, Color.YELLOW, new BasicStroke(3.0f), Color.GRAY);

        row++;  // *****
        moveTo(0, row, g2);
        GradientPaint gp = new GradientPaint(0f, 0f, Color.YELLOW, TILE_WIDTH, 0f, Color.RED);
        fillRectangle(g2, gp);

        // here we change the gradient to start one quarter of the way across the shape
        // and finish at the three quarter mark - the default should be non-cyclic
        moveTo(1, row, g2);
        float p = TILE_WIDTH / 4.0f;
        GradientPaint gp2 = new GradientPaint(p, 0f, Color.YELLOW, p * 3, 0f, Color.RED);
        fillRectangle(g2, gp2);

        moveTo(2, row, g2);
        GradientPaint gp3 = new GradientPaint(p, 0f, Color.YELLOW, p * 3, 0f, Color.RED, true);
        fillRectangle(g2, gp3);

        row++;  // *****
        moveTo(0, row, g2);
        Point2D center = new Point2D.Double(TILE_WIDTH / 2, TILE_HEIGHT / 2);
        fillRectangle(g2, new RadialGradientPaint(center, (float) (TILE_HEIGHT / 2.0 - 5), new float[] {0.0f, 0.75f, 1.0f}, new Color[] {Color.YELLOW, Color.RED, Color.LIGHT_GRAY}));

        moveTo(1, row, g2);
        fillRectangle(g2, new RadialGradientPaint(center, (float) (TILE_HEIGHT / 2.0 - 5), new float[] {0.0f, 0.75f, 1.0f}, new Color[] {Color.YELLOW, Color.RED, Color.LIGHT_GRAY}, MultipleGradientPaint.CycleMethod.REPEAT));

        moveTo(2, row, g2);
        fillRectangle(g2, new RadialGradientPaint(center, (float) (TILE_HEIGHT / 2.0 - 5), new float[] {0.0f, 0.75f, 1.0f}, new Color[] {Color.YELLOW, Color.RED, Color.LIGHT_GRAY}, MultipleGradientPaint.CycleMethod.REFLECT));

        moveTo(3, row, g2);
        Point2D focus = new Point2D.Double(TILE_WIDTH / 3, TILE_HEIGHT / 3);
        fillRectangle(g2, new RadialGradientPaint(center, (float) (TILE_HEIGHT / 2.0 - 5), focus, new float[] {0.0f, 0.75f, 1.0f}, new Color[] {Color.YELLOW, Color.RED, Color.LIGHT_GRAY}, MultipleGradientPaint.CycleMethod.NO_CYCLE));

        moveTo(4, row, g2);
        fillRectangle(g2, new RadialGradientPaint(center, (float) (TILE_HEIGHT / 2.0 - 5), focus, new float[] {0.0f, 0.75f, 1.0f}, new Color[] {Color.YELLOW, Color.RED, Color.LIGHT_GRAY}, MultipleGradientPaint.CycleMethod.REPEAT));

        moveTo(5, row, g2);
        fillRectangle(g2, new RadialGradientPaint(center, (float) (TILE_HEIGHT / 2.0 - 5), focus, new float[] {0.0f, 0.75f, 1.0f}, new Color[] {Color.YELLOW, Color.RED, Color.LIGHT_GRAY}, MultipleGradientPaint.CycleMethod.REFLECT));

        row++;  // *****
        moveTo(0, row, g2);
        ShapeTests.drawQuadCurve2D(g2, bounds, new BasicStroke(3.0f), Color.RED);
        moveTo(1, row, g2);
        ShapeTests.fillQuadCurve2D(g2, bounds, new BasicStroke(3.0f), Color.RED);
        moveTo(2, row, g2);
        ShapeTests.drawCubicCurve2D(g2, bounds, new BasicStroke(3.0f), Color.RED);
        moveTo(3, row, g2);
        ShapeTests.fillCubicCurve2D(g2, bounds, new BasicStroke(3.0f), Color.RED);

        row++;  // *****
        moveTo(0, row, g2);
        drawTileShapeFilledAndStroked(g2, createArc2D(Arc2D.PIE), Color.LIGHT_GRAY, new BasicStroke(1.0f), Color.BLACK);
        moveTo(1, row, g2);
        drawTileShapeFilledAndStroked(g2, createArc2D(Arc2D.OPEN), Color.LIGHT_GRAY, new BasicStroke(1.0f), Color.BLACK);
        moveTo(2, row, g2);
        drawTileShapeFilledAndStroked(g2, createArc2D(Arc2D.CHORD), Color.LIGHT_GRAY, new BasicStroke(1.0f), Color.BLACK);

        row++;  // *****
        moveTo(0, row, g2);
        drawAndFillArea(g2, createCombinedArea("add", new Rectangle2D.Double(0.0, 0.0, TILE_WIDTH, TILE_HEIGHT), 5.0));
        moveTo(1, row, g2);
        drawAndFillArea(g2, createCombinedArea("intersect", new Rectangle2D.Double(0.0, 0.0, TILE_WIDTH, TILE_HEIGHT), 5.0));
        moveTo(2, row, g2);
        drawAndFillArea(g2, createCombinedArea("subtract", new Rectangle2D.Double(0.0, 0.0, TILE_WIDTH, TILE_HEIGHT), 5.0));
        moveTo(3, row, g2);
        drawAndFillArea(g2, createCombinedArea("exclusiveOr", new Rectangle2D.Double(0.0, 0.0, TILE_WIDTH, TILE_HEIGHT), 5.0));

        row++;  // *****
        // show a set of tiles with standard AlphaComposite settings
        moveTo(0, row, g2);
        drawShapesWithAlphaComposite(g2, AlphaComposite.Clear, new Rectangle2D.Double(0.0, 0.0, TILE_WIDTH, TILE_HEIGHT), 5.0);
        moveTo(1, row, g2);
        drawShapesWithAlphaComposite(g2, AlphaComposite.Src, new Rectangle2D.Double(0.0, 0.0, TILE_WIDTH, TILE_HEIGHT), 5.0);
        moveTo(2, row, g2);
        drawShapesWithAlphaComposite(g2, AlphaComposite.SrcOver, new Rectangle2D.Double(0.0, 0.0, TILE_WIDTH, TILE_HEIGHT), 5.0);
        moveTo(3, row, g2);
        drawShapesWithAlphaComposite(g2, AlphaComposite.DstOver, new Rectangle2D.Double(0.0, 0.0, TILE_WIDTH, TILE_HEIGHT), 5.0);
        moveTo(4, row, g2);
        drawShapesWithAlphaComposite(g2, AlphaComposite.SrcIn, new Rectangle2D.Double(0.0, 0.0, TILE_WIDTH, TILE_HEIGHT), 5.0);
        moveTo(5, row, g2);
        drawShapesWithAlphaComposite(g2, AlphaComposite.DstIn, new Rectangle2D.Double(0.0, 0.0, TILE_WIDTH, TILE_HEIGHT), 5.0);
        moveTo(6, row, g2);
        drawShapesWithAlphaComposite(g2, AlphaComposite.SrcOut, new Rectangle2D.Double(0.0, 0.0, TILE_WIDTH, TILE_HEIGHT), 5.0);
        moveTo(7, row, g2);
        drawShapesWithAlphaComposite(g2, AlphaComposite.DstOut, new Rectangle2D.Double(0.0, 0.0, TILE_WIDTH, TILE_HEIGHT), 5.0);
        moveTo(8, row, g2);
        drawShapesWithAlphaComposite(g2, AlphaComposite.Dst, new Rectangle2D.Double(0.0, 0.0, TILE_WIDTH, TILE_HEIGHT), 5.0);
        moveTo(9, row, g2);
        drawShapesWithAlphaComposite(g2, AlphaComposite.SrcAtop, new Rectangle2D.Double(0.0, 0.0, TILE_WIDTH, TILE_HEIGHT), 5.0);
        moveTo(10, row, g2);
        drawShapesWithAlphaComposite(g2, AlphaComposite.DstAtop, new Rectangle2D.Double(0.0, 0.0, TILE_WIDTH, TILE_HEIGHT), 5.0);

        row++;  // *****
        // show a set of tiles with standard AlphaComposite settings
        moveTo(0, row, g2);
        drawShapesWithAlphaComposite(g2, AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.6f), new Rectangle2D.Double(0.0, 0.0, TILE_WIDTH, TILE_HEIGHT), 5.0);
        moveTo(1, row, g2);
        drawShapesWithAlphaComposite(g2, AlphaComposite.getInstance(AlphaComposite.SRC, 0.6f), new Rectangle2D.Double(0.0, 0.0, TILE_WIDTH, TILE_HEIGHT), 5.0);
        moveTo(2, row, g2);
        drawShapesWithAlphaComposite(g2, AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f), new Rectangle2D.Double(0.0, 0.0, TILE_WIDTH, TILE_HEIGHT), 5.0);
        moveTo(3, row, g2);
        drawShapesWithAlphaComposite(g2, AlphaComposite.getInstance(AlphaComposite.DST_OVER, 0.6f), new Rectangle2D.Double(0.0, 0.0, TILE_WIDTH, TILE_HEIGHT), 5.0);
        moveTo(4, row, g2);
        drawShapesWithAlphaComposite(g2, AlphaComposite.getInstance(AlphaComposite.SRC_IN, 0.6f), new Rectangle2D.Double(0.0, 0.0, TILE_WIDTH, TILE_HEIGHT), 5.0);
        moveTo(5, row, g2);
        drawShapesWithAlphaComposite(g2, AlphaComposite.getInstance(AlphaComposite.DST_IN, 0.6f), new Rectangle2D.Double(0.0, 0.0, TILE_WIDTH, TILE_HEIGHT), 5.0);
        moveTo(6, row, g2);
        drawShapesWithAlphaComposite(g2, AlphaComposite.getInstance(AlphaComposite.SRC_OUT, 0.6f), new Rectangle2D.Double(0.0, 0.0, TILE_WIDTH, TILE_HEIGHT), 5.0);
        moveTo(7, row, g2);
        drawShapesWithAlphaComposite(g2, AlphaComposite.getInstance(AlphaComposite.DST_OUT, 0.6f), new Rectangle2D.Double(0.0, 0.0, TILE_WIDTH, TILE_HEIGHT), 5.0);
        moveTo(8, row, g2);
        drawShapesWithAlphaComposite(g2, AlphaComposite.getInstance(AlphaComposite.DST, 0.6f), new Rectangle2D.Double(0.0, 0.0, TILE_WIDTH, TILE_HEIGHT), 5.0);
        moveTo(9, row, g2);
        drawShapesWithAlphaComposite(g2, AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.6f), new Rectangle2D.Double(0.0, 0.0, TILE_WIDTH, TILE_HEIGHT), 5.0);
        moveTo(10, row, g2);
        drawShapesWithAlphaComposite(g2, AlphaComposite.getInstance(AlphaComposite.DST_ATOP, 0.6f), new Rectangle2D.Double(0.0, 0.0, TILE_WIDTH, TILE_HEIGHT), 5.0);

        row++;  // *****
        moveTo(0, row, g2);
        ClippingTests.fillRectangularClippingRegions(g2, bounds);

        moveTo(1, row, g2);
        drawTileArc2DWithRectangularClip(g2);

    }
    private static Image BUG_IMAGE;


    /**
     * Draws a single tile - useful for testing just one feature.
     *
     * @param g2  the graphics target.
     */
    private static void drawTestSingle(Graphics2D g2) {
        moveTo(0, 0, g2);
        Rectangle2D bounds = new Rectangle2D.Double(0, 0, TILE_WIDTH, TILE_HEIGHT);
        //drawCubicCurve2D(g2, new BasicStroke(3.0f), Color.RED);
        Point2D center = new Point2D.Double(TILE_WIDTH / 2, TILE_HEIGHT / 2);
        //fillRectangle(g2, new RadialGradientPaint(center, (float) (TILE_HEIGHT / 2.0 - 5), new float[] {0.0f, 0.75f, 1.0f}, new Color[] {Color.YELLOW, Color.RED, Color.GRAY}));
        //drawAxes(g2, center, 40.0, new BasicStroke(1f), Color.GRAY);
        FontTests.drawStringBounds(g2, bounds);
    }

    /**
     * Renders the test output.
     *
     * @param g2  the graphics target.
     */
    private static void drawTestOutput(Graphics2D g2, boolean single) {
        if (single) {
            drawTestSingle(g2);
        } else {
            drawTestSheet(g2);
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
        drawTestOutput(g2, single);
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
        drawTestOutput(g2, single);
        if (single) {
            fileName += "-single.png";
        } else {
            fileName += ".png";
        }
        ImageIO.write(image, "png", new File(fileName));
    }

    /**
     * Creates Java2D output that exercises many features of the API.
     *
     * @param args
     */
    public static void main(String[] args) throws IOException {
        //BUG_IMAGE = ImageIO.read( ClassLoader.getSystemResource( "bug.png"));
        boolean single = false;
        testSkijaGraphics2D("skija", single);
        testJava2D("java2D", single);
    }

}
