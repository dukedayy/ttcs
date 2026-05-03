//package com.ttcs.ttcs_app.service;
//
//import com.ttcs.ttcs_app.dto.request.AddToCartRequest;
//import com.ttcs.ttcs_app.dto.response.CartItemResponse;
//import com.ttcs.ttcs_app.dto.response.CartResponse;
//import com.ttcs.ttcs_app.dto.response.ProductResponse;
//import com.ttcs.ttcs_app.model.*;
//import com.ttcs.ttcs_app.repository.*;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.net.URI;
//import java.util.List;
//import java.util.Optional;
//
//@Service
//@RequiredArgsConstructor
//public class CustomerService {
//    private final ProductRepository productRepository;
//    private final OrderRepository orderRepository;
//    private final CartRepository cartRepository;
//    private final UserRepository userRepository;
//    private final CartItemRepository cartItemRepository;
//
//    public Page<ProductResponse> getSimpledProductPaged(int page, int size){
//        Pageable pageable = PageRequest.of(page, size);
//        Page<Product> entityPage = productRepository.findAll(pageable);
//        return entityPage.map(product -> ProductResponse.builder()
//                .id(product.getId())
//                .name(product.getName())
//                .price(product.getPrice())
//                .description(product.getDescription())
//                .mainImageUrl(product.getMainImageUrl())
//                .imagesUrl(product.getImageUrls())
//                .stockQuantity(product.getStockQuantity())
//                .weight(product.getWeight())
//                .sizes(product.getSizes())
//                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
//                .build());
//    }
//
//    @Transactional
//    public String addProductToCart(AddToCartRequest request){
//        String email = SecurityContextHolder.getContext().getAuthentication().getName();
//
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy User"));
//
//        Customer customer = user.getCustomer();
//        Cart cart = cartRepository.findByCustomer(customer)
//                .orElseThrow(() -> new RuntimeException("Giỏ hàng không tồn tại"));
//
//        Product product = productRepository.findById(request.getProductId())
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
//
//        if (product.getStockQuantity() < request.getQuantity()) {
//            throw new RuntimeException("Sản phẩm không đủ số lượng tồn kho!");
//        }
//
//        Optional<CartItem> existingCartItem = cartItemRepository.findByCartAndProduct(cart, product);
//
//        if (existingCartItem.isPresent()) {
//            CartItem cartItem = existingCartItem.get();
//            cartItem.setAmountInCart(cartItem.getAmountInCart() + request.getQuantity());
//            cartItemRepository.save(cartItem);
//        } else {
//            CartItem newCartItem = CartItem.builder()
//                    .cart(cart)
//                    .product(product)
//                    .amountInCart(request.getQuantity())
//                    .build();
//            cartItemRepository.save(newCartItem);
//        }
//
//        return "Đã thêm vào giỏ hàng thành công!";
//    }
//
//    @Transactional
//    public CartResponse getCart() {
//        String email = SecurityContextHolder.getContext().getAuthentication().getName();
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy User"));
//
//        Customer customer = user.getCustomer();
//        Cart cart = cartRepository.findByCustomer(customer)
//                .orElseThrow(() -> new RuntimeException("Giỏ hàng không tồn tại"));
//
//        List<CartItem> cartItems = cartItemRepository.findByCart(cart);
//
//        List<CartItemResponse> itemResponses = cartItems.stream().map(item -> {
//            Float subTotal = item.getProduct().getPrice() * item.getAmountInCart();
//            return CartItemResponse.builder()
//                    .id(item.getId())
//                    .productId(item.getProduct().getId())
//                    .productName(item.getProduct().getName())
//                    .mainImageUrl(item.getProduct().getMainImageUrl())
//                    .price(item.getProduct().getPrice())
//                    .amountInCart(item.getAmountInCart())
//                    .subTotal(subTotal)
//                    .build();
//        }).toList();
//
//        Float totalPrice = itemResponses.stream()
//                .map(CartItemResponse::getSubTotal)
//                .reduce(0f, Float::sum);
//
//        return CartResponse.builder()
//                .cartId(cart.getId())
//                .items(itemResponses)
//                .totalPrice(totalPrice)
//                .build();
//    }
//
//
//}
package com.ttcs.ttcs_app.service;

import com.ttcs.ttcs_app.dto.request.AddToCartRequest;
import com.ttcs.ttcs_app.dto.response.CartItemResponse;
import com.ttcs.ttcs_app.dto.response.CartResponse;
import com.ttcs.ttcs_app.dto.response.ProductResponse;
import com.ttcs.ttcs_app.model.*;
import com.ttcs.ttcs_app.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;

    public Page<ProductResponse> getSimpledProductPaged(int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> entityPage = productRepository.findAll(pageable);
        return entityPage.map(product -> ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .mainImageUrl(product.getMainImageUrl())
                .imagesUrl(product.getImageUrls())
                .stockQuantity(product.getStockQuantity())
                .weight(product.getWeight())
                .sizes(product.getSizes())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .build());
    }

    @Transactional
    public String addProductToCart(AddToCartRequest request){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy User"));

        Customer customer = user.getCustomer();
        if (customer == null) {
            throw new RuntimeException("Tài khoản này không có hồ sơ Customer");
        }

        Cart cart = cartRepository.findByCustomer(customer)
                .orElseGet(() -> cartRepository.save(Cart.builder().customer(customer).build()));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        if (product.getStockQuantity() < request.getQuantity()) {
            throw new RuntimeException("Sản phẩm không đủ số lượng tồn kho!");
        }

        Optional<CartItem> existingCartItem = cartItemRepository.findByCartAndProduct(cart, product);

        if (existingCartItem.isPresent()) {
            CartItem cartItem = existingCartItem.get();
            cartItem.setAmountInCart(cartItem.getAmountInCart() + request.getQuantity());
            cartItemRepository.save(cartItem);
        } else {
            CartItem newCartItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .amountInCart(request.getQuantity())
                    .build();
            cartItemRepository.save(newCartItem);
        }

        return "Đã thêm vào giỏ hàng thành công!";
    }

    @Transactional
    public CartResponse getCart() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy User"));

        Customer customer = user.getCustomer();
        if (customer == null) {
            throw new RuntimeException("Tài khoản này không có hồ sơ Customer");
        }

        Cart cart = cartRepository.findByCustomer(customer)
                .orElseGet(() -> cartRepository.save(Cart.builder().customer(customer).build()));

        List<CartItem> cartItems = cartItemRepository.findByCart(cart);

        List<CartItemResponse> itemResponses = cartItems.stream().map(item -> {
            Float subTotal = item.getProduct().getPrice() * item.getAmountInCart();
            return CartItemResponse.builder()
                    .id(item.getId())
                    .productId(item.getProduct().getId())
                    .productName(item.getProduct().getName())
                    .mainImageUrl(item.getProduct().getMainImageUrl())
                    .price(item.getProduct().getPrice())
                    .amountInCart(item.getAmountInCart())
                    .subTotal(subTotal)
                    .build();
        }).toList();

        Float totalPrice = itemResponses.stream()
                .map(CartItemResponse::getSubTotal)
                .reduce(0f, Float::sum);

        return CartResponse.builder()
                .cartId(cart.getId())
                .items(itemResponses)
                .totalPrice(totalPrice)
                .build();
    }

}