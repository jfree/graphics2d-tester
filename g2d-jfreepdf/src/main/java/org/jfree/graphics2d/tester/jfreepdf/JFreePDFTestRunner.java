/*
 * Graphics2D Tester
 *
 * (C)opyright 2021, 2022, by David Gilbert.
 */
package org.jfree.graphics2d.tester.jfreepdf;

import org.jfree.pdf.PDFDocument;
import org.jfree.pdf.PDFGraphics2D;
import org.jfree.pdf.Page;
import org.jfree.graphics2d.Tester;

import java.awt.Rectangle;

import java.io.File;
import java.io.IOException;

public class JFreePDFTestRunner {

    private final static int REPEATS = 10;

    public static void testJFreePDF(String filename, boolean single) throws IOException {
        if (single) {
            filename += "-single.pdf";
        } else {
            filename += ".pdf";
        }
        // Prepare context:
        final Tester.TesterContext tc = Tester.prepareTestOutput(
                "JFree/PDFGraphics2D (v2.0.1)",
                "https://github.com/jfree/jfreepdf", single);

        for (int i = 0; i < REPEATS; i++) {
            final long startTime = System.nanoTime();

            final PDFDocument pdfDoc = new PDFDocument();
            final Page page = pdfDoc.createPage(new Rectangle(Tester.getTestSheetWidth(), Tester.getTestSheetHeight()));
            final PDFGraphics2D g2 = page.getGraphics2D();

            Tester.drawTestOutput(tc, g2);

            pdfDoc.writeToFile(new File(filename));

            final double elapsedTime = 1e-6d * (System.nanoTime() - startTime);
            System.out.println("drawTestOutput(PDFGraphics2D) duration = " + elapsedTime + " ms.");
        }
    }

    /**
     * Creates Java2D output that exercises many features of the API.
     *
     * @param args
     */
    public static void main(String[] args) throws IOException {
        boolean single = false;
        testJFreePDF("jfreepdf", single);
        System.exit(0);
    }

}
