package com.shopme.admin.product;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.common.entity.product.Product;
import com.shopme.common.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ProductService {

    public static final int PRODUCTS_PER_PAGE = 5;

    @Autowired
    private ProductRepository repo;

    public List<Product> listAll() {
        return (List<Product>) repo.findAll();
    }

    public Page<Product> listByPage(int pageNum, String sortField, String sortDir,
                                    String keyword, Integer categoryId) {
        Sort sort = Sort.by(sortField);

        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, PRODUCTS_PER_PAGE, sort);

        if (keyword != null && !keyword.isEmpty()) {
            if (categoryId != null && categoryId > 0) {
                String categoryIdMatch = "-" + String.valueOf(categoryId) + "-";
                return repo.searchInCategory(categoryId, categoryIdMatch, keyword, pageable);
            }

            return repo.findAll(keyword, pageable);
        }

        if (categoryId != null && categoryId > 0) {
            String categoryIdMatch = "-" + String.valueOf(categoryId) + "-";
            return repo.findAllInCategory(categoryId, categoryIdMatch, pageable);
        }

        return repo.findAll(pageable);
    }

    public void saveProductPrice(Product productInForm) {
        Product productInDB = repo.findById(productInForm.getId()).get();
        productInDB.setCost(productInForm.getCost());
        productInDB.setPrice(productInForm.getPrice());
        productInDB.setDiscountPercent(productInForm.getDiscountPercent());

        repo.save(productInDB);
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

    public Product get (Integer id) throws ProductNotFoundException {
        try {
            return repo.findById(id).get();
        }
        catch (NoSuchElementException e) {
            throw new ProductNotFoundException("Could not find any product with ID " + id);
        }
    }

    public void searchProducts(int pageNum, PagingAndSortingHelper helper) {
        Pageable pageable = helper.createPageable(PRODUCTS_PER_PAGE, pageNum);
        String keyword = helper.getKeyword();
        Page<Product> page = repo.searchProductsByName(keyword, pageable);
        helper.updateModelAttributes(pageNum, page);
    }
}
