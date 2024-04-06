package com.shopme.admin.category;

import com.shopme.admin.FileUploadUtil;
import com.shopme.common.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
public class CategoryController {

    @Autowired
    private CategoryService service;

    @GetMapping("/categories")
    public String getCategoriesPage(Model model) {
        List<Category> categories = service.getAllCategories();

        model.addAttribute("categories", categories);

        return "categories/categories";
    }

    @GetMapping("/categories/new")
    public String createNewCategory(Model model) {
        List<Category> listCategories = service.listCategoryUsedInform();

        model.addAttribute("category", new Category());
        model.addAttribute("listCategories", listCategories);
        model.addAttribute("pageTitle", "Create Category");
        return "categories/category_form";
    }

    @PostMapping("/categories/save")
    public String saveCategory(Category category, @RequestParam("fileImage")MultipartFile multipartFile,
                               RedirectAttributes redirectAttributes) throws IOException {
        if(!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            category.setImage(fileName);

            Category savedCategory = service.saveCategory(category);
            String uploadDir = "../category-images/" + savedCategory.getId();

            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        }
        else{
            service.saveCategory(category);
        }

        redirectAttributes.addFlashAttribute("message", "The category has been saved successfully !");
        return "redirect:/categories";
    }

    @GetMapping("/categories/edit/{id}")
    public String editCategory(@PathVariable("id") Integer id, Model model,
                               RedirectAttributes redirectAttributes) {
        try {
            Category category = service.get(id);
            List<Category> categoryList = service.listCategoryUsedInform();

            model.addAttribute("listCategories", categoryList);
            model.addAttribute("category", category);
            model.addAttribute("pageTitle", "Edit Category (ID:" + id + ")");

            return "categories/category_form";
        }
        catch (CategoryNotFoundException exception) {
            redirectAttributes.addFlashAttribute("message", exception.getMessage());
            return "redirect:/categories";
        }
    }


}
