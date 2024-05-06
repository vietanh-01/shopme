package com.shopme.admin.brand;

import com.shopme.common.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Integer> {

    @Query("SELECT b FROM Brand b WHERE b.name LIKE %?1%")
    public Page<Brand> findAll(String keyword, Pageable pageable);

    Long countById(Integer id);

    Brand findByName(String name);

    @Query("SELECT NEW Brand(b.id, b.name) FROM Brand b ORDER BY b.name ASC")
    List<Brand> findAll();

}
