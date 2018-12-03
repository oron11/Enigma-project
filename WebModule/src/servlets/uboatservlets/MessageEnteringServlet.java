package servlets.uboatservlets;

import com.google.gson.Gson;
import utils.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class MessageEnteringServlet extends HttpServlet {
    private final String MESSAGE = "message";
    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        String messageToEncode = request.getParameter(MESSAGE);
        String contestName = SessionUtils.getContestName(request);

        RedirectResponse redirectResponse = new RedirectResponse();
        Gson gson = new Gson();

        try (PrintWriter out = response.getWriter()) {
            if(messageToEncode == null || contestName == null) {
                redirectResponse.setMessage("Error: Server did not get the required parameters.");
                redirectResponse.setErrorOccurred(true);
            }
            else {
                ServletUtils.getGameManager(getServletContext()).encodeMessage(contestName, messageToEncode, redirectResponse);
            }

            String json = gson.toJson(redirectResponse);
            out.print(json);
            if(redirectResponse.isErrorOccurred()) {
                response.setStatus(400);
            }
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
