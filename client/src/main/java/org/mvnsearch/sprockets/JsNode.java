package org.mvnsearch.sprockets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    public void addParent(JsNode parent) {
        if (this.parents == null) {
            parents = new ArrayList<JsNode>();
        }
        parents.add(parent);
    }

    /**
     * get linked parents
     *
     * @return linked parents
     */
    public List<JsNode> getLinkedParents() {
        List<JsNode> linkedParents = new ArrayList<JsNode>();
        for (JsNode parent : getParents()) {
            linkedParents.addAll(0, getLinkedParents(parent));
        }
        return linkedParents;
    }

    /**
     * get linkd parents
     *
     * @param parent parent
     * @return parent list
     */
    private List<JsNode> getLinkedParents(JsNode parent) {
//        todo 要调整依赖关系
        List<JsNode> linkedParents = new ArrayList<JsNode>();
        if (parent != null) {
            linkedParents.add(0, parent);
            if (parent.getParents() != null && !parent.getParents().isEmpty()) {
                for (JsNode jsNode : parent.getParents()) {
                    linkedParents.addAll(0, getLinkedParents(jsNode));
                }
            }
        }
        return linkedParents;
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
