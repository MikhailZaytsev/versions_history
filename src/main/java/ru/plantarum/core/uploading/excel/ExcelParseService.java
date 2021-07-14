package ru.plantarum.core.uploading.excel;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.util.NumberUtils;
import ru.plantarum.core.entity.*;
import ru.plantarum.core.service.*;
import ru.plantarum.core.uploading.response.EntityMessage;
import ru.plantarum.core.uploading.response.ResultParsing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
//@NoArgsConstructor
public class ExcelParseService {

    private final OrganTypeService organTypeService;
    private final TradeMarkService tradeMarkService;
    private final ProductService productService;
    private final BareCodeService bareCodeService;
    private final PriceBuyPreliminarilyService priceBuyPreliminarilyService;
    private final PriceSaleService priceSaleService;
    private final CampaignService campaignService;
    private final CounterAgentService counterAgentService;

    private final ResultParsing resultParsing = new ResultParsing();

    // These lists will be saved in DB
    List<Product> productList = new ArrayList<>();
    List<BareCode> bareCodeList = new ArrayList<>();
    List<PriceBuyPreliminarily> priceBuyList = new ArrayList<>();
    List<PriceSale> priceSaleList = new ArrayList<>();

    private XSSFWorkbook book;
    private Sheet sheet;
    int rows;

    public ResultParsing parseToDataBase(ExcelEntity excelEntity, XSSFWorkbook xssfWorkbook) {

        resultParsing.clearResult();

        book = xssfWorkbook;
        sheet = book.getSheetAt(0);
        rows = sheet.getLastRowNum();

        if (excelEntity.getProducts().size() == 4) {
            parseProduct(excelEntity.getProducts());
        }

        if (excelEntity.getBareCodes().size() == 4) {
            parseBareCode(excelEntity.getBareCodes());
        }

        if (excelEntity.getPriceBuys().size() == 6) {
            parsePriceBuy(excelEntity.getPriceBuys());
        }

        if (excelEntity.getPriceSales().size() == 5) {
            parsePriceSale(excelEntity.getPriceSales());
        }
        return resultParsing;
    }

    private void parsePriceSale(List<String> priceSales) {
        int productNameIndex = Integer.parseInt(priceSales.get(0));
        int productNumberInPackIndex = Integer.parseInt(priceSales.get(2));
        String campaignName = priceSales.get(3);
        int priceIndex = Integer.parseInt(priceSales.get(4));

        for (int i = 1; i <= rows; i++) {

            Row row = sheet.getRow(i);
            PriceSale priceSale = new PriceSale();

            TradeMark tradeMark = getTradeMark(priceSales.get(1), row);

            short numberInPack = 0;
            if (isNumeric(row.getCell(productNumberInPackIndex))) {
                numberInPack = (short) row.getCell(productNumberInPackIndex).getNumericCellValue();
            }

            if (tradeMark != null && numberInPack > 0) {
                priceSale.setProduct(getProduct(productNameIndex, tradeMark.getIdTradeMark(), numberInPack, row));
            }

            priceSale.setCampaign(getCampaign(campaignName));

            if (isNumeric(row.getCell(priceIndex))) {
                priceSale.setPrice(BigDecimal.valueOf(row.getCell(priceIndex).getNumericCellValue()));
            }
            priceSale.setPropertyPrice("Просто_продажа");
            priceSale.setPackOnly(false);

            checkPriceSale(priceSale, row.getRowNum());
        }
        if (resultParsing.getPriceSaleErrors().isEmpty()) {
            resultParsing.setPriceSaleCount(priceSaleService.saveAll(priceSaleList).size());
        }
        priceSaleList.clear();
    }

    private void parsePriceBuy(List<String> priceBuys) {
        int productNameIndex = Integer.parseInt(priceBuys.get(0));
        int productNumberInPackIndex = Integer.parseInt(priceBuys.get(2));
        int priceIndex = Integer.parseInt(priceBuys.get(3));
        String campaignName = priceBuys.get(4);
        Long counterAgentId = Long.parseLong(priceBuys.get(5));

        for (int i = 1; i <= rows; i++) {
            Row row = sheet.getRow(i);
            PriceBuyPreliminarily priceBuyPreliminarily = new PriceBuyPreliminarily();

            TradeMark tradeMark = getTradeMark(priceBuys.get(1), row);

            short numberInPack = 0;
            if (isNumeric(row.getCell(productNumberInPackIndex))) {
                numberInPack = (short) row.getCell(productNumberInPackIndex).getNumericCellValue();
            }

            if (tradeMark != null && numberInPack > 0) {
                priceBuyPreliminarily.setProduct(getProduct(productNameIndex, tradeMark.getIdTradeMark(), numberInPack, row));
            }

            if (isNumeric(row.getCell(priceIndex))) {
                priceBuyPreliminarily.setPriceBuy(BigDecimal.valueOf(row.getCell(priceIndex).getNumericCellValue()));
            }

            priceBuyPreliminarily.setCampaign(getCampaign(campaignName));

            priceBuyPreliminarily.setCounterAgent(getCounterAgent(counterAgentId));

            checkPriceBuy(priceBuyPreliminarily, row.getRowNum());
        }
        if (resultParsing.getPriceBuyErrors().isEmpty()) {
            resultParsing.setPriceBuyCount(priceBuyPreliminarilyService.saveAll(priceBuyList).size());
        }
        priceBuyList.clear();
    }

