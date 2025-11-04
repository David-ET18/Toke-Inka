package com.example.toke.config;

import com.example.toke.entities.*;
import com.example.toke.entities.enums.RolUsuario;
import com.example.toke.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    // Repositorios existentes
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    // Nuevos repositorios para los productos
    private final CategoriaRepository categoriaRepository;
    private final TallaRepository tallaRepository;
    private final ProductoRepository productoRepository;
    private final InventarioRepository inventarioRepository;

    @Autowired
    public DataLoader(UsuarioRepository usuarioRepository,
                      PasswordEncoder passwordEncoder,
                      CategoriaRepository categoriaRepository,
                      TallaRepository tallaRepository,
                      ProductoRepository productoRepository,
                      InventarioRepository inventarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.categoriaRepository = categoriaRepository;
        this.tallaRepository = tallaRepository;
        this.productoRepository = productoRepository;
        this.inventarioRepository = inventarioRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        crearAdminSiNoExiste();
        crearDatosDeProductosSiNoExisten();
    }

    private void crearAdminSiNoExiste() {
        if (!usuarioRepository.existsByRol(RolUsuario.ROLE_ADMIN)) {
            System.out.println("Creando administrador por defecto...");
            Usuario admin = new Usuario();
            admin.setNombre("Admin");
            admin.setApellido("TokeInca");
            admin.setEmail("admin@tokeinca.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRol(RolUsuario.ROLE_ADMIN);
            usuarioRepository.save(admin);
            System.out.println("Administrador creado: admin@tokeinca.com / admin123");
        }
    }

    private void crearDatosDeProductosSiNoExisten() {
        if (productoRepository.count() == 0) {
            System.out.println("Base de datos de productos vacía. Creando datos de muestra...");

            // 1. Crear Categorías
            Categoria catCamisetas = new Categoria();
            catCamisetas.setNombre("Camisetas");

            Categoria catPantalones = new Categoria();
            catPantalones.setNombre("Pantalones");

            Categoria catCasacas = new Categoria();
            catCasacas.setNombre("Casacas");
            
            categoriaRepository.saveAll(Arrays.asList(catCamisetas, catPantalones, catCasacas));

            // 2. Crear Tallas
            Talla tallaS = new Talla();
            tallaS.setNombre("S");
            Talla tallaM = new Talla();
            tallaM.setNombre("M");
            Talla tallaL = new Talla();
            tallaL.setNombre("L");
            Talla tallaXL = new Talla();
            tallaXL.setNombre("XL");

            tallaRepository.saveAll(Arrays.asList(tallaS, tallaM, tallaL, tallaXL));

            // 3. Crear Productos y su Inventario
            
            // Producto 1
            Producto p1 = new Producto();
            p1.setNombre("Camiseta Azul Andino");
            p1.setDescripcion("Una camiseta de algodón pima con un diseño inspirado en la sutiles del mar. Perfecta para un look casual y cultural.");
            p1.setPrecio(new BigDecimal("79.90"));
            p1.setCategoria(catCamisetas);
            p1.setUrlImagen("../../../resources/images/camisero_azul.jpg");
            productoRepository.save(p1);
            crearInventario(p1, tallaS, 20);
            crearInventario(p1, tallaM, 25);
            crearInventario(p1, tallaL, 15);

            // Producto 2
            Producto p2 = new Producto();
            p2.setNombre("Jogger Azul para Aventura");
            p2.setDescripcion("Jogger de tela ripstop resistente, con múltiples bolsillos para máxima funcionalidad. Ideal para trekking o uso urbano.");
            p2.setPrecio(new BigDecimal("149.50"));
            p2.setCategoria(catPantalones);
            p2.setUrlImagen("/images/jogger_azul.jpg");
            productoRepository.save(p2);
            crearInventario(p2, tallaM, 18);
            crearInventario(p2, tallaL, 22);
            crearInventario(p2, tallaXL, 10);
            
            // Producto 3
            Producto p3 = new Producto();
            p3.setNombre("Polera Marrón");
            p3.setDescripcion("Ligera y resistente al agua, esta polera es tu mejor aliada contra el viento. Diseño minimalista con el logo del cóndor bordado.");
            p3.setPrecio(new BigDecimal("199.00"));
            p3.setCategoria(catCasacas);
            p3.setUrlImagen("/images/polera_marroninca.jpg");
            productoRepository.save(p3);
            crearInventario(p3, tallaS, 12);
            crearInventario(p3, tallaM, 15);
            crearInventario(p3, tallaL, 11);
            crearInventario(p3, tallaXL, 8);

            // Producto 4
            Producto p4 = new Producto();
            p4.setNombre("Camisero Beige Representativo");
            p4.setDescripcion("Diseño sutil y elegante que evoca una comodidad única. Fabricada con algodón orgánico suave al tacto.");
            p4.setPrecio(new BigDecimal("85.00"));
            p4.setCategoria(catCamisetas);
            p4.setUrlImagen("../src/main/resource/static/imagesg");
            productoRepository.save(p4);
            crearInventario(p4, tallaS, 30);
            crearInventario(p4, tallaM, 30);
            crearInventario(p4, tallaL, 20);

            System.out.println("Datos de muestra creados con éxito.");
        }
    }

    /**
     * Método de ayuda para crear una entrada de inventario.
     */
    private void crearInventario(Producto producto, Talla talla, int stock) {
        Inventario inventario = new Inventario();
        inventario.setProducto(producto);
        inventario.setTalla(talla);
        inventario.setStock(stock);
        inventarioRepository.save(inventario);
    }
}