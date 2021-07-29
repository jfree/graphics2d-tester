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
     * Draws string bounds.
     *
     * @param g2
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
}
