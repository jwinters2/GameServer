/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.wintersjames.gameserver.Config;

import net.wintersjames.gameserver.Session.SessionStateManager;
import net.wintersjames.gameserver.LoginController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;

/**
 *
 * @author james
 */
@Configuration
@ComponentScan(basePackageClasses = LoginController.class)
public class LoginConfig {
    
    //@Bean
    public SessionStateManager getSessionState() {
        return new SessionStateManager();
    }
    
}
