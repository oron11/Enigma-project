package servlets.commonservlets;

import com.google.gson.Gson;
import utils.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static utils.Constants.*;

//@WebServlet(name = "SignUpServlet", urlPatterns = "/pages/signup/Signup")
public class SignUpServlet extends HttpServlet {
    public enum TypeEntity {
        Uboat, Alies
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        String nickname = request.getParameter(Constants.NICKNAME);
        String typeEntityString = request.getParameter(Constants.TYPE_ENTITY);
        TypeEntity typeEntity = returnTypeEntity(typeEntityString);

        RedirectResponse redirectResponse = new RedirectResponse();
        Gson gson = new Gson();

        try (PrintWriter out = response.getWriter()) {
            if(typeEntity == null || nickname == null) {
                redirectResponse.setMessage("Error: Server did not get the required parameters.");
                redirectResponse.setErrorOccurred(true);
            }
            else {
                nickname = nickname.trim();
                handleUserNickname(nickname, redirectResponse);
                String urlRedirectResponse = typeEntity == TypeEntity.Uboat ? UPLOAD_XML_FILE_URL : ALIES_CONTEST_SELECTION_URL;
                redirectResponse.setRedirectUrl(urlRedirectResponse);
                request.getSession(true).setAttribute(NICKNAME, nickname);
                request.getSession().setAttribute(TYPE_ENTITY, typeEntity);
                if(typeEntity.equals(TypeEntity.Alies)) {
                    ServletUtils.getGameManager(getServletContext()).addNewAliesToTheGame(nickname);
                }
            }

            String json = gson.toJson(redirectResponse);
            out.print(json);
            if(redirectResponse.isErrorOccurred()) {
                response.setStatus(400);
            }
        }
    }

    private void handleUserNickname(String nickname, RedirectResponse redirectResponse) {
        String messageResponse;

        try {
            ServletUtils.getGameManager(getServletContext()).addNicknameToServer(nickname);
            messageResponse = "The server was updated with your data successfully." + System.lineSeparator() + "You are being transferred to the next stage.";

        }catch (NicknameExistsException e) {
            String nicknameSuggestion = ServletUtils.getGameManager(getServletContext()).getNicknameSuggestion();

            if(nicknameSuggestion != null) {
                messageResponse = String.format("Error: The nickname: %s is already taken, " + System.lineSeparator() +
                        "Try to enter a different one." + System.lineSeparator() +
                        "You can try entering: %s for example.", nickname, nicknameSuggestion );
            }
            else {
                messageResponse = String.format("Error: The nickname: %s is already taken, " + System.lineSeparator() +
                                                 "Try to enter a different one." + System.lineSeparator(), nickname);
            }

            redirectResponse.setErrorOccurred(true);
        }

        redirectResponse.setMessage(messageResponse);
    }

    private TypeEntity returnTypeEntity(String stringTypeEntity) {
        for(TypeEntity typeEntityOption : TypeEntity.values()) {
            if(typeEntityOption.name().equals(stringTypeEntity)) {
                return typeEntityOption;
            }
        }
        return null;
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
