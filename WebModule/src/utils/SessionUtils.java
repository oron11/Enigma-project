package utils;



import servlets.commonservlets.SignUpServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static utils.Constants.CONTEST_NAME;

public class SessionUtils {

    public static String getNickname (HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object sessionAttribute = session != null ? session.getAttribute(Constants.NICKNAME) : null;
        return sessionAttribute != null ? sessionAttribute.toString() : null;
    }

    public static String getContestName (HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object sessionAttribute = session != null ? session.getAttribute(Constants.CONTEST_NAME) : null;
        return sessionAttribute != null ? sessionAttribute.toString() : null;
    }

    public static SignUpServlet.TypeEntity getTypeEntity (HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        SignUpServlet.TypeEntity sessionAttribute = session != null ? (SignUpServlet.TypeEntity)session.getAttribute(Constants.TYPE_ENTITY) : null;
        return sessionAttribute != null ? sessionAttribute : null;
    }

    public static void setContestName(HttpServletRequest request, String contestName) {
        if(contestName != null) {
            request.getSession(false).setAttribute(CONTEST_NAME, contestName);
        }
    }

    public static void clearSession (HttpServletRequest request) {
        request.getSession().invalidate();
    }
}