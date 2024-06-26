package com.shopme.admin.product;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.brand.BrandService;
import com.shopme.admin.category.CategoryCsvExporter;
import com.shopme.admin.category.CategoryService;
import com.shopme.admin.security.ShopmeUserDetails;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Product;
import com.shopme.common.entity.ProductImage;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class ProductController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/products")
    public String listFirstPage(Model model) {
        return listByPage(1, model, "name", "asc", null, 0);
    }

    @GetMapping("/products/page/{pageNum}")
    public String listByPage(
            @PathVariable(name = "pageNum") int pageNum, Model model,
            @RequestParam("sortField") String sortField, @RequestParam("sortDir") String sortDir,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam("categoryId") Integer categoryId
    ) {
        Page<Product> page = productService.listByPage(pageNum, sortField, sortDir, keyword, categoryId);
        List<Product> listProducts = page.getContent();

        List<Category> listCategories = categoryService.listCategoryUsedInform();

        long startCount = (pageNum - 1) * ProductService.PRODUCTS_PER_PAGE + 1;
        long endCount = startCount + ProductService.PRODUCTS_PER_PAGE - 1;
        if (endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }

        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        if(categoryId != null) model.addAttribute("categoryId", categoryId);

        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", reverseSortDir);
        model.addAttribute("keyword", keyword);
        model.addAttribute("listProducts", listProducts);
        model.addAttribute("listCategories", listCategories);


        return "products/products";
    }

//    @GetMapping("/products")
//    public String listAll(Model model) {
//        List<Product> listProducts = productService.listAll();
//
//        model.addAttribute("listProducts", listProducts);
//
//        return "products/products";
//    }

    @GetMapping("/products/new")
    public String newProduct(Model model) {
       List<Brand> listBrands = brandService.listAll();

       Product product = new Product();
       product.setEnabled(true);
       product.setInStock(true);
       Integer numberOfExistingExtraImages = product.getImages().size();

       model.addAttribute("product", product);
       model.addAttribute("listBrands", listBrands);
       model.addAttribute("pageTitle", "Create New Product");
       model.addAttribute("numberOfExistingExtraImages", numberOfExistingExtraImages);

       return "products/product_form";
    }

    @PostMapping("/products/save")
    public String saveProduct(Product product, RedirectAttributes ra,
                              @RequestParam(value = "fileImage", required = false) MultipartFile mainImageMultipart,
                              @RequestParam(value = "extraImage", required = false) MultipartFile[] extraImageMultiparts,
                              @RequestParam(name = "detailIDs", required = false) String[] detailIDs,
                              @RequestParam(name = "detailNames", required = false) String[] detailNames,
                              @RequestParam(name = "detailValues", required = false) String[] detailValues,
                              @RequestParam(name = "imageIDs", required = false) String[] imageIDs,
                              @RequestParam(name = "imageNames", required = false) String[] imageNames,
                              @AuthenticationPrincipal ShopmeUserDetails loggedUser
                              ) throws IOException {
        if (loggedUser.hasRole("Salesperson")) {
            productService.saveProductPrice(product);
            ra.addFlashAttribute("message", "The product has been saved successfully.");
            return "redirect:/products";
        }

        ProductSaveHelper.setMainImageName(mainImageMultipart, product);
        ProductSaveHelper.setExistingExtraImageNames(imageIDs, imageNames, product);
        ProductSaveHelper.setNewExtraImageNames(extraImageMultiparts, product);
        ProductSaveHelper.setProductDetails(detailIDs, detailNames, detailValues, product);

        Product savedProduct = productService.save(product);

        ProductSaveHelper.saveUploadedImages(mainImageMultipart, extraImageMultiparts, savedProduct);

        ProductSaveHelper.deleteExtraImagesWeredRemovedOnForm(product);

        ra.addFlashAttribute("message", "The product has been saved successfully.");

        return "redirect:/products";
    }


    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable("id") Integer id,
                              RedirectAttributes redirectAttributes) {
        try {
            productService.delete(id);
            String productExtraImagesDir = "../product-images/" + id + "/extras";
            String productImagesDir = "../product-images/" + id;

            FileUploadUtil.removeDir(productExtraImagesDir);
            FileUploadUtil.removeDir(productImagesDir);

            redirectAttributes.addFlashAttribute("message",
                    "The product ID " + id + " has been deleted successfully");
        } catch (ProductNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/products";
    }

    @GetMapping("/products/edit/{id}")
    public String editProduct(@PathVariable("id") Integer id, Model model,
                              RedirectAttributes redirectAttributes) {
        try {
            Product product = productService.get(id);
            List<Brand> listBrands = brandService.listAll();
            Integer numberOfExistingExtraImages = product.getImages().size();

            model.addAttribute("product", product);
            model.addAttribute("pageTitle", "Edit Product(ID: " + id + ")");
            model.addAttribute("listBrands", listBrands);
            model.addAttribute("numberOfExistingExtraImages", numberOfExistingExtraImages);

            return "products/product_form";

        } catch (ProductNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());

            return "redirect:/products";
        }
    }

    @GetMapping("/products/detail/{id}")
    public String viewProductDetails(@PathVariable("id") Integer id, Model model,
                                     RedirectAttributes redirectAttributes) {
        try {
            Product product = productService.get(id);

            model.addAttribute("product", product);

            return "products/product_detail_modal";

        } catch (ProductNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());

            return "redirect:/products";
        }
    }

    @GetMapping("/products/export/csv")
    public void exportToCSV(HttpServletResponse response) throws IOException {
        List<Product> productList = productService.listAll();
        ProductCsvExporter exporter = new ProductCsvExporter();
        exporter.export(productList, response);
    }
}
