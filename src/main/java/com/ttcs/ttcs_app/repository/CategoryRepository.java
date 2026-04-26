package com.ttcs.ttcs_app.repository;

import com.ttcs.ttcs_app.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, String> {
    boolean existsByName(String name);

    @Modifying
    @Query("UPDATE Category c SET c.name = :name WHERE c.id = :id")
    int renameCategory(@Param("name") String name,
                        @Param("id") String id
    );

    @Query("SELECT c FROM Category c")
    List<Category> findAllCategory();

}
