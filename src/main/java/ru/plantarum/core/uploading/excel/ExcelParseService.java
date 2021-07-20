package ru.plantarum.core.uploading.excel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import ru.plantarum.core.entity.*;
import ru.plantarum.core.service.*;
import ru.plantarum.core.uploading.response.InvalidParse;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelParseService {

    private final ObjectMapper objectMapper;
    private final ExcelBookService excelBookService;
    private final CounterAgentService counterAgentService;
    private final CampaignService campaignService;
    private final TradeMarkService tradeMarkService;
    private final OrganTypeService organTypeService;
    private final ProductService productService;

    //TODO rename const for better in meaning
    private final String PRODUCT_CLASS = Product.class.getSimpleName();
    private final String BARE_CODE_CLASS = BareCode.class.getSimpleName();
    private final String PRICE_BUY_PRELIMINARILY_CLASS = PriceBuyPreliminarily.class.getSimpleName();
    private final String PRICE_SALE_CLASS = PriceSale.class.getSimpleName();

    public String getJSON(List list) {
        try {
            return objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    //TODO create error messages
    public void parseToDb(ExcelEntity excelEntity) {

        XSSFWorkbook book = excelBookService.getBook(excelEntity.getTempFileName());

        if (book == null) {
            return; //error if book is destroyed
        }

        Sheet sheet = book.getSheet(excelBookService.SOURCE_SHEET_NAME);

        for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Product product = Product.builder()
                    .bareCodes(new HashSet<>())
                    .priceBuyPreliminarilies(new HashSet<>())
                    .priceSales(new HashSet<>()).build();
            product.setProductName(createProductName(excelEntity, sheet, rowIndex));
            product.setNumberInPack(createNumberInPack(excelEntity, sheet, rowIndex));
            product.setTradeMark(createTradeMark(excelEntity, sheet, rowIndex));
            product.setOrganType(createOrganType(excelEntity, sheet, rowIndex));
            BareCode bareCode = createBareCode(excelEntity, sheet, rowIndex);
            if (bareCode != null) {
                product.getBareCodes().add(bareCode);
            }
            PriceBuyPreliminarily priceBuyPreliminarily = createPriceBuyPreliminarily(excelEntity, sheet, rowIndex);
            if (priceBuyPreliminarily != null) {
                product.getPriceBuyPreliminarilies().add(priceBuyPreliminarily);
            }
            PriceSale priceSale = createPriceSale(excelEntity, sheet, rowIndex);
            if (priceSale != null) {
                product.getPriceSales().add(priceSale);
            }
            if(checkProduct(excelEntity, product, rowIndex)) {
                excelEntity.getProducts().add(product);
            }
        }
        if (excelEntity.getErrors().isEmpty()) {
            productService.saveAll(excelEntity.getProducts());
        }
    }

    private boolean checkProduct(ExcelEntity excelEntity, Product product, int rowIndex) {
        boolean isValid = true;
        if (StringUtils.isBlank(product.getProductName())) {
            addError(excelEntity, rowIndex, PRODUCT_CLASS, "Не установилось наименование продукта");
            isValid = false;
        }
        if (product.getOrganType() == null) {
            addError(excelEntity, rowIndex, PRODUCT_CLASS, "Не установился тип органа");
            isValid = false;
        }
        if (product.getTradeMark() == null) {
            addError(excelEntity, rowIndex, PRODUCT_CLASS, "Не установилась торговая марка");
            isValid = false;
        }
        if (!isValid) {
            return false;
        }
        Long productTradeMarkId = product.getTradeMark().getIdTradeMark();
        for (Product added: excelEntity.getProducts()) {
            Long addedTradeMarkId = added.getTradeMark().getIdTradeMark();
            if (addedTradeMarkId.equals(productTradeMarkId) &&
            added.getProductName().equals(product.getProductName()) &&
            added.getNumberInPack().equals(product.getNumberInPack())) {
                addError(excelEntity, rowIndex, PRODUCT_CLASS, "Продукт с такими названием, торговой маркой и кол-ом в упаковке уже был добавлен ранее");
                return false;
            }
        }
        if (productService.exists(product.getProductName(), product.getTradeMark().getIdTradeMark(), product.getNumberInPack())) {
            //TODO make decision what to do with existing product
            addWarning(excelEntity, rowIndex, PRODUCT_CLASS,
                    String.format("Продукт с названием %s, торговой маркой %s и кол-ом в упаковке %d уже существует",
                            product.getProductName(), product.getTradeMark().getTradeMarkName(), product.getNumberInPack()));
            return false;
        } else {
            setProduct(product);
            return true;
        }
    }

    private void setProduct(Product product) {
        if (!product.getBareCodes().isEmpty()) {
            for (BareCode bareCode : product.getBareCodes()) {
                bareCode.setProduct(product);
            }
        }
        if (!product.getPriceBuyPreliminarilies().isEmpty()) {
            for (PriceBuyPreliminarily priceBuyPreliminarily : product.getPriceBuyPreliminarilies()) {
                priceBuyPreliminarily.setProduct(product);
            }
        }
        if (!product.getPriceSales().isEmpty()) {
            for (PriceSale priceSale : product.getPriceSales()) {
                priceSale.setProduct(product);
            }
        }
    }

    private Short createNumberInPack(ExcelEntity excelEntity, Sheet sheet, int rowIndex) {
        if (!excelEntity.getHeadersToCols().containsKey(EntityFields.NUMBER_IN_PACK)) {
            return null;
        }
        int cellIndex = excelEntity.getHeadersToCols().get(EntityFields.NUMBER_IN_PACK);
        BigDecimal result = isBigDecimal(excelBookService.getStringFromCell(sheet.getRow(rowIndex), cellIndex));
        if (result == null) {
            addError(excelEntity, rowIndex, PRODUCT_CLASS, "в клетке кол-во в упаковке неверный тип данных", cellIndex);
            return null;
        } else {
            int complete = result.intValue();
            BigDecimal compare = new BigDecimal("0.0");
            BigDecimal left = new BigDecimal(result.subtract(new BigDecimal(complete)).toPlainString());
            if (left.compareTo(compare) > 0) {
                addError(excelEntity, rowIndex, PRODUCT_CLASS, "в клетке кол-во в упаковке неверный формат", cellIndex);
                return null;
            } else {
                short numberInPack = result.shortValueExact();
                if (numberInPack > 0) {
                    return numberInPack;
                } else {
                    addError(excelEntity, rowIndex, PRODUCT_CLASS, "в клетке кол-во в упаковке значение меньше 1", cellIndex);
                    return null;
                }
            }
        }
    }

    private OrganType createOrganType(ExcelEntity excelEntity, Sheet sheet, int rowIndex) {
        if (!excelEntity.getHeadersToCols().containsKey(EntityFields.ORGAN_TYPE)) {
            if (organTypeService.exists(excelEntity.getIdOrganType())) {
                return organTypeService.findById(excelEntity.getIdOrganType());
            } else {
                addError(excelEntity, rowIndex, PRODUCT_CLASS, String.format("Типа органа с id %d в БД не существует", excelEntity.getIdOrganType()));
                return null;
            }
        } else {
            int cellIndex = excelEntity.getHeadersToCols().get(EntityFields.ORGAN_TYPE);
            String value = excelBookService.getStringFromCell(sheet.getRow(rowIndex), cellIndex);
            if (organTypeService.exists(value)) {
                return organTypeService.findByOrganTypeName(value);
            } else {
                addError(excelEntity, rowIndex, PRODUCT_CLASS, String.format("Типа органа с названием %s в БД не существует", value), cellIndex);
                return null;
            }
        }
    }

    private String createProductName(ExcelEntity excelEntity, Sheet sheet, int rowIndex) {
        //TODO high chances of NPE
        int cellIndex = excelEntity.getHeadersToCols().get(EntityFields.PRODUCT_NAME);
        Cell cell = sheet.getRow(rowIndex).getCell(cellIndex);
        if (cell.getCellType() == CellType.STRING) {
            String name = cell.getStringCellValue();
            if (name.length() < 255) {
                return name;
            } else {
                addError(excelEntity, rowIndex, PRODUCT_CLASS, "В клетке имя_продукта слишком большое значение", cellIndex);
                return null;
            }
        } else {
            addError(excelEntity, rowIndex, PRODUCT_CLASS, "В клетке имя_продукта неверный тип данных", cellIndex);
            return null;
        }
    }

    private TradeMark createTradeMark(ExcelEntity excelEntity, Sheet sheet, int rowIndex) {
        if (!excelEntity.getHeadersToCols().containsKey(EntityFields.TRADEMARK)) {
            if (tradeMarkService.exists(excelEntity.getIdTradeMark())) {
                return tradeMarkService.findById(excelEntity.getIdTradeMark());
            } else {
                addError(excelEntity, rowIndex, PRODUCT_CLASS, String.format("Торговой марки с id %d в БД не существует", excelEntity.getIdTradeMark()));
                return null;
            }
        } else {
            int cellIndex = excelEntity.getHeadersToCols().get(EntityFields.TRADEMARK);
            String value = excelBookService.getStringFromCell(sheet.getRow(rowIndex), cellIndex);
            if (tradeMarkService.exists(value)) {
                return tradeMarkService.findByTradeMarkName(value);
            } else {
                addError(excelEntity, rowIndex, PRODUCT_CLASS, String.format("Торговой марки с названием %s в БД не существует", value), cellIndex);
                return null;
            }
        }
    }

    private PriceSale createPriceSale(ExcelEntity excelEntity, Sheet sheet, int rowIndex) {
        if (!excelEntity.getHeadersToCols().containsKey(EntityFields.PRICE_OUT)) {
            return null;
        }
        boolean isValid = true;
        PriceSale priceSale = new PriceSale();
        priceSale.setPackOnly(false);
        priceSale.setPropertyPrice("Просто_Продажа");
        int cellIndex = excelEntity.getHeadersToCols().get(EntityFields.PRICE_OUT);
        BigDecimal price = isBigDecimal(excelBookService.getStringFromCell(sheet.getRow(rowIndex), cellIndex));
        if (price == null) {
            addError(excelEntity, rowIndex, PRICE_SALE_CLASS, "В клетке цена_выход значение неправильного типа");
            isValid = false;
        } else if (price.compareTo(new BigDecimal("0")) < 0) {
            addError(excelEntity, rowIndex, PRICE_SALE_CLASS, "В клетке цена_выход значение меньше 0", cellIndex);
            isValid = false;
        } else {
            priceSale.setPrice(price.setScale(2, RoundingMode.HALF_UP));
        }
        Campaign campaign = campaignService.getOne(excelEntity.getIdCampaign()).orElse(null);
        if (campaign == null) {
            addError(excelEntity, rowIndex, PRICE_SALE_CLASS, String.format("Кампания с id %d не записана в БД", excelEntity.getIdCampaign()));
            isValid = false;
        } else {
            priceSale.setCampaign(campaign);
        }
        if (isValid) {
            return priceSale;
        } else {
            return null;
        }
    }

    private BareCode createBareCode(ExcelEntity excelEntity, Sheet sheet, int rowIndex) {
        if (!excelEntity.getHeadersToCols().containsKey(EntityFields.EAN13)) {
            return null;
        }
        int cellIndex = excelEntity.getHeadersToCols().get(EntityFields.EAN13);
        BareCode bareCode = new BareCode();
        BigDecimal ean13 = isBigDecimal(excelBookService.getStringFromCell(sheet.getRow(rowIndex), cellIndex));
        if (ean13 == null) {
            addError(excelEntity, rowIndex, BARE_CODE_CLASS, "В клетке штрих-кода значение неправильного типа", cellIndex);
            return null;
        } else if (ean13.precision() != 13) {
            addError(excelEntity, rowIndex, BARE_CODE_CLASS, "В штрих-коде меньше 13 цифр", cellIndex);
            return null;
        } else {
            bareCode.setEan13(ean13);
            return bareCode;
        }
    }

    private PriceBuyPreliminarily createPriceBuyPreliminarily(ExcelEntity excelEntity, Sheet sheet, int rowIndex) {
        if (!excelEntity.getHeadersToCols().containsKey(EntityFields.PRICE_IN)) {
            return null;
        }
        boolean isValid = true;
        int cellIndex = excelEntity.getHeadersToCols().get(EntityFields.PRICE_IN);
        PriceBuyPreliminarily priceBuyPreliminarily = new PriceBuyPreliminarily();
        Campaign campaign = campaignService.getOne(excelEntity.getIdCampaign()).orElse(null);
        if (campaign == null) {
            addError(excelEntity, rowIndex, PRICE_BUY_PRELIMINARILY_CLASS, String.format("Кампания с id %d не записана в БД", excelEntity.getIdCampaign()));
            isValid = false;
        } else {
            priceBuyPreliminarily.setCampaign(campaign);
        }
        CounterAgent counterAgent = counterAgentService.getOne(excelEntity.getIdCounterAgent()).orElse(null);
        if (counterAgent == null) {
            addError(excelEntity, rowIndex, PRICE_BUY_PRELIMINARILY_CLASS, String.format("Контрагент с id %d не записан в БД", excelEntity.getIdCounterAgent()));
            isValid = false;
        } else {
            priceBuyPreliminarily.setCounterAgent(counterAgent);
        }
        BigDecimal price = isBigDecimal(excelBookService.getStringFromCell(sheet.getRow(rowIndex), cellIndex));
        if (price == null) {
            addError(excelEntity, rowIndex, PRICE_BUY_PRELIMINARILY_CLASS, "В клетке цена_вход значение неправильного типа", cellIndex);
            isValid = false;
        } else if (price.compareTo(new BigDecimal("0")) < 0) {
            addError(excelEntity, rowIndex, PRICE_BUY_PRELIMINARILY_CLASS, "В клетке цена_вход значение меньше 0", cellIndex);
            isValid = false;
        } else {
            priceBuyPreliminarily.setPriceBuy(price.setScale(2, RoundingMode.HALF_UP));
        }
        if (isValid) {
            return priceBuyPreliminarily;
        } else {
            return null;
        }
    }

    private void addWarning(ExcelEntity excelEntity, int rowIndex, String entity, String warning) {
        if (excelEntity.getWarnings().containsKey(rowIndex)) {
            excelEntity.getWarnings().get(rowIndex).add(new InvalidParse(entity, warning));
        } else {
            List<InvalidParse> invalidParseList = new ArrayList<>();
            invalidParseList.add(new InvalidParse(entity, warning));
            excelEntity.getWarnings().put(rowIndex, invalidParseList);
        }
    }

    private void addError(ExcelEntity excelEntity, int rowIndex, String entity, String error) {
        if (excelEntity.getErrors().containsKey(rowIndex)) {
            excelEntity.getErrors().get(rowIndex).add(new InvalidParse(entity, error));
        } else {
            List<InvalidParse> invalidParseList = new ArrayList<>();
            invalidParseList.add(new InvalidParse(entity, error));
            excelEntity.getErrors().put(rowIndex, invalidParseList);
        }
    }

    private void addError(ExcelEntity excelEntity, int rowIndex, String entity, String error, int cellIndex) {
        if (excelEntity.getErrors().containsKey(rowIndex)) {
            excelEntity.getErrors().get(rowIndex).add(new InvalidParse(entity, error, cellIndex));
        } else {
            List<InvalidParse> invalidParseList = new ArrayList<>();
            invalidParseList.add(new InvalidParse(entity, error, cellIndex));
            excelEntity.getErrors().put(rowIndex, invalidParseList);
        }
    }

    private BigDecimal isBigDecimal(String value) {
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
