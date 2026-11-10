package cl.ochodigital.pasteleriamydreams.catalogoservice.controller;

import cl.ochodigital.pasteleriamydreams.catalogoservice.model.Producto;
import cl.ochodigital.pasteleriamydreams.catalogoservice.service.DeliciaDelDiaService;
import cl.ochodigital.pasteleriamydreams.catalogoservice.service.ProductoService;
import cl.ochodigital.pasteleriamydreams.catalogoservice.storage.ImageStorage;
import cl.ochodigital.pasteleriamydreams.catalogoservice.storage.KinesisEventosPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://pasteleria-my-dreams-web-8digital.s3-website-us-east-1.amazonaws.com"
})
public class ProductoController {

    private final ProductoService productoService;
    private final DeliciaDelDiaService deliciaDelDiaService;
    private final ImageStorage imageStorage;
    private final KinesisEventosPublisher kinesisPublisher;

    public ProductoController(ProductoService productoService, DeliciaDelDiaService deliciaDelDiaService,
            ImageStorage imageStorage, KinesisEventosPublisher kinesisPublisher) {
        this.productoService = productoService;
        this.deliciaDelDiaService = deliciaDelDiaService;
        this.imageStorage = imageStorage;
        this.kinesisPublisher = kinesisPublisher;
    }

    // Listar todo el inventario (Usado por el catálogo y el admin)
    @GetMapping
    public Map<String, List<Producto>> listarProductos() {
        return productoService.obtenerInventario();
    }

    // Delicia del Día: producto elegido de forma determinista por fecha, el mismo
    // para todos los usuarios (Usado por el Home y por pedidos-service)
    @GetMapping("/delicia-del-dia")
    public ResponseEntity<?> obtenerDeliciaDelDia() {
        return deliciaDelDiaService.obtenerDeliciaDelDia(LocalDate.now())
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("mensaje", "El catálogo está vacío; no hay delicia del día disponible")));
    }

    // Crear un nuevo producto (Usado al publicar desde el AdminPanel)
    @PostMapping
    public ResponseEntity<Producto> crearProducto(@RequestBody Producto producto) {
        Producto nuevoProducto = productoService.guardarProducto(producto);
        return ResponseEntity.ok(nuevoProducto);
    }

    // Actualizar un producto existente (Usado al modificar en el AdminPanel)
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizarProducto(@PathVariable Long id, @RequestBody Producto producto) {
        Producto productoActualizado = productoService.actualizarProducto(id, producto);
        if (productoActualizado != null) {
            return ResponseEntity.ok(productoActualizado);
        }
        return ResponseEntity.notFound().build();
    }

    // Eliminar un producto (Usado al borrar en el AdminPanel)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        boolean eliminado = productoService.eliminarProducto(id);
        if (eliminado) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    private static final long LIMITE_IMAGEN_BYTES = 5L * 1024 * 1024;

    // Subir la imagen de un producto desde el admin (multipart "archivo").
    // La imagen NO vive en el codigo del front: se guarda en el storage (disco
    // local en dev, S3 al migrar) y la URL que se responde viaja en el
    // imagenUrl del create/PUT existente. Es escritura bajo /api/productos,
    // asi que AdminApiKeyFilter exige X-Api-Key.
    @PostMapping("/imagen")
    public ResponseEntity<?> subirImagen(@RequestParam("archivo") MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "No se recibió ningún archivo"));
        }

        String extension = extensionDe(archivo.getContentType());
        if (extension == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", "Formato no permitido: usa JPG, PNG o WebP"));
        }

        if (archivo.getSize() > LIMITE_IMAGEN_BYTES) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "La imagen supera el máximo de 5 MB"));
        }

        try {
            String nombreGenerado = UUID.randomUUID() + extension;
            String url = imageStorage.guardar(nombreGenerado, archivo.getBytes());
            return ResponseEntity.ok(Map.of("url", url));
        } catch (IOException | RuntimeException e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("mensaje", "No se pudo guardar la imagen en el servidor"));
        }
    }

    // Extension de archivo segun el content-type declarado (JPG, PNG o WebP)
    private static String extensionDe(String contentType) {
        return switch (contentType == null ? "" : contentType.toLowerCase()) {
            case "image/jpeg", "image/jpg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> null;
        };
    }

    // Ping de "producto visto" (Entrega 3): el front lo llama cuando la foto
    // del producto entra en pantalla. Publica el evento `producto_visto` en el
    // stream de Kinesis para los "productos más vistos". Best-effort por
    // diseño: el ping jamás rompe la navegación, responde 204 siempre.
    // Evento JSON (mismo contrato que los eventos de pedidos):
    // {"evento":"ProductoVisto","id":<productoId>,"fecha":"<ISO-8601>"}
    @PostMapping("/{id}/visto")
    public ResponseEntity<Void> marcarProductoVisto(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().build();
        }
        // JSON armado a mano: el evento solo lleva id y fecha, sin caracteres
        // que escapar, y así el servicio no necesita dependencias de JSON
        String json = String.format("{\"evento\":\"ProductoVisto\",\"id\":%d,\"fecha\":\"%s\"}",
                id, LocalDateTime.now());
        kinesisPublisher.publicar("producto_visto", id, json);
        return ResponseEntity.noContent().build();
    }
}