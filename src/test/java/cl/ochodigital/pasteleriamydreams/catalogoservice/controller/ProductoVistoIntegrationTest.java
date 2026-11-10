package cl.ochodigital.pasteleriamydreams.catalogoservice.controller;

import cl.ochodigital.pasteleriamydreams.catalogoservice.storage.KinesisEventosPublisher;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Ping de "producto visto" (Entrega 3): responde 204 siempre (el ping jamás
// rompe la navegación), publica el evento `producto_visto` en el stream y, como
// toda escritura del catálogo, exige X-Api-Key.
@SpringBootTest
@AutoConfigureMockMvc
class ProductoVistoIntegrationTest {

    private static final String API_KEY = "test-admin-key";

    @Autowired
    private MockMvc mockMvc;

    @MockitoSpyBean
    private KinesisEventosPublisher kinesisPublisher;

    @Test
    void conApiKeyPublicaElEventoProductoVistoEnElStream() throws Exception {
        mockMvc.perform(post("/api/productos/5/visto").header("X-Api-Key", API_KEY))
                .andExpect(status().isNoContent());

        ArgumentCaptor<String> json = ArgumentCaptor.forClass(String.class);
        verify(kinesisPublisher).publicar(eq("producto_visto"), eq(5L), json.capture());
        assertTrue(json.getValue().contains("\"ProductoVisto\""),
                "el evento debía ser de tipo ProductoVisto y fue: " + json.getValue());
    }

    @Test
    void sinApiKeyResponde401() throws Exception {
        mockMvc.perform(post("/api/productos/5/visto"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void idInvalidoResponde400() throws Exception {
        mockMvc.perform(post("/api/productos/0/visto").header("X-Api-Key", API_KEY))
                .andExpect(status().isBadRequest());
        verify(kinesisPublisher, org.mockito.Mockito.never()).publicar(any(), any(), any());
    }
}
