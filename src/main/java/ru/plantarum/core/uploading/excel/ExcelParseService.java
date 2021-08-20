package ru.plantarum.core.uploading.excel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.plantarum.core.entity.*;
import ru.plantarum.core.service.*;
import ru.plantarum.core.uploading.response.InvalidParse;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ExcelParseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelParseService.class);

    private final ObjectMapper objectMapper;
    private final Validator validator;

    private final ExcelBookService excelBookService;
    private final CounterAgentService counterAgentService;
    private final CampaignService campaignService;
    private final TradeMarkService tradeMarkService;
    private final OrganTypeService organTypeService;
    private final ProductService productService;
    private final BareCodeService bareCodeService;
    private final PriceBuyPreliminarilyService priceBuyPreliminarilyService;
    private final PriceSaleService priceSaleService;

    public String getJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    //TODO create error messages
    public ExcelEntity parseToDb(ExcelEntity excelEntity) {

        XSSFWorkbook book = excelBookService.getBook(excelEntity.getTempFileName());

        if (book == null) {
            addError(excelEntity, 0, Product.class, "Файл для записи был утерян");
            LOGGER.error(String.format("Файл для записи был утерян - %s", excelEntity.getTempFileName()));
            excelBookService.deleteBook(excelEntity.getTempFileName());
            return excelEntity; // error if tempFile is destroyed
        }

        Sheet sheet = book.getSheet(excelBookService.SOURCE_SHEET_NAME);
        excelEntity.setLastRowNum(sheet.getLastRowNum());

        checkExcelEntity(excelEntity);
        if (!excelEntity.getErrors().isEmpty()) {
            excelBookService.deleteBook(excelEntity.getTempFileName());
            LOGGER.error("Найдены ошибки в файле");
            return excelEntity; //error if excelEntity not correct
        }

        for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            createProduct(excelEntity, row);
            if (excelEntity.getHeadersToCols().containsKey(EntityFields.EAN13)) {
                createBareCode(excelEntity, row);
            }
            if (excelEntity.getHeadersToCols().containsKey(EntityFields.PRICE_IN)) {
                createPriceBuyPreliminarily(excelEntity, row);
            }
            if (excelEntity.getHeadersToCols().containsKey(EntityFields.PRICE_OUT)) {
                createPriceSale(excelEntity, row);
            }
        }
        if (excelEntity.getErrors().isEmpty()) {
            saveInDb(excelEntity);
        }
        excelBookService.deleteBook(excelEntity.getTempFileName());
        return excelEntity;
    }

    private void checkExcelEntity(ExcelEntity excelEntity) {
        if (!excelEntity.getHeadersToCols().containsKey(EntityFields.PRODUCT_NAME)) {
            addError(excelEntity, 0, Product.class, "Не установлена колонка для наименования продукта");
        }
        if (!excelEntity.getTradeMark().getTradeMarkName().equalsIgnoreCase("Из excel")) {
            if (excelEntity.getHeadersToCols().containsKey(EntityFields.TRADEMARK)) {
                addError(excelEntity, 0, Product.class, "Торговая марка установлена и из БД, и из excel");
            }
            TradeMark tradeMark = tradeMarkService.findByTradeMarkName(excelEntity.getTradeMark().getTradeMarkName());
            if (tradeMark != null) {
                excelEntity.setTradeMark(tradeMark);
            } else {
                addError(excelEntity, 0, Product.class, "Торговой марки с таким именем не существует в БД");
            }
        } else {
            if (!excelEntity.getHeadersToCols().containsKey(EntityFields.TRADEMARK)) {
                addError(excelEntity, 0, Product.class, "Не установлен выбор торговой марки");
            }
        }
        if (!excelEntity.getOrganType().getOrganTypeName().equalsIgnoreCase("Из excel")) {
            if (excelEntity.getHeadersToCols().containsKey(EntityFields.ORGAN_TYPE)) {
                addError(excelEntity, 0, Product.class, "Тип органа установлен и из БД, и из Excel");
            }
            OrganType organType = organTypeService.findByOrganTypeName(excelEntity.getOrganType().getOrganTypeName());
            if (organType != null) {
                excelEntity.setOrganType(organType);
            } else {
                addError(excelEntity, 0, Product.class, "Типа органа с таким именем не существует в БД");
            }
        } else {
            if (!excelEntity.getHeadersToCols().containsKey(EntityFields.ORGAN_TYPE)) {
                addError(excelEntity, 0, Product.class, "Не установлен выбор типа органа");
            }
        }
        if (excelEntity.getHeadersToCols().containsKey(EntityFields.PRICE_OUT) || excelEntity.getHeadersToCols().containsKey(EntityFields.PRICE_IN)) {
            if (campaignService.exists(excelEntity.getCampaign())) {
                excelEntity.setCampaign(campaignService.findById(excelEntity.getCampaign().getIdCampaign()));
            } else {
                addError(excelEntity, 0, PriceBuyPreliminarily.class, (String.format("Кампании с таким id: %d не существует в БД", excelEntity.getCampaign().getIdCampaign())));
                addError(excelEntity, 0, PriceSale.class, (String.format("Кампании с таким id: %d не существует в БД", excelEntity.getCampaign().getIdCampaign())));
            }
        }
        if (excelEntity.getHeadersToCols().containsKey(EntityFields.PRICE_IN)) {
            if (counterAgentService.exists(excelEntity.getCounterAgent())) {
                excelEntity.setCounterAgent(counterAgentService.findCounterAgent(excelEntity.getCounterAgent()));
            } else {
                addError(excelEntity, 0, PriceBuyPreliminarily.class,
                        String.format("Котрагента с таким именем: %s, телефоном: %s и профилем: %s не существует в БД",
                                excelEntity.getCounterAgent().getCounterAgentName(), excelEntity.getCounterAgent().getCounterAgentPhone(),
                                excelEntity.getCounterAgent().getCounterAgentProfile()));
            }
        }
        if (!excelEntity.getHeadersToCols().containsKey(EntityFields.NUMBER_IN_PACK)) {
            addWarning(excelEntity, 0, Product.class, "Не выбрано значение \"кол-во в упаковке\", все продукты сохранятся с пустым полем");
        }
    }

    private void createProduct(ExcelEntity excelEntity, Row row) {
        Product product = new Product();
        product.setProductName(excelBookService.getStringFromCell(row, excelEntity.getHeadersToCols().get(EntityFields.PRODUCT_NAME)));
        product.setTradeMark(createTradeMark(excelEntity, row));
        product.setOrganType(createOrganType(excelEntity, row));
        if (excelEntity.getHeadersToCols().containsKey(EntityFields.NUMBER_IN_PACK)) {
            product.setNumberInPack(createNumberInPack(excelEntity, row));
        }
        checkProduct(excelEntity, product, row);
    }

    private TradeMark createTradeMark(ExcelEntity excelEntity, Row row) {
        if (excelEntity.getHeadersToCols().containsKey(EntityFields.TRADEMARK)) {
            TradeMark tradeMark = excelEntity.getTradeMark();
            String tradeMarkName = excelBookService.getStringFromCell(row, excelEntity.getHeadersToCols().get(EntityFields.TRADEMARK));
            if (tradeMark.getIdTradeMark() == null ||
                    !tradeMarkName.equalsIgnoreCase(tradeMark.getTradeMarkName())) {
                tradeMark = tradeMarkService.findByTradeMarkName(tradeMarkName);
            }
            if (tradeMark == null) {
                addError(excelEntity, row.getRowNum(), Product.class, String.format("Торговой марки с названием %s в БД не существует", tradeMarkName));
            } else {
                excelEntity.setTradeMark(tradeMark);
            }
            return tradeMark;
        } else {
            return excelEntity.getTradeMark();
        }
    }

    private OrganType createOrganType(ExcelEntity excelEntity, Row row) {
        if (excelEntity.getHeadersToCols().containsKey(EntityFields.ORGAN_TYPE)) {
            OrganType organType = excelEntity.getOrganType();
            String organTypeName = excelBookService.getStringFromCell(row, excelEntity.getHeadersToCols().get(EntityFields.ORGAN_TYPE));
            if (organType.getIdOrganType() == null ||
                    !organTypeName.equalsIgnoreCase(organType.getOrganTypeName())) {
                organType = organTypeService.findByOrganTypeName(organTypeName);
            }
            if (organType == null) {
                addError(excelEntity, row.getRowNum(), Product.class, String.format("Типа органа с названием %s в БД не существует", organTypeName));
            } else {
                excelEntity.setOrganType(organType);
            }
            return organType;
        } else {
            return excelEntity.getOrganType();
        }
    }

    private Short createNumberInPack(ExcelEntity excelEntity, Row row) {
        int cellIndex = excelEntity.getHeadersToCols().get(EntityFields.NUMBER_IN_PACK);
        if (row.getCell(cellIndex).getCellType().equals(CellType.BLANK)) {
            addWarning(excelEntity, row.getRowNum(), Product.class, "Нету значения для поля \"кол-во в упаковке\"");
            return null;
        }
        try {
            return Short.valueOf(excelBookService.getStringFromCell(row, cellIndex));
        } catch (NumberFormatException e) {
            addError(excelEntity, row.getRowNum(), Product.class, "Невозможно преобразовать в \"кол-во в упаковке\" значение из ячейки");
            return null;
        }
    }

    private void checkProduct(ExcelEntity excelEntity, Product product, Row row) {
        boolean isValid = true;
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        if (!violations.isEmpty()) {
            violations.forEach(violation -> addError(excelEntity, row.getRowNum(), Product.class, violation.getMessage()));
        }
        if (StringUtils.isBlank(product.getProductName()) || product.getTradeMark() == null) {
            isValid = false;
        }
        if (isValid && productService.exists(product)) {
            addWarning(excelEntity, row.getRowNum(), Product.class,
                    String.format("Продукт с названием %s, торговой маркой %s и кол-ом в упаковке %d уже существует",
                            product.getProductName(), product.getTradeMark().getTradeMarkName(), product.getNumberInPack()));
            excelEntity.getExistsProducts().put(row.getRowNum(),
                    productService.findProduct(product));
        }
        for (Map.Entry<Integer, Product> pair : excelEntity.getProducts().entrySet()) {
            Integer key = pair.getKey();
            Product added = pair.getValue();
            int rowIndex = key + 1;
            if (added.equals(product)) {
                addError(excelEntity, row.getRowNum(), Product.class, String.format("Продукт с такими названием, торговой маркой и кол-ом в упаковке уже был добавлен ранее из строки %d", rowIndex));
            }
        }
        if (!excelEntity.getExistsProducts().containsKey(row.getRowNum())) {
            excelEntity.getProducts().put(row.getRowNum(), product);
        }
    }

    private void createBareCode(ExcelEntity excelEntity, Row row) {
        BareCode bareCode = new BareCode();
        bareCode.setEan13(excelBookService.getStringFromCell(row, excelEntity.getHeadersToCols().get(EntityFields.EAN13)));
        checkBareCode(excelEntity, bareCode, row);
    }

    private void checkBareCode(ExcelEntity excelEntity, BareCode bareCode, Row row) {
        Set<ConstraintViolation<BareCode>> violations = validator.validate(bareCode);
        if (!violations.isEmpty()) {
            for (ConstraintViolation<BareCode> violation : violations) {
                if (!violation.getMessage().equalsIgnoreCase("Значение продукта не должно быть пустым")) {
                    addError(excelEntity, row.getRowNum(), BareCode.class, violation.getMessage() + " " + violation.getRootBean().getEan13());
                }
            }
        }
        if (excelEntity.getExistsProducts().containsKey(row.getRowNum()) && bareCode.getEan13() != null) {
            Product product = excelEntity.getExistsProducts().get(row.getRowNum());
            if (bareCodeService.exists(product.getIdProduct(), bareCode.getEan13())) {
                addError(excelEntity, row.getRowNum(), BareCode.class,
                        String.format("Пара штрих-код %s и продукт %s уже существуетв  БД",
                                bareCode.getEan13(), product.getProductName()));

            } else {
                bareCode.setProduct(product);
            }
        }
        excelEntity.getBareCodes().put(row.getRowNum(), bareCode);
    }

    private void createPriceBuyPreliminarily(ExcelEntity excelEntity, Row row) {
        PriceBuyPreliminarily priceBuyPreliminarily = new PriceBuyPreliminarily();
        priceBuyPreliminarily.setCounterAgent(excelEntity.getCounterAgent());
        priceBuyPreliminarily.setCampaign(excelEntity.getCampaign());
        int cellIndex = excelEntity.getHeadersToCols().get(EntityFields.PRICE_IN);
        priceBuyPreliminarily.setPriceBuy(
                isBigDecimal(excelBookService.getStringFromCell(row, cellIndex), excelEntity, row.getRowNum(), PriceBuyPreliminarily.class)
        );
        checkPriceBuyPreliminarily(excelEntity, priceBuyPreliminarily, row);
    }

    private void checkPriceBuyPreliminarily(ExcelEntity excelEntity, PriceBuyPreliminarily priceBuyPreliminarily, Row row) {
        Set<ConstraintViolation<PriceBuyPreliminarily>> violations = validator.validate(priceBuyPreliminarily);
        if (!violations.isEmpty()) {
            for (ConstraintViolation<PriceBuyPreliminarily> violation : violations) {
                if (!violation.getMessage().equalsIgnoreCase("Значение продукта не должно быть пустым")) {
                    addError(excelEntity, row.getRowNum(), PriceBuyPreliminarily.class, violation.getMessage());
                }
            }
        }
        if (excelEntity.getExistsProducts().containsKey(row.getRowNum()) &&
                priceBuyPreliminarily.getCounterAgent() != null &&
                priceBuyPreliminarily.getCampaign() != null) {
            Product product = excelEntity.getExistsProducts().get(row.getRowNum());
            Long idCounterAgent = priceBuyPreliminarily.getCounterAgent().getIdCounterAgent();
            Long idCampaign = priceBuyPreliminarily.getCampaign().getIdCampaign();
            if (priceBuyPreliminarilyService.exists(product.getIdProduct(), idCounterAgent, idCampaign)) {
                addError(excelEntity, row.getRowNum(), PriceBuyPreliminarily.class,
                        String.format("Пара цена_вход_предв %s и продукт %s уже существуетв  БД",
                                priceBuyPreliminarily.getPriceBuy().toString(), product.getProductName()));
            } else {
                priceBuyPreliminarily.setProduct(product);
            }
        }
        excelEntity.getPriceBuyPreliminarilyMap().put(row.getRowNum(), priceBuyPreliminarily);
    }

    private void createPriceSale(ExcelEntity excelEntity, Row row) {
        PriceSale priceSale = new PriceSale();
        priceSale.setCampaign(excelEntity.getCampaign());
        priceSale.setPropertyPrice("Просто_Продажа");
        priceSale.setPackOnly(false);
        int cellIndex = excelEntity.getHeadersToCols().get(EntityFields.PRICE_OUT);
        priceSale.setPrice(
                isBigDecimal(excelBookService.getStringFromCell(row, cellIndex), excelEntity, row.getRowNum(), PriceSale.class)
        );
        checkPriceSale(excelEntity, priceSale, row);
    }

    private void checkPriceSale(ExcelEntity excelEntity, PriceSale priceSale, Row row) {
        Set<ConstraintViolation<PriceSale>> violations = validator.validate(priceSale);
        if (!violations.isEmpty()) {
            for (ConstraintViolation<PriceSale> violation : violations) {
                if (!violation.getMessage().equalsIgnoreCase("Значение продукта не должно быть пустым")) {
                    addError(excelEntity, row.getRowNum(), PriceSale.class, violation.getMessage());
                }
            }
        }
        if (excelEntity.getExistsProducts().containsKey(row.getRowNum()) && priceSale.getCampaign() != null) {
            Product product = excelEntity.getExistsProducts().get(row.getRowNum());
            Long idCampaign = priceSale.getCampaign().getIdCampaign();
            if (priceSaleService.exists(product.getIdProduct(), priceSale.getPropertyPrice(), idCampaign)) {
                addError(excelEntity, row.getRowNum(), PriceSale.class,
                        String.format("Пара цена_выход_предв %s и продукт %s уже существуетв  БД",
                                priceSale.getPrice().toString(), product.getProductName()));
            } else {
                priceSale.setProduct(product);
            }
        }
        excelEntity.getPriceSales().put(row.getRowNum(), priceSale);
    }

    private void addWarning(ExcelEntity excelEntity, int rowIndex, Class<?> c, String warning) {
        LOGGER.warn(String.format("%s, строка %d, проблема - %s", c.getSimpleName(), rowIndex, warning));
        excelEntity.getWarnings().add(new InvalidParse(++rowIndex, c.getSimpleName(), warning));
    }

    private void addError(ExcelEntity excelEntity, int rowIndex, Class<?> c, String error) {
        LOGGER.error(String.format("%s, строка %d, ошибка - %s", c.getSimpleName(), rowIndex, error));
        excelEntity.getErrors().add(new InvalidParse(++rowIndex, c.getSimpleName(), error));
    }

    private BigDecimal isBigDecimal(String value, ExcelEntity excelEntity, int rowIndex, Class<?> c) {
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            addError(excelEntity, rowIndex, c, "Невозможно преобразовать в цену значение из ячейки");
            return null;
        }
    }

    public void saveInDb(ExcelEntity excelEntity) {
        //save products
        int count = productService.saveAll(new ArrayList<>(excelEntity.getProducts().values())).size();
        excelEntity.getResult().append("Продуктов: ").append(count).append("\n");
        //add for another entities their products
        for (int rowIndex = 1; rowIndex <= excelEntity.getLastRowNum(); rowIndex++) {
            if (!excelEntity.getBareCodes().isEmpty()) {
                if (excelEntity.getBareCodes().get(rowIndex).getProduct() == null) {
                    Product product = productService.findProduct(excelEntity.getProducts().get(rowIndex));
                    excelEntity.getBareCodes().get(rowIndex).setProduct(product);
                }
            }
            if (!excelEntity.getPriceBuyPreliminarilyMap().isEmpty()) {
                if (excelEntity.getPriceBuyPreliminarilyMap().get(rowIndex).getProduct() == null) {
                    Product product = productService.findProduct(excelEntity.getProducts().get(rowIndex));
                    excelEntity.getPriceBuyPreliminarilyMap().get(rowIndex).setProduct(product);
                }
            }
            if (!excelEntity.getPriceSales().isEmpty()) {
                if (excelEntity.getPriceSales().get(rowIndex).getProduct() == null) {
                    Product product = productService.findProduct(excelEntity.getProducts().get(rowIndex));
                    excelEntity.getPriceSales().get(rowIndex).setProduct(product);
                }
            }
        }
        if (!excelEntity.getBareCodes().isEmpty()) {
            count = bareCodeService.saveAll(new ArrayList<>(excelEntity.getBareCodes().values())).size();
            excelEntity.getResult().append("Штрих-кодов: ").append(count).append("\n");
        }
        if (!excelEntity.getPriceBuyPreliminarilyMap().isEmpty()) {
            count = priceBuyPreliminarilyService.saveAll(new ArrayList<>(excelEntity.getPriceBuyPreliminarilyMap().values())).size();
            excelEntity.getResult().append("Цены вход: ").append(count).append("\n");
        }
        if (!excelEntity.getPriceSales().isEmpty()) {
            count = priceSaleService.saveAll(new ArrayList<>(excelEntity.getPriceSales().values())).size();
            excelEntity.getResult().append("Цены выход: ").append(count).append("\n");
        }
    }
}
