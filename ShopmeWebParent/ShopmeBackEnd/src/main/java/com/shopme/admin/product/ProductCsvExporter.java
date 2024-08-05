package com.shopme.admin.product;

import com.shopme.admin.AbstractClassExporter;
import com.shopme.common.entity.product.Product;
import jakarta.servlet.http.HttpServletResponse;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class ProductCsvExporter extends AbstractClassExporter {



    public void export(List<Product> listProducts, HttpServletResponse response) throws IOException {

        super.setResponseHeader(response, "text/csv", ".csv", "products_");
        OutputStream outputStream = response.getOutputStream();
        Writer writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);

        // Add BOM for UTF-8
        writer.write('\uFEFF');

        ICsvBeanWriter csvWriter = new CsvBeanWriter(writer, CsvPreference.STANDARD_PREFERENCE);

        String[] csvHeader = {"ID", "Product Name", "Alias", "Description", "Price"};
        String[] fieldMapping = {"id", "name", "alias", "shortDescription", "price"};

        csvWriter.writeHeader(csvHeader);

        for (Product product : listProducts) {
            convertHtmlToPlainText(product.getShortDescription());
            csvWriter.write(product, fieldMapping);
        }

        csvWriter.close();
    }

    public static String convertHtmlToPlainText(String htmlString) {
        // Parse the HTML string using Jsoup
        Document doc = Jsoup.parse(htmlString);

        // Get the text content from the parsed document
        String plainText = doc.text();

        return plainText;
    }
}
