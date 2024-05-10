package com.shopme.admin.brand;

import com.shopme.admin.AbstractClassExporter;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class BrandExporterCsv extends AbstractClassExporter {

    private final BrandService brandService;

    public BrandExporterCsv(BrandService brandService) {
        this.brandService = brandService;
    }

    public void export(List<Brand> brandList, HttpServletResponse response) throws IOException {

        super.setResponseHeader(response, "text/csv", ".csv", "brands_");
        OutputStream outputStream = response.getOutputStream();
        Writer writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);

        // Add BOM for UTF-8
        writer.write('\uFEFF');

        ICsvBeanWriter csvWriter = new CsvBeanWriter(writer, CsvPreference.STANDARD_PREFERENCE);

        String[] csvHeader = {"ID", "Brand Name", "List Categories"};
        csvWriter.writeHeader(csvHeader);

        csvWriter.writeHeader(csvHeader);

        for (Brand brand : brandList) {
            // Lấy danh sách các danh mục tương ứng với mỗi thương hiệu
            List<Category> categories = brandService.getListCategoriesByBrand(brand.getId());

            // Tạo một chuỗi để chứa tên các danh mục
            StringBuilder categoryNames = new StringBuilder();
            for (Category category : categories) {
                categoryNames.append(category.getName()).append(", ");
            }

            // Xóa dấu phẩy và khoảng trắng cuối cùng nếu có
            if (categoryNames.length() > 0) {
                categoryNames.delete(categoryNames.length() - 2, categoryNames.length());
            }

            // Tạo một mảng để map các trường của đối tượng Brand với cột tương ứng trong CSV
            String[] fieldMapping = {"id", "name", categoryNames.toString()};

            // Ghi thông tin của thương hiệu và danh sách các danh mục vào file CSV
            csvWriter.write(brand, fieldMapping);
        }

        csvWriter.close();
    }
}
