package jsl.moum.chatappmodule.config;

import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

public class CorsWebFluxConfig implements WebFluxConfigurer {

    //CorsRegistry object is provided as a parameter to the addCorsMappings method. It is used to configure CORS settings.
    @Override
    public void addCorsMappings(CorsRegistry corsRegistry){
        /**
         * addMapping configures the URL patterns to which the CORS configuration applies.
         * In this case, it is set to apply to all paths ("/**").
         * .allowedOrigins method sets the list of origins (domains) allowed to access resources on the server.
         * In this case, the server accepts requests from any domain ("*").
         * .allowedMethods method sets the HTTP methods that are allowed for cross-origin requests.
         */

        corsRegistry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowCredentials(true)
                .allowedHeaders("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH");
    }

}
