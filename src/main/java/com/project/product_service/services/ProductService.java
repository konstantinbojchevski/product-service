package com.project.product_service.services;

import com.project.product_service.entities.Product;
import com.project.product_service.mappers.ProductMapper;
import io.micrometer.core.instrument.util.StringUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.project.product_service.dtos.ProductDTO;
import com.project.product_service.repositories.ProductRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(productMapper::productToProductDTO)
                .collect(Collectors.toList());
    }

    public ProductDTO findById(Long uuid) {
        return productMapper.productToProductDTO(
                productRepository
                        .findById(uuid)
                        .orElseThrow(() ->
                                new ElementNotFoundException("Product with uuid " + uuid + " not found")));
    }

    public List<ProductDTO> getProductsByNameContaining(String name) {
        List<ProductDTO> products = productRepository.findByNameContaining(name).stream()
                .map(productMapper::productToProductDTO)
                .collect(Collectors.toList());

        if (products.isEmpty()) {
            throw new ElementNotFoundException("Product with name containing '" + name + "' not found");
        }

        return products;
    }

    public Product addNewProduct(ProductDTO productDTO) {
        validate(productDTO);
        Product newProduct = productMapper.productDTOToProduct(productDTO);
        //newProduct.setId(UUID.randomUUID());
        productRepository.save(newProduct);
        return newProduct;
    }

    public void deleteProduct(Long id) {
        Product product =
                productRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "Product with id " + id + " does not exist."));
        productRepository.deleteById(id);
    }

    @Transactional
    public ProductDTO updateProduct(Long id, ProductDTO newProductDTO) {
        validate(newProductDTO);
        Product existingProduct =
                productRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "Product with id " + id + " does not exist."));

        productMapper.mapToProduct(newProductDTO, existingProduct);
        productRepository.save(existingProduct);

        return productMapper.productToProductDTO(existingProduct);
    }

    public void validate(ProductDTO productDTO) {
        if (StringUtils.isBlank(productDTO.getName()) || productDTO.getName().length() > 255) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid product name");
        }
        if (StringUtils.isBlank(productDTO.getDescription())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid product description");
        }
        if (productDTO.getPrice() == null || productDTO.getPrice().compareTo(BigDecimal.valueOf(0.0)) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid product price");
        }
    }
}

