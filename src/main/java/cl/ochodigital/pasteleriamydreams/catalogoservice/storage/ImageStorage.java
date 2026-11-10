package cl.ochodigital.pasteleriamydreams.catalogoservice.storage;

// Puerto de almacenamiento de imagenes de producto. El AdminPanel sube el
// archivo y el catalogo lo guarda aqui: disco local en desarrollo, S3 al
// migrar a AWS. El contrato no cambia con la migracion: solo se cambia el
// adaptador (por eso la URL publica la decide cada implementacion).
public interface ImageStorage {

    /** Guarda la imagen con el nombre generado y devuelve su URL publica. */
    String guardar(String nombreGenerado, byte[] contenido);
}
