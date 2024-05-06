package com.shopme.admin.product;

import com.shopme.admin.brand.BrandNotFoundException;
import com.shopme.common.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repo;

    public List<Product> listAll() {
        return (List<Product>) repo.findAll();
    }

    public Product save(Product product) {
        if (product.getId() == null) {
            product.setCreatedTime(new Date());
        }

        if (product.getAlias() == null || product.getAlias().isEmpty()) {
            String defaultAlias = product.getName().replaceAll(" ", "-");
            product.setAlias(defaultAlias);
        } else {
            product.setAlias(product.getAlias().replaceAll(" ", "-"));
        }

        product.setUpdatedTime(new Date());

        return repo.save(product);
    }

    public String checkUnique(Integer id, String name) {
        boolean isCreating = (id == null || id == 0);

        Product productByName = repo.findByName(name);

        if(isCreating) {
            if(productByName != null) return "Duplicated";
        }
        else{
            if (productByName != null && productByName.getId() != id)
                return "Duplicated";
        }

        return "OK";
    }

    public void delete(Integer id) throws ProductNotFoundException {
        Long countById = repo.countById(id);
        if(countById == 0 || countById == null) {
            throw new ProductNotFoundException("Cannot find the product with id " + id);
        }
        repo.deleteById(id);
    }
}
