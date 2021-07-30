package org.jfree.graphics2d;

import java.awt.*;
import java.awt.geom.*;

public class ShapeTests {

    static void fillAndStrokeQuadCurve2D(Graphics2D g2, Rectangle2D bounds, double margin, Paint paint, Stroke stroke, Paint outlinePaint) {
        double w = bounds.getWidth();
        double h = bounds.getHeight();
        Point2D pt0 = new Point2D.Double(margin, h - margin);
        Point2D pt1 = new Point2D.Double(w - margin, h - margin);
        Point2D cp = new Point2D.Double(w / 2.0, margin);

        // draw guide lines
        g2.setStroke(new BasicStroke(0.5f));
        g2.setPaint(Color.GRAY);
        g2.drawLine((int) pt0.getX(), (int) pt0.getY(), (int) cp.getX(), (int) cp.getY());
        g2.drawLine((int) pt1.getX(), (int) pt1.getY(), (int) cp.getX(), (int) cp.getY());
        QuadCurve2D curve = new QuadCurve2D.Double(pt0.getX(), pt0.getY(), cp.getX(), cp.getY(), pt1.getX(), pt1.getY());
        if (paint != null) {
            g2.setPaint(paint);
            g2.fill(curve);
        }
        if (stroke != null && outlinePaint != null) {
            g2.setStroke(stroke);
            g2.setPaint(outlinePaint);
            g2.draw(curve);
        }
    }

    static void fillAndStrokeCubicCurve2D(Graphics2D g2, Rectangle2D bounds, double margin, Paint paint, Stroke stroke, Paint outlinePaint) {
        double w = bounds.getWidth();
        double h = bounds.getHeight();

        Point2D pt0 = new Point2D.Double(margin, h - margin);
        Point2D pt1 = new Point2D.Double(w - 4 * margin, margin);
        Point2D cp1 = new Point2D.Double(2 * margin, margin);
        Point2D cp2 = new Point2D.Double(w - margin, h - margin);

        // draw guide lines
        g2.setStroke(new BasicStroke(0.5f));
        g2.setPaint(Color.GRAY);
        g2.drawLine((int) pt0.getX(), (int) pt0.getY(), (int) cp1.getX(), (int) cp1.getY());
        g2.drawLine((int) pt1.getX(), (int) pt1.getY(), (int) cp2.getX(), (int) cp2.getY());

        CubicCurve2D.Double curve = new CubicCurve2D.Double();
        curve.setCurve(pt0, cp1, cp2, pt1);
        if (paint != null) {
            g2.setPaint(paint);
            g2.fill(curve);
        }
        if (stroke != null && outlinePaint != null) {
            g2.setStroke(stroke);
            g2.setPaint(outlinePaint);
            g2.draw(curve);
        }
    }

    /**
     * Creates an Arc2D instance with the specified style.
     *
     * @param style
     *
     * @return
     */
     static Arc2D createArc2D(int style, Rectangle2D bounds, int margin) {
        Arc2D arc = new Arc2D.Double(margin, margin, bounds.getWidth() - 2 * margin, bounds.getHeight() - 2 * margin, 45, 270, style);
        return arc;
    }

    /**
     * Draws a shape with the specified stroke and fill.
     *
     * @param g2  the graphics target
     * @param shape  the shape ({@code null} not permitted)
     * @param paint  the paint (if {@code null}, then the shape is not filled).
     * @param stroke  the stroke (if {@code null}, then the shape is not stroked).
     * @param outlinePaint  the outline paint (if {@code null}, then the shape is not stroked).
     */
     static void fillAndStrokeShape(Graphics2D g2, Shape shape, Paint paint, Stroke stroke, Paint outlinePaint) {
        if (paint != null) {
            g2.setPaint(paint);
            g2.fill(shape);
        }
        if (stroke != null && outlinePaint != null) {
            g2.setStroke(stroke);
            g2.setPaint(outlinePaint);
            g2.draw(shape);
        }
    }

    public static void drawLineCaps(Graphics2D g2, Rectangle2D bounds) {
        g2.setPaint(Color.BLACK);
        double delta = bounds.getHeight() / 4.0;
        double right = bounds.getWidth() - 10.0;
        Line2D line = new Line2D.Double(5.0, delta, right, delta);
        g2.setStroke(new BasicStroke(5.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
        g2.draw(line);
        line = new Line2D.Double(5.0, delta * 2, right, delta * 2);
        g2.setStroke(new BasicStroke(5.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.draw(line);
        g2.setStroke(new BasicStroke(5.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));
        line = new Line2D.Double(5.0, delta * 3, right, delta * 3);
        g2.draw(line);
    }

    public static void drawLineCapAndDash(Graphics2D g2, Rectangle2D bounds) {
        g2.setPaint(Color.RED);
        double delta = bounds.getHeight() / 4.0;
        double xmax = bounds.getWidth() - 10.0;
        Line2D line = new Line2D.Double(5.0, delta, xmax, delta);
        g2.setStroke(new BasicStroke(5.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 10f, new float[] { 10.0f, 5.0f, 20.0f, 10.0f }, 0f));
        g2.draw(line);
        line = new Line2D.Double(5.0, delta * 2, xmax, delta * 2);
        g2.setStroke(new BasicStroke(5.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10f, new float[] { 10.0f, 5.0f, 20.0f, 10.0f }, 0f));
        g2.draw(line);
        g2.setStroke(new BasicStroke(5.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND, 10f, new float[] { 10.0f, 5.0f, 20.0f, 10.0f }, 0f));
        line = new Line2D.Double(5.0, delta * 3, xmax, delta * 3);
        g2.draw(line);
    }

    public static void drawLines(Graphics2D g2, Rectangle2D bounds, double margin, Stroke stroke) {
        double maxX = bounds.getWidth() - margin;
        double maxY = bounds.getHeight() - margin;
        double deltaX = (bounds.getWidth() - 2 * margin) / 3.0;
        double deltaY = (bounds.getHeight() - 2 * margin) / 3.0;
        g2.setPaint(Color.BLUE);
        g2.setStroke(stroke);
        g2.draw(new Line2D.Double(margin, margin, maxX, margin));
        g2.draw(new Line2D.Double(margin, margin, maxX, margin + deltaY));
        g2.draw(new Line2D.Double(margin, margin, maxX, margin + deltaY * 2));
        g2.draw(new Line2D.Double(margin, margin, maxX, maxY));
        g2.draw(new Line2D.Double(margin, margin, maxX - deltaX, maxY));
        g2.draw(new Line2D.Double(margin, margin, maxX - deltaX * 2, maxY));
        g2.draw(new Line2D.Double(margin, margin, margin, maxY));
    }

    /**
     * Fills the specified area and then draws its outline.  This tile is used to
     * show various combinations of Constructive Area Geometry (CAG) operations.
     *
     * @param g2  the graphics target.
     * @param area  the shape.
     */
    static void drawAndFillArea(Graphics2D g2, Area area) {
        g2.setPaint(Color.LIGHT_GRAY);
        g2.fill(area);

        g2.setPaint(Color.BLACK);
        g2.setStroke(new BasicStroke(1.0f));
        g2.draw(area);
    }
}
