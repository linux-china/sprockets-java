package org.mvnsearch.sprockets;

import java.util.HashMap;
import java.util.Map;

/**
 * javascript dependency tree
 *
 * @author linux_china
 */
public class JsDependencyTree {
    private static JsDependencyTree instance;
    /**
     * node info
     */
    Map<String, JsNode> nodes = new HashMap<String, JsNode>();

    /**
     * get javascript dependency tree
     *
     * @return instance
     */
    public static JsDependencyTree getInstance() {
        if (instance == null) {
            instance = new JsDependencyTree();
        }
        return instance;
    }

    /**
     * find js node
     *
     * @param uri request url
     * @return js node
     */
    public JsNode findNode(String uri) {
        return nodes.get(uri);
    }

    /**
     * add js node
     *
     * @param jsNode js node
     */
    public void addNode(JsNode jsNode) {
        nodes.put(jsNode.getUri(), jsNode);
    }
}
