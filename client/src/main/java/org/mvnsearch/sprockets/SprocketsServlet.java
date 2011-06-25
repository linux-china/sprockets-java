package org.mvnsearch.sprockets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * sprockets servlet
 *
 * @author linux_china
 */
public class SprocketsServlet extends HttpServlet {
    /**
     * sprockets request handler
     *
     * @param req  http servlet request
     * @param resp http servlet response
     * @throws ServletException servlet exception
     * @throws IOException      io exception
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }
}
