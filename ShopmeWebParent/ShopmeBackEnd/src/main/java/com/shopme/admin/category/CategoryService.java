package com.shopme.admin.category;

import com.shopme.common.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository repository;

    public List<Category> getAllCategories() {
        List<Category> rootCategories = repository.findRootCategories();
        return listHierarchicalCategories(rootCategories);
    }

    private List<Category> listHierarchicalCategories(List<Category> rootCategories) {
        List<Category> hirerachicalCategories = new ArrayList<>();

        for (Category rootCategory : rootCategories) {
            hirerachicalCategories.add(Category.CategoryCopyFull(rootCategory));

            Set<Category> children = rootCategory.getChildren();
            for (Category subCategory : children) {
                String name = "--" + subCategory.getName();
                hirerachicalCategories.add(Category.CategoryCopyFull(subCategory, name));

                listSubHierarchicalCategories(hirerachicalCategories, subCategory, 1);
            }
        }

        return hirerachicalCategories;
    }

    private void listSubHierarchicalCategories(List<Category> hirerachicalCategories,
                    Category parent, int subLevel) {
        Set<Category> children = parent.getChildren();
        int newSubLevel = subLevel + 1;
        for (Category subCategory : children) {
            String name = "";
            for(int i = 0; i < newSubLevel; i++) {
                name += "--";
            }
            name += subCategory.getName();
            hirerachicalCategories.add(Category.CategoryCopyFull(subCategory, name));

            listSubHierarchicalCategories(hirerachicalCategories, subCategory, newSubLevel);
        }
    }

    public Category saveCategory(Category category) {
        Category save = repository.save(category);
        return save;
    }

    public List<Category> listCategoryUsedInform() {
        List<Category> categoriesUsedInform = new ArrayList<>();

        Iterable<Category> categoriesInDB= repository.findAll();

        for (Category category : categoriesInDB) {
            if (category.getParent() == null) {
                categoriesUsedInform.add(Category.copyIdAndName(category));

                Set<Category> children = category.getChildren();

                for (Category subCategory : children) {
                    String name = "--" + subCategory.getName();
                    categoriesUsedInform.add(Category.copyIdAndName(subCategory.getId(), name));
                    listChildren(categoriesUsedInform, subCategory, 1);
                }
            }
        }

        return categoriesUsedInform;
    }

    private void listChildren(List<Category> categoriesUsedInForm,Category parent, int subLevel) {
        int newSubLevel = subLevel + 1;
        Set<Category> children = parent.getChildren();

        for (Category subCategory : children) {
            String name = "";
            for (int i = 0; i < newSubLevel; i++) {
                name += "--";
            }
            name += subCategory.getName();
            categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));

            listChildren(categoriesUsedInForm, subCategory, newSubLevel);
        }
    }

    public Category get(Integer id) throws CategoryNotFoundException {
        try {
            return repository.findById(id).get();
        }
        catch (NoSuchElementException message) {
            throw new CategoryNotFoundException("Could not found Category with ID:" + id);
        }
    }

}
