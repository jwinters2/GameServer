package net.wintersjames.gameserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;


@SpringBootApplication
@EntityScan({"net.wintersjames.gameserver.User", "net.wintersjames.gameserver.Games.GameDao"})
public class GameserverApplication extends SpringBootServletInitializer{

	public static void main(String[] args) {
		SpringApplication.run(GameserverApplication.class, args);
	}
        
}
