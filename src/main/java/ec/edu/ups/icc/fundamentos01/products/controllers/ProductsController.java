package ec.edu.ups.icc.fundamentos01.products.controllers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ec.edu.ups.icc.fundamentos01.core.dtos.PaginationDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.CreateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.PartialUpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.UpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.services.ProductService;
import ec.edu.ups.icc.fundamentos01.security.services.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(
    name = "Productos",
    description = "Gestión de productos con soporte para CRUD, paginación, autenticación mediante JWT y control de permisos basado en roles y ownership."
)
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/products")
public class ProductsController {

    private final ProductService service;

    public ProductsController(ProductService service) {
        this.service = service;
    }

    @Operation(
        summary = "Obtener todos los productos",
        description = """
                Devuelve la lista completa de productos activos registrados en el sistema.
                
                Requisitos:
                - Estar autenticado mediante JWT.
                - Tener el rol ADMIN o USER.
                
                No utiliza paginación.
                """
    )
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')") 
    public List<ProductResponseDto> findAll() {
        return service.findAll();
    }

    @Operation(
        summary = "Obtener productos paginados (Page)",
        description = """
                Devuelve los productos utilizando paginación basada en Page de Spring Data.
                
                Requisitos:
                - Estar autenticado mediante JWT.
                
                Parámetros de consulta:
                - page: número de página.
                - size: cantidad de registros por página.
                - sort: campo y dirección de ordenamiento.
                
                La respuesta incluye información de paginación como total de elementos,
                total de páginas y cantidad de registros.
                """
    )
    @GetMapping("/page")
    public Page<ProductResponseDto> findAllPage(
            @Valid @ModelAttribute PaginationDto pagination,
            @AuthenticationPrincipal UserDetailsImpl currentUser
    ) {
        return service.findAllPage(pagination, currentUser); 
    }

    @Operation(
        summary = "Obtener productos del usuario autenticado (Slice)",
        description = """
                Devuelve únicamente los productos utilizando paginación basada en Slice.
                
                Requisitos:
                - Estar autenticado mediante JWT.
                
                El usuario se obtiene automáticamente desde el token mediante
                @AuthenticationPrincipal.
                
                Parámetros de consulta:
                - page: número de página.
                - size: cantidad de registros.
                - sort: campo de ordenamiento.
                
                La respuesta indica si existe una siguiente página, pero no calcula
                el número total de registros.
                """
    )
    @GetMapping("/slice")
    public Slice<ProductResponseDto> findAllSlice(
            @Valid @ModelAttribute PaginationDto pagination,
            @AuthenticationPrincipal UserDetailsImpl currentUser 
    ) {
        return service.findAllSlice(pagination, currentUser); 
    }

    @Operation(
        summary = "Obtener un producto por ID",
        description = """
                Devuelve la información de un producto específico.
                
                Requisitos:
                - Estar autenticado mediante JWT.
                
                Parámetro de ruta:
                - id: identificador del producto.
                
                Retorna el producto correspondiente si existe.
                """
    )
    @GetMapping("/{id}")
    public ProductResponseDto findOne(@PathVariable Long id) {
        return service.findOne(id);
    }

    @Operation(
        summary = "Crear un producto",
        description = """
                Registra un nuevo producto.
                
                Requisitos:
                - Estar autenticado mediante JWT.
                
                El propietario del producto NO se envía en el cuerpo de la petición.
                El usuario propietario se obtiene automáticamente desde el token JWT
                mediante @AuthenticationPrincipal.
                
                El cuerpo de la petición debe contener un objeto CreateProductDto
                con la información necesaria para crear el producto.
                """
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponseDto create(
            @Valid @RequestBody CreateProductDto dto,
            @AuthenticationPrincipal UserDetailsImpl currentUser
    ) {
        return service.create(dto, currentUser);
    }

    @Operation(
        summary = "Actualizar completamente un producto",
        description = """
                Reemplaza completamente la información de un producto existente.
                
                Requisitos:
                - Estar autenticado mediante JWT.
                - Ser propietario del producto o tener rol ADMIN.
                
                Parámetro de ruta:
                - id: identificador del producto.
                
                El cuerpo de la petición debe contener un UpdateProductDto
                con toda la información del producto.
                """
    )
    @PutMapping("/{id}")
    public ProductResponseDto update(
            @PathVariable Long id, 
            @Valid @RequestBody UpdateProductDto dto,
            @AuthenticationPrincipal UserDetailsImpl currentUser
    ) {
        return service.update(id, dto, currentUser);
    }

    @Operation(
        summary = "Actualizar parcialmente un producto",
        description = """
                Actualiza únicamente los campos enviados del producto.
                
                Requisitos:
                - Estar autenticado mediante JWT.
                - Ser propietario del producto o tener rol ADMIN.
                
                Parámetro de ruta:
                - id: identificador del producto.
                
                El cuerpo de la petición debe contener un PartialUpdateProductDto
                con únicamente los campos que se desean modificar.
                """
    )
    @PatchMapping("/{id}")
    public ProductResponseDto partialUpdate(
            @PathVariable Long id, 
            @Valid @RequestBody PartialUpdateProductDto dto,
            @AuthenticationPrincipal UserDetailsImpl currentUser 
    ) {
        return service.partialUpdate(id, dto, currentUser);
    }

    @Operation(
        summary = "Eliminar un producto",
        description = """
                Elimina un producto mediante su identificador.
                
                Requisitos:
                - Estar autenticado mediante JWT.
                - Ser propietario del producto o tener rol ADMIN.
                
                Parámetro de ruta:
                - id: identificador del producto que se desea eliminar.
                
                Si la operación es exitosa no devuelve contenido.
                """
    )
    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl currentUser
    ) {
        service.delete(id, currentUser);
    }

    @Operation(
        summary = "Buscar productos por usuario",
        description = """
                Devuelve todos los productos pertenecientes a un usuario específico.
                
                Requisitos:
                - Estar autenticado mediante JWT.
                
                Parámetro de ruta:
                - userId: identificador del usuario propietario.
                
                Retorna una lista con todos los productos asociados al usuario.
                """
    )
    @GetMapping("/user/{userId}")
    public List<ProductResponseDto> findByUserId(@PathVariable Long userId) {
        return service.findByUserId(userId);
    }
}