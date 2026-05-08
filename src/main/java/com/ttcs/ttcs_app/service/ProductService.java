package com.ttcs.ttcs_app.service;

import com.ttcs.ttcs_app.dto.request.ViewProductDetailedRequest;
import com.ttcs.ttcs_app.dto.response.ProductDetailedResponse;
import com.ttcs.ttcs_app.exception.ResourceNotFoundException;
import com.ttcs.ttcs_app.model.Product;
import com.ttcs.ttcs_app.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;

    public ProductDetailedResponse viewProductDetailed(String productId){
        Product product = productRepository.findById(productId).orElseThrow(()->new ResourceNotFoundException("Không tìm thấy sản phẩm"));
        return ProductDetailedResponse.builder()
                .id(product.getId())
                .views(product.getViews())
                .averageScore(product.getAverageScore())
                .categoryId(product.getCategory().getId())
                .name(product.getName())
                .price(product.getPrice())
                .sizes(product.getSizes())
                .likeCounts(product.getLikeCounts())
                .mainImageUrl(product.getMainImageUrl())
                .description(product.getDescription())
                .weight(product.getWeight())
                .stockQuantity(product.getStockQuantity())
                .isOnFlashSale(product.isOnFlashSale())
                .reviewCount(product.getReviewCount())
//                .flashSalePrice(product.get)
                .build();
    }

}
