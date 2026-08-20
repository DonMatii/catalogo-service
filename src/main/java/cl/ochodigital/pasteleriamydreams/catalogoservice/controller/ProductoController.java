package cl.ochodigital.pasteleriamydreams.catalogoservice.controller;

import cl.ochodigital.pasteleriamydreams.catalogoservice.model.Producto;
import cl.ochodigital.pasteleriamydreams.catalogoservice.service.ProductoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    // Inyección de dependencias (Estándar de Spring)
    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public Map<String, List<Producto>> listarProductos() {
        return productoService.obtenerInventario();
    }
}