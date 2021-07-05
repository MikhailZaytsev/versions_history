package ru.plantarum.core.uploading.controller;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.plantarum.core.entity.*;
import ru.plantarum.core.service.CampaignService;
import ru.plantarum.core.service.CounterAgentService;
import ru.plantarum.core.service.OrganTypeService;
import ru.plantarum.core.service.TradeMarkService;
import ru.plantarum.core.uploading.excel.ExcelEntity;
import ru.plantarum.core.uploading.excel.ExcelParseService;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
@RequestMapping("/apache")
public class ExcelParseController {

    public final String EXCEL_FORMAT = "xlsx";
    private final String UPLOAD_DIR = "D:/test/excel/";

    private final ExcelParseService excelParseService;

    private final TradeMarkService tradeMarkService;
    private final OrganTypeService organTypeService;
    private final CounterAgentService counterAgentService;
    private final CampaignService campaignService;

    private XSSFWorkbook book;

    private List<TradeMark> getAllTradeMArks() {
        return tradeMarkService.findAllActive();
    }

    private List<OrganType> getAllOrganTypes() {
        return organTypeService.findAllActive();
    }

    private List<CounterAgent> getAllCounterAgents() {
        return counterAgentService.findAllActive();
    }

    private List<Campaign> getAllCampaigns() {
        return campaignService.findAllActive();
    }

    @GetMapping
    public String loadFileForm() {
        return "excel-parse";
    }

    @PostMapping("/start")
    public String saveInDb(@Valid @RequestBody ExcelEntity excelEntity,
                           BindingResult bindingResult, Model model) {
        excelParseService.parseToDataBase(excelEntity, book);
        return "redirect:/products/all";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes attributes) {

        // check if file is empty
        if (file.isEmpty()) {
            attributes.addFlashAttribute("message", "Выберите файл для загрузки");
            return "redirect:/apache";
        }

        // normalize the file path
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        //check EXCEl files
        if (!FilenameUtils.getExtension(fileName).equals(EXCEL_FORMAT)) {
            attributes.addFlashAttribute("message", "Выбранный файл не EXCEL");
            return "redirect:/apache";
        }

        // save the file on the local file system
        try {
            book = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = book.getSheetAt(0);
            Row row0 = sheet.getRow(0);
            Row row1 = sheet.getRow(1);
            Row row2 = sheet.getRow(2);
            int last = row0.getLastCellNum();
            ArrayList<String> cells0 = new ArrayList<>();
            ArrayList<String> cells1 = new ArrayList<>();
            ArrayList<String> cells2 = new ArrayList<>();
                for (int i = 0; i < last; i++) {
                    cells0.add(getStringFromCell(row0.getCell(i)));
                    cells1.add(getStringFromCell(row1.getCell(i)));
                    cells2.add(getStringFromCell(row2.getCell(i)));
                }
                attributes.addFlashAttribute("cells0", cells0);
                attributes.addFlashAttribute("cells1", cells1);
                attributes.addFlashAttribute("cells2", cells2);
            Path path = Paths.get(UPLOAD_DIR + "downloaded_" + fileName);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }

        attributes.addFlashAttribute("tradeMarks", getAllTradeMArks());
        attributes.addFlashAttribute("organTypes", getAllOrganTypes());
        attributes.addFlashAttribute("counterAgents", getAllCounterAgents());
        attributes.addFlashAttribute("campaigns", getAllCampaigns());

        // return success response
        attributes.addFlashAttribute("message", "К сохранению (загрузке) приготовлен файл: " + fileName + '!');

        return "redirect:/apache";
    }

    String getStringFromCell(Cell cell) {
        if (cell.getCellType() != CellType.FORMULA) {
            switch (cell.getCellType()) {
                case STRING:
                    return cell.getStringCellValue();
                case NUMERIC:
                    return BigDecimal.valueOf(cell.getNumericCellValue()).toString();
                case BOOLEAN:
                    return Boolean.toString(cell.getBooleanCellValue());
            }
            return "Не значение";
        } else {
            switch (cell.getCachedFormulaResultType()) {
                case BOOLEAN: return Boolean.toString(cell.getBooleanCellValue());
                case STRING: return cell.getStringCellValue();
                case NUMERIC: return BigDecimal.valueOf(cell.getNumericCellValue()).toString();
            }
            return "Не значение";
        }
    }

}
