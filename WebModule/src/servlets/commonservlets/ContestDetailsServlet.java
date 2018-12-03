package servlets.commonservlets;

import com.google.gson.Gson;
import componentsEx03.ContestInfo;
import utils.GameManager;
import utils.ServletUtils;
import utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class ContestDetailsServlet extends HttpServlet {

    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        String contestName = SessionUtils.getContestName(request);
        GameManager gameManager = ServletUtils.getGameManager(getServletContext());

        if (contestName == null) {
            response.sendRedirect("index.html");
        }

        try (PrintWriter out = response.getWriter()) {
            ContestInfo contestInfo = gameManager.getContestInfo(contestName);
            if(contestInfo!= null) {
                Gson gson = new Gson();
                String jsonResponse = gson.toJson(contestInfo);
                out.print(jsonResponse);
                out.flush();
            }
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }
}
