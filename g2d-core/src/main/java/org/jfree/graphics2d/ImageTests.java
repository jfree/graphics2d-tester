/**
 * Graphics2D Tester
 *
 * (C)opyright 2021, David Gilbert.
 */
package org.jfree.graphics2d;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Tests for drawing images.
 */
public class ImageTests {

    private static Image TRIUMPH_IMAGE;

    /**
     * Draws an image of a motorcycle within the specified bounds.
     *
     * @param g2  the graphics target.
     * @param bounds  the cell bounds.
     * @param margin  the margin.
     */
    static void drawImage(Graphics2D g2, Rectangle2D bounds, int margin) {
        if (TRIUMPH_IMAGE == null) {
            try {
                TRIUMPH_IMAGE = ImageIO.read(ClassLoader.getSystemResource("triumph.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        g2.drawImage(TRIUMPH_IMAGE, (int) bounds.getX() + margin, (int) bounds.getY() + margin, (int) bounds.getWidth(), (int) bounds.getHeight(), null);
    }

    /**
     * Draws a QR code representing the specified text (usually a URL pointing to the project page for the
     * Graphics2D instance being tested.
     *
     * @param g2  the graphics target.
     * @param bounds  the cell bounds.
     * @param margin  the margin.
     * @param text  the text to be encoded in the QR code.
     *
     * @throws Exception if there is a problem.
     */
    static void drawQRCodeImage(Graphics2D g2, Rectangle2D bounds, int margin, String text) throws Exception {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(text, BarcodeFormat.QR_CODE, 250, 250);
        BufferedImage image = MatrixToImageWriter.toBufferedImage(matrix);
        g2.drawImage(image, (int) bounds.getX() + margin, (int) bounds.getY() + margin, (int) bounds.getWidth() - 2 * margin, (int) bounds.getHeight() - 2 * margin, null);
    }
}