    private void parseProduct(List<String> products) {
        int productNameIndex = Integer.parseInt(products.get(0));
        int productNumberInPackIndex = Integer.parseInt(products.get(3));

        for (int i = 1; i <= rows; i++) {
            Row row = sheet.getRow(i);
            Product product = new Product();

            if (isString(row.getCell(productNameIndex))) {
                product.setProductName(row.getCell(productNameIndex).getStringCellValue());
            }

            product.setTradeMark(getTradeMark(products.get(1), row));
            product.setOrganType(getOrganType(products.get(2), row));

            if (isNumeric(row.getCell(productNumberInPackIndex))) {
                product.setNumberInPack((short) row.getCell(productNumberInPackIndex).getNumericCellValue());
            }
            checkProduct(product, row.getRowNum());
        }
        if (resultParsing.getProductErrors().isEmpty()) {
            resultParsing.setProductCount(productService.saveAll(productList).size());
        }
        productList.clear();
    }

    private void parseBareCode(List<String> bareCodes) {
        int productNameIndex = Integer.parseInt(bareCodes.get(0));
        int productNumberInPackIndex = Integer.parseInt(bareCodes.get(2));
        int ean13Index = Integer.parseInt(bareCodes.get(3));

        for (int i = 1; i <= rows; i++) {
            Row row = sheet.getRow(i);
            BareCode bareCode = new BareCode();

            TradeMark tradeMark = getTradeMark(bareCodes.get(1), row);

            short numberInPack = 0;
            if (isNumeric(row.getCell(productNumberInPackIndex))) {
                numberInPack = (short) row.getCell(productNumberInPackIndex).getNumericCellValue();
            }

            if (tradeMark != null && numberInPack > 0) {
                bareCode.setProduct(getProduct(productNameIndex, tradeMark.getIdTradeMark(), numberInPack, row));
            }

            if (isNumeric(row.getCell(ean13Index))) {
                bareCode.setEan13(BigDecimal.valueOf(row.getCell(ean13Index).getNumericCellValue()));

            }
            checkBareCode(bareCode, row.getRowNum());
        }
        if (resultParsing.getBareCodeErrors().isEmpty()) {
            resultParsing.setBareCodeCount(bareCodeService.saveAll(bareCodeList).size());
        }
        bareCodeList.clear();
    }

    private void checkPriceSale(PriceSale priceSale, int row) {
        ++row;
        boolean isValid = true;
        if (priceSale.getCampaign() == null) {
            resultParsing.addError(EntityMessage.PRICE_SALE, "Кампания не установлена", row);
            return;
        }

        if (priceSale.getProduct() == null) {
            resultParsing.addError(EntityMessage.PRICE_SALE, "Товар не установлен", row);
            return;
        }
        if (priceSale.getPrice() == null) {
            isValid = false;
            resultParsing.addError(EntityMessage.PRICE_SALE, "Цена не установлена", row);
        }
        if (priceSaleService.exists(priceSale.getProduct().getIdProduct(), priceSale.getPropertyPrice(), priceSale.getCampaign().getIdCampaign())) {
            isValid = false;
            resultParsing.addError(EntityMessage.PRICE_SALE, "Такая цена_выход уже существует в базе", row);
        }
        if (isValid) {
            priceSaleList.add(priceSale);
        }
    }

    private void checkPriceBuy(PriceBuyPreliminarily price, int row) {
        ++row;
        boolean isValid = true;
        if (price.getCounterAgent() == null) {
            resultParsing.addError(EntityMessage.PRICE_BUY_PRELIMINARILY, "Не установлен контрагент", row);
            return;
        }
        if (price.getCampaign() == null) {
            resultParsing.addError(EntityMessage.PRICE_BUY_PRELIMINARILY, "Не установлена кампания", row);
            return;
        }
        if (price.getProduct() == null) {
            resultParsing.addError(EntityMessage.PRICE_BUY_PRELIMINARILY, "Не установлен продукт", row);
            return;
        }
        if (price.getPriceBuy() == null) {
            isValid = false;
            resultParsing.addError(EntityMessage.PRICE_BUY_PRELIMINARILY, "Не установлена цена", row);
        }
        if (priceBuyPreliminarilyService.exists(price.getProduct().getIdProduct(), price.getCounterAgent().getIdCounterAgent(),
                price.getCampaign().getIdCampaign())) {
            isValid = false;
            resultParsing.addError(EntityMessage.PRICE_BUY_PRELIMINARILY, "Такая цена_вход уже существует", row);
        }
        if (isValid) {
            priceBuyList.add(price);
        }
    }

