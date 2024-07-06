package pro.tendermarket;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000") // Remplacez par l'URL de votre application React
                .allowedMethods("GET", "POST", "PUT", "DELETE", "DOWNLOAD")
                .allowedHeaders("*");
    }
}