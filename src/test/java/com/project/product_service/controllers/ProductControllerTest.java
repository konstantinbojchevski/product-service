package com.project.product_service.controllers;

import com.project.product_service.dtos.ProductDTO;
import com.project.product_service.entities.Product;
import com.project.product_service.services.ElementNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles(profiles = "test")
class ProductControllerTest {

    @Autowired
    private ProductController productController;

    private final List<Long> idsToDelete = new ArrayList<>();

    @AfterEach
    void clearInsertedEntities() {
        if (!idsToDelete.isEmpty()) {
            for (Long id : idsToDelete) {
                productController.deleteProduct(id);
            }
            idsToDelete.clear();
        }
    }

    ResponseEntity<Product> addNewTestProductToDatabase(ProductDTO productDTO) {
        ResponseEntity<Product> product = productController.createProduct(productDTO);
        idsToDelete.add(Objects.requireNonNull(product.getBody()).getId());

        return product;
    }

    @Test
    void ensureThatGetAllProductsWorks() {
        int size = productController.getAllProducts(null).size();
        addNewTestProductToDatabase(createProductDTO("Product 1", BigDecimal.valueOf(10)));
        addNewTestProductToDatabase(createProductDTO("Product 2", BigDecimal.valueOf(20)));

        int sizeAfterAddition = productController.getAllProducts(null).size();

        assertEquals(size + 2, sizeAfterAddition);
    }

    @Test
    void ensureThatGetProductByIdWorks() {
        ResponseEntity<Product> newProduct = addNewTestProductToDatabase(createProductDTO("New Product", BigDecimal.valueOf(30)));

        ProductDTO returnedProduct = productController.getProductById(Objects.requireNonNull(newProduct.getBody()).getId());

        assertEquals(returnedProduct.getName(), newProduct.getBody().getName());
    }

    @Test
    void ensureGetProductByIdThrowsErrorResponseWhenIdNotExists() {
        Long id = 999L;

        ElementNotFoundException thrown =
                assertThrows(ElementNotFoundException.class, () -> productController.getProductById(id));

        assertTrue(thrown.getMessage().contains("Product with id " + id + " not found."));
    }

    @Test
    void ensureThatCreateProductSavesProductInRepository() {
        ProductDTO productDTO = createProductDTO("New Product", BigDecimal.valueOf(40));

        ResponseEntity<Product> newProduct = addNewTestProductToDatabase(productDTO);

        assertEquals(Objects.requireNonNull(newProduct.getBody()).getName(), productDTO.getName());
    }

    @Test
    void ensureCreateProductThrowsErrorResponseWhenNameIsInvalid() {
        ProductDTO productDTOInvalid = createProductDTO("", BigDecimal.valueOf(50));

        ResponseStatusException thrown =
                assertThrows(
                        ResponseStatusException.class,
                        () -> productController.createProduct(productDTOInvalid));

        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatusCode());
        assertTrue(thrown.getMessage().contains("Invalid input"));
    }

    @Test
    void ensureThatUpdateProductUpdatesProductInRepository() {
        ResponseEntity<Product> newProduct = addNewTestProductToDatabase(createProductDTO("Initial Product", BigDecimal.valueOf(60)));
        ProductDTO updatedProductDTO = createProductDTO("Updated Product", BigDecimal.valueOf(70));

        productController.updateProduct(Objects.requireNonNull(newProduct.getBody()).getId(), updatedProductDTO);

        assertEquals(productController.getProductById(newProduct.getBody().getId()).getName(), updatedProductDTO.getName());
    }

    @Test
    void ensureUpdateProductThrowsErrorResponseWhenIdNotExists() {
        Long id = 999L;

        ElementNotFoundException thrown =
                assertThrows(
                        ElementNotFoundException.class,
                        () -> productController.updateProduct(id, createProductDTO("Updated Product", BigDecimal.valueOf(80))));

        assertTrue(thrown.getMessage().contains("Product with id " + id + " not found."));
    }

    @Test
    void ensureThatDeleteProductDeletesProductFromRepository() {
        ResponseEntity<Product> product = addNewTestProductToDatabase(createProductDTO("Product to Delete", BigDecimal.valueOf(90)));

        productController.deleteProduct(Objects.requireNonNull(product.getBody()).getId());

        ElementNotFoundException thrown =
                assertThrows(
                        ElementNotFoundException.class,
                        () -> productController.getProductById(product.getBody().getId()));

        assertTrue(thrown.getMessage().contains("Product with id " + product.getBody().getId() + " not found."));
    }

    @Test
    void ensureDeleteProductThrowsErrorResponseWhenIdNotExists() {
        Long id = 999L;

        ElementNotFoundException thrown =
                assertThrows(ElementNotFoundException.class, () -> productController.deleteProduct(id));

        assertTrue(thrown.getMessage().contains("Product with id " + id + " not found."));
    }

    private ProductDTO createProductDTO(String name, BigDecimal price) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName(name);
        productDTO.setPrice(price);
        return productDTO;
    }
}