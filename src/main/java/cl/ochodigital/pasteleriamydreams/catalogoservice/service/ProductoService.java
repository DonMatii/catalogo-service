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

    // Inyectamos el repositorio que conecta con la base de datos cloud en AWS RDS
    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    // Listar y agrupar todo el inventario por categoría
    public Map<String, List<Producto>> obtenerInventario() {
        List<Producto> todosLosProductos = productoRepository.findAll();
        return todosLosProductos.stream()
                .collect(Collectors.groupingBy(Producto::getCategoria));
    }

    // Guardar un nuevo producto en la base de datos
    public Producto guardarProducto(Producto producto) {
        return productoRepository.save(producto);
    }

    // Actualizar un producto existente buscando por su ID (usando los campos exactos de tu modelo)
    public Producto actualizarProducto(Long id, Producto productoDetalles) {
        return productoRepository.findById(id).map(producto -> {
            producto.setNombre(productoDetalles.getNombre());
            producto.setDescripcion(productoDetalles.getDescripcion());
            producto.setPrecio(productoDetalles.getPrecio());
            producto.setImagen(productoDetalles.getImagen());
            producto.setCategoria(productoDetalles.getCategoria());
            return productoRepository.save(producto);
        }).orElse(null);
    }

    // Eliminar un producto de la base de datos por su ID
    public boolean eliminarProducto(Long id) {
        if (productoRepository.existsById(id)) {
            productoRepository.deleteById(id);
            return true;
        }
        return false;
    }
}