    private void checkBareCode(BareCode bareCode, int row) {
        ++row;
        boolean isValid = true;
        if (bareCode.getEan13() != null) {
            if (bareCode.getProduct() == null) {
                resultParsing.addError(EntityMessage.BARE_CODE, "Не установлен продукт", row);
                return;
            }
            if (bareCodeService.exists(bareCode.getProduct().getIdProduct(), bareCode.getEan13())) {
                isValid = false;
                resultParsing.addError(EntityMessage.BARE_CODE, "Такой штрих-код для этого продукта уже есть", row);
            }
            if (bareCode.getEan13().precision() != 13) {
                isValid = false;
                resultParsing.addError(EntityMessage.BARE_CODE, "Штрих код не соответствует 13 знакам", row);
            }
            if (isValid) {
                bareCodeList.add(bareCode);
            }
        } else {
            resultParsing.addError(EntityMessage.BARE_CODE, "Поле штрих-код пустое", row);
        }
    }

    private void checkProduct(Product product, int row) {
        ++row;
        boolean isValid = true;
        if (StringUtils.isBlank(product.getProductName())) {
            isValid = false;
            resultParsing.addError(EntityMessage.PRODUCT, "Наименование не может быть пустым", row);
        }
        if (product.getOrganType() == null) {
            isValid = false;
            resultParsing.addError(EntityMessage.PRODUCT, "Не установлен тип органа", row);
        }
        if (product.getTradeMark() == null) {
            isValid = false;
            resultParsing.addError(EntityMessage.PRODUCT, "Не установлена торговая марка", row);
        }
        if (product.getNumberInPack() < 1) {
            product.setNumberInPack(null);
            resultParsing.addWarning(EntityMessage.PRODUCT, "Кол-во в упаковке меньше 1, значение убрано", row);
        }
        if (productService.exists(product.getProductName(), product.getTradeMark().getIdTradeMark(), product.getNumberInPack())) {
            isValid = false;
            resultParsing.addWarning(EntityMessage.PRODUCT, "Такой продукт уже существует", row);
        }
        if (isValid) {
            productList.add(product);
        }
    }

    private CounterAgent getCounterAgent(Long id) {
        return counterAgentService.getOne(id).orElse(null);
    }

    private Campaign getCampaign(String name) {
        if (campaignService.exists(name)) {
            return campaignService.findByName(name);
        } else {
            return null;
        }
    }

    private Product getProduct(int NameCellIndex, Long idTradeMark, short numberInPack, Row row) {
        String name;
        if (isString(row.getCell(NameCellIndex))) {
            name = row.getCell(NameCellIndex).getStringCellValue();
        } else {
            return null;
        }
        if (productService.exists(name, idTradeMark, numberInPack)) {
            return productService.findProduct(name, idTradeMark, numberInPack);
        } else {
            return null;
        }
    }

    private TradeMark getTradeMark(String mark, Row row) {
        String name;
        if (StringUtils.isNumeric(mark)) {
            if (isString(row.getCell(Integer.parseInt(mark)))) {
                name = row.getCell(Integer.parseInt(mark)).getStringCellValue();
            } else {
                return null;
            }
        } else {
            name = mark;
        }
        if (tradeMarkService.exists(name)) {
            return tradeMarkService.findByTradeMarkName(name);
        } else {
            return null;
        }
    }

    private OrganType getOrganType(String organ, Row row) {
        String name;
        if (StringUtils.isNumeric(organ)) {
            if (isString(row.getCell(Integer.parseInt(organ)))) {
                name = row.getCell(Integer.parseInt(organ)).getStringCellValue();
            } else {
                return null;
            }
        } else {
            name = organ;
        }
        if (organTypeService.exists(name)) {
            return organTypeService.findByOrganTypeName(name);
        } else {
            return null;
        }
    }

    private boolean isNumeric(Cell cell) {
        if (cell == null) {
            return false;
        } else if (cell.getCellType() == CellType.FORMULA) {
            switch (cell.getCachedFormulaResultType()) {
                case NUMERIC:
                    return true;
            }
            return false;
        } else if (cell.getCellType() == CellType.NUMERIC) {
            return true;
        }
        return false;
    }

    private boolean isString(Cell cell) {
        if (cell == null) {
            return false;
        } else if (cell.getCellType() == CellType.FORMULA) {
            switch (cell.getCachedFormulaResultType()) {
                case STRING:
                    return true;
            }
            return false;
        } else if (cell.getCellType() == CellType.STRING) {
            return true;
        }
        return false;
    }
}
