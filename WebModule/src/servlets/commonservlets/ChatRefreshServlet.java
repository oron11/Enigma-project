package servlets.commonservlets;


import com.google.gson.Gson;
import componentsEx03.ChatManager;
import componentsEx03.ContestManager;
import componentsEx03.LogData;
import utils.Constants;
import utils.GameManager;
import utils.ServletUtils;
import utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class ChatRefreshServlet extends HttpServlet {
    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        GameManager gameManager = ServletUtils.getGameManager(getServletContext());
        String contestName = SessionUtils.getContestName(request);
        int chatVersion = ServletUtils.getIntParameter(request, Constants.CHAT_VERSION);

        if (contestName == null || chatVersion == -1) {
            response.sendRedirect(Constants.INDEX_URL);
        }

        try (PrintWriter out = response.getWriter()) {
            LogData logData = gameManager.refreshChat(contestName, chatVersion);
            if (logData != null) {
                Gson gson = new Gson();
                String jsonResponse = gson.toJson(logData);
                out.print(jsonResponse);
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
