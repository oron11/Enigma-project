package servlets.commonservlets;

import utils.RedirectResponse;
import utils.ServletUtils;
import utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class LogoutServlet extends HttpServlet {
    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException{
        response.setContentType("text/html;charset=UTF-8");
        String nickname = SessionUtils.getNickname(request);
        String contestName = SessionUtils.getContestName(request);
        SignUpServlet.TypeEntity typeEntity = SessionUtils.getTypeEntity(request);
        RedirectResponse redirectResponse = new RedirectResponse();

        if(nickname == null || contestName == null){
            response.setStatus(400);
        }
        else {
            try (PrintWriter out = response.getWriter()) {
                ServletUtils.getGameManager(getServletContext()).logoutUserFromContest(nickname, contestName, typeEntity, redirectResponse);
                out.print(redirectResponse.getMessage());
                if(redirectResponse.isErrorOccurred()) {
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
