package cl.ochodigital.pasteleriamydreams.catalogoservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "productos")
@Data
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "descripcion", length = 500)
    @JsonProperty("desc")
    private String descripcion;

    @Column(name = "imagen_url")
    @JsonProperty("img")
    private String imagen;

    @Column(nullable = false)
    private String categoria;

    private Integer precio;
}