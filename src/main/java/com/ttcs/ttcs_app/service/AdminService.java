package com.ttcs.ttcs_app.service;

import com.ttcs.ttcs_app.dto.request.CategoryCreateRequestDTO;
import com.ttcs.ttcs_app.dto.request.CategoryRenameRequestDTO;
import com.ttcs.ttcs_app.dto.request.CreateProductRequest;
import com.ttcs.ttcs_app.dto.response.CategoryResponseDTO;
import com.ttcs.ttcs_app.exception.DataConflictException;
import com.ttcs.ttcs_app.exception.ResourceNotFoundException;
import com.ttcs.ttcs_app.model.Category;
import com.ttcs.ttcs_app.model.Product;
import com.ttcs.ttcs_app.model.ProductStatus;
import com.ttcs.ttcs_app.repository.CategoryRepository;
import com.ttcs.ttcs_app.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    @Transactional
    public Category createCategory(CategoryCreateRequestDTO request){
        Category newCategory = Category.builder()
                .name(request.getName())
                .build();
        return categoryRepository.save(newCategory);
    }

//    @Transactional
//    public void renameCategory(CategoryRenameRequestDTO request){
//
//        if(categoryRepository.existsByName(request.getNewName())) throw new DataConflictException("Dữ liệu bị trùng");
//        int updatedRows = categoryRepository.renameCategory(request.getNewName(), request.getId());
//        if(updatedRows == 0){
//            throw new ResourceNotFoundException("Id không hợp lệ");
//        }
//    }
@Transactional
public void renameCategory(String id, String newName) {

    // 2. Bắn lệnh Update siêu tốc
    int updatedRows = categoryRepository.renameCategory(newName, id);

    // 3. Nếu không có dòng nào suy chuyển -> ID gửi lên là đồ giả!
    if(updatedRows == 0) {
        throw new ResourceNotFoundException("Không tìm thấy danh mục có ID: " + id);
    }
}

    private CategoryResponseDTO convertToCategoryResponseDTO(Category category){
        CategoryResponseDTO cr = new CategoryResponseDTO(category.getId(), category.getName());
        return cr;
    }

    public List<CategoryResponseDTO> listCategory(){
        List<Category> list = categoryRepository.findAllCategory();
        List<CategoryResponseDTO> res = new ArrayList<>();
        for(Category x:list){
            res.add(convertToCategoryResponseDTO(x));
        }
        return res;
    }

    public void addProduct(CreateProductRequest req){
        Category category = categoryRepository.findById(req.getCategoryId()).orElseThrow(()-> new ResourceNotFoundException("Danh mục không tồn tại"));
        Product newProduct = Product.builder()
                .name(req.getName())
                .price(req.getPrice())
                .costPrice(req.getCostPrice()).description(req.getDescription()).stockQuantity(req.getStockQuantity()).weight(req.getWeight()).sizes(req.getSizes()).category(category).taxRate(req.getTaxRate()).status(ProductStatus.ACTIVE).mainImageUrl(req.getMainImageUrl()).build();
        productRepository.save(newProduct);
    }

}
