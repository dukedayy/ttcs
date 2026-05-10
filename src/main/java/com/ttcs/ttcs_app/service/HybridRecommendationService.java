package com.ttcs.ttcs_app.service;

import com.ttcs.ttcs_app.dto.response.ProductResponse;
import com.ttcs.ttcs_app.engine.RecommendationEngine;
import com.ttcs.ttcs_app.model.Product;
import com.ttcs.ttcs_app.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HybridRecommendationService {
    private final UserActivityLogService userActivityLogService;
    private final RecommendationEngine engine;
    private final ProductRepository productRepository;

    public List<ProductResponse> getHomePageRecommend(String userId) {
        Map<String, Map<String, Double>> matrix = userActivityLogService.buildMatrix();
        List<String> ids = engine.getUserBasedRecommendations(userId, matrix);

        List<Product> products;
        if (ids.isEmpty()) {
            products = productRepository.findTop10ByOrderByLikeCountsDesc();
        } else {
            products = productRepository.findAllById(ids);
        }

        return products.stream()
                .map(p -> ProductResponse.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .price(p.getPrice())
                        .mainImageUrl(p.getMainImageUrl())
                        .description(p.getDescription())
                        .stockQuantity(p.getStockQuantity())
                        .weight(p.getWeight())
                        .categoryId(p.getCategory().getId())
                        .build())
                .collect(Collectors.toList());
    }

    public List<ProductResponse> getRelatedProducts(String productId) {
        Map<String, Map<String, Double>> matrix = userActivityLogService.buildMatrix();
        List<String> ids = engine.getItemBasedRecommendations(productId, matrix);

        List<Product> products = productRepository.findAllById(ids);

        return products.stream()
                .map(p -> ProductResponse.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .price(p.getPrice())
                        .mainImageUrl(p.getMainImageUrl())
                        .build())
                .collect(Collectors.toList());
    }
}