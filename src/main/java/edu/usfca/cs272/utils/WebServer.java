package edu.usfca.cs272.utils;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import edu.usfca.cs272.utils.InvertedIndex.QueryEntry;

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
        server.setHandler(context);

        context.addServlet(new ServletHolder(new WebPageServlet()), "/");

        server.start();
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
     * The Servlet for the search page
     */
    private class WebPageServlet extends HttpServlet {
        @Override
        public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println(createPage(request));
        }

        /**
         * Creates the page from the content (appends the header and closing tags to the list)
         * @param content the list of content
         * @return the html string
         */
        private String createPage(List<String> content) {
            StringBuilder htmlBuilder = new StringBuilder();
            htmlBuilder.append("<html><head><title>Simple Servlet</title></head><body>");

            var iterator = content.iterator();
            while (iterator.hasNext()) {
                htmlBuilder.append(iterator.next());
                htmlBuilder.append("\n");
            }

            htmlBuilder.append("</body></html>");

            return htmlBuilder.toString();
        }

        /**
         * creates the main content of the html page
         * 
         * @param request the request
         * @return the main content of the html page
         */
        private String createPage(HttpServletRequest request) {
            List<String> content = new ArrayList<>();
            content.add("<h1 style='text-align: center;'>Search Engine</h1>");
            String queryLine = getParameterValue(request, "search");
            content.add(createForm(queryLine));
            if (queryLine != null) {
                List<QueryEntry> queryEntries = queryHandler.getQueryResults(queryLine);
                if (queryEntries.size() > 0)
                    queryEntries.forEach((QueryEntry result) -> content.add(formatQueryResult(result)));
                else
                    content.add("<p style='text-align: center;'>Sorry, no results found</p>");
            }

            return createPage(content);
        }

        private String formatQueryResult(QueryEntry entry) {
            StringBuilder builder = new StringBuilder();

            builder.append(
                    "<fieldset style='border: 1px solid #ccc; padding: 10px; margin-bottom: 10px; border-radius: 5px;'>");
            builder.append("<legend style='font-weight: bold;'>Score: " + round(entry.getScore(), 3) + "\tCount: "
                    + entry.getTotalWords() + "</legend>");
            builder.append("<a href='" + entry.getFile() + "' style='text-decoration: none; color: #007bff;'>"
                    + entry.getFile() + "</a>");
            builder.append("</fieldset>");

            return builder.toString();
        }

        private String createForm(String query) {
            query = (query != null ? query : "");
            StringBuilder builder = new StringBuilder();

            builder.append("<form method='get' action='/' style='text-align: center;'>");
            builder.append("<input required type='text' id='search' name='search' placeholder='Search' value='"
                    + query + "' style='padding: 5px; width: 300px; border-radius: 5px;'>");
            builder.append(
                    "<input type='submit' value='Submit' style='padding: 5px; background-color: #007bff; color: white; border: 1px solid #007bff; border-radius: 5px; cursor: pointer;'>");
            builder.append("</form>");
            builder.append("<hr>");
            builder.append("<h3 style='text-align: center;'>Showing Results for: " + query + "</h3>");

            return builder.toString();
        }

        public static String round(double value, int places) {
            if (places < 0)
                throw new IllegalArgumentException();

            DecimalFormat df = new DecimalFormat("#." + "0".repeat(places));
            return df.format(value);
        }
    }
}