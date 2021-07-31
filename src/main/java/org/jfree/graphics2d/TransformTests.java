package org.jfree.graphics2d;

import java.awt.*;
import java.awt.geom.*;

/**
 * A collection of tests that exercise transformations.
 */
public class TransformTests {
    /**
     * Draws a small upward pointer with the tip at (x, y).
     *
     * @param g2
     * @param x
     * @param y
     */
    private static void drawArrowUp(Graphics2D g2, double x, double y) {
        Path2D path = new Path2D.Double();
        path.moveTo(x, y);
        path.lineTo(x + 4, y + 4);
        path.lineTo(x - 4, y + 4);
        path.closePath();
        g2.fill(path);
    }

    private static void drawArrowRight(Graphics2D g2, double x, double y) {
        Path2D path = new Path2D.Double();
        path.moveTo(x, y);
        path.lineTo(x - 4, y - 4);
        path.lineTo(x - 4, y + 4);
        path.closePath();
        g2.fill(path);
    }

    /**
     * Draws an x and y axis with zero at the center.
     *
     * @param g2
     * @param center
     * @param length
     * @param stroke
     * @param paint
     */
    private static void drawAxes(Graphics2D g2, Point2D center, double length, Stroke stroke, Paint paint) {
        g2.setStroke(stroke);
        g2.setPaint(paint);
        // draw x-axis
        Line2D xAxis = new Line2D.Double(center.getX() - length, center.getY(), center.getX() + length, center.getY());
        g2.draw(xAxis);
        drawArrowRight(g2, center.getX() + length, center.getY());
        // draw y-axis
        Line2D yAxis = new Line2D.Double(center.getX(), center.getY() - length, center.getX(), center.getY() + length);
        g2.draw(yAxis);
        drawArrowUp(g2, center.getX(), center.getY() - length);
    }

    /**
     * Tests translations of a shape.
     *
     * @param g2  the graphics target.
     * @param bounds  the bounds.
     * @param shape  the shape (should occupy the top-left quadrant of the bounds)
     * @param fillPaint  the fill paint.
     * @param stroke  the outline stroke.
     * @param outlinePaint  the outline paint.
     */
    static void translateShape(Graphics2D g2, Rectangle2D bounds, Shape shape, Paint fillPaint, Stroke stroke, Paint outlinePaint) {
        AffineTransform saved = g2.getTransform();

        // show the quadrants and the original shape which should be in the top left
        g2.setPaint(Color.LIGHT_GRAY);
        g2.setStroke(new BasicStroke(0.5f));
        g2.draw(new Line2D.Double(bounds.getX(), bounds.getCenterY(), bounds.getMaxX(), bounds.getCenterY()));
        g2.draw(new Line2D.Double(bounds.getCenterX(), bounds.getMinY(), bounds.getCenterX(), bounds.getMaxY()));
        g2.fill(shape);

        // translate in x-direction and draw
        g2.translate(bounds.getWidth() / 2.0, 0.0);
        g2.setStroke(stroke);
        g2.setPaint(outlinePaint);
        g2.draw(shape);

        // translate in y direction and fill
        g2.setTransform(saved);
        g2.translate(0.0, bounds.getHeight() / 2.0);
        g2.setPaint(fillPaint);
        g2.fill(shape);

        // translate in both x and Y and fill and stroke
        g2.setTransform(saved);
        g2.translate(bounds.getWidth() / 2.0, bounds.getHeight() / 2.0);
        g2.setPaint(fillPaint);
        g2.fill(shape);
        g2.setStroke(stroke);
        g2.setPaint(outlinePaint);
        g2.draw(shape);

        g2.setTransform(saved);
    }

    /**
     * Draws a rotated shape (over top of a light grey fill of the original shape).
     *
     * @param g2
     * @param bounds
     * @param shape  the shape (should be centered within bounds).
     */
    static void rotateShape(Graphics2D g2, Rectangle2D bounds, Shape shape, double theta, Paint fillPaint, Stroke stroke, Paint outlinePaint) {
        AffineTransform saved = g2.getTransform();
        g2.setPaint(Color.LIGHT_GRAY);
        g2.fill(shape);

        g2.rotate(theta, bounds.getCenterX(), bounds.getCenterY());
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        g2.setPaint(fillPaint);
        g2.fill(shape);
        g2.setStroke(stroke);
        g2.setPaint(outlinePaint);
        g2.draw(shape);
        g2.setComposite(AlphaComposite.SrcOver);
        g2.setTransform(saved);
    }

    /**
     * Draws a sheared shape (over top of a light grey fill of the original shape).
     *
     * @param g2
     * @param bounds
     * @param shape  the shape (should be centered within bounds).
     */
    static void shearShape(Graphics2D g2, Rectangle2D bounds, Shape shape, double dx, double dy, Paint fillPaint, Stroke stroke, Paint outlinePaint) {
        AffineTransform saved = g2.getTransform();
        g2.setPaint(Color.LIGHT_GRAY);
        g2.fill(shape);

        g2.shear(dx, dy);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        g2.setPaint(fillPaint);
        g2.fill(shape);
        g2.setStroke(stroke);
        g2.setPaint(outlinePaint);
        g2.draw(shape);
        g2.setComposite(AlphaComposite.SrcOver);
        g2.setTransform(saved);
    }
}
