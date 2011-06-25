package org.mvnsearch.sprockets;

import junit.framework.TestCase;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.List;

/**
 * js node test case
 *
 * @author linux_china
 */
public class JsNodeTest extends TestCase {
    /**
     * js file
     */
    private File jsFile = new File("E:\\source\\java\\sprockets-java\\web\\src\\main\\webapp\\assets\\javascripts\\controller.js");

    /**
     * parse test
     *
     * @throws Exception exception
     */
    public void testParse() throws Exception {
        JsNode jsNode = new JsNode();
        jsNode.setContent(FileUtils.readFileToString(jsFile));
        List<String> dependencies = jsNode.getRequireSentences();
        System.out.println(dependencies.size());
    }
}
