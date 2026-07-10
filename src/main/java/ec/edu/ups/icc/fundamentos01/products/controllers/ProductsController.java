package ec.edu.ups.icc.fundamentos01.products.controllers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
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
import org.springframework.web.bind.annotation.RestController;

import ec.edu.ups.icc.fundamentos01.core.dtos.PaginationDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.CreateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.PartialUpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.UpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.services.ProductService;
import ec.edu.ups.icc.fundamentos01.security.services.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/products")
public class ProductsController {

    private final ProductService service;

    public ProductsController(ProductService service) {
        this.service = service;
    }


    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") 
    public List<ProductResponseDto> findAll() {
        return service.findAll();
    }

    @GetMapping("/page")
    public Page<ProductResponseDto> findAllPage(
            @Valid @ModelAttribute PaginationDto pagination,
            @AuthenticationPrincipal UserDetailsImpl currentUser
    ) {
        return service.findAllPage(pagination, currentUser); 
    }

    @GetMapping("/slice")
    public Slice<ProductResponseDto> findAllSlice(
            @Valid @ModelAttribute PaginationDto pagination,
            @AuthenticationPrincipal UserDetailsImpl currentUser 
    ) {
        return service.findAllSlice(pagination, currentUser); 
    }

    @GetMapping("/{id}")
    public ProductResponseDto findOne(@PathVariable Long id) {
        return service.findOne(id);
    }

    @PostMapping
    public ProductResponseDto create(
            @Valid @RequestBody CreateProductDto dto,
            @AuthenticationPrincipal UserDetailsImpl currentUser
    ) {
        return service.create(dto, currentUser);
    }

    @PutMapping("/{id}")
    public ProductResponseDto update(
            @PathVariable Long id, 
            @Valid @RequestBody UpdateProductDto dto,
            @AuthenticationPrincipal UserDetailsImpl currentUser
    ) {
        return service.update(id, dto, currentUser);
    }

    @PatchMapping("/{id}")
    public ProductResponseDto partialUpdate(
            @PathVariable Long id, 
            @Valid @RequestBody PartialUpdateProductDto dto,
            @AuthenticationPrincipal UserDetailsImpl currentUser 
    ) {
        return service.partialUpdate(id, dto, currentUser);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl currentUser
    ) {
        service.delete(id, currentUser);
    }

    @GetMapping("/user/{userId}")
    public List<ProductResponseDto> findByUserId(@PathVariable Long userId) {
        return service.findByUserId(userId);
    }
}