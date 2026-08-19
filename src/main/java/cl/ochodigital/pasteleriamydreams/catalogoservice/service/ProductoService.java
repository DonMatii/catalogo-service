package cl.ochodigital.pasteleriamydreams.catalogoservice.service;

import cl.ochodigital.pasteleriamydreams.catalogoservice.model.Producto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductoService {

    public Map<String, List<Producto>> obtenerInventario() {
        Map<String, List<Producto>> inventario = new HashMap<>();

        // 🍰 Categoría: Tortas
        List<Producto> tortas = new ArrayList<>();
        tortas.add(new Producto(1L, "Merengue Frambuesa", "tortaMerengueFrambueza.jpg", "Nube de dulzura con discos de merengue, crema chantilly y frambuesas frescas.", "tortas"));
        tortas.add(new Producto(2L, "Selva Negra", "tortaSelvaNegra.jpg", "Clásico bizcocho de chocolate, licor de cereza, crema y guindas.", "tortas"));
        tortas.add(new Producto(3L, "Manjar Lúcuma", "tortaManjarLucuma.jpg", "Tradición chilena: bizcocho suave con manjar y crema de lúcuma natural.", "tortas"));
        tortas.add(new Producto(4L, "Torta Crema Piña", "tortaCremaPina.jpg", "Frescura tropical con bizcocho mojadito, crema suave y trozos de piña.", "tortas"));
        tortas.add(new Producto(5L, "Manjar Nuez", "tortaManjarNuez.jpg", "La favorita: capas de bizcocho unidas por abundante manjar y nueces picadas.", "tortas"));
        inventario.put("tortas", tortas);

        // 🍞 Categoría: Queques
        List<Producto> queques = new ArrayList<>();
        queques.add(new Producto(6L, "Queque Marmoleado", "quequeMarmoladoVainilla.jpg", "Lo mejor de dos mundos: una hermosa mezcla de vainilla y cacao puro.", "queques"));
        queques.add(new Producto(7L, "Queque Vainilla", "quequeVainilla.jpg", "Sencillo y perfecto. Ideal para la hora del té, con esencia natural de vainilla.", "queques"));
        queques.add(new Producto(8L, "Queque Arándano", "quequeArandano.jpg", "Explosión de sabor con masa húmeda cargada de arándanos frescos del huerto.", "queques"));
        inventario.put("queques", queques);

        // 🥧 Categoría: Tartas
        List<Producto> tartas = new ArrayList<>();
        tartas.add(new Producto(9L, "Tartaleta Frutal", "tartaletaFrutas.jpg", "Base crujiente, crema pastelera suave y un mix de frutas de la estación.", "tartas"));
        tartas.add(new Producto(10L, "Tarta de Yogurt", "tartaYogurth.jpg", "Ligera y refrescante. Mousse de yogurt con capa brillante de frambuesa.", "tartas"));
        tartas.add(new Producto(11L, "Pie de Limón", "pieDeLimon.jpg", "Ácido y dulce. Crema de limón sobre base de galleta y merengue tostado.", "tartas"));
        tartas.add(new Producto(12L, "Kuchen de Manzana", "kutchenDeManzana.jpg", "Receta casera: con manzanas frescas, masa suave y un toque de canela.", "tartas"));
        inventario.put("tartas", tartas);

        // 🧁 Categoría: Personales
        List<Producto> personales = new ArrayList<>();
        personales.add(new Producto(13L, "Muffins Plátano Arándano", "muffinsPlatanoArandano.jpg", "Energía saludable en formato individual con plátano maduro y arándanos.", "personales"));
        personales.add(new Producto(14L, "Rollos de Canela", "rollosDeCanela.jpg", "Espirales de masa suave, canela intensa y un glaseado irresistible.", "personales"));
        personales.add(new Producto(15L, "Cupcakes Surtidos", "cupcakesVariedades.jpg", "Coloridos, esponjosos y con el frosting más dulce para alegrar tu día.", "personales"));
        personales.add(new Producto(16L, "Brownie Chocolate Nuez", "brownieChocolateNuez.jpg", "La perfección del chocolate: interior húmedo, cubierta crujiente y nueces.", "personales"));
        personales.add(new Producto(17L, "Donas Glaseadas", "donasGlaseadas.jpg", "El capricho perfecto. Esponjosas, con un glaseado suave y llenas de color.", "personales"));
        inventario.put("personales", personales);

        return inventario;
    }
}