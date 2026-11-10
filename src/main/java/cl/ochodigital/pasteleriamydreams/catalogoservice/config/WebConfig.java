package cl.ochodigital.pasteleriamydreams.catalogoservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

// Sirve las imagenes subidas desde el admin en GET /uploads/** (fuera del
// prefijo /api/productos, asi AdminApiKeyFilter las deja publicas: las ve el
// catalogo sin login). El directorio es el mismo que usa LocalDiskImageStorage.
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final String directorio;

    public WebConfig(@Value("${app.imagenes.directorio:./uploads}") String directorio) {
        this.directorio = directorio;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String ubicacion = Path.of(directorio).toAbsolutePath().toUri().toString();
        if (!ubicacion.endsWith("/")) {
            ubicacion += "/";
        }
        registry.addResourceHandler("/uploads/**").addResourceLocations(ubicacion);
    }
}
