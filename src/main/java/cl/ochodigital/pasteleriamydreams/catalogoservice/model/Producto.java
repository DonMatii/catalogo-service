package cl.ochodigital.pasteleriamydreams.catalogoservice.model;

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
    private String descripcion;

    @Column(name = "imagen_url")
    private String imagenUrl;

    @Column(nullable = false)
    private String categoria;

    private Integer precio;
}