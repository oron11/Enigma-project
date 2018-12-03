package servlets.commonservlets;

import utils.GameManager;
import utils.ServletUtils;
import utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UploadChatMessageServlet extends HttpServlet {
    private final String USER_CHAT_MESSAGE = "UserChatMessage";

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userMessageToUpload = request.getParameter(USER_CHAT_MESSAGE);
        String contestName = SessionUtils.getContestName(request);
        GameManager gameManager = ServletUtils.getGameManager(getServletContext());
        String nickname = SessionUtils.getNickname(request);

        if(contestName != null && userMessageToUpload != null) {
            gameManager.uploadChatMessage(contestName, nickname, userMessageToUpload);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }
}
