/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.wintersjames.gameserver;

import org.springframework.http.HttpHeaders;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import org.springframework.http.server.ServerHttpRequest;

/**
 *
 * @author james
 */
public class CookieUtils {
    
    final static String cookieName = "gamecookie";
    
    private CookieUtils() {}
    
    public static String getSessionCookie(
            HttpServletRequest request, HttpServletResponse response) {

        // get the gameserver cookie
        Cookie[] cookies = request.getCookies();
        if(cookies != null) {
            for (Cookie cookie : cookies) {
                if(cookie.getName().equals(cookieName)) {
                    return cookie.getValue();
                }
            }
        }
        
        final String charset = "0123456789ABCDEF";
        String cookieStr = "";
        for(int i=0; i<30; i++) {
            cookieStr += charset.charAt((int) Math.floor(Math.random() * charset.length()));
        }
        
        if(response != null) {
            response.addCookie(new Cookie(cookieName, cookieStr));
        }
        
        return cookieStr;
    }
    
    public static String getSessionCookie(HttpServletRequest request) {
        return getSessionCookie(request, null);
    }
    
    public static String getSessionCookie(ServerHttpRequest request) {
        
        HttpHeaders headers = request.getHeaders();
        String cookieStr = headers.get(HttpHeaders.COOKIE).get(0);
        
        String[] cookies = cookieStr.split("; ");
        for(String cookie: cookies) {
            String[] keyval = cookie.split("=");
            if(keyval[0].equals(cookieName)) {
                return keyval[1];
            }
        }

        return null;
    }
    
}
