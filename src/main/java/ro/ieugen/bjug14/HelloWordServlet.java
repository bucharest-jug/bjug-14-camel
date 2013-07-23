package ro.ieugen.bjug14;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class HelloWordServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.getWriter().print("Hello from Java!\n");
        for (Map.Entry entry : System.getProperties().entrySet()) {
            resp.getWriter().printf("%s -> %s\n", entry.getKey(), entry.getValue());
        }
    }
}
