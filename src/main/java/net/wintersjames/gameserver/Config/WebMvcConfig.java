package net.wintersjames.gameserver.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.reactive.config.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 *
 * @author james
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer  {

    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix("gameserver", HandlerTypePredicate.forAnnotation(RestController.class));
    }
}
