package servlets.commonservlets;

import com.google.gson.Gson;
import utils.RedirectResponse;
import utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static utils.Constants.SIGN_UP_URL;

//@WebServlet(name = "StartServlet", urlPatterns = "/start")
public class StartServlet extends HttpServlet {

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        RedirectResponse redirectResponse = new RedirectResponse();
        Gson gson = new Gson();

        if(request.getSession(false) == null) {
            redirectResponse.setRedirectUrl(SIGN_UP_URL);
        }
        else {
            response.setStatus(400);
            redirectResponse.setMessage("We are sorry, only 1 session is allowed simultaneously in the application.");
        }

        try( PrintWriter out = response.getWriter()) {
            String json = gson.toJson(redirectResponse);
            out.print(json);
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
