package servlets.aliesservlets;

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
import java.util.Map;

public class ContestsSelectionRefreshServlet extends HttpServlet {
    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException{
        response.setContentType("application/json");
        String nickname = SessionUtils.getNickname(request);

        if(nickname!= null) {
            try (PrintWriter out = response.getWriter()) {
                Gson gson = new Gson();
                GameManager gameManager = ServletUtils.getGameManager(getServletContext());
                Map<String, ContestInfo> mapContestInfo = gameManager.getMapContestsInfo();
                if(mapContestInfo != null && mapContestInfo.size() > 0) {
                    String json = gson.toJson(mapContestInfo);
                    out.println(json);
                    out.flush();
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
