package cl.ochodigital.pasteleriamydreams.catalogoservice.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.kinesis.KinesisClient;
import software.amazon.awssdk.services.kinesis.model.CreateStreamRequest;
import software.amazon.awssdk.services.kinesis.model.DescribeStreamRequest;
import software.amazon.awssdk.services.kinesis.model.PutRecordRequest;
import software.amazon.awssdk.services.kinesis.model.ResourceNotFoundException;

// Publica los eventos de negocio del catálogo en el stream de Kinesis
// (Entrega 3). Best-effort: ver un producto es un dato, jamás debe romper la
// navegación ni la compra. Partition key = id del producto (los eventos de un
// mismo producto conservan su orden).
@Component
public class KinesisEventosPublisher {

    private static final Logger log = LoggerFactory.getLogger(KinesisEventosPublisher.class);

    private final KinesisClient kinesisClient;
    private final String stream;
    private final boolean habilitado;
    private final boolean autoCrearStream;

    public KinesisEventosPublisher(KinesisClient kinesisClient,
                                   @Value("${app.kinesis.stream:eventos-negocio}") String stream,
                                   @Value("${app.kinesis.habilitado:false}") boolean habilitado,
                                   @Value("${app.kinesis.auto-crear-stream:true}") boolean autoCrearStream) {
        this.kinesisClient = kinesisClient;
        this.stream = stream;
        this.habilitado = habilitado;
        this.autoCrearStream = autoCrearStream;
    }

    public void publicar(String tipoEvento, Long productoId, String json) {
        if (!habilitado) {
            return;
        }
        try {
            asegurarStream();
            kinesisClient.putRecord(PutRecordRequest.builder()
                    .streamName(stream)
                    .partitionKey(String.valueOf(productoId))
                    .data(SdkBytes.fromUtf8String(json))
                    .build());
            log.debug("Evento {} del producto {} publicado en el stream '{}'", tipoEvento, productoId, stream);
        } catch (Exception e) {
            log.error("No se pudo publicar el evento {} del producto {} en el stream '{}': {}",
                    tipoEvento, productoId, stream, e.getMessage());
        }
    }

    // En local el stream se crea solo; crear es ASÍNCRONO (CREATING -> ACTIVE),
    // así que se espera a ACTIVE antes de escribir el primer registro
    private void asegurarStream() {
        try {
            kinesisClient.describeStream(DescribeStreamRequest.builder().streamName(stream).build());
        } catch (ResourceNotFoundException e) {
            if (!autoCrearStream) {
                throw e;
            }
            log.warn("El stream '{}' no existe: creándolo (modo local)", stream);
            kinesisClient.createStream(CreateStreamRequest.builder()
                    .streamName(stream).shardCount(1).build());
            esperarStreamActivo();
        }
    }

    private void esperarStreamActivo() {
        for (int intento = 0; intento < 30; intento++) {
            try {
                var descripcion = kinesisClient.describeStream(
                        DescribeStreamRequest.builder().streamName(stream).build());
                if ("ACTIVE".equals(descripcion.streamDescription().streamStatusAsString())) {
                    return;
                }
            } catch (Exception e) {
                log.debug("Aún no se puede leer el estado del stream '{}': {}", stream, e.getMessage());
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        log.warn("El stream '{}' no informó ACTIVE en 15 s: se intenta publicar igual", stream);
    }
}
