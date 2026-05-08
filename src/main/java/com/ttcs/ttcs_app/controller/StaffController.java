package com.ttcs.ttcs_app.controller;

import com.ttcs.ttcs_app.dto.request.UpdateOrderStatusRequest;
import com.ttcs.ttcs_app.dto.request.UpdateStockRequest;
import com.ttcs.ttcs_app.service.AdminService;
import com.ttcs.ttcs_app.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/staff")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StaffController {

    private final StaffService staffService;
    private final AdminService adminService; // Tái sử dụng API lấy danh sách sản phẩm

    @GetMapping("/orders")
    public ResponseEntity<?> getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(staffService.getAllOrdersPaged(page, size));
    }

    @PutMapping("/orders/{id}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable String id,
            @RequestBody UpdateOrderStatusRequest request) {
        staffService.updateOrderStatus(id, request.getStatus());
        return ResponseEntity.ok().body("{\"message\": \"Cập nhật trạng thái thành công\"}");
    }

    // Tái sử dụng hàm lấy danh sách sản phẩm của Admin/Customer
    @GetMapping("/products")
    public ResponseEntity<?> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(adminService.getDetailedProductPaged(page, size));
    }

    @PutMapping("/products/{id}/stock")
    public ResponseEntity<?> updateStock(
            @PathVariable String id,
            @RequestBody UpdateStockRequest request) {
        staffService.updateProductStock(id, request.getStockQuantity());
        return ResponseEntity.ok().body("{\"message\": \"Cập nhật tồn kho thành công\"}");
    }
}