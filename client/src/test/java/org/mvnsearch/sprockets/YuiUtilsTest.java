package org.mvnsearch.sprockets;

import junit.framework.TestCase;

/**
 * YUI Utils test case
 *
 * @author linux_china
 */
public class YuiUtilsTest extends TestCase {
    /**
     * test to compress js content
     */
    public void testCompressJs() {
        String jsContent = "/** good */ var name=1;";
        System.out.println(YuiUtils.compressJs(jsContent));
    }
}
