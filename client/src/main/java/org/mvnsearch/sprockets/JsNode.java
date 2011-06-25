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
    private List<JsNode> parent;

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

    public List<JsNode> getParent() {
        return parent;
    }

    public void setParent(List<JsNode> parent) {
        this.parent = parent;
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
