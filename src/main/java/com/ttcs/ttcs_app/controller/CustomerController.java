package com.ttcs.ttcs_app.controller;

import com.ttcs.ttcs_app.dto.request.AddToCartRequest;
import com.ttcs.ttcs_app.dto.request.OrderRequest;
import com.ttcs.ttcs_app.dto.response.*;
import com.ttcs.ttcs_app.service.AdminService;
import com.ttcs.ttcs_app.service.CustomerService;
import com.ttcs.ttcs_app.service.OrderService;
import com.ttcs.ttcs_app.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/customer")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CustomerController {
    private final AdminService adminService;
    private final CustomerService customerService;
    private final OrderService orderService;
    private final ProductService productService;

    @GetMapping("/product")
    public ResponseEntity<Page<ProductResponse>> getSimpledProductPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ){
        Page<ProductResponse> result = customerService.getSimpledProductPaged(page, size);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ProductDetailedResponse> viewProductDetailed(
            @PathVariable("productId") String productId
    ){
        ProductDetailedResponse result = productService.viewProductDetailed(productId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/add-to-cart")
    public ResponseEntity<ApiResponse> addProductToCart(@Valid @RequestBody AddToCartRequest request){
        customerService.addProductToCart(request);
        return ResponseEntity.ok(new ApiResponse("Thêm sản phẩm vào giỏ hàng thành công!"));
    }

    @PostMapping("/order")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request){
        OrderResponse orderResponse = orderService.createOrder(request);
        return ResponseEntity.ok(orderResponse);
    }

    @GetMapping("/order-list")
    public ResponseEntity<List<OrderResponse>> getOrderList(){
        List<OrderResponse> orderResponseList = orderService.getOrders();
        return ResponseEntity.ok(orderResponseList);
    }

    @GetMapping("/order")
    public ResponseEntity<OrderResponse> getOrder(@RequestParam("orderId") String orderId){
        OrderResponse o = orderService.getOrder(orderId);
        return ResponseEntity.ok(o);
    }

    @PutMapping("/cancel-order")
    public ResponseEntity<ApiResponse> cancelOrder(@RequestParam("orderId") String orderId){
        return ResponseEntity.ok(new ApiResponse(orderService.cancelOrder(orderId)));
    }
    @GetMapping("/product/search")
    public ResponseEntity<?> search(@RequestParam(name = "q") String keyword,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(productService.searchProducts(keyword, page, size));
    }
    @PostMapping("/product/{productId}/like")
    public ResponseEntity<?> toggleLike(@PathVariable String productId) {
        // Lấy thông tin user từ Security Context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(new ApiResponse("Bạn cần đăng nhập để like!"));
        }

        // Giả sử sếp lưu username (email) vào principal
        String userEmail = authentication.getName();

        boolean isLiked = productService.toggleLike(userEmail, productId);
        return ResponseEntity.ok(new ApiResponse(isLiked ? "Đã thích" : "Đã bỏ thích"));
    }
    @GetMapping("/cart")
    public ResponseEntity<CartResponse> getCart() {
        return ResponseEntity.ok(customerService.getCart());
    }
//    @PutMapping("updateInfo")
//    public ResponseEntity<>
}
