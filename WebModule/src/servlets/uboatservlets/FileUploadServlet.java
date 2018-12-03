package servlets.uboatservlets;

import com.google.gson.Gson;
import utils.GameManager;
import utils.RedirectResponse;
import utils.ServletUtils;
import utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

//@WebServlet(name = "FileUploadServlet", urlPatterns = "/pages/fileupload/FileUpload")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class FileUploadServlet extends HttpServlet {
    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        GameManager gameManager = ServletUtils.getGameManager(getServletContext());
        String nickname = SessionUtils.getNickname(request);
        RedirectResponse redirectResponse = new RedirectResponse();
        Gson gson = new Gson();

        try (PrintWriter out = response.getWriter()) {
            Part filePart = request.getPart("file"); // Retrieves <input type="file" name="file">
            String fileName = getSubmittedFileName(filePart);
            if(checkIfFileNameIsLegal(fileName, redirectResponse) && isNicknameFound(nickname, redirectResponse)) {
                InputStream inputStream = filePart.getInputStream();
                String contestName = gameManager.parseXmlFileAndDefineUboatAndReturnContestName(nickname, inputStream, redirectResponse);
                SessionUtils.setContestName(request, contestName);
            }
            String json = gson.toJson(redirectResponse);
            out.print(json);
            if(redirectResponse.isErrorOccurred()) {
                response.setStatus(400);
            }
        }
    }

    private boolean isNicknameFound(String nickname, RedirectResponse redirectResponse) {
        if(nickname == null) {
            redirectResponse.setErrorOccurred(true);
            redirectResponse.setMessage("Error: server did not get session nickname as expected.");
            return false;
        }
        return true;
    }

    private boolean checkIfFileNameIsLegal(String fileName, RedirectResponse redirectResponse) {
        if(fileName == null){
            redirectResponse.setMessage("Error: File given isn't found.");
            redirectResponse.setErrorOccurred(true);
            return false;
        }
        else if(!(fileName.endsWith(".xml") || fileName.endsWith(".XML"))) {
            redirectResponse.setMessage("Error: File given is not an XML file.");
            redirectResponse.setErrorOccurred(true);
            return false;
        }

        return true;
    }

    private static String getSubmittedFileName(Part part) {
        for (String cd : part.getHeader("content-disposition").split(";")) {
            if (cd.trim().startsWith("filename")) {
                String fileName = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
                return fileName.substring(fileName.lastIndexOf('/') + 1).substring(fileName.lastIndexOf('\\') + 1); // MSIE fix.
            }
        }
        return null;
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
