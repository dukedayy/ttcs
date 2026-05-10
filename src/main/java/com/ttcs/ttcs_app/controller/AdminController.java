package com.ttcs.ttcs_app.controller;

import com.ttcs.ttcs_app.dto.request.*;
import com.ttcs.ttcs_app.dto.response.ApiResponse;
import com.ttcs.ttcs_app.dto.response.CategoryResponse;
import com.ttcs.ttcs_app.dto.response.ProductResponse;
import com.ttcs.ttcs_app.service.AdminService;
import com.ttcs.ttcs_app.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminController {
    private final AdminService adminService;
    private final AuthService authService;


    @PostMapping("/products")
    public ResponseEntity<ApiResponse> createProduct(@Valid @RequestBody CreateProductRequest req){
        adminService.addProduct(req);
        return ResponseEntity.ok(new ApiResponse("Đã thêm sản phẩm thành công"));
    }

    @PostMapping("/categories")
    public ResponseEntity<ApiResponse> createCategory(@Valid @RequestBody CategoryCreateRequestDTO req){
        adminService.createCategory(req);
        return ResponseEntity.ok(new ApiResponse("Đã tạo danh mục thành công"));
    }
    @PutMapping("/products/{id}")
    public ResponseEntity<ApiResponse> updateProduct(
            @PathVariable("id") String id,
            @Valid @RequestBody UpdateProductRequest req) {
        adminService.updateProduct(id, req);
        return ResponseEntity.ok(new ApiResponse("Cập nhật sản phẩm thành công!"));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponse>> listCategory(){
        List<CategoryResponse> list = adminService.listCategory();
        return ResponseEntity.ok(list);
    }
    // Bắt ID từ URL, bắt Tên mới từ Body
    @PutMapping("/{id}/rename") // Có thể dùng PUT hoặc PATCH
    public ResponseEntity<Map<String, String>> renameCategory(
            @PathVariable("id") String id,
            @Valid @RequestBody CategoryRenameRequestDTO req) {

        // Ném 2 mảnh ghép xuống cho Service xử lý
        adminService.renameCategory(id, req.getNewName());

        // Trả về câu thông báo dạng JSON cho Frontend dễ hiển thị
        Map<String, String> response = new HashMap<>();
        response.put("message", "Đổi tên danh mục thành công!");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/products/batch")
    public ResponseEntity<ApiResponse> createMultipleProducts(@Valid @RequestBody List<CreateProductRequest> reqList){
        adminService.addMultipleProducts(reqList);
        return ResponseEntity.ok(new ApiResponse("Đã thêm danh sách sản phẩm thành công"));
    }
    @PostMapping("/fs")
    public ResponseEntity<ApiResponse> createFlashSale(@Valid @RequestBody CreateFlashSaleRequest req){
        adminService.addFlashSale(req);
        return ResponseEntity.ok(new ApiResponse("đã tạo đợt Flash Sale mới!"));
    }

    @PostMapping("voucher")
    public ResponseEntity<ApiResponse> createVoucher(@Valid @RequestBody CreateVoucherRequest req){
        adminService.addVoucher(req);
        return ResponseEntity.ok(new ApiResponse("Đã tạo voucher mới!"));
    }

//    @GetMapping("/product")
//    public ResponseEntity<Page<ProductResponse>> getDetailedProductPaged(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size
//    ){
//        Page<ProductResponse> result = adminService.getDetailedProductPaged(page, size);
//        return ResponseEntity.ok(result);
//    }
@GetMapping("/product")
public ResponseEntity<Page<ProductResponse>> getDetailedProductPaged(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
){
    Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").ascending());

    Page<ProductResponse> result = adminService.getDetailedProductPaged(pageable);

    return ResponseEntity.ok(result);
}

    @PostMapping("/users")
    public ResponseEntity<ApiResponse> createAccountForStaff(@Valid @RequestBody CreateStaffAccountRequest request){
        authService.createAccountForStaff(request);
        return ResponseEntity.ok(new ApiResponse("Tạo tài khoản thành công"));
    }



}
