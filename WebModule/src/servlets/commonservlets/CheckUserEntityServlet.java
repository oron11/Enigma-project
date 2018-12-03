package servlets.commonservlets;

import utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class CheckUserEntityServlet extends HttpServlet {


    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException{
        response.setContentType("text/html;charset=UTF-8");
        String nickname = SessionUtils.getNickname(request);
        SignUpServlet.TypeEntity typeEntity = SessionUtils.getTypeEntity(request);
        if(nickname == null || typeEntity == null){
            response.setStatus(400);
        }
        else {
            try (PrintWriter out = response.getWriter()) {
               out.print(typeEntity);
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
