/*
 * Graphics2D Tester
 *
 * (C)opyright 2021, by David Gilbert.
 */
package org.jfree.graphics2d;

import java.awt.*;
import java.awt.font.LineMetrics;
import java.awt.font.TextAttribute;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

/**
 * A collection of tests related to fonts.
 */
public class FontTests {

    /**
     * Draws strings in SERIF, SANS_SERIF and MONOSPACED fonts.
     *
     * @param g2  the graphics target.
     * @param bounds  the bounds within which the text will be drawn.
     */
    public static void drawString(Graphics2D g2, Rectangle2D bounds) {
        g2.setPaint(Color.BLACK);
        g2.setFont(new Font(Font.SERIF, Font.PLAIN, 14));
        //g2.setFont(new Font("Times New Roman", Font.PLAIN, 14)); // remove later
        g2.drawString("Serif Font 14pt", (float) bounds.getX() + 5f, (float) bounds.getY() + 20);
        g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        //g2.setFont(new Font("Helvetica", Font.PLAIN, 14)); // remove later
        g2.drawString("Sans Serif Font 14pt", (float) bounds.getX() + 5f, (float) bounds.getY() + 40);
        g2.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        //g2.setFont(new Font("Courier New", Font.PLAIN, 14)); // remove later
        g2.drawString("Monospaced Font 14pt", (float) bounds.getX() + 5f, (float) bounds.getY() + 60);
    }

    /**
     * Draws a string then fetches and highlights the string bounds.  This tests
     * whether or not the graphics target is supporting font metrics correctly.
     *
     * @param g2  the graphics target.
     * @param bounds  the cell bounds.
     */
    public static void drawStringBounds(Graphics2D g2, Rectangle2D bounds) {
        double x = 5.0;
        double y = bounds.getMaxY() - 15.0;
        g2.setPaint(Color.BLACK);
        g2.setFont(new Font(Font.SERIF, Font.PLAIN, 36));
        g2.drawString("Java2D!", (float) x, (float) y);
        FontMetrics fm = g2.getFontMetrics();
        Rectangle2D strBounds = fm.getStringBounds("Java2D!", g2);
        g2.setStroke(new BasicStroke(0.5f));
        g2.setPaint(Color.BLUE);
        g2.translate(x, y);
        g2.draw(strBounds);
    }

    /**
     * Draws some text and the typical text metrics.
     *
     * @param g2  the graphics target.
     * @param bounds  the cell bounds.
     */
    public static void drawTextMetrics(Graphics2D g2, Rectangle2D bounds) {
        double x = 5.0;
        double y = bounds.getMaxY() - 12.0;
        String str = "Murphy \u00C9";
        FontMetrics fm = g2.getFontMetrics();
        Rectangle2D strBounds = fm.getStringBounds(str, g2);
        g2.setPaint(Color.RED);
        g2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 4f, new float[] {2f, 2f}, 0f));
        g2.draw(new Line2D.Double(x, y, x + strBounds.getWidth(), y));
        g2.setPaint(Color.BLACK);
        g2.setFont(new Font(Font.SERIF, Font.PLAIN, 36));
        g2.drawString(str, (float) x, (float) y);
        g2.setPaint(Color.RED);
        g2.fillRect((int) x, (int) y, 1, 1);

        LineMetrics lm = fm.getLineMetrics(str, g2);
        g2.setStroke(new BasicStroke(0.5f));
        g2.setPaint(Color.DARK_GRAY);
        float ascent = lm.getAscent();
        g2.draw(new Line2D.Double(x, y - ascent, x + strBounds.getWidth(), y - ascent));
        float descent = lm.getDescent();
        g2.draw(new Line2D.Double(x, y + descent, x + strBounds.getWidth(), y + descent));
        float leading = lm.getLeading();
        g2.draw(new Line2D.Double(x, y + descent + leading, x + strBounds.getWidth(), y + descent + leading));
    }

    /**
     * Draws some text with different tracking attributes.
     *
     * @param g2  the graphics target.
     * @param bounds  the cell bounds.
     */
    public static void drawTextWithTracking(Graphics2D g2, Rectangle2D bounds) {
        g2.setPaint(Color.BLACK);
        Map<TextAttribute, Object> attributes = new HashMap<>();
        attributes.put(TextAttribute.TRACKING, TextAttribute.TRACKING_LOOSE);
        g2.setFont(new Font(Font.SERIF, Font.PLAIN, 14).deriveFont(attributes));
        g2.drawString("Tracking: TRACKING_LOOSE", (float) bounds.getX() + 5f, (float) bounds.getY() + 20);
        attributes.put(TextAttribute.TRACKING, 0.0);
        g2.setFont(new Font(Font.SERIF, Font.PLAIN, 14).deriveFont(attributes));
        g2.drawString("Tracking: None", (float) bounds.getX() + 5f, (float) bounds.getY() + 40);
        attributes.put(TextAttribute.TRACKING, TextAttribute.TRACKING_TIGHT);
        g2.setFont(new Font(Font.SERIF, Font.PLAIN, 14).deriveFont(attributes));
        g2.drawString("Tracking: TRACKING_TIGHT", (float) bounds.getX() + 5f, (float) bounds.getY() + 60);

    }

}
