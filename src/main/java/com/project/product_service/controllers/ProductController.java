package com.project.product_service.controllers;

import com.project.product_service.dtos.ProductDTO;
import com.project.product_service.entities.Product;
import com.project.product_service.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://your-frontend-url")
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/products")
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "Returns a list of all products")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Products found",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            array =
                                            @ArraySchema(
                                                    schema =
                                                    @Schema(
                                                            implementation =
                                                                    ProductDTO.class)))
                            }),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Products not found",
                            content = @Content)
            })
    @GetMapping
    public List<ProductDTO> getAllProducts(
            @RequestParam(name = "name", required = false) String name) {
        return name == null ? productService.getAllProducts() : productService.getProductsByNameContaining(name);
    }


    @Operation(summary = "Returns a product by ID")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Product found",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema =
                                            @Schema(
                                                    implementation =
                                                            ProductDTO.class))
                            }),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Product not found",
                            content = @Content)
            })
    @GetMapping("/{id}")
    public ProductDTO getProductById(@PathVariable("id") Long id) {
        return productService.findById(id);
    }

    @Operation(summary = "Creates a new product")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Product created",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema =
                                            @Schema(
                                                    implementation =
                                                            ProductDTO.class))
                            }),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input",
                            content = @Content)
            })
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody ProductDTO productDTO) {
        Product createdProduct = productService.addNewProduct(productDTO);
        URI location =
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(createdProduct.getId())
                        .toUri();
        return ResponseEntity.created(location).body(createdProduct);
    }

    @Operation(summary = "Updates an existing product")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Product updated",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema =
                                            @Schema(
                                                    implementation =
                                                            ProductDTO.class))
                            }),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Product not found",
                            content = @Content)
            })
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ProductDTO updateProduct(@PathVariable("id") Long id, @RequestBody ProductDTO productDTO) {
        return productService.updateProduct(id, productDTO);
    }

    @Operation(summary = "Deletes a product by ID")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Product deleted",
                            content = @Content),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Product not found",
                            content = @Content)
            })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

}