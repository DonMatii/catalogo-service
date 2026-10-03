package cl.ochodigital.pasteleriamydreams.catalogoservice.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Seguridad del catalogo: GET publico (formulario de pedido) y escrituras
// solo con header X-Api-Key (antes cualquiera podia borrar/editar productos)
@SpringBootTest
@AutoConfigureMockMvc
class ProductoSeguridadIntegrationTest {

    // Misma key que define src/test/resources/application.properties
    private static final String API_KEY = "test-admin-key";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void listadoDelCatalogoEsPublicoSinApiKey() throws Exception {
        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk());
    }

    @Test
    void escriturasResponden401SinApiKey() throws Exception {
        String cuerpo = "{\"nombre\":\"Torta Segura\",\"categoria\":\"Tortas\",\"precio\":10000}";

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(put("/api/productos/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(delete("/api/productos/{id}", 1L))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void escrituraConApiKeyValidaPasaElFiltro() throws Exception {
        mockMvc.perform(post("/api/productos")
                        .header("X-Api-Key", API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Torta Segura\",\"categoria\":\"Tortas\",\"precio\":10000}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Torta Segura"));
    }
}
