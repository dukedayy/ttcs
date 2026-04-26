package com.ttcs.ttcs_app.controller;

import com.ttcs.ttcs_app.dto.request.CategoryCreateRequestDTO;
import com.ttcs.ttcs_app.dto.request.CategoryRenameRequestDTO;
import com.ttcs.ttcs_app.dto.request.CreateProductRequest;
import com.ttcs.ttcs_app.dto.response.ApiResponse;
import com.ttcs.ttcs_app.dto.response.CategoryResponseDTO;
import com.ttcs.ttcs_app.model.Category;
import com.ttcs.ttcs_app.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

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

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponseDTO>> listCategory(){
        List<CategoryResponseDTO> list = adminService.listCategory();
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

}
