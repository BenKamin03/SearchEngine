package edu.usfca.cs272.utils;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * Class responsible for creating the webserver and displaying the HTML to the
 * user
 *
 * @author Ben Kamin
 * @author CS 272 Software Development (University of San Francisco)
 * @version Spring 2024
 */
public class WebServer {

    /**
     * The query handler
     */
    private final QueryHandlerInterface queryHandler;

    /**
     * the port
     */
    private final int port;

    /**
     * the server
     */
    private Server server;

    /**
     * sets the port and query handler
     * 
     * @param port
     * @param queryHandler
     */
    public WebServer(int port, QueryHandlerInterface queryHandler) {
        this.port = port;
        this.queryHandler = queryHandler;
    }

    /**
     * Starts the server
     *
     * @throws Exception if an exception occurs when starting the server
     */
    public void start() throws Exception {
        server = new Server(port);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        // Add the ApiServlet to handle API requests
        context.addServlet(new ServletHolder(new ApiServlet()), "/api/search");

        // Add the StaticFileServlet to handle all other requests
        context.addServlet(new ServletHolder(new WebPageServlet("frontend/build")), "/*");

        server.setHandler(context);
        server.start();
        server.join();
    }

    /**
     * Stops the server
     * 
     * @throws Exception if an exception occurs when stopping the server
     */
    public void stop() throws Exception {
        if (server != null) {
            server.stop();
        }
    }

    /**
     * Gets the the query string
     * 
     * @param request
     * @return
     */
    public static String getCurrentQueryString(HttpServletRequest request) {
        String queryString = request.getQueryString();
        if (queryString != null) {
            try {
                queryString = URLDecoder.decode(queryString, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                System.out.println("QueryString Encoding Issue");
            }
        }
        return queryString;
    }

    /**
     * gets the parameter from the query string
     * 
     * @param request
     * @param paramName
     * @return
     */
    public static String getParameterValue(HttpServletRequest request, String paramName) {
        String queryString = getCurrentQueryString(request);
        String[] params = queryString.split("&");
        for (String param : params) {
            String[] pair = param.split("=");
            if (pair.length == 2 && pair[0].equals(paramName)) {
                return pair[1];
            }
        }
        return null;
    }

    /**
     * The Servlet for the API
     */
    private class ApiServlet extends HttpServlet {
        @Override
        public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setContentType("text/json");
            PrintWriter out = response.getWriter();
            String query = getParameterValue(request, "query");
            System.out.println("API Request: " + query);
            if (query != null) {
                JsonWriter.toJsonString(queryHandler.getQueryResults(query), out);
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No query provided");
            }
        }
    }

    /**
     * The Servlet for the search page
     */
    private class WebPageServlet extends HttpServlet {
        private final String buildDirectory;

        public WebPageServlet(String buildDirectory) {
            this.buildDirectory = buildDirectory;
        }

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            System.out.println("GET request: " + req.getRequestURI());
            String path = req.getRequestURI();
            if (path.equals("/") || path.equals("")) {
                path = "/index.html";
            }
            File file = new File(buildDirectory, path);
            if (file.exists() && !file.isDirectory()) {
                resp.setContentType(getServletContext().getMimeType(file.getName()));
                Files.copy(file.toPath(), resp.getOutputStream());
            } else {
                // Serve index.html for any other paths to support client-side routing
                File fallbackFile = new File(buildDirectory, "/index.html");
                if (fallbackFile.exists()) {
                    resp.setContentType(getServletContext().getMimeType(fallbackFile.getName()));
                    Files.copy(fallbackFile.toPath(), resp.getOutputStream());
                } else {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                }
            }
        }

        public static String round(double value, int places) {
            if (places < 0)
                throw new IllegalArgumentException();

            DecimalFormat df = new DecimalFormat("#." + "0".repeat(places));
            return df.format(value);
        }
    }
}