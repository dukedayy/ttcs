package com.ttcs.ttcs_app.service;

import com.ttcs.ttcs_app.dto.request.ViewProductDetailedRequest;
import com.ttcs.ttcs_app.dto.response.ProductDetailedResponse;
import com.ttcs.ttcs_app.dto.response.ProductResponse;
import com.ttcs.ttcs_app.exception.ResourceNotFoundException;
import com.ttcs.ttcs_app.model.Product;
import com.ttcs.ttcs_app.model.ProductLike;
import com.ttcs.ttcs_app.model.User;
import com.ttcs.ttcs_app.repository.ProductLikeRepository;
import com.ttcs.ttcs_app.repository.ProductRepository;
import com.ttcs.ttcs_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductLikeRepository productLikeRepository;
    private final UserRepository userRepository;
    private final UserActivityLogService userActivityLogService;
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


    @Transactional
    public boolean toggleLike(String email, String productId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User không tồn tại"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm không tồn tại"));

        Optional<ProductLike> existingLike = productLikeRepository.findByUserAndProduct(user, product);

        if (existingLike.isPresent()) {
            // 1. Xử lý Bỏ Like
            productLikeRepository.delete(existingLike.get());

            if (product.getLikeCounts() != null && product.getLikeCounts() > 0) {
                product.setLikeCounts(product.getLikeCounts() - 1);
            }

            // Ghi log UNLIKE nếu sếp có type này, hoặc bỏ qua tùy logic RS
            userActivityLogService.saveLog(email, productId, "UNLIKE", 0f);

            return false;
        } else {
            // 2. Xử lý Like
            ProductLike newLike = ProductLike.builder()
                    .user(user)
                    .product(product)
                    .build();
            productLikeRepository.save(newLike);

            product.setLikeCounts((product.getLikeCounts() == null ? 0 : product.getLikeCounts()) + 1);

            // 3. BẮN LOG LIKE (Hành vi vàng cho Recommendation System)
            // Vì saveLog có @Async nên nó sẽ chạy ngầm, không làm chậm request của khách
            userActivityLogService.saveLog(email, productId, "LIKE", 0f);

            return true;
        }
    }
//    @Transactional
//    public boolean toggleLike(String email, String productId) {
//        // 1. Tìm User và Product
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new ResourceNotFoundException("User không tồn tại"));
//        Product product = productRepository.findById(productId)
//                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm không tồn tại"));
//
//        Optional<ProductLike> existingLike = productLikeRepository.findByUserAndProduct(user, product);
//
//        if (existingLike.isPresent()) {
//            productLikeRepository.delete(existingLike.get());
//
//            // Cập nhật lại likeCounts trong bảng Product (giảm 1)
//            if (product.getLikeCounts() != null && product.getLikeCounts() > 0) {
//                product.setLikeCounts(product.getLikeCounts() - 1);
//            }
//            return false;
//        } else {
//            // Nếu chưa like -> Tạo mới
//            ProductLike newLike = ProductLike.builder()
//                    .user(user)
//                    .product(product)
//                    .build();
//            productLikeRepository.save(newLike);
//
//            product.setLikeCounts((product.getLikeCounts() == null ? 0 : product.getLikeCounts()) + 1);
//
//
//             userActivityLogService.saveLog(user.getId(), productId, "LIKE");
//
//            return true;
//        }
//    }

    public Page<ProductResponse> searchProducts(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        if (keyword == null || keyword.trim().isEmpty()) {
            return productRepository.findAll(pageable).map(this::convertToResponse);
        }

        Page<Product> productPage = productRepository.searchByNameOrDescription(keyword.trim(), pageable);
        return productPage.map(this::convertToResponse);
    }
    private ProductResponse convertToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .mainImageUrl(product.getMainImageUrl())
                .stockQuantity(product.getStockQuantity())
                .build();
    }

}
