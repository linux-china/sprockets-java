package org.mvnsearch.sprockets;

import java.util.*;

/**
 * javascript node
 *
 * @author linux_china
 */
public class JsNode {
    /**
     * request url
     */
    private String uri;
    /**
     * query string
     */
    private String queryString;
    /**
     * file content
     */
    private String content;
    /**
     * parent node
     */
    private List<JsNode> parents;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<JsNode> getParents() {
        return parents;
    }

    public void setParents(List<JsNode> parents) {
        this.parents = parents;
    }

    /**
     * add parent node
     *
     * @param parent parent node
     */
    public void addParent(JsNode parent) {
        if (this.parents == null) {
            parents = new ArrayList<JsNode>();
        }
        parents.add(parent);
    }

    /**
     * get dependency nodes, first node's index is 0
     *
     * @return dependency nodes
     */
    public List<JsNode> getDepedencyNodes() {
        //dependencies path
        Map<Integer, List<JsNode>> dependenciesPath = new HashMap<Integer, List<JsNode>>();
        if (getParents() != null && !getParents().isEmpty()) {
            fillNodePath(dependenciesPath, getParents(), 1);
        }
        List<String> nodeUriList = new ArrayList<String>();
        nodeUriList.add(uri);
        if (!dependenciesPath.isEmpty()) {
            for (int i = 1; i <= dependenciesPath.size(); i++) {
                List<JsNode> jsNodes = dependenciesPath.get(i);
                for (JsNode jsNode : jsNodes) {
                    if (!nodeUriList.contains(jsNode.getUri())) {
                        nodeUriList.add(jsNode.getUri());
                    }
                }
            }
        }
        List<JsNode> nodes = new ArrayList<JsNode>();
        for (int i = nodeUriList.size() - 1; i >= 0; i--) {
            nodes.add(JsDependencyTree.getInstance().findNode(nodeUriList.get(i)));
        }
        return nodes;
    }

    /**
     * fill node depdency path info
     *
     * @param nodePath node path
     * @param parents  parents
     * @param sequence path sequence, start from 1
     */
    private void fillNodePath(Map<Integer, List<JsNode>> nodePath, List<JsNode> parents, Integer sequence) {
        if (!parents.isEmpty() && sequence < 100) {
            nodePath.put(sequence, parents);
            List<JsNode> nodes = new ArrayList<JsNode>();
            for (JsNode parent : parents) {
                List<JsNode> tempParents = parent.getParents();
                if (tempParents != null && !tempParents.isEmpty()) {
                    nodes.addAll(tempParents);
                }
            }
            fillNodePath(nodePath, nodes, sequence + 1);
        }
    }

    /**
     * repository js mark
     *
     * @return repository js mark
     */
    public boolean isRepositoryJs() {
        return uri.startsWith("http://") || uri.startsWith("https://");
    }

    /**
     * get require sentences
     *
     * @return sentence list
     */
    public List<String> getRequireSentences() {
        if (content != null && content.startsWith("//=")) {
            return getRequireSentences(content);
        }
        return Collections.emptyList();
    }

    /**
     * get require sentences from content
     *
     * @param content js content
     * @return require sentences
     */
    private List<String> getRequireSentences(String content) {
        List<String> requireSentences = new ArrayList<String>();
        if (content != null && content.startsWith("//=")) {
            String requireSentence = content.substring(0, content.indexOf("\n"));
            requireSentences.add(requireSentence.trim());
            String subContent = content.substring(content.indexOf("\n")).trim();
            requireSentences.addAll(getRequireSentences(subContent));
        }
        return requireSentences;
    }

}
