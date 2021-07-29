package org.jfree.graphics2d;

import java.awt.*;
import java.awt.geom.*;

public class ShapeTests {

    /**
     * Draws a {@code QuadCurve2D} instance.
     *
     * @param g2  the graphics target.
     * @param stroke  the stroke.
     * @param paint  the paint.
     */
    static void drawQuadCurve2D(Graphics2D g2, Rectangle2D bounds, Stroke stroke, Paint paint) {
        double w = bounds.getWidth();
        double h = bounds.getHeight();
        Point2D pt0 = new Point2D.Double(5.0, h - 5.0);
        Point2D pt1 = new Point2D.Double(w - 5.0, h - 5.0);
        Point2D cp = new Point2D.Double(w / 2.0, 5.0);
        g2.setStroke(new BasicStroke(0.5f));
        g2.setPaint(Color.GRAY);
        g2.drawLine((int) pt0.getX(), (int) pt0.getY(), (int) cp.getX(), (int) cp.getY());
        g2.drawLine((int) pt1.getX(), (int) pt1.getY(), (int) cp.getX(), (int) cp.getY());
        g2.setStroke(stroke);
        g2.setPaint(paint);
        QuadCurve2D curve = new QuadCurve2D.Double(pt0.getX(), pt0.getY(), cp.getX(), cp.getY(), pt1.getX(), pt1.getY());
        g2.draw(curve);
    }

    static void fillQuadCurve2D(Graphics2D g2, Rectangle2D bounds, Stroke stroke, Paint paint) {
        double w = bounds.getWidth();
        double h = bounds.getHeight();
        Point2D pt0 = new Point2D.Double(5.0, h - 5.0);
        Point2D pt1 = new Point2D.Double(w - 5.0, h - 5.0);
        Point2D cp = new Point2D.Double(w / 2.0, 5.0);
        g2.setStroke(new BasicStroke(0.5f));
        g2.setPaint(Color.GRAY);
        g2.drawLine((int) pt0.getX(), (int) pt0.getY(), (int) cp.getX(), (int) cp.getY());
        g2.drawLine((int) pt1.getX(), (int) pt1.getY(), (int) cp.getX(), (int) cp.getY());
        g2.setStroke(stroke);
        g2.setPaint(paint);
        QuadCurve2D curve = new QuadCurve2D.Double(pt0.getX(), pt0.getY(), cp.getX(), cp.getY(), pt1.getX(), pt1.getY());
        g2.fill(curve);
    }

    /**
     * Draws a {@code CubicCurve2D} instance.
     *
     * @param g2  the graphics target.
     * @param stroke  the stroke.
     * @param paint  the paint.
     */
    static void drawCubicCurve2D(Graphics2D g2, Rectangle2D bounds, Stroke stroke, Paint paint) {
        double w = bounds.getWidth();
        double h = bounds.getHeight();
        Point2D pt0 = new Point2D.Double(5.0, h - 5.0);
        Point2D pt1 = new Point2D.Double(w - 20.0, 5.0);
        Point2D cp1 = new Point2D.Double(10.0, 5.0);
        Point2D cp2 = new Point2D.Double(w - 5.0, h - 5.0);
        g2.setStroke(new BasicStroke(0.5f));
        g2.setPaint(Color.GRAY);
        g2.drawLine((int) pt0.getX(), (int) pt0.getY(), (int) cp1.getX(), (int) cp1.getY());
        g2.drawLine((int) pt1.getX(), (int) pt1.getY(), (int) cp2.getX(), (int) cp2.getY());
        g2.setStroke(stroke);
        g2.setPaint(paint);
        CubicCurve2D.Double curve = new CubicCurve2D.Double();
        curve.setCurve(pt0, cp1, cp2, pt1);
        g2.draw(curve);
    }

    static void fillCubicCurve2D(Graphics2D g2, Rectangle2D bounds, Stroke stroke, Paint paint) {
        double w = bounds.getWidth();
        double h = bounds.getHeight();
        Point2D pt0 = new Point2D.Double(5.0, h - 5.0);
        Point2D pt1 = new Point2D.Double(w - 20.0, 5.0);
        Point2D cp1 = new Point2D.Double(10.0, 5.0);
        Point2D cp2 = new Point2D.Double(w - 5.0, h - 5.0);
        g2.setStroke(new BasicStroke(0.5f));
        g2.setPaint(Color.GRAY);
        g2.drawLine((int) pt0.getX(), (int) pt0.getY(), (int) cp1.getX(), (int) cp1.getY());
        g2.drawLine((int) pt1.getX(), (int) pt1.getY(), (int) cp2.getX(), (int) cp2.getY());
        g2.setStroke(stroke);
        g2.setPaint(paint);
        CubicCurve2D.Double curve = new CubicCurve2D.Double();
        curve.setCurve(pt0, cp1, cp2, pt1);
        g2.fill(curve);
    }

    /**
     * Fills then strokes an ellipse.
     *
     * @param g2  the graphics target.
     * @param bounds  the cell bounds.
     * @param margin  the margin.
     * @param fillPaint  the fill paint (if {@code null}, no fill is performed).
     * @param stroke  the outline stroke (if {@code null}, no outline is drawn).
     */
    static void drawEllipse2D(Graphics2D g2, Rectangle2D bounds, int margin, Paint fillPaint, Stroke stroke) {
        Ellipse2D ellipse = new Ellipse2D.Double(margin, margin, bounds.getWidth() - 2 * margin, bounds.getHeight() - 2 * margin);
        if (fillPaint != null) {
            g2.setPaint(fillPaint);
            g2.fill(ellipse);
        }
        if (stroke != null) {
            g2.setStroke(stroke);
            g2.setPaint(Color.BLACK);
            g2.draw(ellipse);
        }
    }
}
