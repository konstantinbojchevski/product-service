package com.project.product_service.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import com.project.product_service.dtos.ProductDTO;
import com.project.product_service.entities.Product;
import com.project.product_service.repositories.ProductRepository;
import com.project.product_service.mappers.ProductMapper;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    private ProductDTO newProductDTO;
    private Product existingProduct;

    @BeforeEach
    void setUp() {
        newProductDTO = new ProductDTO();
        newProductDTO.setName("Test Product");
        newProductDTO.setDescription("Test Description");
        newProductDTO.setPrice(BigDecimal.valueOf(100.0));

        existingProduct = new Product();
        existingProduct.setName("Existing Product");
        existingProduct.setDescription("Existing Description");
        existingProduct.setPrice(BigDecimal.valueOf(200.0));
    }

    @Test
    void testGetAllProducts() {
        when(productRepository.findAll()).thenReturn(Collections.singletonList(existingProduct));

        when(productMapper.productToProductDTO(existingProduct)).thenReturn(newProductDTO);

        List<ProductDTO> products = productService.getAllProducts();

        assertEquals(1, products.size());
        assertEquals(newProductDTO.getName(), products.get(0).getName());
        assertEquals(newProductDTO.getDescription(), products.get(0).getDescription());
        assertEquals(newProductDTO.getPrice(), products.get(0).getPrice());
    }

    @Test
    void testFindById() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productMapper.productToProductDTO(existingProduct)).thenReturn(newProductDTO);

        ProductDTO productDTO = productService.findById(1L);

        assertEquals(newProductDTO.getName(), productDTO.getName());
        assertEquals(newProductDTO.getDescription(), productDTO.getDescription());
        assertEquals(newProductDTO.getPrice(), productDTO.getPrice());
    }

    @Test
    void testFindById_ProductNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ElementNotFoundException.class, () -> {
            productService.findById(1L);
        });
    }

    @Test
    void testAddNewProduct() {
        when(productMapper.productDTOToProduct(newProductDTO)).thenReturn(existingProduct);

        verify(productRepository, times(1)).save(existingProduct);

        assertEquals(existingProduct.getName(), newProductDTO.getName());
        assertEquals(existingProduct.getDescription(), newProductDTO.getDescription());
        assertEquals(existingProduct.getPrice(), newProductDTO.getPrice());
    }

    @Test
    void testDeleteProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteProduct_ProductNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> {
            productService.deleteProduct(1L);
        });
    }

    @Test
    void testUpdateProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));

        when(productMapper.mapToProduct(any(ProductDTO.class), eq(existingProduct)))
                .thenAnswer(invocation -> {
                    ProductDTO updatedProductDTO = invocation.getArgument(0);
                    existingProduct.setName(updatedProductDTO.getName());
                    existingProduct.setDescription(updatedProductDTO.getDescription());
                    existingProduct.setPrice(updatedProductDTO.getPrice());
                    return null;
                });

        ProductDTO updatedProductDTO = new ProductDTO();
        updatedProductDTO.setName("Updated Product");
        updatedProductDTO.setDescription("Updated Description");
        updatedProductDTO.setPrice(BigDecimal.valueOf(300.0));
        ProductDTO returnedProductDTO = productService.updateProduct(1L, updatedProductDTO);

        verify(productRepository, times(1)).save(existingProduct);

        assertEquals(updatedProductDTO.getName(), returnedProductDTO.getName());
        assertEquals(updatedProductDTO.getDescription(), returnedProductDTO.getDescription());
        assertEquals(updatedProductDTO.getPrice(), returnedProductDTO.getPrice());
    }

    @Test
    void testUpdateProduct_ProductNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> {
            productService.updateProduct(1L, newProductDTO);
        });
    }

    @Test
    void testValidate_InvalidProductName() {
        newProductDTO.setName("");

        assertThrows(ResponseStatusException.class, () -> {
            productService.validate(newProductDTO);
        });
    }

    @Test
    void testValidate_InvalidProductDescription() {
        newProductDTO.setDescription(null);

        assertThrows(ResponseStatusException.class, () -> {
            productService.validate(newProductDTO);
        });
    }

    @Test
    void testValidate_InvalidProductPrice() {
        newProductDTO.setPrice(BigDecimal.ZERO);

        assertThrows(ResponseStatusException.class, () -> {
            productService.validate(newProductDTO);
        });
    }
}