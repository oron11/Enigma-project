package servlets.aliesservlets;

import com.google.gson.Gson;
import componentsEx03.Alies;
import servlets.commonservlets.SignUpServlet;
import utils.GameManager;
import utils.ServletUtils;
import utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class AliesExtraDetailsServlet extends HttpServlet {

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        Gson gson = new Gson();
        Alies.InitializeMessage initializeMessage;
        GameManager gameManager = ServletUtils.getGameManager(getServletContext());
        String nickname = SessionUtils.getNickname(request);
        String contestName = SessionUtils.getContestName(request);

        if(nickname == null || contestName == null){
             response.setStatus(400);
        }
        else {
            try (PrintWriter out = response.getWriter()) {
                initializeMessage = gameManager.getAliesInitializeMessage(nickname);
                if(initializeMessage != null) {
                    String json = gson.toJson(initializeMessage);
                    out.println(json);
                }
                else {
                    response.setStatus(400);
                }
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

