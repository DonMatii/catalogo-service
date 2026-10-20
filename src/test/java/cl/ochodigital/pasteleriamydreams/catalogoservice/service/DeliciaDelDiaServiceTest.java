package cl.ochodigital.pasteleriamydreams.catalogoservice.service;

import cl.ochodigital.pasteleriamydreams.catalogoservice.model.Producto;
import cl.ochodigital.pasteleriamydreams.catalogoservice.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

// La Delicia del Dia es determinista por fecha (S1):
// misma fecha -> misma delicia; semana ISO sin repeticiones si hay >= 7 productos.
class DeliciaDelDiaServiceTest {

    // Lunes de la semana ISO 2026-W43 (lunes a domingo: 2026-10-19 .. 2026-10-25)
    private static final LocalDate LUNES_SEMANA = LocalDate.of(2026, 10, 19);

    private ProductoRepository productoRepository;
    private DeliciaDelDiaService deliciaDelDiaService;

    @BeforeEach
    void configurar() {
        productoRepository = mock(ProductoRepository.class);
        deliciaDelDiaService = new DeliciaDelDiaService(productoRepository);
    }

    private void darCatalogo(List<Producto> catalogo) {
        when(productoRepository.findAll()).thenReturn(catalogo);
    }

    private static Producto producto(long id) {
        Producto producto = new Producto();
        producto.setId(id);
        producto.setNombre("Producto " + id);
        producto.setCategoria("Test");
        producto.setPrecio(1000);
        return producto;
    }

    // Catalogo de n productos con ids 1..n, entregado fuera de orden a proposito:
    // el servicio debe ordenar por id antes de barajar.
    private static List<Producto> catalogoDesordenado(int n) {
        List<Producto> catalogo = new ArrayList<>();
        for (long id = 1; id <= n; id++) {
            catalogo.add(producto(id));
        }
        Collections.shuffle(catalogo, new Random(42));
        return catalogo;
    }

    private static long idDe(Optional<Producto> delicia) {
        return delicia.orElseThrow().getId();
    }

    private List<Long> deliciasDeLaSemana(LocalDate lunes, int cantidadDias) {
        List<Long> delicias = new ArrayList<>();
        for (int i = 0; i < cantidadDias; i++) {
            delicias.add(idDe(deliciaDelDiaService.obtenerDeliciaDelDia(lunes.plusDays(i))));
        }
        return delicias;
    }

    @Test
    void mismaFechaEntregaMismaDelicia() {
        darCatalogo(catalogoDesordenado(10));

        long primera = idDe(deliciaDelDiaService.obtenerDeliciaDelDia(LUNES_SEMANA));
        long segunda = idDe(deliciaDelDiaService.obtenerDeliciaDelDia(LUNES_SEMANA));

        assertEquals(primera, segunda, "La misma fecha debe devolver siempre el mismo producto");
    }

    @Test
    void sieteDiasDeLaMismaSemanaIsoNoRepiteProductosConCatalogoGrande() {
        darCatalogo(catalogoDesordenado(10));

        List<Long> delicias = deliciasDeLaSemana(LUNES_SEMANA, 7);

        assertEquals(7, new HashSet<>(delicias).size(),
                "Con catalogo de 10 productos, lunes a domingo deben salir 7 productos distintos");
    }

    @Test
    void sieteDiasDeLaMismaSemanaIsoCubrenTodoElCatalogoCuandoHayExactamente7() {
        darCatalogo(catalogoDesordenado(7));

        List<Long> delicias = deliciasDeLaSemana(LUNES_SEMANA, 7);

        Set<Long> esperado = delicias.stream().collect(Collectors.toSet());
        assertEquals(Set.of(1L, 2L, 3L, 4L, 5L, 6L, 7L), esperado,
                "Con exactamente 7 productos, la semana debe ser una permutacion completa del catalogo");
        assertEquals(7, new HashSet<>(delicias).size(), "Ningun producto debe repetirse en la semana");
    }

    @Test
    void catalogoConMenosDe7ProductosUsaModuloYRepiteSinFallar() {
        darCatalogo(catalogoDesordenado(3));

        List<Long> delicias = deliciasDeLaSemana(LUNES_SEMANA, 7);

        Set<Long> unicos = new HashSet<>(delicias);
        assertEquals(Set.of(1L, 2L, 3L), unicos,
                "Con catalogo de 3 productos, el modulo debe cubrir los 3 a lo largo de la semana");
        // Dia 0 (lunes) y dia 3 (jueves) mapean al mismo indice 0 % 3
        assertEquals(delicias.get(0), delicias.get(3),
                "Con menos de 7 productos el modulo repite el mismo producto en el mismo indice");
    }

    @Test
    void semanasIsoDistintasRealizanUnSorteoDistinto() {
        darCatalogo(catalogoDesordenado(10));

        // 8 lunes de semanas ISO consecutivas de 2026
        List<Long> delicias = new ArrayList<>();
        LocalDate lunes = LocalDate.of(2026, 1, 5);
        for (int semana = 0; semana < 8; semana++) {
            delicias.add(idDe(deliciaDelDiaService.obtenerDeliciaDelDia(lunes)));
            lunes = lunes.plusWeeks(1);
        }

        assertNotEquals(1, new HashSet<>(delicias).size(),
                "Semanas ISO distintas deben poder cambiar la delicia (nuevo sorteo por semana)");
    }

    @Test
    void catalogoVacioEntregaVacio() {
        darCatalogo(new ArrayList<>());

        Optional<Producto> delicia = deliciaDelDiaService.obtenerDeliciaDelDia(LUNES_SEMANA);

        assertTrue(delicia.isEmpty(), "Con el catalogo vacio el servicio debe devolver vacio");
    }

    // Fijacion del contrato: lista ordenada por id -> Fisher-Yates con semilla
    // (anio de semana ISO * 100 + numero de semana ISO) -> indice 0=lunes .. 6=domingo
    @Test
    void eligeElProductoDelBarajadoDeterministicoDelContrato() {
        List<Producto> catalogo = catalogoDesordenado(10);
        darCatalogo(catalogo);

        List<LocalDate> fechas = List.of(
                LocalDate.of(2026, 10, 19), // lunes, semana ISO 2026-W43
                LocalDate.of(2026, 10, 20), // martes, misma semana
                LocalDate.of(2026, 10, 25), // domingo, misma semana
                LocalDate.of(2026, 1, 5),   // lunes, semana ISO 2026-W02
                LocalDate.of(2026, 12, 31)  // jueves, semana ISO 2026-W53
        );

        for (LocalDate fecha : fechas) {
            long esperado = esperadaDelContrato(catalogo, fecha).getId();
            long obtenido = idDe(deliciaDelDiaService.obtenerDeliciaDelDia(fecha));
            assertEquals(esperado, obtenido, "La delicia de " + fecha + " debe seguir el barajado del contrato");
        }
    }

    // Implementacion de referencia del contrato, escrita a mano en el test para fijar el algoritmo
    private static Producto esperadaDelContrato(List<Producto> catalogo, LocalDate fecha) {
        List<Producto> ordenada = new ArrayList<>(catalogo);
        ordenada.sort((a, b) -> a.getId().compareTo(b.getId()));

        List<Producto> barajada = new ArrayList<>(ordenada);
        int anioIso = fecha.get(java.time.temporal.WeekFields.ISO.weekBasedYear());
        int semanaIso = fecha.get(java.time.temporal.WeekFields.ISO.weekOfWeekBasedYear());
        Collections.shuffle(barajada, new Random(anioIso * 100L + semanaIso));

        int dia = fecha.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue();
        return barajada.get(dia % barajada.size());
    }
}
