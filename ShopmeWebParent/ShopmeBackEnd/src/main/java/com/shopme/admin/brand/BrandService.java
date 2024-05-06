package com.shopme.admin.brand;

import com.shopme.common.entity.Brand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class BrandService {

    public static final int BRAND_PER_PAGE = 6;

    @Autowired
    private BrandRepository repo;

    public List<Brand> listAll() {
        return (List<Brand>) repo.findAll();
    }

    public Page<Brand> listByPage(int pageNum, String sortDir, String keyword) {
        Sort sort = Sort.by("name");

        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, BRAND_PER_PAGE, sort);

        if(keyword != null){
            return repo.findAll(keyword, pageable);
        }
        return repo.findAll(pageable);
    }

    public Brand saveBrand(Brand brand) {
        Brand save = repo.save(brand);
        return save;
    }

    public Brand get(Integer id) throws BrandNotFoundException {
        try {
           return repo.findById(id).get();
        }catch (NoSuchElementException e) {
            throw new BrandNotFoundException("Cannot find the brand with id " + id);
        }
    }

    public void delete(Integer id) throws BrandNotFoundException {
        Long countById = repo.countById(id);
        if(countById == 0 || countById == null) {
            throw new BrandNotFoundException("Cannot find the brand with id " + id);
        }
        repo.deleteById(id);
    }

    public String checkUnique(Integer id, String name) {
        boolean isCreatingNew = (id == null || id == 0);

        Brand brandByName = repo.findByName(name);

        if(isCreatingNew) {
            if(brandByName != null) return "Duplicated";
        }
        else {
            if(brandByName != null && brandByName.getId() != id)
                return "Duplicated";
        }
        return "OK";
    }
}
