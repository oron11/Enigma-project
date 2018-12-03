package utils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import static utils.Constants.GAME_MANAGER_NAME;

public class ServletUtils {
    public static GameManager getGameManager(ServletContext servletContext) {
        if (servletContext.getAttribute(GAME_MANAGER_NAME) == null) {
            servletContext.setAttribute(GAME_MANAGER_NAME, GameManager.getInstance());
        }
        return (GameManager) servletContext.getAttribute(GAME_MANAGER_NAME);
    }

    public static int getIntParameter(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException numberFormatException) {
            }
        }
        return -1;
    }
}
