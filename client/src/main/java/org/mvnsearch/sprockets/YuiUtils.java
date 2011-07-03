package org.mvnsearch.sprockets;

import com.yahoo.platform.yui.compressor.JavaScriptCompressor;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;

import java.io.StringReader;
import java.io.StringWriter;

/**
 * YUI Utils
 *
 * @author linux_china
 */
public class YuiUtils {
    /**
     * empty error reporter
     */
    private static ErrorReporter emptyReporter = new ErrorReporter() {
        public void warning(String s, String s1, int i, String s2, int i1) {

        }

        public void error(String s, String s1, int i, String s2, int i1) {

        }

        public EvaluatorException runtimeError(String s, String s1, int i, String s2, int i1) {
            return null;
        }
    };

    /**
     * compress javascript code
     *
     * @param jsContent js content
     * @return js content
     */
    public static String compressJs(String jsContent) {
        try {
            JavaScriptCompressor jsCompressor = new JavaScriptCompressor(new StringReader(jsContent), emptyReporter);
            StringWriter jsWriter = new StringWriter();
            jsCompressor.compress(jsWriter, -1, true, false, true, false);
            return jsWriter.toString();
        } catch (Exception e) {
            return jsContent;
        }

    }
}
