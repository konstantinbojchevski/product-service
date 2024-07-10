package com.project.product_service.mappers;

import com.project.product_service.dtos.ProductDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.project.product_service.entities.Product;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    ProductDTO productToProductDTO(Product product);

    Product productDTOToProduct(ProductDTO productDTO);

    Product mapToProduct(ProductDTO productDTO, @MappingTarget Product product);

}

