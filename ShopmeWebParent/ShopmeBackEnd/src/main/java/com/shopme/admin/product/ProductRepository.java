package com.shopme.admin.product;

import com.shopme.common.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    Product findByName(String name);

    Long countById(Integer id);
}
