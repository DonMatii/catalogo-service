package cl.ochodigital.pasteleriamydreams.catalogoservice.controller;

import cl.ochodigital.pasteleriamydreams.catalogoservice.model.Producto;
import cl.ochodigital.pasteleriamydreams.catalogoservice.repository.ProductoRepository;
import cl.ochodigital.pasteleriamydreams.catalogoservice.service.DeliciaDelDiaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// GET /api/productos/delicia-del-dia: 200 con el mismo JSON del catalogo o
// 404 con {"mensaje": "..."} si el catalogo esta vacio. Publico, sin X-Api-Key.
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class DeliciaDelDiaIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private DeliciaDelDiaService deliciaDelDiaService;

    private void guardarCatalogo(int cantidad) {
        productoRepository.deleteAll();
        for (int i = 1; i <= cantidad; i++) {
            Producto producto = new Producto();
            producto.setNombre("Delicia " + i);
            producto.setDescripcion("Descripcion " + i);
            producto.setImagenUrl("https://ejemplo.com/" + i + ".png");
            producto.setCategoria("Delicias");
            producto.setPrecio(5000 + i);
            productoRepository.save(producto);
        }
    }

    @Test
    void catalogoVacioResponde404ConMensaje() throws Exception {
        guardarCatalogo(0);

        mockMvc.perform(get("/api/productos/delicia-del-dia"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").isNotEmpty());
    }

    @Test
    void conCatalogoResponde200ConElMismoJsonDelRestoDelCatalogo() throws Exception {
        guardarCatalogo(8);

        long esperado = deliciaDelDiaService
                .obtenerDeliciaDelDia(LocalDate.now()).orElseThrow().getId();

        mockMvc.perform(get("/api/productos/delicia-del-dia"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(esperado))
                .andExpect(jsonPath("$.nombre").isNotEmpty())
                .andExpect(jsonPath("$.descripcion").isNotEmpty())
                .andExpect(jsonPath("$.imagenUrl").isNotEmpty())
                .andExpect(jsonPath("$.categoria").isNotEmpty())
                .andExpect(jsonPath("$.precio").isNumber());
    }

    @Test
    void mismaFechaRespondeMismaDeliciaParaTodosLosUsuarios() throws Exception {
        guardarCatalogo(8);

        String primero = mockMvc.perform(get("/api/productos/delicia-del-dia"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String segundo = mockMvc.perform(get("/api/productos/delicia-del-dia"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals(primero, segundo, "Dos usuarios en la misma fecha deben ver la misma delicia");
    }

    // El endpoint es publico: no se envia header X-Api-Key (lo consume el Home sin login
    // y pedidos-service servidor a servidor)
    @Test
    void respondeSinHeaderDeApiKey() throws Exception {
        guardarCatalogo(8);

        mockMvc.perform(get("/api/productos/delicia-del-dia")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
