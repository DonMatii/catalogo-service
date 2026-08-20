package cl.ochodigital.pasteleriamydreams.catalogoservice.service;

import cl.ochodigital.pasteleriamydreams.catalogoservice.model.Producto;
import cl.ochodigital.pasteleriamydreams.catalogoservice.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    // Inyectamos el repositorio que conecta con XAMPP
    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public Map<String, List<Producto>> obtenerInventario() {
        // 1. Obtenemos absolutamente todos los productos desde la base de datos
        List<Producto> todosLosProductos = productoRepository.findAll();

        // 2. Usamos Streams para agruparlos automáticamente por el campo "categoria"
        // Esto creará exactamente el formato que React espera: { "tortas": [...], "queques": [...] }
        return todosLosProductos.stream()
                .collect(Collectors.groupingBy(Producto::getCategoria));
    }
}