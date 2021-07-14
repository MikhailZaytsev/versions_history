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
import ru.plantarum.core.service.*;
import ru.plantarum.core.uploading.excel.EntityFields;
import ru.plantarum.core.uploading.excel.ExcelEntity;
import ru.plantarum.core.uploading.excel.ExcelParseService;
import ru.plantarum.core.uploading.response.InvalidParse;
import ru.plantarum.core.uploading.response.ResultParsing;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
@RequestMapping("/apache")
public class ExcelParseController {

    private final ExcelParseService excelParseService;

    private final TradeMarkService tradeMarkService;
    private final OrganTypeService organTypeService;
    private final CounterAgentService counterAgentService;
    private final CampaignService campaignService;
    private final ExcelBookService excelBookService;

    private final int HEADERS_ROW = 0;
    private final int FIRST_ROW = 1;
    private final int SECOND_ROW = 2;

//    private XSSFWorkbook book;
    private ResultParsing resultParsing;

    private List<TradeMark> getAllTradeMarks() {
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
        return "parsing-excel";
    }

    @GetMapping("/result")
    public String resultForm(Model model) {
        model.addAttribute("productResult", resultParsing.getProductCount());
        model.addAttribute("bareCodeResult", resultParsing.getBareCodeCount());
        model.addAttribute("priceBuyResult", resultParsing.getPriceBuyCount());
        model.addAttribute("priceSaleResult", resultParsing.getPriceSaleCount());

        if (!resultParsing.getProductErrors().isEmpty()) {
            model.addAttribute("productErrors", resultParsing.getProductErrors());
        }
        if (!resultParsing.getProductWarnings().isEmpty()) {
            model.addAttribute("productWarnings", resultParsing.getProductWarnings());
        }
        if (!resultParsing.getBareCodeErrors().isEmpty()) {
            model.addAttribute("bareCodeErrors", resultParsing.getBareCodeErrors());
        }
        if (!resultParsing.getBareCodeWarnings().isEmpty()) {
            model.addAttribute("bareCodeWarnings", resultParsing.getBareCodeWarnings());
        }
        if (!resultParsing.getPriceBuyErrors().isEmpty()) {
            model.addAttribute("priceBuyErrors", resultParsing.getPriceBuyErrors());
        }
        if (!resultParsing.getPriceBuyWarnings().isEmpty()) {
            model.addAttribute("priceBuyWarnings", resultParsing.getPriceBuyWarnings());
        }
        if (!resultParsing.getPriceSaleErrors().isEmpty()) {
            model.addAttribute("priceSaleErrors", resultParsing.getPriceSaleErrors());
        }
        if (!resultParsing.getPriceSaleWarnings().isEmpty()) {
            model.addAttribute("priceSaleWarnings", resultParsing.getPriceSaleWarnings());
        }
        int productExistCount = 0;
        for (InvalidParse warning: resultParsing.getProductWarnings()) {
            if (warning.getError().equals("Такой продукт уже существует")) {
                productExistCount++;
            }
        }
        if (productExistCount > 0) {
            model.addAttribute("productExist", productExistCount);
        }
        return "excel-parse-response";
    }

    @PostMapping("/start")
    public void saveInDb(@Valid @RequestBody ExcelEntity excelEntity) {
        //TODO readFile from file system
        resultParsing = excelParseService.parseToDataBase(excelEntity,new XSSFWorkbook());
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes attributes) {

        String checkFile = excelBookService.loadBook(file);

        if (checkFile.equals(excelBookService.WRONG_EXTENSION) ||
            checkFile.equals(excelBookService.EMPTY_FILE) ||
            checkFile.equals(excelBookService.HIDDEN_SHEET) ||
            checkFile.equals(excelBookService.EMPTY_BOOK) ||
            checkFile.equals(excelBookService.SMALL_FILE)) {
            attributes.addFlashAttribute("message", checkFile);
            return "redirect:/apache";
        }

        attributes.addFlashAttribute("message", checkFile);

        attributes.addFlashAttribute("headers", excelBookService.getValues(checkFile, HEADERS_ROW));
        attributes.addFlashAttribute("firstRow", excelBookService.getValues(checkFile, FIRST_ROW));
        attributes.addFlashAttribute("secondRow", excelBookService.getValues(checkFile, SECOND_ROW));
        attributes.addFlashAttribute("entityFields", EntityFields.values());
        attributes.addFlashAttribute("campaigns", getAllCampaigns());
        attributes.addFlashAttribute("trademarks", getAllTradeMarks());
        attributes.addFlashAttribute("organTypes", getAllOrganTypes());

        return "redirect:/apache";
        // check if file is empty
//        if (file.isEmpty()) {
//            attributes.addFlashAttribute("message", "Выберите файл для загрузки");
//            return "redirect:/apache";
//        }
//
//        // normalize the file path
//        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
//        //check EXCEl files
//        if (!FilenameUtils.getExtension(fileName).equals(excelBookService.EXCEL_FORMAT)) {
//            attributes.addFlashAttribute("message", "Выбранный файл не EXCEL");
//            return "redirect:/apache";
//        }
//
//        // save the file on the local file system
//        try {
//            XSSFWorkbook book = new XSSFWorkbook(file.getInputStream());
//            Sheet sheet = book.getSheetAt(0);
//            Row row0 = sheet.getRow(0);
//            Row row1 = sheet.getRow(1);
//            Row row2 = sheet.getRow(2);
//            int last = row0.getLastCellNum();
//            ArrayList<String> cells0 = new ArrayList<>();
//            ArrayList<String> cells1 = new ArrayList<>();
//            ArrayList<String> cells2 = new ArrayList<>();
//                for (int i = 0; i < last; i++) {
//                    cells0.add(getStringFromCell(row0.getCell(i)));
//                    cells1.add(getStringFromCell(row1.getCell(i)));
//                    cells2.add(getStringFromCell(row2.getCell(i)));
//                }
//                attributes.addFlashAttribute("cells0", cells0);
//                attributes.addFlashAttribute("cells1", cells1);
//                attributes.addFlashAttribute("cells2", cells2);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        attributes.addFlashAttribute("tradeMarks", getAllTradeMArks());
//        attributes.addFlashAttribute("organTypes", getAllOrganTypes());
//        attributes.addFlashAttribute("counterAgents", getAllCounterAgents());
//        attributes.addFlashAttribute("campaigns", getAllCampaigns());
//        attributes.addFlashAttribute("fullFileName", fileName);
//
//        // return success response
//        attributes.addFlashAttribute("message", "К сохранению (загрузке) приготовлен файл: " + fileName + '!');
    }
}
