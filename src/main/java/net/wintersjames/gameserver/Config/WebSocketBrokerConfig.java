/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.wintersjames.gameserver.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 *
 * @author james
 */
@Configuration
@EnableWebSocketMessageBroker
@PropertySource(value = "classpath:webpath.properties")
public class WebSocketBrokerConfig implements WebSocketMessageBrokerConfigurer {
    
	@Value("${context-root}")
	private String contextRoot;
	
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker(contextRoot + "/websocket");
        registry.setApplicationDestinationPrefixes(contextRoot + "/to-server");
    }
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
         registry.addEndpoint("/server").withSockJS();
    }
    
    
}
