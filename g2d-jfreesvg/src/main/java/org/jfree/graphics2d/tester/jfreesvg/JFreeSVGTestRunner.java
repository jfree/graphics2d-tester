/*
 * Graphics2D Tester
 *
 * (C)opyright 2021-present, by David Gilbert.
 */
package org.jfree.graphics2d.tester.jfreesvg;

import org.jfree.svg.SVGGraphics2D;
import org.jfree.svg.SVGUtils;
import org.jfree.graphics2d.Tester;

import java.io.File;
import java.io.IOException;

public class JFreeSVGTestRunner {

    private final static int REPEATS = 10;

    public static void testJFreeSVG(String filename, boolean single) throws IOException {
        if (single) {
            filename += "-single.svg";
        } else {
            filename += ".svg";
        }

        // Prepare context:
        final Tester.TesterContext tc = Tester.prepareTestOutput("JFree/SVGGraphics2D (v5.0.3)", single);

        for (int i = 0; i < REPEATS; i++) {
            final long startTime = System.nanoTime();

            final SVGGraphics2D g2 = new SVGGraphics2D(Tester.getTestSheetWidth(), Tester.getTestSheetHeight());

            Tester.drawTestOutput(tc, g2, "https://github.com/jfree/jfreesvg", single);

            SVGUtils.writeToSVG(new File(filename), g2.getSVGElement());

            final double elapsedTime = 1e-6d * (System.nanoTime() - startTime);
            System.out.println("drawTestOutput(SVGGraphics2D) duration = " + elapsedTime + " ms.");
        }
    }

    /**
     * Creates Java2D output that exercises many features of the API.
     *
     * @param args
     */
    public static void main(String[] args) throws IOException {
        boolean single = false;
        testJFreeSVG("jfreesvg", single);
        System.exit(0);
    }

}
