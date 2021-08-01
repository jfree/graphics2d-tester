package org.jfree.graphics2d;

import java.awt.*;
import java.awt.geom.*;

/**
 * Tests relating to drawing and filling shapes.
 */
public class ShapeTests {

    static QuadCurve2D createQuadCurve2D(Rectangle2D bounds, double margin) {
        double w = bounds.getWidth();
        double h = bounds.getHeight();
        Point2D pt0 = new Point2D.Double(margin, h - margin);
        Point2D pt1 = new Point2D.Double(w - margin, h - margin);
        Point2D cp = new Point2D.Double(w / 2.0, margin);
        return new QuadCurve2D.Double(pt0.getX(), pt0.getY(), cp.getX(), cp.getY(), pt1.getX(), pt1.getY());
    }

    static CubicCurve2D createCubicCurve2D(Rectangle2D bounds, double margin) {
        double w = bounds.getWidth();
        double h = bounds.getHeight();

        Point2D pt0 = new Point2D.Double(margin, h - margin);
        Point2D pt1 = new Point2D.Double(w - 4 * margin, margin);
        Point2D cp1 = new Point2D.Double(2 * margin, margin);
        Point2D cp2 = new Point2D.Double(w - margin, h - margin);
        CubicCurve2D.Double curve = new CubicCurve2D.Double();
        curve.setCurve(pt0, cp1, cp2, pt1);
        return curve;
    }

    /**
     * Fill and/or strokes a QuadCurve2D instance.
     *
     * @param g2  the graphics target.
     * @param bounds  the bounds.
     * @param margin  the margin.
     * @param paint  the fill paint (no fill if {@code null}).
     * @param stroke  the outline stroke (no outline if {@code null}).
     * @param outlinePaint  the outline paint (no outline if {@code null}).
     */
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

