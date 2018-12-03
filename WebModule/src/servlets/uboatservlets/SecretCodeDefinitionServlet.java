package servlets.uboatservlets;

import com.google.gson.Gson;
import utils.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class SecretCodeDefinitionServlet extends HttpServlet {
    private final String TYPE_CODE = "typeCode";
    private final String ROTORS_SELECTION = "RotorsSelection";
    private final String ROTORS_FIRST_POSITION = "RotorsFirstPosition";
    private final String REFLECTOR_SELECTION = "ReflectorSelection";
    private final String RANDOM_CHOICE = "Random";
    private final String MANUAL_CHOICE = "Manual";

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        String typeCode = request.getParameter(TYPE_CODE);
        String contestName = SessionUtils.getContestName(request);

        RedirectResponse redirectResponse = new RedirectResponse();
        Gson gson = new Gson();

        try (PrintWriter out = response.getWriter()) {
            if(contestName == null) {
                redirectResponse.setMessage("Error: The session is invalid you do not have a nickname.");
                redirectResponse.setErrorOccurred(true);
            }
            else if(typeCode == null) {
                redirectResponse.setMessage("Error: The server did not get a legal a request, has to be type code choice.");
                redirectResponse.setErrorOccurred(true);
            }
            else {
                defineSecretCode(typeCode, contestName, request, redirectResponse);
            }

            String json = gson.toJson(redirectResponse);
            out.print(json);
            if(redirectResponse.isErrorOccurred()) {
                response.setStatus(400);
            }
        }
    }

    private void defineSecretCode(String typeCode, String contestName, HttpServletRequest request, RedirectResponse redirectResponse) {
        GameManager gameManager = ServletUtils.getGameManager(getServletContext());
        String rotorsSelection, rotorsFirstPosition, reflectorSelection;

        switch (typeCode) {
            case RANDOM_CHOICE:
                gameManager.defineRandomSecretCode(contestName, redirectResponse);
                break;
            case MANUAL_CHOICE:
                rotorsSelection = request.getParameter(ROTORS_SELECTION);
                rotorsFirstPosition = request.getParameter(ROTORS_FIRST_POSITION);
                reflectorSelection = request.getParameter(REFLECTOR_SELECTION);
                if (rotorsSelection == null || rotorsFirstPosition == null || reflectorSelection == null) {
                    redirectResponse.setMessage("Error: The server did not get a legal a request, has to be parameters of manual choice.");
                    redirectResponse.setErrorOccurred(true);
                } else {
                    gameManager.defineManualSecretCode(contestName, rotorsSelection, rotorsFirstPosition, reflectorSelection, redirectResponse);
                }
                break;
            default:
                redirectResponse.setMessage("Error: The server did not get a legal a request, has to be type code choice.");
                redirectResponse.setErrorOccurred(true);
                break;
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }
}
