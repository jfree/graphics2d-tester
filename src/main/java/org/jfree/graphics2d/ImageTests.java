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

    private static Image BUG_IMAGE;

    static void drawImage(Graphics2D g2, Rectangle2D bounds, int margin) {
        if (BUG_IMAGE == null) {
            try {
                BUG_IMAGE = ImageIO.read(ClassLoader.getSystemResource("triumph.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        g2.drawImage(BUG_IMAGE, (int) bounds.getX() + margin, (int) bounds.getY() + margin, (int) bounds.getWidth(), (int) bounds.getHeight(), null);
    }

    static void drawQRCodeImage(Graphics2D g2, Rectangle2D bounds, int margin, String text) throws Exception {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(text, BarcodeFormat.QR_CODE, 250, 250);
        BufferedImage image = MatrixToImageWriter.toBufferedImage(matrix);
        g2.drawImage(image, (int) bounds.getX() + margin, (int) bounds.getY() + margin, (int) bounds.getWidth() - 2 * margin, (int) bounds.getHeight() - 2 * margin, null);
    }
}
