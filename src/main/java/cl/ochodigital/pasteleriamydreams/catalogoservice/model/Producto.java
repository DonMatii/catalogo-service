package cl.ochodigital.pasteleriamydreams.catalogoservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Producto {

    private Long id;

    private String nombre;

    private String img;

    @JsonProperty("desc") // la variable exactamente como está en el front
    private String descripcion;

    private String categoria; // Para clasificar si es torta, queque, tarta o personal

}