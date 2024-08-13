package com.shopme.admin.product;

import com.shopme.common.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    Product findByName(String name);

    Long countById(Integer id);

    @Query("SELECT p FROM Product p WHERE p.name LIKE %:keyword% "
            + "OR p.shortDescription LIKE %:keyword% "
            + "OR p.fullDescription LIKE %:keyword% "
            + "OR p.brand.name LIKE %:keyword% "
            + "OR p.category.name LIKE %:keyword% "
    )
    Page<Product> findAll(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId "
            + "OR p.category.allParentIDs LIKE %:categoryIdMatch%")
    Page<Product> findAllInCategory(@Param("categoryId") Integer categoryId,
                                         @Param("categoryIdMatch") String categoryIdMatch,
                                         Pageable pageable);

    @Query("SELECT p FROM Product p WHERE (p.category.id = :categoryId "
            + "OR p.category.allParentIDs LIKE %:categoryIdMatch%) AND "
            + "(p.name LIKE %:keyword% "
            + "OR p.shortDescription LIKE %:keyword% "
            + "OR p.fullDescription LIKE %:keyword% "
            + "OR p.brand.name LIKE %:keyword% "
            + "OR p.category.name LIKE %:keyword%)")
    Page<Product> searchInCategory(@Param("categoryId") Integer categoryId,
                                          @Param("categoryIdMatch") String categoryIdMatch,
                                          @Param("keyword") String keyword, Pageable pageable);


    @Query("SELECT p FROM Product p WHERE p.name LIKE %?1%")
    Page<Product> searchProductsByName(String keyword, Pageable pageable);

    @Query("Update Product p SET p.averageRating = COALESCE((SELECT AVG(r.rating) FROM Review r WHERE r.product.id = ?1), 0),"
            + " p.reviewCount = (SELECT COUNT(r.id) FROM Review r WHERE r.product.id =?1) "
            + "WHERE p.id = ?1")
    @Modifying
    void updateReviewCountAndAverageRating(Integer productId);
}
