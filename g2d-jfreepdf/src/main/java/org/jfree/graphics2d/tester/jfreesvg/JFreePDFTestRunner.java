/*
 * Graphics2D Tester
 *
 * (C)opyright 2021, 2022, by David Gilbert.
 */

package org.jfree.graphics2d.tester.jfreesvg;

import org.jfree.pdf.PDFDocument;
import org.jfree.pdf.PDFGraphics2D;
import org.jfree.pdf.Page;
import org.jfree.graphics2d.Tester;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class JFreePDFTestRunner {

    public static void testJFreePDF(String filename, boolean single) throws IOException {
        if (single) {
            filename += "-single.pdf";
        } else {
            filename += ".pdf";
        }
        PDFDocument pdfDoc = new PDFDocument();
        Page page = pdfDoc.createPage(new Rectangle((int) Tester.getTestSheetWidth(), (int) Tester.getTestSheetHeight()));
        PDFGraphics2D g2 = page.getGraphics2D();
        Tester.drawTestOutput(g2, "JFree/PDFGraphics2D (v2.0)", "https://github.com/jfree/jfreepdf", single);
        pdfDoc.writeToFile(new File(filename));
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
