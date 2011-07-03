package org.mvnsearch.sprockets;

import junit.framework.TestCase;

import java.util.List;

/**
 * js node test case
 *
 * @author linux_china
 */
public class JsNodeTest extends TestCase {
    /**
     * parse test
     *
     * @throws Exception exception
     */
    public void testParse() throws Exception {
        JsNode jsNode = new JsNode();
        jsNode.setContent("//= require 'model'\n" +
                "//= require 'utils'\n" +
                "var userController = {\n" +
                "    index: function() {\n" +
                "\n" +
                "    }\n" +
                "};");
        List<String> dependencies = jsNode.getRequireSentences();
        System.out.println(dependencies.size());
    }
}
