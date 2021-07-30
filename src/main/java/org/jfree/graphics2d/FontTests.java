package org.jfree.graphics2d;

import java.awt.*;
import java.awt.font.LineMetrics;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

public class FontTests {

    /**
     * Draws some text and the typical text metrics.
     *
     * @param g2
     */
    public static void drawTextMetrics(Graphics2D g2, Rectangle2D bounds) {
        double x = 5.0;
        double y = bounds.getMaxY() - 5.0;
        g2.setPaint(Color.BLACK);
        g2.setFont(new Font(Font.SERIF, Font.PLAIN, 36));
        g2.drawString("Murphy \u00C9", (float) x, (float) y);
        FontMetrics fm = g2.getFontMetrics();
        LineMetrics lm = fm.getLineMetrics("Murphy \u00C9", g2);
        g2.setStroke(new BasicStroke(0.5f));
        g2.setPaint(Color.RED);
        float ascent = lm.getAscent();
        g2.draw(new Line2D.Double(x, y - ascent, bounds.getWidth() - 10.0, y - ascent));
        float descent = lm.getDescent();
        g2.draw(new Line2D.Double(x, y +descent, bounds.getWidth() - 10.0, y + descent));

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
        double y = bounds.getMaxY() - 5.0;
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
     * Draws strings in SERIF, SANS_SERIF and MONOSPACED fonts.
     *
     * @param g2  the graphics target.
     */
    public static void drawString(Graphics2D g2) {
        g2.setPaint(Color.BLACK);
        g2.setFont(new Font(Font.SERIF, Font.PLAIN, 14));
        g2.setFont(new Font("Times New Roman", Font.PLAIN, 14)); // remove later
        g2.drawString("Serif Font 1234567890", 15, 20);
        g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        g2.setFont(new Font("Helvetica", Font.PLAIN, 14)); // remove later
        g2.drawString("Sans Serif Font 1234567890", 15, 40);
        g2.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        g2.setFont(new Font("Courier New", Font.PLAIN, 14)); // remove later
        g2.drawString("Monospaced Font 1234567890", 15, 60);
    }
}
