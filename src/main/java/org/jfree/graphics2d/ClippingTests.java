package org.jfree.graphics2d;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class ClippingTests {
    /**
     * This test sets rectangular clipping regions and then fills the complete
     * tile with a color.  This tests clipping and the ability to reset a
     * user clip completely.
     *
     * @param g2  the graphics target.
     */
    static void fillRectangularClippingRegions(Graphics2D g2, Rectangle2D bounds) {
        int margin = 5;
        double w = bounds.getWidth();
        double h = bounds.getHeight();
        // set a region in the top left
        g2.clipRect(margin, margin, (int) (w / 2) - margin, (int) (h / 2) - margin);
        g2.setPaint(Color.BLUE);
        g2.fill(new Rectangle2D.Double(0.0, 0.0, w , h ));

        // set a region in the bottom right
        g2.setClip(new Rectangle2D.Double(w / 2, h / 2, w/ 2 - margin, h / 2 - margin));
        g2.setPaint(Color.RED);
        g2.fill(new Rectangle2D.Double(0.0, 0.0, w, h));

        // set a region in the top right
        g2.setClip((int) (w / 2), margin, (int) (w / 2) - margin, (int) (h / 2) - margin);
        g2.setPaint(new GradientPaint(0f, 0f, Color.YELLOW, (float) w, (float) h, Color.GREEN));
        g2.fill(new Rectangle2D.Double(0.0, 0.0, w, h));

        // set a region in the bottom left
        g2.setClip(margin, (int) (h / 2), (int) (w / 2) - margin, (int) (h / 2) - margin);
        g2.setPaint(new GradientPaint((float) w, 0f, Color.YELLOW, (float) -w, (float) h, Color.GRAY));
        g2.fill(new Rectangle2D.Double(0.0, 0.0, w , h));
    }
}
