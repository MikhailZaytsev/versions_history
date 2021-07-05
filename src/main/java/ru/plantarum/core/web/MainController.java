package ru.plantarum.core.web;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import ru.plantarum.core.entity.OrganType;
import ru.plantarum.core.entity.Product;
import ru.plantarum.core.entity.TradeMark;
import ru.plantarum.core.service.OrganTypeService;
import ru.plantarum.core.service.PriceBuyPreliminarilyService;
import ru.plantarum.core.service.ProductService;
import ru.plantarum.core.service.TradeMarkService;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final TradeMarkService tradeMarkService;
    private final ProductService productService;
    private final OrganTypeService organTypeService;
    private final PriceBuyPreliminarilyService priceService;

    @GetMapping("/index")
    public String getHomePage() {
        return "start";
    }

//    @GetMapping("/apache")
//    public String excel() {
//        List<Product> products = new ArrayList<>();
//        TradeMark tradeMark = tradeMarkService.getOne(10L).orElse(null);
//        OrganType organType = organTypeService.getOne(9L).orElse(null);
//        Product product = Product.builder()
//                .tradeMark(tradeMark)
//                .organType(organType)
//                .build();
//        Workbook wb = new HSSFWorkbook();
//        Sheet sheet = wb.createSheet("product");
//        FileOutputStream fileOutputStream = new FileOutputStream("D:/test/excel/castor" + i++ + ".xls");
//        wb.write(refileOutputStream);
//        fileOutputStream.close();
//        try {
//            FileInputStream input = new FileInputStream("C:/Users/Михаил/Desktop/пробный_прайс.xlsx");
//            Workbook sourceBook = new XSSFWorkbook(input);
//            Sheet sheet = sourceBook.getSheetAt(0);
//            int rows = sheet.getLastRowNum();
//            for (int i = 1; i <= rows; i++) {
//                Product product = new Product();
//                product.setOrganType(organType);
//                product.setTradeMark(tradeMark);
//                product.setProductName(sheet.getRow(i).getCell(3).getStringCellValue());
//                product.setNumberInPack((short) sheet.getRow(i).getCell(0).getNumericCellValue());
//                Product productFromDb = productService.findByProductName(product.getProductName());
//                if (productFromDb == null || StringUtils.isNotBlank(product.getProductName())) {
//                    products.add(product);
//                }
//            }
//            input.close();
//            productService.saveAll(products);
//            FileOutputStream fileOutputStream = new FileOutputStream("D:/test/excel/castor.xlsx");
//            Workbook resultBook = new XSSFWorkbook();
//            Sheet table = resultBook.createSheet("Таблица");
//            Row row = table.createRow(0);
//            Cell cell = row.createCell(0);
//            cell.setCellValue("Продукт");
//            cell = row.createCell(1);
//            cell.setCellValue("Орган");
//            cell = row.createCell(2);
//            cell.setCellValue("Марка");
//            cell = row.createCell(3);
//            cell.setCellValue("В упак.");
//            int n = 1;
//            for (Product p : products) {
//                row = table.createRow(n);
//                cell = row.createCell(0);
//                cell.setCellValue(p.getProductName());
//                cell = row.createCell(1);
//                cell.setCellValue(p.getOrganType().getOrganTypeName());
//                cell = row.createCell(2);
//                cell.setCellValue(p.getTradeMark().getTradeMarkName());
//                cell = row.createCell(3);
//                cell.setCellValue(p.getNumberInPack());
//                n++;
//            }
//            resultBook.write(fileOutputStream);
//            fileOutputStream.close();
//        } catch (FileNotFoundException e) {
//            System.err.println("Файл не найден");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return "redirect:/";
//    }

    // Login form
//    @RequestMapping("/login")
//    public String login() {
//        return "login";
//    }

}
