    package com.ttcs.ttcs_app.service;

    import com.ttcs.ttcs_app.dto.request.*;
    import com.ttcs.ttcs_app.dto.response.CategoryResponse;
    import com.ttcs.ttcs_app.dto.response.ProductResponse;
    import com.ttcs.ttcs_app.exception.ResourceNotFoundException;
    import com.ttcs.ttcs_app.model.*;
    import com.ttcs.ttcs_app.repository.CategoryRepository;
    import com.ttcs.ttcs_app.repository.FlashSaleRepository;
    import com.ttcs.ttcs_app.repository.ProductRepository;
    import com.ttcs.ttcs_app.repository.VoucherRepository;
    import lombok.RequiredArgsConstructor;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.PageRequest;
    import org.springframework.data.domain.Pageable;
    import org.springframework.data.domain.Sort;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;

    import java.util.ArrayList;
    import java.util.List;

    @Service
    @RequiredArgsConstructor
    public class AdminService {
        private final CategoryRepository categoryRepository;
        private final ProductRepository productRepository;
        private final FlashSaleRepository flashSaleRepository;
        private final VoucherRepository voucherRepository;
        @Transactional
        public Category createCategory(CategoryCreateRequestDTO request){
            Category newCategory = Category.builder()
                    .name(request.getName())
                    .build();
            return categoryRepository.save(newCategory);
        }

    @Transactional
    public void renameCategory(String id, String newName) {

        // 2. Bắn lệnh Update siêu tốc
        int updatedRows = categoryRepository.renameCategory(newName, id);

        // 3. Nếu không có dòng nào suy chuyển -> ID gửi lên là đồ giả!
        if(updatedRows == 0) {
            throw new ResourceNotFoundException("Không tìm thấy danh mục có ID: " + id);
        }
    }

        private CategoryResponse convertToCategoryResponseDTO(Category category){
            CategoryResponse cr = new CategoryResponse(category.getId(), category.getName());
            return cr;
        }

        public List<CategoryResponse> listCategory(){
            List<Category> list = categoryRepository.findAllCategory();
            List<CategoryResponse> res = new ArrayList<>();
            for(Category x:list){
                res.add(convertToCategoryResponseDTO(x));
            }
            return res;
        }

//        public Page<ProductResponse> getDetailedProductPaged(int page, int size){
//            Pageable pageable = PageRequest.of(page, size);
//            Page<Product> entityPage = productRepository.findAll(pageable);
//            return entityPage.map(product -> ProductResponse.builder()
//                    .id(product.getId())
//                    .name(product.getName())
//                    .price(product.getPrice())
//                    .weight(product.getWeight())
//                    .description(product.getDescription())
//                    .stockQuantity(product.getStockQuantity())
//                    .categoryId(product.getCategory().getId())
//                    .mainImageUrl(product.getMainImageUrl())
//                    .build());
//        }
public Page<ProductResponse> getDetailedProductPaged(Pageable pageable) {
    // Sử dụng trực tiếp pageable đã được truyền từ Controller (đã bao gồm sort)
    Page<Product> entityPage = productRepository.findAll(pageable);

    return entityPage.map(product -> ProductResponse.builder()
            .id(product.getId())
            .name(product.getName())
            .price(product.getPrice())
            .weight(product.getWeight())
            .description(product.getDescription())
            .stockQuantity(product.getStockQuantity())
            // Kiểm tra null cho category để tránh lỗi sập API nếu dữ liệu thiếu
            .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
            .mainImageUrl(product.getMainImageUrl())
            .build());
}
        // chưa đc dùng
        public Page<ProductResponse> getSimpledProductPaged(int page, int size){
            Pageable pageable = PageRequest.of(page, size);
            Page<Product> entityPage = productRepository.findAll(pageable);
            return entityPage.map(product -> ProductResponse.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .price(product.getPrice())
                    .build());
        }

        public void addProduct(CreateProductRequest req){
            Category category = categoryRepository.findById(req.getCategoryId()).orElseThrow(()-> new ResourceNotFoundException("Danh mục không tồn tại"));
            Product newProduct = Product.builder()
                    .name(req.getName())
                    .price(req.getPrice())
                    .costPrice(req.getCostPrice()).description(req.getDescription()).stockQuantity(req.getStockQuantity()).weight(req.getWeight()).sizes(req.getSizes()).taxRate(req.getTaxRate()).status(ProductStatus.ACTIVE).mainImageUrl(req.getMainImageUrl()).build();
            productRepository.save(newProduct);
        }

        @Transactional
        public void updateProduct(String id, UpdateProductRequest req) {
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

            Category category = categoryRepository.findById(req.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));

            product.setName(req.getName());
            product.setPrice(req.getPrice());
            product.setCostPrice(req.getCostPrice() != null ? req.getCostPrice() : 0f);
            product.setStockQuantity(req.getStockQuantity() != null ? req.getStockQuantity() : 0);
            product.setWeight(req.getWeight() != null ? req.getWeight() : 0f);
            product.setSizes(req.getSizes());
            product.setTaxRate(req.getTaxRate() != null ? req.getTaxRate() : 0f);
            product.setMainImageUrl(req.getMainImageUrl());
            product.setDescription(req.getDescription());
            product.setCategory(category);

            productRepository.save(product);
        }

        public void addFlashSale(CreateFlashSaleRequest req){
            FlashSale fl = flashSaleRepository.save(FlashSale.builder().startTime(req.getStartTime()).endTime(req.getEndTime()).status(req.getStatus()).build());
        }

        public void addVoucher(CreateVoucherRequest req){
            voucherRepository.save(Voucher.builder().voucherCode(req.getVoucherCode()).usePercentage(req.getUsePercentage()).discountPercentage(req.getDiscountPercentage()).discountValue(req.getDiscountValue()).minOrderValue(req.getMinOrderValue()).startDate(req.getStartDate()).endDate(req.getEndDate()).usageLimit(req.getUsageLimit()).usedCount(req.getUsedCount()).limitPerUser(req.getLimitPerUser()).status(req.getStatus()).build());
        }
        @Transactional
        public void addMultipleProducts(List<CreateProductRequest> requests) {
            List<Product> products = new ArrayList<>();

            for (CreateProductRequest req : requests) {
                Category category = categoryRepository.findById(req.getCategoryId())
                        .orElseThrow(() -> new ResourceNotFoundException("Danh mục không tồn tại: " + req.getCategoryId()));

                Product newProduct = Product.builder()
                        .name(req.getName())
                        .price(req.getPrice())
                        .costPrice(req.getCostPrice())
                        .description(req.getDescription())
                        .stockQuantity(req.getStockQuantity())
                        .weight(req.getWeight())
                        .sizes(req.getSizes())
                        .category(category)
                        .taxRate(req.getTaxRate())
                        .status(ProductStatus.ACTIVE)
                        .mainImageUrl(req.getMainImageUrl())
                        .build();

                products.add(newProduct);
            }

            productRepository.saveAll(products);
        }


    }

