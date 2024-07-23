package com.shopme.admin.customer;

import com.shopme.admin.AbstractClassExporter;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.User;
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

public class CustomerCsvExport extends AbstractClassExporter {
    public void export(List<Customer> listCustomers, HttpServletResponse response) throws IOException {

        super.setResponseHeader(response, "text/csv", ".csv", "customers_");
        OutputStream outputStream = response.getOutputStream();
        Writer writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);

        // Add BOM for UTF-8
        writer.write('\uFEFF');

        ICsvBeanWriter csvWriter = new CsvBeanWriter(writer, CsvPreference.STANDARD_PREFERENCE);

        String[] csvHeader = {"Customer ID", "E-mail", "First Name", "Last Name", "City", "State", "Country"};
        String[] fieldMapping = {"id", "email", "firstName", "lastName", "city", "state", "country"};

        csvWriter.writeHeader(csvHeader);

        for (Customer customer : listCustomers) {
            csvWriter.write(customer, fieldMapping);
        }

        csvWriter.close();
    }
}
