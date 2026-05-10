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

        Cart cart = cartRepository.findByCustomer(customer).orElse(null);
        if (cart == null) {
            cart = Cart.builder().customer(customer).build();
            cart = cartRepository.save(cart);
            customer.setCart(cart);
            userRepository.save(user);
        }

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

//        "e4b087c5-0118-400a-9207-06e024356252"
//        "ae797735-45fe-4705-8834-387de885bc91"
//        "c046a03c-49f8-413d-9138-15bb44d5c07c"
//        "7c8a4101-01b8-4051-877d-8e90c9b66b7e"
//        "0215663c-3834-4a2f-91f4-ce1ed0067758"
//        "b0e84bf9-8e28-48fa-a617-9de47c419704"
//        "5c2a50a0-f184-4524-af27-6d94c20d50d2"
//        "58893411-f8f7-4a94-ba0c-bd986ef3a83b"
//        "ed027f08-8dcb-4c62-b273-e9d855804ad4"
//        "4b8103fe-5aee-4557-bf40-ff40e61b0c41"
//        "4a0faf17-2471-4f70-89e2-8c6f1e6cfdcf"
//        "33eaf3af-8494-4afd-bd22-ac3b7b78efa7"
//        "26916794-0a05-4499-987d-6cbda4698744"
//        "e0d76c62-87bf-4d77-adf2-74564d5dab15"
//        "14453bfe-58e2-4c04-b24b-fa9d679afb7f"
//        "60962779-e719-401d-989a-dc8b41727d01"
//        "226e28cb-4cef-4b28-95af-a19ea16b4aa8"
//        "76da002e-8937-4739-809e-55e271835f79"
//        "f1c7519d-d9b2-4951-b91a-5636ed7a3347"
//        "b5fa2818-9a5c-4076-a8c9-c9906f2d62fe"
//        "9c686cbf-d018-41d6-b99f-b774b50dcf5a"
//        "260e1068-7d61-40b1-8f80-b2cf39b9e170"
//        "8d2a161b-cea8-4a5d-b6b0-84ae8677c54a"
//        "d428ed40-84cb-489f-862c-f4e8c1af5f65"
//        "6fd623b5-48ed-4a00-aca4-fdaad77b6c5a"
//        "d0c38ad1-269f-4268-9112-5a90e4c5023e"
//        "b3e5cbb8-e79a-4570-9f5b-305445af57b5"
//        "90dfa70d-e2bd-4648-a202-31ef9be4e55b"
//        "72f9a2e3-082f-4696-89c0-8b222ab2641b"
//        "2d1a6bbc-e484-431c-8d17-44271644025f"
//
//INSERT INTO customers (id, loyalty_point, address, cart_id, user_id) VALUES
//(5, NULL, NULL, 'e4b087c5-0118-400a-9207-06e024356252', '030d1321-4273-418c-abb9-c9e9ad7b0f24'),
//        (6, NULL, NULL, 'ae797735-45fe-4705-8834-387de885bc91', '03cfc87c-4bbe-4266-b6eb-c5ab1cf62ebe'),
//        (7, NULL, NULL, 'c046a03c-49f8-413d-9138-15bb44d5c07c', '1202c6eb-a293-4bad-ab82-d43b9f61f139'),
//        (8, NULL, NULL, '7c8a4101-01b8-4051-877d-8e90c9b66b7e', '19757e4d-11eb-45a5-9684-e30a86f0c108'),
//        (9, NULL, NULL, '0215663c-3834-4a2f-91f4-ce1ed0067758', '1c520e81-1aa1-46ae-b00a-fe32b9f8ee7a'),
//        (10, NULL, NULL, 'b0e84bf9-8e28-48fa-a617-9de47c419704', '20e3baf9-be84-4267-89ef-07ff6dad7935'),
//        (11, NULL, NULL, '5c2a50a0-f184-4524-af27-6d94c20d50d2', '21ac4b3f-2d4f-45ab-a083-8191fa63a83a'),
//        (12, NULL, NULL, '58893411-f8f7-4a94-ba0c-bd986ef3a83b', '3038127f-7ac7-40d2-8897-9840911ea640'),
//        (13, NULL, NULL, 'ed027f08-8dcb-4c62-b273-e9d855804ad4', '363dc47d-6a6c-4ce8-8b27-555236247207'),
//        (14, NULL, NULL, '4b8103fe-5aee-4557-bf40-ff40e61b0c41', '3c1b742b-d035-4f02-98c1-114949fbdb60'),
//        (15, NULL, NULL, '4a0faf17-2471-4f70-89e2-8c6f1e6cfdcf', '49f85ff8-a2cb-4f74-93b1-ac12b15ef4cc'),
//        (16, NULL, NULL, '33eaf3af-8494-4afd-bd22-ac3b7b78efa7', '4cc074df-6b1a-438d-ac31-ae49ea6549d9'),
//        (17, NULL, NULL, '26916794-0a05-4499-987d-6cbda4698744', '5d4be911-043c-4d9f-80db-bd29b3d529ba'),
//        (18, NULL, NULL, 'e0d76c62-87bf-4d77-adf2-74564d5dab15', '700651af-4868-43a4-869c-6af6c0604c84'),
//        (19, NULL, NULL, '14453bfe-58e2-4c04-b24b-fa9d679afb7f', '75feb493-5ec8-40cc-95b6-52cc5c48a3a4'),
//        (20, NULL, NULL, '60962779-e719-401d-989a-dc8b41727d01', '792c9f6b-704a-46ae-96ec-e1b492350bba'),
//        (21, NULL, NULL, '226e28cb-4cef-4b28-95af-a19ea16b4aa8', '7c611960-e3a2-4e11-883d-a63b02dbb3ca'),
//        (22, NULL, NULL, '76da002e-8937-4739-809e-55e271835f79', '846867d6-6329-45a5-9999-c11ab9ba6b5e'),
//        (23, NULL, NULL, 'f1c7519d-d9b2-4951-b91a-5636ed7a3347', '8bdce25a-bd06-4b96-b719-0f9d3b79dcee'),
//        (24, NULL, NULL, 'b5fa2818-9a5c-4076-a8c9-c9906f2d62fe', '9b405ba2-d66b-48b1-9349-8ece780e34f3'),
//        (25, NULL, NULL, '9c686cbf-d018-41d6-b99f-b774b50dcf5a', 'a6a90e6f-9654-4fea-a331-1a26fbbbd8d6'),
//        (26, NULL, NULL, '260e1068-7d61-40b1-8f80-b2cf39b9e170', 'aa026d3c-3f2a-4472-b320-626592220f86'),
//        (27, NULL, NULL, '8d2a161b-cea8-4a5d-b6b0-84ae8677c54a', 'adc846e9-51a3-4560-a8aa-bf9936150570'),
//        (28, NULL, NULL, 'd428ed40-84cb-489f-862c-f4e8c1af5f65', 'c4017762-b6a0-41e2-b42f-9a8b59b9734f'),
//        (29, NULL, NULL, '6fd623b5-48ed-4a00-aca4-fdaad77b6c5a', 'cb7d54ce-6e79-4d5c-8951-f95424bbdc19'),
//        (30, NULL, NULL, 'd0c38ad1-269f-4268-9112-5a90e4c5023e', 'd88b1685-6574-4770-bbfd-26beafeda048'),
//        (31, NULL, NULL, 'b3e5cbb8-e79a-4570-9f5b-305445af57b5', 'dad385ce-a32b-4ad0-9e81-020ff3ecbffe'),
//        (32, NULL, NULL, '90dfa70d-e2bd-4648-a202-31ef9be4e55b', 'db471e26-06cd-4d96-b256-4700c0833db0'),
//        (33, NULL, NULL, '72f9a2e3-082f-4696-89c0-8b222ab2641b', 'e40ad6b4-8959-4398-9618-69d114f9fbf7'),
//        (34, NULL, NULL, '2d1a6bbc-e484-431c-8d17-44271644025f', 'f3e10880-a9dc-47f1-a1b3-0ded5269bc9d');