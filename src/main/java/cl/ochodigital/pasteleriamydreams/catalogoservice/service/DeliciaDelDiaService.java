package cl.ochodigital.pasteleriamydreams.catalogoservice.service;

import cl.ochodigital.pasteleriamydreams.catalogoservice.model.Producto;
import cl.ochodigital.pasteleriamydreams.catalogoservice.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;

// Delicia del Dia: descuento destacado determinista por fecha.
// Un solo dueno de la decision: este servicio decide la delicia; los demas
// consumen el resultado via GET /api/productos/delicia-del-dia.
@Service
public class DeliciaDelDiaService {

    private final ProductoRepository productoRepository;

    public DeliciaDelDiaService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    // Devuelve la delicia para la fecha dada, o vacio si el catalogo esta vacio.
    // Contrato: lista ordenada por id -> Fisher-Yates con new Random(seed) donde
    // seed deriva establemente del par (anio de semana ISO, numero de semana ISO)
    // -> indice final (0=lunes .. 6=domingo) % tamanio. Misma fecha = mismo resultado.
    public Optional<Producto> obtenerDeliciaDelDia(LocalDate fecha) {
        List<Producto> productos = new ArrayList<>(productoRepository.findAll());
        if (productos.isEmpty()) {
            return Optional.empty();
        }
        productos.sort(Comparator.comparing(Producto::getId));

        List<Producto> barajada = new ArrayList<>(productos);
        Collections.shuffle(barajada, new Random(seedDeLaSemana(fecha)));

        int diaDeLaSemana = fecha.getDayOfWeek().getValue() - 1; // 0=lunes .. 6=domingo
        return Optional.of(barajada.get(diaDeLaSemana % barajada.size()));
    }

    // Semilla estable del par (anio de semana ISO, numero de semana ISO)
    private long seedDeLaSemana(LocalDate fecha) {
        int anioDeSemana = fecha.get(WeekFields.ISO.weekBasedYear());
        int numeroDeSemana = fecha.get(WeekFields.ISO.weekOfWeekBasedYear());
        return anioDeSemana * 100L + numeroDeSemana;
    }
}
