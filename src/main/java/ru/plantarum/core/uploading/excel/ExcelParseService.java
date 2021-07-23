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
import java.util.*;

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
    private final BareCodeService bareCodeService;
    private final PriceBuyPreliminarilyService priceBuyPreliminarilyService;
    private final PriceSaleService priceSaleService;

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
            //TODO create PRODUCT
            //TODO create BARE_CODE
            //TODO create PRICE_BUY_PRELIMINARILY
            //TODO create PRICE_SALE
            Row row = sheet.getRow(rowIndex);
            createProduct(excelEntity, row);
            createBareCode(excelEntity, row);
            createPriceBuyPreliminarily(excelEntity, row);
            createPriceSale(excelEntity, row);
//            Product product = Product.builder()
//                    .bareCodes(new HashSet<>())
//                    .priceBuyPreliminarilies(new HashSet<>())
//                    .priceSales(new HashSet<>()).build();
//            product.setProductName(createProductName(excelEntity, sheet, rowIndex));
//            product.setNumberInPack(createNumberInPack(excelEntity, sheet, rowIndex));
//            product.setTradeMark(createTradeMark(excelEntity, sheet, rowIndex));
//            product.setOrganType(createOrganType(excelEntity, sheet, rowIndex));
//            BareCode bareCode = createBareCode(excelEntity, sheet, rowIndex);
//            if (bareCode != null) {
//                product.getBareCodes().add(bareCode);
//            }
//            PriceBuyPreliminarily priceBuyPreliminarily = createPriceBuyPreliminarily(excelEntity, sheet, rowIndex);
//            if (priceBuyPreliminarily != null) {
//                product.getPriceBuyPreliminarilies().add(priceBuyPreliminarily);
//            }
//            PriceSale priceSale = createPriceSale(excelEntity, sheet, rowIndex);
//            if (priceSale != null) {
//                product.getPriceSales().add(priceSale);
//            }
//            if(checkProduct(excelEntity, product, rowIndex)) {
//                excelEntity.getProducts().add(product);
//            }
        }
        if (excelEntity.getErrors().isEmpty()) {
            //save entities if errors is empty
//            productService.saveAll(excelEntity.getProducts());
        }
        System.err.println("here");
    }

    private void createProduct(ExcelEntity excelEntity, Row row) {
        Product product = new Product();
        product.setProductName(createProductName(excelEntity, row));
        product.setTradeMark(createTradeMark(excelEntity, row));
        product.setOrganType(createOrganType(excelEntity, row));
        product.setNumberInPack(createNumberInPack(excelEntity, row));
        checkProduct(excelEntity, product, row);
    }

    private void createBareCode(ExcelEntity excelEntity, Row row) {
        if (excelEntity.getHeadersToCols().containsKey(EntityFields.EAN13)) {
            BareCode bareCode = new BareCode();
            bareCode.setEan13(createEan13(excelEntity, row));
            checkBareCode(excelEntity, bareCode, row);
        }
    }

    private void createPriceBuyPreliminarily(ExcelEntity excelEntity, Row row) {
        if (excelEntity.getHeadersToCols().containsKey(EntityFields.PRICE_IN)) {
            PriceBuyPreliminarily priceBuyPreliminarily = new PriceBuyPreliminarily();
            priceBuyPreliminarily.setCounterAgent(
                    counterAgentService.getOne(excelEntity.getIdCounterAgent()).orElse(null)
            );
            priceBuyPreliminarily.setCampaign(
                    campaignService.getOne(excelEntity.getIdCampaign()).orElse(null)
            );
            int cellIndex = excelEntity.getHeadersToCols().get(EntityFields.PRICE_IN);
            priceBuyPreliminarily.setPriceBuy(createPrice(excelEntity, PRICE_BUY_PRELIMINARILY_CLASS, row, cellIndex));
            checkPriceBuyPreliminarily(excelEntity, priceBuyPreliminarily, row);
        }
    }

    private void createPriceSale(ExcelEntity excelEntity, Row row) {
        if (excelEntity.getHeadersToCols().containsKey(EntityFields.PRICE_OUT)) {
            PriceSale priceSale = new PriceSale();
            priceSale.setCampaign(
                    campaignService.getOne(excelEntity.getIdCampaign()).orElse(null)
            );
            priceSale.setPropertyPrice("Просто_Продажа");
            priceSale.setPackOnly(false);
            int cellIndex = excelEntity.getHeadersToCols().get(EntityFields.PRICE_OUT);
            priceSale.setPrice(createPrice(excelEntity, PRICE_SALE_CLASS, row, cellIndex));
            checkPriceSale(excelEntity, priceSale, row);

        }
    }

    private void checkPriceSale(ExcelEntity excelEntity, PriceSale priceSale, Row row) {
        boolean isValid = true;
        if (priceSale.getCampaign() == null) {
            addError(excelEntity, row.getRowNum(), PRICE_BUY_PRELIMINARILY_CLASS,
                    "Не установлена кампания");
            isValid = false;
        }
        if (priceSale.getPrice() == null) {
            addError(excelEntity, row.getRowNum(), PRICE_BUY_PRELIMINARILY_CLASS,
                    "Не установлена цена");
            isValid = false;
        }
        if (!isValid) {
            return;
        }
        if (excelEntity.getExistsProducts().containsKey(row.getRowNum())) {
            Product product = excelEntity.getExistsProducts().get(row.getRowNum());
            Long idCampaign = priceSale.getCampaign().getIdCampaign();
            if (priceSaleService.exists(product.getIdProduct(), priceSale.getPropertyPrice(), idCampaign)) {
                addError(excelEntity, row.getRowNum(), PRICE_SALE_CLASS,
                        String.format("Пара цена_вход_предв %s и продукт %s уже существуетв  БД",
                                priceSale.getPrice().toString(), product.getProductName()));
            }
            isValid = false;
        }
        if (isValid) {
            excelEntity.getPriceSales().put(row.getRowNum(), priceSale);
        }
    }

    private void checkPriceBuyPreliminarily(ExcelEntity excelEntity, PriceBuyPreliminarily priceBuyPreliminarily, Row row) {
        boolean isValid = true;
        if (priceBuyPreliminarily.getCounterAgent() == null) {
            addError(excelEntity, row.getRowNum(), PRICE_BUY_PRELIMINARILY_CLASS,
                    "Не установлен контрагент");
            isValid = false;
        }
        if (priceBuyPreliminarily.getCampaign() == null) {
            addError(excelEntity, row.getRowNum(), PRICE_BUY_PRELIMINARILY_CLASS,
                    "Не установлена кампания");
            isValid = false;
        }
        if (priceBuyPreliminarily.getPriceBuy() == null) {
            addError(excelEntity, row.getRowNum(), PRICE_BUY_PRELIMINARILY_CLASS,
                    "Не установлена цена");
            isValid = false;
        }
        if (!isValid) {
            return;
        }
        if (excelEntity.getExistsProducts().containsKey(row.getRowNum())) {
            Product product = excelEntity.getExistsProducts().get(row.getRowNum());
            Long idCounterAgent = priceBuyPreliminarily.getCounterAgent().getIdCounterAgent();
            Long idCampaign = priceBuyPreliminarily.getCampaign().getIdCampaign();
            if (priceBuyPreliminarilyService.exists(product.getIdProduct(), idCounterAgent, idCampaign)) {
                addError(excelEntity, row.getRowNum(), PRICE_BUY_PRELIMINARILY_CLASS,
                        String.format("Пара цена_вход_предв %s и продукт %s уже существуетв  БД",
                                priceBuyPreliminarily.getPriceBuy().toString(), product.getProductName()));
            }
            isValid = false;
        }
        if (isValid) {
            excelEntity.getPriceBuyPreliminarilyMap().put(row.getRowNum(), priceBuyPreliminarily);
        }
    }

    private void checkBareCode(ExcelEntity excelEntity, BareCode bareCode, Row row) {
        boolean isValid = true;
        if (bareCode.getEan13() == null) {
            addError(excelEntity, row.getRowNum(), BARE_CODE_CLASS, "Не установилось значение штрих-кода");
            isValid = false;
        }
        if (!isValid) {
            return;
        }
        if (excelEntity.getExistsProducts().containsKey(row.getRowNum())) {
            Product product = excelEntity.getExistsProducts().get(row.getRowNum());
            if (bareCodeService.exists(product.getIdProduct(), bareCode.getEan13())) {
                addError(excelEntity, row.getRowNum(), BARE_CODE_CLASS,
                        String.format("Пара штрих-код %s и продукт %s уже существуетв  БД",
                                bareCode.getEan13().toString(), product.getProductName()));
            }
            isValid = false;
        }
        if (isValid) {
            excelEntity.getBareCodes().put(row.getRowNum(), bareCode);
        }
    }

    private void checkProduct(ExcelEntity excelEntity, Product product, Row row) {
        boolean isValid = true;
        if (StringUtils.isBlank(product.getProductName())) {
            addError(excelEntity, row.getRowNum(), PRODUCT_CLASS, "Не установилось наименование продукта");
            isValid = false;
        }
        if (product.getOrganType() == null) {
            addError(excelEntity, row.getRowNum(), PRODUCT_CLASS, "Не установился тип органа");
            isValid = false;
        }
        if (product.getTradeMark() == null) {
            addError(excelEntity, row.getRowNum(), PRODUCT_CLASS, "Не установилась торговая марка");
            isValid = false;
        }
        if (!isValid) {
            return;
        }
        if (productService.exists(product)) {
            addWarning(excelEntity, row.getRowNum(), PRODUCT_CLASS,
                    String.format("Продукт с названием %s, торговой маркой %s и кол-ом в упаковке %d уже существует",
                            product.getProductName(), product.getTradeMark().getTradeMarkName(), product.getNumberInPack()));
            isValid = false;
            excelEntity.getExistsProducts().put(row.getRowNum(),
                    productService.findProduct(product));
        }
        for (Map.Entry<Integer, Product> pair : excelEntity.getProducts().entrySet()) {
            Integer key = pair.getKey();
            Product added = pair.getValue();
            if (added.getTradeMark().equals(product.getTradeMark()) &&
                    added.getProductName().equals(product.getProductName()) &&
                    added.getNumberInPack().equals(product.getNumberInPack())) {
                addError(excelEntity, key, PRODUCT_CLASS, "Продукт с такими названием, торговой маркой и кол-ом в упаковке уже был добавлен ранее");
                addError(excelEntity, row.getRowNum(), PRODUCT_CLASS, "Продукт с такими названием, торговой маркой и кол-ом в упаковке уже был добавлен ранее");
                isValid = false;
            }
        }
        if (isValid) {
            excelEntity.getProducts().put(row.getRowNum(), product);
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

    private Short createNumberInPack(ExcelEntity excelEntity, Row row) {
        if (!excelEntity.getHeadersToCols().containsKey(EntityFields.NUMBER_IN_PACK)) {
            return null;
        }
        int cellIndex = excelEntity.getHeadersToCols().get(EntityFields.NUMBER_IN_PACK);
        if (row.getCell(cellIndex).getCellType().equals(CellType.BLANK)) {
            addWarning(excelEntity, row.getRowNum(), PRODUCT_CLASS, "в клетке кол-во в упаковке пустое значение");
            return null;
        }
        BigDecimal result = isBigDecimal(excelBookService.getStringFromCell(row, cellIndex));
        if (result == null) {
            addError(excelEntity, row.getRowNum(), PRODUCT_CLASS, "в клетке кол-во в упаковке неверный тип данных");
            return null;
        } else {
            int complete = result.intValue();
            BigDecimal compare = new BigDecimal("0.0");
            BigDecimal left = new BigDecimal(result.subtract(new BigDecimal(complete)).toPlainString());
            if (left.compareTo(compare) > 0) {
                addError(excelEntity, row.getRowNum(), PRODUCT_CLASS, "в клетке кол-во в упаковке неверный формат");
                return null;
            } else {
                short numberInPack = result.shortValueExact();
                if (numberInPack > 0) {
                    return numberInPack;
                } else {
                    addError(excelEntity, row.getRowNum(), PRODUCT_CLASS, "в клетке кол-во в упаковке значение меньше 1");
                    return null;
                }
            }
        }
    }

    private OrganType createOrganType(ExcelEntity excelEntity, Row row) {
        if (!excelEntity.getHeadersToCols().containsKey(EntityFields.ORGAN_TYPE)) {
            if (organTypeService.exists(excelEntity.getIdOrganType())) {
                return organTypeService.findById(excelEntity.getIdOrganType());
            } else {
                addError(excelEntity, row.getRowNum(), PRODUCT_CLASS, String.format("Типа органа с id %d в БД не существует", excelEntity.getIdOrganType()));
                return null;
            }
        } else {
            int cellIndex = excelEntity.getHeadersToCols().get(EntityFields.ORGAN_TYPE);
            String value = excelBookService.getStringFromCell(row, cellIndex);
            if (organTypeService.exists(value)) {
                return organTypeService.findByOrganTypeName(value);
            } else {
                addError(excelEntity, row.getRowNum(), PRODUCT_CLASS, String.format("Типа органа с названием %s в БД не существует", value));
                return null;
            }
        }
    }

    private String createProductName(ExcelEntity excelEntity, Row row) {
        //TODO high chances of NPE
        if (!excelEntity.getHeadersToCols().containsKey(EntityFields.PRODUCT_NAME)) {
            return null;
        }
        int cellIndex = excelEntity.getHeadersToCols().get(EntityFields.PRODUCT_NAME);
        Cell cell = row.getCell(cellIndex);
        if (cell.getCellType() == CellType.STRING) {
            String name = cell.getStringCellValue();
            if (name.length() < 255) {
                return name;
            } else {
                addError(excelEntity, row.getRowNum(), PRODUCT_CLASS, "В клетке имя_продукта слишком большое значение");
                return null;
            }
        } else {
            addError(excelEntity, row.getRowNum(), PRODUCT_CLASS, "В клетке имя_продукта неверный тип данных");
            return null;
        }
    }

    private TradeMark createTradeMark(ExcelEntity excelEntity, Row row) {
        if (!excelEntity.getHeadersToCols().containsKey(EntityFields.TRADEMARK)) {
            if (tradeMarkService.exists(excelEntity.getIdTradeMark())) {
                return tradeMarkService.findById(excelEntity.getIdTradeMark());
            } else {
                addError(excelEntity, row.getRowNum(), PRODUCT_CLASS, String.format("Торговой марки с id %d в БД не существует", excelEntity.getIdTradeMark()));
                return null;
            }
        } else {
            int cellIndex = excelEntity.getHeadersToCols().get(EntityFields.TRADEMARK);
            String value = excelBookService.getStringFromCell(row, cellIndex);
            if (tradeMarkService.exists(value)) {
                return tradeMarkService.findByTradeMarkName(value);
            } else {
                addError(excelEntity, row.getRowNum(), PRODUCT_CLASS, String.format("Торговой марки с названием %s в БД не существует", value));
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
            addError(excelEntity, rowIndex, PRICE_SALE_CLASS, "В клетке цена_выход значение меньше 0");
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

    private BigDecimal createEan13(ExcelEntity excelEntity, Row row) {
        int cellIndex = excelEntity.getHeadersToCols().get(EntityFields.EAN13);
        BigDecimal ean13 = isBigDecimal(excelBookService.getStringFromCell(row, cellIndex));
        if (ean13 == null) {
            addError(excelEntity, row.getRowNum(), BARE_CODE_CLASS, "В клетке штрих-кода значение неправильного типа");
            return null;
        } else if (ean13.precision() != 13 || ean13.scale() > 0) {
            addError(excelEntity, row.getRowNum(), BARE_CODE_CLASS, "Неверный формат штрих-кода");
            return null;
        } else {
            return ean13;
        }
    }

    private BigDecimal createPrice(ExcelEntity excelEntity, String entityClass, Row row, int cellIndex) {
        BigDecimal price = isBigDecimal(excelBookService.getStringFromCell(row, cellIndex));
        if (price == null) {
            addError(excelEntity, row.getRowNum(), entityClass, "В клетке цены значение неправильного типа");
            return null;
        } else if (price.compareTo(new BigDecimal("0")) < 0) {
            addError(excelEntity, row.getRowNum(), entityClass, "В клетке цены значение меньше 0");
            return null;
        } else {
            return price;
        }
    }

    private Product mergeProducts(ExcelEntity excelEntity, Product product, int rowIndex) {
        //TODO may produce NPE
        //TODO confrontation with product editing methods
        //may be DTO could help, or just save entities by itself, not all in products
        Product oldProduct = productService.findProduct(product);
        product.setIdProduct(oldProduct.getIdProduct());
        product.setProductComment(oldProduct.getProductComment());
        product.setInactive(oldProduct.getInactive());
        product.setOperationRows(oldProduct.getOperationRows());
        product.getBareCodes().addAll(oldProduct.getBareCodes());
        product.getPriceBuyPreliminarilies().addAll(oldProduct.getPriceBuyPreliminarilies());
        product.getPriceSales().addAll(oldProduct.getPriceSales());
//        if (product.getBareCodes() != null && !product.getBareCodes().isEmpty()) {
//            oldProduct.getBareCodes().addAll(product.getBareCodes());
//            BareCode testCode = new BareCode();
//            testCode.setEan13(new BigDecimal("1234569876666"));
//            oldProduct.getBareCodes().add(testCode);
//        }
//        if (product.getPriceBuyPreliminarilies() != null && !product.getPriceBuyPreliminarilies().isEmpty()) {
//            oldProduct.getPriceBuyPreliminarilies().addAll(product.getPriceBuyPreliminarilies());
//        }
//        if (product.getPriceSales() != null && !product.getPriceSales().isEmpty()) {
//            oldProduct.getPriceSales().addAll(product.getPriceSales());
//        }
        if (!oldProduct.getOrganType().equals(product.getOrganType())) {
            product.setOrganType(oldProduct.getOrganType());
            addWarning(excelEntity, rowIndex, PRODUCT_CLASS,
                    "У старого product и нового не совпадают organType, оставлен старый вариант");
        }
        return product;
//        return oldProduct;
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

    private BigDecimal isBigDecimal(String value) {
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
