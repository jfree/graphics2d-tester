package org.jfree.graphics2d;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

/**
 * Tests for drawing images.
 */
public class ImageTests {

    private static Image BUG_IMAGE;

    static void drawImage(Graphics2D g2, Rectangle2D bounds, int margin) {
        if (BUG_IMAGE == null) {
            try {
                BUG_IMAGE = ImageIO.read(ClassLoader.getSystemResource("bug.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        g2.drawImage(BUG_IMAGE, (int) bounds.getX() + margin, (int) bounds.getY() + margin, (int) bounds.getWidth() - 2 * margin, (int) bounds.getHeight() - 2 * margin, null);
    }

}
