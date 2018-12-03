package servlets.aliesservlets;

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

public class AliesParametersDefinitionServlet extends HttpServlet {
    private String MISSION_SIZE = "missionSize";
    private String AGENTS_NUMBER_COUNTER = "agentsNumberCounter";
    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException{
        response.setContentType("text/html;charset=UTF-8");
        String contestName = SessionUtils.getContestName(request);
        String nickname = SessionUtils.getNickname(request);
        int missionSize = ServletUtils.getIntParameter(request, MISSION_SIZE );
        int agentsNumberCounter = ServletUtils.getIntParameter(request, AGENTS_NUMBER_COUNTER);

        if(nickname == null || contestName== null) {
            response.sendRedirect("/enigma/index.html");
        }
        else {
            try (PrintWriter out = response.getWriter()) {
                if(agentsNumberCounter < 1) {
                    out.print("Error: Agents number is less than one." + System.lineSeparator() + "Must start with at least one agent.");
                    response.setStatus(400);
                }
                else if(missionSize <= 0) {
                    out.print("Error: Mission size has to be positive number.");
                    response.setStatus(400);
                }
                else {
                    GameManager gameManager = ServletUtils.getGameManager(getServletContext());
                    RedirectResponse redirectResponse = new RedirectResponse();
                    gameManager.checkAliesParameters(nickname, contestName, missionSize, agentsNumberCounter, redirectResponse);
                    out.print(redirectResponse.getMessage());
                    if(redirectResponse.isErrorOccurred()){
                        response.setStatus(400);
                    }
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
