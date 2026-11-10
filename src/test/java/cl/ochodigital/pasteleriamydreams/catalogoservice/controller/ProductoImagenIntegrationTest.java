package cl.ochodigital.pasteleriamydreams.catalogoservice.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Upload de imagenes de producto desde el admin: el archivo se guarda en el
// storage (disco local en dev, S3 despues) y se responde con la URL publica
// que viaja en el imagenUrl del producto. Es escritura, asi que exige
// X-Api-Key igual que el resto del catalogo (AdminApiKeyFilter).
@SpringBootTest
@AutoConfigureMockMvc
class ProductoImagenIntegrationTest {

    // Misma key que define src/test/resources/application.properties
    private static final String API_KEY = "test-admin-key";

    @Autowired
    private MockMvc mockMvc;

    private MockMultipartFile imagenJpg() {
        return new MockMultipartFile("archivo", "torta.jpg", "image/jpeg", new byte[] { 1, 2, 3, 4 });
    }

    @Test
    void subirImagenSinApiKeyResponde401() throws Exception {
        mockMvc.perform(multipart("/api/productos/imagen").file(imagenJpg()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void subirImagenValidaDevuelveUrlDeUploads() throws Exception {
        mockMvc.perform(multipart("/api/productos/imagen")
                        .file(imagenJpg())
                        .header("X-Api-Key", API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value(startsWith("/uploads/")))
                .andExpect(jsonPath("$.url").value(endsWith(".jpg")));
    }

    @Test
    void laImagenSubidaSeSirveEnUploads() throws Exception {
        String respuesta = mockMvc.perform(multipart("/api/productos/imagen")
                        .file(imagenJpg())
                        .header("X-Api-Key", API_KEY))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String url = com.jayway.jsonpath.JsonPath.read(respuesta, "$.url");
        mockMvc.perform(get(url))
                .andExpect(status().isOk());
    }

    @Test
    void subirArchivoQueNoEsImagenResponde400() throws Exception {
        MockMultipartFile texto = new MockMultipartFile("archivo", "notas.txt", "text/plain", "hola".getBytes());

        mockMvc.perform(multipart("/api/productos/imagen")
                        .file(texto)
                        .header("X-Api-Key", API_KEY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").exists());
    }

    @Test
    void subirArchivoVacioResponde400() throws Exception {
        MockMultipartFile vacio = new MockMultipartFile("archivo", "torta.jpg", "image/jpeg", new byte[0]);

        mockMvc.perform(multipart("/api/productos/imagen")
                        .file(vacio)
                        .header("X-Api-Key", API_KEY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").exists());
    }
}
