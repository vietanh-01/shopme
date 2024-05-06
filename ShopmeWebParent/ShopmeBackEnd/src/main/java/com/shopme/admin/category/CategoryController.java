package com.shopme.admin.category;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.user.UserService;
import com.shopme.admin.user.export.UserCsvExport;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
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
    public String getCategoriesPage(@Param("sortDir") String sortDir , Model model) {
        return listByPage(1, sortDir, model, null);
    }

    @GetMapping("/categories/page/{pageNum}")
    public String listByPage(@PathVariable(name = "pageNum") int pageNum,
                             @Param("sortDir") String sortDir , Model model,
                             @Param("keyword") String keyword) {

        if(sortDir == null) sortDir = "asc";
        CategoryPageInfo pageInfo = new CategoryPageInfo();
        List<Category> categories = service.listByPage(pageInfo, pageNum, sortDir, keyword);

        long startCount = (pageNum - 1) * CategoryService.ROOT_CATEGORIES_PER_PAGE + 1;
        long endCount = startCount + CategoryService.ROOT_CATEGORIES_PER_PAGE - 1;
        if(endCount > pageInfo.getTotalElements()) {
            endCount = pageInfo.getTotalElements();
        }

        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        model.addAttribute("totalItems", pageInfo.getTotalElements());
        model.addAttribute("totalPages", pageInfo.getTotalPages());
        model.addAttribute("sortField", "name");
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("keyword", keyword);
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);

        model.addAttribute("reverseSortDir", reverseSortDir);
        model.addAttribute("categories", categories);

        return "categories/categories";
    }

    @GetMapping("/categories/new")
    public String createNewCategory(Model model) {
        List<Category> listCategories = service.listCategoryUsedInform();

        model.addAttribute("category", new Category());
        model.addAttribute("listCategories", listCategories);
        model.addAttribute("pageTitle", "Create New Category");
        return "categories/category_form";
    }

    @PostMapping("/categories/save")
    public String saveCategory(Category category, @RequestParam("fileImage") MultipartFile multipartFile,
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
            categoryList.remove(category);

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


    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable("id") Integer id,
                                 Model model, RedirectAttributes redirectAttributes) {
        try {
            service.delete(id);
            String catDir = "../category-images/" + id;
            FileUploadUtil.removeDir(catDir);
            redirectAttributes.addFlashAttribute("message",
                    "The Category with Id " + id + " has been delete successfully !");
        }catch (CategoryNotFoundException exception) {
            redirectAttributes.addFlashAttribute("message", exception.getMessage());
        }

        return "redirect:/categories";
    }

    @GetMapping("/categories/export/csv")
    public void exportToCSV(HttpServletResponse response) throws IOException {
        List<Category> categoriesList = service.listCategoryUsedInform();
        CategoryCsvExporter exporter = new CategoryCsvExporter();
        exporter.export(categoriesList, response);
    }
}
