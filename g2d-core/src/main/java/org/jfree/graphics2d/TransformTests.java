/**
 * Graphics2D Tester
 *
 * (C)opyright 2021, 2022, by David Gilbert.
 */
package org.jfree.graphics2d;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

/**
 * A collection of tests that exercise transformations (translate, rotate, scale and shear).
 */
public class TransformTests {

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
     * @param g2  the graphics target.
     * @param bounds  the cell bounds.
     * @param shape  the shape (should be centered within bounds).
     * @param theta  the rotation (in radians).
     * @param fillPaint  the fill paint.
     * @param stroke  the outline stroke.
     * @param outlinePaint  the outline paint.
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
     * @param g2  the graphics target.
     * @param bounds  the cell bounds.
     * @param shape  the shape (should be centered within bounds).
     * @param dx  the x-shear factor.
     * @param dy  the y-shear factor.
     * @param fillPaint  the fill paint.
     * @param stroke  the stroke.
     * @param outlinePaint  the outline paint.
     */
    static void shearShape(Graphics2D g2, Rectangle2D bounds, Shape shape, double dx, double dy, Paint fillPaint, Stroke stroke, Paint outlinePaint) {
        AffineTransform saved = g2.getTransform();
        g2.setPaint(Color.LIGHT_GRAY);
        g2.fill(shape);

        // perform the shear around the center of the shape
        double tx = shape.getBounds().getCenterX();
        double ty = shape.getBounds().getCenterY();
        g2.translate(tx, ty);
        g2.shear(dx, dy);
        g2.translate(-tx, -ty);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        g2.setPaint(fillPaint);
        g2.fill(shape);
        g2.setStroke(stroke);
        g2.setPaint(outlinePaint);
        g2.draw(shape);
        g2.setComposite(AlphaComposite.SrcOver);
        g2.setTransform(saved);
    }

    private TransformTests() {
    }

}
