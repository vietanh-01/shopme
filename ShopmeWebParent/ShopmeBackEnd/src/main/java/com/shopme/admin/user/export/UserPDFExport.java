package com.shopme.admin.user.export;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;
import com.shopme.admin.AbstractClassExporter;
import com.shopme.common.entity.User;


import jakarta.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class UserPDFExport extends AbstractClassExporter {

    public void export(List<User> userList, HttpServletResponse response) throws IOException {
        super.setResponseHeader(response, "application/pdf", ".pdf", "users_");

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        Font f = new Font(BaseFont.createFont("static/font/arial.ttf",
                BaseFont.IDENTITY_H, BaseFont.EMBEDDED));

        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setSize(16);
        font.setColor(Color.BLACK);

        Paragraph paragraph = new Paragraph("List of users", font);
        paragraph.setAlignment(Paragraph.ALIGN_CENTER);

        document.add(paragraph);

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100f);
        table.setSpacingBefore(10);
        table.setWidths(new float[] {1.0f, 3.5f, 3.0f, 3.0f, 3.0f, 1.5f});
        writeTableHeader(table);
        writeTabledData(table, userList, f);

        document.add(table);

        document.close();
    }

    private void writeTabledData(PdfPTable table, List<User> userList, Font font) {
        for(User u : userList) {
            table.addCell(new Phrase(String.valueOf(u.getId()), font));
            table.addCell(new Phrase(u.getEmail(), font));
            table.addCell(new Phrase(u.getFirstName(), font));
            table.addCell(new Phrase(u.getLastName(), font));
            table.addCell(new Phrase(u.getRoles().toString(), font));
            table.addCell(new Phrase(String.valueOf(u.isEnabled()), font));
        }
    }

    private void writeTableHeader(PdfPTable table) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(new Color(0,191,255));
        cell.setPadding(5);


        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setColor(Color.WHITE);

        cell.setPhrase(new Phrase("ID", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("E-mail", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("First Name", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Last Name", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Roles", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Enabled", font));
        table.addCell(cell);
    }
}
