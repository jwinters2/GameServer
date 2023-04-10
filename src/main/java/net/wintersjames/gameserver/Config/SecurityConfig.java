/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.wintersjames.gameserver.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

/**
 *
 * @author james
 */
@Configuration
public class SecurityConfig {
   @Bean
   SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
   	
		http.csrf().csrfTokenRepository(
	CookieCsrfTokenRepository.withHttpOnlyFalse());
		   
		return http
		.requiresChannel(channel -> channel.anyRequest().requiresSecure())
			.authorizeRequests(authorize -> authorize.anyRequest().permitAll())
			.build();
   }
}
