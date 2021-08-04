package ru.plantarum.core.uploading.controller;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.plantarum.core.cut.CounterAgentCut;
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
    private final CounterAgentCutService counterAgentCutService;
    private final CampaignService campaignService;
    private final ExcelBookService excelBookService;

    private final int HEADERS_ROW = 0;
    private final int FIRST_ROW = 1;
    private final int SECOND_ROW = 2;

//    private XSSFWorkbook book;
//    private ResultParsing resultParsing;

    private List<TradeMark> getAllTradeMarks() {
        return tradeMarkService.findAllActive();
    }

    private List<OrganType> getAllOrganTypes() {
        return organTypeService.findAllActive();
    }

    /**
     * return list of CounterAgents with only 4 fields (id, name, phone, profile)
     * */
    private List<CounterAgentCut> getAllCounterAgents() {
        return counterAgentCutService.getCutAgents();
    }

    private List<Campaign> getAllCampaigns() {
        return campaignService.findAllActive();
    }

    @GetMapping
    public String loadFileForm() {
        return "parsing-excel";
    }

    @GetMapping("/result")
    public String resultForm(@RequestParam(required = false) String excelEntity, Model model) {
        model.addAttribute("message", excelEntity);
//        model.addAttribute("bareCodeResult", resultParsing.getBareCodeCount());
//        model.addAttribute("priceBuyResult", resultParsing.getPriceBuyCount());
//        model.addAttribute("priceSaleResult", resultParsing.getPriceSaleCount());
//
//        if (!resultParsing.getProductErrors().isEmpty()) {
//            model.addAttribute("productErrors", resultParsing.getProductErrors());
//        }
//        if (!resultParsing.getProductWarnings().isEmpty()) {
//            model.addAttribute("productWarnings", resultParsing.getProductWarnings());
//        }
//        if (!resultParsing.getBareCodeErrors().isEmpty()) {
//            model.addAttribute("bareCodeErrors", resultParsing.getBareCodeErrors());
//        }
//        if (!resultParsing.getBareCodeWarnings().isEmpty()) {
//            model.addAttribute("bareCodeWarnings", resultParsing.getBareCodeWarnings());
//        }
//        if (!resultParsing.getPriceBuyErrors().isEmpty()) {
//            model.addAttribute("priceBuyErrors", resultParsing.getPriceBuyErrors());
//        }
//        if (!resultParsing.getPriceBuyWarnings().isEmpty()) {
//            model.addAttribute("priceBuyWarnings", resultParsing.getPriceBuyWarnings());
//        }
//        if (!resultParsing.getPriceSaleErrors().isEmpty()) {
//            model.addAttribute("priceSaleErrors", resultParsing.getPriceSaleErrors());
//        }
//        if (!resultParsing.getPriceSaleWarnings().isEmpty()) {
//            model.addAttribute("priceSaleWarnings", resultParsing.getPriceSaleWarnings());
//        }
//        int productExistCount = 0;
//        for (InvalidParse warning: resultParsing.getProductWarnings()) {
//            if (warning.getError().equals("Такой продукт уже существует")) {
//                productExistCount++;
//            }
//        }
//        if (productExistCount > 0) {
//            model.addAttribute("productExist", productExistCount);
//        }
        return "excel-parse-response";
    }

    @PostMapping("/start")
    public String saveInDb(@Valid @RequestBody ExcelEntity excelEntity, Model model) {
        excelEntity = excelBookService.setHeaders(excelEntity);
        excelEntity = excelParseService.parseToDb(excelEntity);
        model.addAttribute("excelEntity", excelEntity);
        if (!excelEntity.getErrors().isEmpty()) {
            model.addAttribute("errors", excelEntity.getErrors());
            return "excel-parse-response";
        }
        if (!excelEntity.getWarnings().isEmpty()) {
            model.addAttribute("warnings", excelEntity.getWarnings());
            return "excel-parse-response";
        }
        if (excelEntity.getProductCount() == 0) {
            model.addAttribute("products", excelEntity.getProducts());
        }
        if (excelEntity.getBareCodeCount() == 0) {
            model.addAttribute("bareCodes", excelEntity.getBareCodes());
        }
        if (excelEntity.getPriceBuyCount() == 0) {
            model.addAttribute("priceBuys", excelEntity.getPriceBuyPreliminarilyMap());
        }
        if (excelEntity.getPriceSaleCount() == 0) {
            model.addAttribute("priceSales", excelEntity.getPriceSales());
        }
        return "excel-parse-response";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes attributes){

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
        /*
        * get the valid JSON object of counterAgents list
        * */
        attributes.addFlashAttribute("counterAgents", excelParseService.getJSON(getAllCounterAgents()));

        return "redirect:/apache";
    }
}