    /**
     * Fill and/or strokes a CubicCurve2D instance.
     *
     * @param g2  the graphics target.
     * @param bounds  the bounds.
     * @param margin  the margin.
     * @param paint  the fill paint (no fill if {@code null}).
     * @param stroke  the outline stroke (no outline if {@code null}).
     * @param outlinePaint  the outline paint (no outline if {@code null}).
     */
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
    static Arc2D createArc2D(int style, double start, double extent, Rectangle2D bounds, int margin) {
        Arc2D arc = new Arc2D.Double(margin, margin, bounds.getWidth() - 2 * margin, bounds.getHeight() - 2 * margin, start, extent, style);
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

    static void transformShape(Graphics2D g2, Rectangle2D bounds, Shape shape, TransformType transformType, Paint paint, Stroke stroke, Paint outlinePaint) {
        AffineTransform savedTransform = g2.getTransform();
        g2.translate(bounds.getWidth() / 2.0, bounds.getHeight() / 2.0);
        g2.scale(0.5, 0.5);
        switch (transformType) {
            case SCALE:
                // we already scaled
                break;
            case ROTATE:
                g2.rotate(Math.PI / 3.0, bounds.getCenterX(), bounds.getCenterY());
                break;
            case SHEAR:
                g2.shear(2.0, 2.0);
                break;
            default:
                break;
        }
        if (paint != null) {
            g2.setPaint(paint);
            g2.fill(shape);
        }
        if (stroke != null && outlinePaint != null) {
            g2.setStroke(stroke);
            g2.setPaint(outlinePaint);
            g2.draw(shape);
        }
        g2.setTransform(savedTransform);
    }

    /**
     * Draws three lines with different CAP settings (BUTT, ROUND and SQUARE).
     *
     * @param g2  the graphics target.
     * @param bounds  the bounds.
     * @param strokeWidth  the stroke width.
     * @param margin  the margin.
     * @param paint  the paint.
     */
    public static void drawLineCaps(Graphics2D g2, Rectangle2D bounds, double margin, float strokeWidth, Paint paint) {
        g2.setPaint(paint);
        double midY = bounds.getCenterY();
        double deltaY = bounds.getHeight() / 4.0;
        double left = bounds.getX() + 2 * margin;
        double right = bounds.getWidth() - 2 * margin;
        Line2D line = new Line2D.Double(left, midY - deltaY, right, midY - deltaY);
        g2.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
        g2.draw(line);
        line = new Line2D.Double(left, midY, right, midY);
        g2.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.draw(line);
        g2.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));
        line = new Line2D.Double(left, midY + deltaY, right, midY + deltaY);
        g2.draw(line);
    }
    /**
     * Draws three lines with different CAP settings (BUTT, ROUND and SQUARE).
     *
     * @param g2  the graphics target.
     * @param bounds  the bounds.
     * @param margin  the margin.
     */
    public static void drawLineCapAndDash(Graphics2D g2, Rectangle2D bounds, float strokeWidth, double margin) {
        g2.setPaint(Color.RED);
        double midY = bounds.getCenterY();
        double deltaY = bounds.getHeight() / 4.0;
        double left = bounds.getX() + 2 * margin;
        double right = bounds.getWidth() - 2 * margin;
        Line2D line = new Line2D.Double(left, midY - deltaY, right, midY - deltaY);
        g2.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 10f, new float[] { 4.0f, 8.0f, 10.0f, 8.0f }, 0f));
        g2.draw(line);
        line = new Line2D.Double(left, midY, right, midY);
        g2.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10f, new float[] { 4.0f, 8.0f, 10.0f, 8.0f }, 0f));
        g2.draw(line);
        g2.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND, 10f, new float[] { 4.0f, 8.0f, 10.0f, 8.0f }, 0f));
        line = new Line2D.Double(left, midY + deltaY, right, midY + deltaY);
        g2.draw(line);
    }

    /**
     * Draws a fan-out of lines to show line rendering.
     *
     * @param g2  the graphics target.
     * @param bounds  the bounds.
     * @param margin  the margin.
     * @param stroke  the line stroke.
     * @param paint  the line color.
     */
    public static void drawLines(Graphics2D g2, Rectangle2D bounds, double margin, Stroke stroke, Paint paint) {
        double maxX = bounds.getWidth() - margin;
        double maxY = bounds.getHeight() - margin;
        double deltaX = (bounds.getWidth() - 2 * margin) / 3.0;
        double deltaY = (bounds.getHeight() - 2 * margin) / 3.0;
        g2.setPaint(paint);
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

    /**
     * Creates an area from two shapes.
     *
     * @param operation  the operation ("add", "intersect", "subtract" or "exclusiveOr")
     * @param bounds  the bounds (controls the size of the shapes).
     * @param margin  the margin.
     *
     * @return An area.
     */
    static Area createCombinedArea(String operation, Rectangle2D bounds, double margin) {
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

    /**
     * Creates a path that is used for some tests.
     *
     * @param bounds  the cell bounds.
     * @param margin  the margin.
     *
     * @return A new path instance.
     */
    static Path2D createPath2D(Rectangle2D bounds, double margin) {
        double deltaY = (bounds.getHeight() - 2 * margin) / 5.0;
        GeneralPath path = new GeneralPath();
        path.setWindingRule(GeneralPath.WIND_EVEN_ODD);
        double x0 = margin;
        double x1 = bounds.getWidth() - margin;
        double y0 = margin;
        double y1 = y0 + deltaY;
        double y2 = y0 + deltaY * 2;
        double y3 = y0 + deltaY * 3;
        double y4 = y0 + deltaY * 4;
        double y5 = y0 + deltaY * 5;
        path.moveTo(x0, y0);
        path.lineTo(x1, y0);
        path.lineTo(x1, y5);
        path.lineTo(x0, y5);
        path.lineTo(x0, y1);
        path.lineTo(x1 - margin, y1);
        path.lineTo(x1 - margin, y4);
        path.lineTo(x0 + margin, y4);
        path.lineTo(x0 + margin, y2);
        path.lineTo(x1 - margin * 2, y2);
        path.lineTo(x1 - margin * 2, y3);
        path.lineTo(bounds.getCenterX(), y3);
        path.closePath();
        return path;
    }

    static void drawShapesWithAlphaComposite(Graphics2D g2, AlphaComposite ac, Rectangle2D bounds, double margin) {
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
}