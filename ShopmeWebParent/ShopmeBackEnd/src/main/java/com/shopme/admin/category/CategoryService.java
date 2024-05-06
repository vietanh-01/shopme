package com.shopme.admin.category;

import com.shopme.common.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CategoryService {

    public static final int ROOT_CATEGORIES_PER_PAGE = 4;
    @Autowired
    private CategoryRepository repository;

    public List<Category> listAll() {
        return repository.findAll(Sort.by("name").ascending());
    }

    public List<Category> listByPage(CategoryPageInfo pageInfo, int pageNum, String sortDir, String keyword) {
        Sort sort = Sort.by("name");

        if(sortDir.equals("asc"))
            sort = sort.ascending();
        else sort = sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, ROOT_CATEGORIES_PER_PAGE, sort);

        Page<Category> pageCategories = null;
        if (keyword != null && !keyword.isEmpty())
            pageCategories = repository.search(keyword, pageable);
        else
            pageCategories = repository.findRootCategories(pageable);


        List<Category> rootCategories = pageCategories.getContent();

        pageInfo.setTotalPages(pageCategories.getTotalPages());
        pageInfo.setTotalElements(pageCategories.getTotalElements());

        if (keyword != null && !keyword.isEmpty()) {
            List<Category> searchResult = pageCategories.getContent();
            for(Category category : searchResult) {
                category.setHasChildren(category.getChildren().size() > 0);
            }
            return searchResult;
        } else
            return listHierarchicalCategories(rootCategories, sortDir);
    }

    // categories listing page
    private List<Category> listHierarchicalCategories(List<Category> rootCategories, String sortDir) {
        List<Category> hierarchicalCategories = new ArrayList<>();

        for (Category rootCategory : rootCategories) {
            hierarchicalCategories.add(Category.CategoryCopyFull(rootCategory));

            Set<Category> children = sortSubCategories(rootCategory.getChildren(), sortDir);
            for (Category subCategory : children) {
                String name = "--" + subCategory.getName();
                hierarchicalCategories.add(Category.CategoryCopyFull(subCategory, name));

                listSubHierarchicalCategories(hierarchicalCategories, subCategory, 1, sortDir);
            }
        }

        return hierarchicalCategories;
    }
    // recursive code for finding children category in listing page
    private void listSubHierarchicalCategories(List<Category> hierarchicalCategories,
                    Category parent, int subLevel, String sortDir) {
        Set<Category> children = sortSubCategories(parent.getChildren(), sortDir);
        int newSubLevel = subLevel + 1;
        for (Category subCategory : children) {
            String name = "";
            for(int i = 0; i < newSubLevel; i++) {
                name += "--";
            }
            name += subCategory.getName();
            hierarchicalCategories.add(Category.CategoryCopyFull(subCategory, name));

            listSubHierarchicalCategories(hierarchicalCategories, subCategory, newSubLevel, sortDir);
        }
    }

    public Category saveCategory(Category category) {
        Category save = repository.save(category);
        return save;
    }

    // categories use inform
    public List<Category> listCategoryUsedInform() {
        List<Category> categoriesUsedInform = new ArrayList<>();

        Iterable<Category> categoriesInDB= repository.findRootCategories(Sort.by("name").ascending());

        for (Category category : categoriesInDB) {
            if (category.getParent() == null) {
                categoriesUsedInform.add(Category.copyIdAndName(category));

                Set<Category> children = sortSubCategories(category.getChildren());

                for (Category subCategory : children) {
                    String name = "--" + subCategory.getName();
                    categoriesUsedInform.add(Category.copyIdAndName(subCategory.getId(), name));
                    listChildren(categoriesUsedInform, subCategory, 1);
                }
            }
        }

        return categoriesUsedInform;
    }

    // recursive code for finding children category used inform
    private void listChildren(List<Category> categoriesUsedInForm,Category parent, int subLevel) {
        int newSubLevel = subLevel + 1;
        Set<Category> children = sortSubCategories(parent.getChildren());

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

    public String checkUnique(Integer id, String name, String alias) {
        boolean isCreatingNew = (id == null || id == 0);

        Category categoryByName = repository.findByName(name);
        Category categoryByAlias = repository.findByAlias(alias);

        if(isCreatingNew) {
            if(categoryByName != null) return "DuplicateName";
            else {
                if (categoryByAlias != null) return "DuplicateAlias";
            }
        }
        else{
            if(categoryByName != null && categoryByName.getId() != id)
                return "DuplicateName";

            if(categoryByAlias != null && categoryByAlias.getId() != id)
                return "DuplicateAlias";
        }

        return "OK";
    }

    private SortedSet<Category> sortSubCategories(Set<Category> children) {
        return sortSubCategories(children, "asc");
    }

    private SortedSet<Category> sortSubCategories(Set<Category> children, String sortDir) {
            SortedSet<Category> sortedChildren = new TreeSet<>(new Comparator<Category>() {
                @Override
                public int compare(Category o1, Category o2) {
                    if(sortDir.equals("asc")) {
                        return o1.getName().compareTo(o2.getName());
                    }
                    else
                        return o2.getName().compareTo(o1.getName());
                }
            });

            sortedChildren.addAll(children);
            return sortedChildren;
    }


    // delete category
    public void delete(Integer id) throws CategoryNotFoundException {
        Long countById = repository.countById(id);
        if(countById == 0 || countById == null) {
            throw new CategoryNotFoundException("Could not find any category with ID " + id);
        }

        repository.deleteById(id);
    }
}
