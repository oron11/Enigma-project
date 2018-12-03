package servlets.aliesservlets;


import com.google.gson.Gson;
import servlets.commonservlets.SignUpServlet;
import utils.GameManager;
import utils.RedirectResponse;
import utils.ServletUtils;
import utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class JoinContestServlet extends HttpServlet {
    private final String CONTEST_NAME = "ContestName";
    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException{
        response.setContentType("application/json");
        String nickname = SessionUtils.getNickname(request);
        String contestName = request.getParameter(CONTEST_NAME);
        SignUpServlet.TypeEntity typeEntity = SessionUtils.getTypeEntity(request);

        if(nickname == null || contestName== null || typeEntity == null || !typeEntity.equals(SignUpServlet.TypeEntity.Alies)) {
            response.sendRedirect("/enigma/index.html");
        }
        else {
            try (PrintWriter out = response.getWriter()) {
                Gson gson = new Gson();
                RedirectResponse redirectResponse = new RedirectResponse();
                GameManager gameManager = ServletUtils.getGameManager(getServletContext());
                gameManager.addAliesToContest(nickname, contestName, redirectResponse);
                String json = gson.toJson(redirectResponse);
                out.print(json);
                if(redirectResponse.isErrorOccurred()) {
                    response.setStatus(400);
                }
                else {
                    SessionUtils.setContestName(request, contestName);
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
