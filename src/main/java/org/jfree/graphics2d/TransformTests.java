package org.jfree.graphics2d;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

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

}
