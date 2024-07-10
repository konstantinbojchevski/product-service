package com.project.product_service.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.product_service.dtos.ProductDTO;
import com.project.product_service.entities.Product;
import com.project.product_service.services.ProductService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductService productService;

    private Long productId;

    @BeforeEach
    public void setup() {
    }

    @AfterEach
    public void cleanup() {
        if (productId != null) {
            productService.deleteProduct(productId);
        }
    }

    @Test
    public void testGetAllProducts() throws Exception {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("Test Product");
        productDTO.setPrice(BigDecimal.valueOf(100));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        Product createdProduct = objectMapper.readValue(result.getResponse().getContentAsString(), Product.class);
        productId = createdProduct.getId();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Test Product"));
    }
}
