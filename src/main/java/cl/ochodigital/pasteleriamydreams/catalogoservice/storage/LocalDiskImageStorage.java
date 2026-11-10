package cl.ochodigital.pasteleriamydreams.catalogoservice.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

// Adaptador local de ImageStorage: guarda los archivos en un directorio fuera
// del jar (app.imagenes.directorio, default ./uploads) que WebConfig sirve en
// /uploads/**. Al migrar a AWS se reemplaza por el adaptador S3 sin tocar el
// controlador ni el front.
@Component
public class LocalDiskImageStorage implements ImageStorage {

    private static final String PREFIJO_URL = "/uploads/";

    private final Path directorio;

    public LocalDiskImageStorage(@Value("${app.imagenes.directorio:./uploads}") String directorio) {
        this.directorio = Path.of(directorio);
    }

    @Override
    public String guardar(String nombreGenerado, byte[] contenido) {
        try {
            Files.createDirectories(directorio);
            Files.write(directorio.resolve(nombreGenerado), contenido);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo escribir la imagen " + nombreGenerado, e);
        }
        return PREFIJO_URL + nombreGenerado;
    }
}
