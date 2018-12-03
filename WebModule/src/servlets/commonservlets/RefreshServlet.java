package servlets.commonservlets;

import com.google.gson.Gson;
import utils.GameManager;
import componentsEx03.RefreshAnswer;
import utils.ServletUtils;
import utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class RefreshServlet extends HttpServlet {
    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException{
        response.setContentType("application/json");
        String contestName = SessionUtils.getContestName(request);
        String nickname = SessionUtils.getNickname(request);

        if(contestName!= null) {
            try (PrintWriter out = response.getWriter()) {
                Gson gson = new Gson();
                GameManager gameManager = ServletUtils.getGameManager(getServletContext());
                RefreshAnswer refreshAnswer = gameManager.getRefreshAnswer(contestName, nickname);
                if(refreshAnswer != null) {
                    String json = gson.toJson(refreshAnswer);
                    out.println(json);
                    out.flush();
                } else {
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
