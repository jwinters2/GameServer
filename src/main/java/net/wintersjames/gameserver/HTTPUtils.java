package net.wintersjames.gameserver;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 * @author james
 */
public class HTTPUtils {
	public static boolean redirectIfNotLoggedIn(int uid, HttpServletResponse response, String redirectTo) {
		if(uid == 0) {
			try {
				response.sendRedirect(redirectTo);
			} catch (IOException e) {}
			return true;
		} else {
			return false;
		}
	}
}
