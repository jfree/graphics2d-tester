/**
 * Graphics2D Tester
 *
 * (C)opyright 2021, 2022, by David Gilbert.
 */
package org.jfree.graphics2d;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.imageio.ImageIO;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import org.jfree.graphics2d.Tester.TesterContext;

/**
 * Tests for drawing images.
 */
public class ImageTests {

    static void prepareImage(final TesterContext tc) {
        try {
            tc.TRIUMPH_IMAGE = ImageIO.read(ClassLoader.getSystemResource("triumph.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Draws an image of a motorcycle within the specified bounds.
     *
     * @param tc  the tester context.
     * @param g2  the graphics target.
     * @param bounds  the cell bounds.
     * @param margin  the margin.
     */
    static void drawImage(final TesterContext tc, Graphics2D g2, Rectangle2D bounds, int margin) {
        g2.drawImage(tc.TRIUMPH_IMAGE, (int) bounds.getX() + margin, (int) bounds.getY() + margin, 
                (int) bounds.getWidth(), (int) bounds.getHeight(), null);
    }
    
    static void prepareQRCodeImage(final TesterContext tc, String text) throws Exception {
        final QRCodeWriter writer = new QRCodeWriter();
        final BitMatrix matrix = writer.encode(text, BarcodeFormat.QR_CODE, 250, 250);
        tc.qrCodeImage = MatrixToImageWriter.toBufferedImage(matrix);
    }
    
    /**
     * Draws a QR code representing the specified text (usually a URL pointing to the project page for the
     * Graphics2D instance being tested.
     *
     * @param tc  the tester context.
     * @param g2  the graphics target.
     * @param bounds  the cell bounds.
     * @param margin  the margin.
     */
    static void drawQRCodeImage(final TesterContext tc, Graphics2D g2, Rectangle2D bounds, int margin) {
        g2.drawImage(tc.qrCodeImage, (int) bounds.getX() + margin, (int) bounds.getY() + margin, 
                (int) bounds.getWidth() - 2 * margin, (int) bounds.getHeight() - 2 * margin, null);
    }
}
