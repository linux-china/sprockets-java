package org.mvnsearch.sprockets;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

/**
 * sprockets servlet
 *
 * @author linux_china
 */
public class SprocketsServlet extends HttpServlet {
    /**
     * current environment
     */
    private String env;
    /**
     * js repository url
     */
    private String repository;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        env = config.getInitParameter("env");
        repository = config.getInitParameter("repository");
    }

    /**
     * sprockets request handler
     *
     * @param request  http servlet request
     * @param response http servlet response
     * @throws ServletException servlet exception
     * @throws IOException      io exception
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/javascript");
        PrintWriter out = response.getWriter();
        String jsUri = request.getRequestURI();
        String queryString = request.getQueryString();
        //output js with sprockets
        if (queryString != null && queryString.contains("sprockets")) {
            JsNode jsNode;
            //dev env, output js with loader
            if ("dev".equals(env)) {
                jsNode = parseNode(jsUri, request.getQueryString());
                List<JsNode> nodePath = jsNode.getDepedencyNodes();
                for (JsNode node : nodePath) {
                    out.println("document.write('<script type=\"text/javascript\" src=\"" + node.getUri() + "\"></script>');");
                }
            } else { //concat all compressed js and output
                jsNode = JsDependencyTree.getInstance().findNode(jsUri);
                if (jsNode == null || queryString.equals(jsNode.getQueryString())) {
                    jsNode = parseNode(jsUri, request.getQueryString());
                    if (jsNode != null) {
                        JsDependencyTree.getInstance().addNode(jsNode);
                    }
                }
                if (jsNode != null) {
                    for (JsNode node : jsNode.getDepedencyNodes()) {
                        if (node.getCompressedContent() == null) {
                            node.setCompressedContent(YuiUtils.compressJs(node.getContent()));
                        }
                        out.println("/* --- " + node.getUri() + " --- */\n" + node.getCompressedContent());
                    }
                }
            }
        } else { // plain js
            out.print(readContentFromInputStream(getServletContext().getResourceAsStream(jsUri)));
        }
        out.flush();
        out.close();

    }

    /**
     * parse js node
     *
     * @param jsUri       js uri
     * @param queryString query string
     * @return js node
     */
    public JsNode parseNode(String jsUri, String queryString) {
        try {
            JsNode jsNode = new JsNode();
            jsNode.setUri(jsUri);
            jsNode.setQueryString(queryString);
            jsNode.setContent(readContentFromInputStream(getServletContext().getResourceAsStream(jsUri)));
            JsDependencyTree.getInstance().addNode(jsNode);
            resolveParent(jsNode);
            return jsNode;
        } catch (Exception e) {
            getServletContext().log("Failed to resolve js node: " + jsUri, e);
        }
        return null;
    }

    /**
     * resolve parent jsNode
     *
     * @param jsNode js node
     */
    @SuppressWarnings({"ConstantConditions"})
    private void resolveParent(JsNode jsNode) {
        try {
            List<String> requireSentences = jsNode.getRequireSentences();
            if (!requireSentences.isEmpty()) {
                for (String requireSentence : requireSentences) {
                    String path = requireSentence.substring(requireSentence.indexOf("require") + 7).trim();
                    String jsUri = null;
                    //加载本地文件
                    if (path.startsWith("\"") || path.startsWith("'")) {
                        jsUri = getFilePath(jsNode.getUri()) + trimQuote(path) + ".js";
                    } else if (path.startsWith("<")) { //加载库文件
                        jsUri = repository + "/" + trimQuote(path) + ".js";
                        //如果含空格，表示指定版本啦
                        if (path.contains(" ")) {
                            String[] parts = trimQuote(path).split("\\s");
                            jsUri = repository + "/" + parts[0] + "/" + parts[1] + "/" + parts[0] + "-" + parts[1] + ".js";
                        }
                    }
                    if (jsUri != null) {
                        JsNode parent = new JsNode();
                        parent.setUri(jsUri);
                        if (parent.isRepositoryJs()) {
                            //repository js, use cache
                            if (JsDependencyTree.getInstance().findNode(jsUri) != null) {
                                parent = JsDependencyTree.getInstance().findNode(jsUri);
                            } else {
                                parent.setContent(getUriText(parent.getUri()));
                            }
                        } else {
                            parent.setContent(readContentFromInputStream(getServletContext().getResourceAsStream(parent.getUri())));
                        }
                        jsNode.addParent(parent);
                        JsDependencyTree.getInstance().addNode(parent);
                        resolveParent(parent);
                    }
                }
            }
        } catch (Exception e) {
            getServletContext().log("Failed to resolve parent Js:  " + jsNode.getUri(), e);
        }
    }

    /**
     * trim quote, include " ' < >
     *
     * @param text tet
     * @return new text
     */
    private String trimQuote(String text) {
        StringBuilder builder = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (c != '"' && c != '\'' && c != '<' && c != '>') {
                builder.append(c);
            }
        }
        return builder.toString().trim();
    }

    /**
     * get uri text content
     *
     * @param uri uri
     * @return text content
     */
    private String getUriText(String uri) {
        try {
            GetMethod getMethod = new GetMethod(uri);
            createHttpClient().executeMethod(getMethod);
            return readContentFromInputStream(getMethod.getResponseBodyAsStream());
        } catch (Exception e) {
            getServletContext().log("Failed to download text content:" + uri, e);
        }
        return null;
    }

    /**
     * create http client instance
     *
     * @return HttpClient object
     */
    private HttpClient createHttpClient() {
        HttpClient clientTemp = new HttpClient();     //HttpClient create
        HttpClientParams clientParams = clientTemp.getParams();
        clientParams.setParameter("http.socket.timeout", 10000); //10 seconds for socket waiting
        clientParams.setParameter("http.connection.timeout", 10000); //10 seconds http connection creation
        clientParams.setParameter("http.connection-manager.timeout", 3000L); //3 seconds waiting to get connection from http connection manager
        clientParams.setParameter("http.method.retry-handler", new DefaultHttpMethodRetryHandler()); // if failed, try 3
        return clientTemp;
    }

    /**
     * read content from input stream
     *
     * @param inputStream input stream
     * @return string content
     * @throws IOException io exception
     */
    public String readContentFromInputStream(InputStream inputStream) throws IOException {
        StringWriter sw = new StringWriter();
        InputStreamReader inputReader = new InputStreamReader(inputStream);
        int DEFAULT_BUFFER_SIZE = 1024 * 4;
        char[] buffer = new char[DEFAULT_BUFFER_SIZE];
        int n;
        while (-1 != (n = inputReader.read(buffer))) {
            sw.write(buffer, 0, n);
        }
        return sw.toString();
    }

    /**
     * get file path from file name
     *
     * @param fileName file name with path included
     * @return file path
     */
    public String getFilePath(String fileName) {
        if (fileName.contains("/")) {
            return fileName.substring(0, fileName.lastIndexOf("/")+1);
        } else {
            return "";
        }
    }
}
