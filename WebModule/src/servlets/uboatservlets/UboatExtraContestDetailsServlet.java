package servlets.uboatservlets;

import com.google.gson.Gson;
import utils.GameManager;
import utils.ServletUtils;
import utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class UboatExtraContestDetailsServlet extends HttpServlet {
    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException{
        response.setContentType("application/json");
        String contestName = SessionUtils.getContestName(request);
        GameManager gameManager = ServletUtils.getGameManager(getServletContext());

        if(contestName != null) {
            try (PrintWriter out = response.getWriter()) {
                Gson gson = new Gson();
                GameManager.UboatExtraContestInfo uboatExtraContestInfo = gameManager.getUboatExtraContestInfo(contestName);
                if(uboatExtraContestInfo != null) {
                    String json = gson.toJson(uboatExtraContestInfo);
                    out.println(json);
                    out.flush();
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
