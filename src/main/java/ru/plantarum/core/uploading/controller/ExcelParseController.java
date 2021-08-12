package ru.plantarum.core.uploading.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.plantarum.core.cut.CounterAgentCut;
import ru.plantarum.core.entity.Campaign;
import ru.plantarum.core.entity.OrganType;
import ru.plantarum.core.entity.TradeMark;
import ru.plantarum.core.service.*;
import ru.plantarum.core.uploading.excel.EntityFields;
import ru.plantarum.core.uploading.excel.ExcelEntity;
import ru.plantarum.core.uploading.excel.ExcelParseService;
import ru.plantarum.core.uploading.response.RowSorter;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/apache")
public class ExcelParseController {

    private final ExcelParseService excelParseService;

    private final TradeMarkService tradeMarkService;
    private final OrganTypeService organTypeService;
//    private final CounterAgentService counterAgentService;
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
     */
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

    @PostMapping("/start")
    public String saveInDb(@Valid @RequestBody ExcelEntity excelEntity, Model model) {
        excelEntity = excelBookService.setHeaders(excelEntity);
        excelEntity = excelParseService.parseToDb(excelEntity);
        if (!excelEntity.getErrors().isEmpty()) {
            excelEntity.getErrors().sort(new RowSorter());
            model.addAttribute("errors", excelEntity.getErrors());
            model.addAttribute("response", "Найденные ошибки");
            return "excel-parse-response";
        } else if (!excelEntity.getWarnings().isEmpty()) {
            excelEntity.getWarnings().sort(new RowSorter());
            model.addAttribute("warnings", excelEntity.getWarnings());
            model.addAttribute("response", "Найденные предупреждения при сохранении");
            model.addAttribute("result", excelEntity.getResult());
            return "excel-parse-response";
        } else {
            model.addAttribute("result", excelEntity.getResult());
            return "excel-parse-response";
        }
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
        /*
         * get the valid JSON object of counterAgents list
         * */
        attributes.addFlashAttribute("counterAgents", excelParseService.getJson(getAllCounterAgents()));

        return "redirect:/apache";
    }
}
