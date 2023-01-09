/*
 * Graphics2D Tester
 *
 * (C)opyright 2021, 2022, by David Gilbert.
 */
package org.jfree.graphics2d.tester.jfreesvg;

import org.jfree.svg.SVGGraphics2D;
import org.jfree.svg.SVGUtils;
import org.jfree.graphics2d.Tester;

import java.io.File;
import java.io.IOException;

public class JFreeSVGTestRunner {

    public static void testJFreeSVG(String filename, boolean single) throws IOException {
        SVGGraphics2D g2 = new SVGGraphics2D(Tester.getTestSheetWidth(), Tester.getTestSheetHeight());
        Tester.drawTestOutput(g2, "JFree/SVGGraphics2D", "https://github.com/jfree/jfreesvg", single);
        if (single) {
            filename += "-single.svg";
        } else {
            filename += ".svg";
        }
        SVGUtils.writeToSVG(new File(filename), g2.getSVGElement());
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
