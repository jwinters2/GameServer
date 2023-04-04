/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.wintersjames.gameserver;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
        
        response.addCookie(new Cookie(cookieName, cookieStr));
        return cookieStr;
    }
    
}
