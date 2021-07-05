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

    // These lists will be saved in DB
    List<Product> productList = new ArrayList<>();
    List<BareCode> bareCodeList = new ArrayList<>();
    List<PriceBuyPreliminarily> priceBuyList = new ArrayList<>();
    List<PriceSale> priceSaleList = new ArrayList<>();

    private XSSFWorkbook book;
    private Sheet sheet;
    int rows;

    public void parseToDataBase(ExcelEntity excelEntity, XSSFWorkbook xssfWorkbook) {

        book = xssfWorkbook;
        sheet = book.getSheetAt(0);
        rows = sheet.getLastRowNum();

        if (excelEntity.getProducts().size() == 4) {
            parseProduct(excelEntity.getProducts());
        }

        if (excelEntity.getBareCodes().size() == 2) {
            parseBareCode(excelEntity.getBareCodes());
        }

        if (excelEntity.getPriceBuys().size() == 4) {
            parsePriceBuy(excelEntity.getPriceBuys());
        }

        if (excelEntity.getPriceSales().size() == 3) {
            parsePriceSale(excelEntity.getPriceSales());
        }
    }

    private void parsePriceSale(List<String> priceSales) {
        int productNameIndex = Integer.parseInt(priceSales.get(0));
        String campaignName = priceSales.get(1);
        int priceIndex = Integer.parseInt(priceSales.get(2));

        for (int i = 1; i <= rows; i++) {
            Row row = sheet.getRow(i);
            PriceSale priceSale = new PriceSale();

            if (isString(row.getCell(productNameIndex))) {
                priceSale.setProduct(getProduct(productNameIndex, row));
            }
            priceSale.setCampaign(getCampaign(campaignName));
            if (isNumeric(row.getCell(priceIndex))) {
                priceSale.setPrice(BigDecimal.valueOf(row.getCell(priceIndex).getNumericCellValue()));
            }
            priceSale.setPropertyPrice("Просто_продажа");
            priceSale.setPackOnly(false);

            checkPriceSale(priceSale);
        }

        priceSaleService.saveAll(priceSaleList);
        priceSaleList.clear();
    }

    private void parsePriceBuy(List<String> priceBuys) {
        int productNameIndex = Integer.parseInt(priceBuys.get(0));
        int priceIndex = Integer.parseInt(priceBuys.get(1));
        String campaignName = priceBuys.get(2);
        Long counterAgentId = Long.parseLong(priceBuys.get(3));

        for (int i = 1; i <= rows; i++) {
            Row row = sheet.getRow(i);
            PriceBuyPreliminarily priceBuyPreliminarily = new PriceBuyPreliminarily();

            if (isString(row.getCell(productNameIndex))) {
                priceBuyPreliminarily.setProduct(getProduct(productNameIndex, row));
            }
            if (isNumeric(row.getCell(priceIndex))) {
                priceBuyPreliminarily.setPriceBuy(BigDecimal.valueOf(row.getCell(priceIndex).getNumericCellValue()));
            }
            priceBuyPreliminarily.setCampaign(getCampaign(campaignName));
            priceBuyPreliminarily.setCounterAgent(getCounterAgent(counterAgentId));

            checkPriceBuy(priceBuyPreliminarily);
        }

        priceBuyPreliminarilyService.saveAll(priceBuyList);
        priceBuyList.clear();
    }

    private void parseProduct(List<String> products) {
        int productNameIndex = Integer.parseInt(products.get(0));
        int productCountIndex = Integer.parseInt(products.get(3));

        for (int i = 1; i <= rows; i++) {
            Row row = sheet.getRow(i);
            Product product = new Product();

            if (isString(row.getCell(productNameIndex))) {
                product.setProductName(row.getCell(productNameIndex).getStringCellValue());
            }

            product.setTradeMark(getTradeMark(products.get(1), row));
            product.setOrganType(getOrganType(products.get(2), row));

            if (isNumeric(row.getCell(productCountIndex))) {
                product.setNumberInPack((short) row.getCell(productCountIndex).getNumericCellValue());
            }
            checkProduct(product);
        }

        productService.saveAll(productList);
        productList.clear();
    }

    private void parseBareCode(List<String> bareCodes) {
        int productNameIndex = Integer.parseInt(bareCodes.get(0));
        int ean13Index = Integer.parseInt(bareCodes.get(1));

        for (int i = 1; i <= rows; i++) {
            Row row = sheet.getRow(i);
            BareCode bareCode = new BareCode();

            if (isString(row.getCell(productNameIndex))) {
                bareCode.setProduct(getProduct(productNameIndex, row));
            }
            if (isNumeric(row.getCell(ean13Index))) {
                bareCode.setEan13(BigDecimal.valueOf(row.getCell(ean13Index).getNumericCellValue()));
            }
            checkBareCode(bareCode);
        }
        bareCodeService.saveAll(bareCodeList);
        bareCodeList.clear();
    }

    private void checkPriceSale(PriceSale priceSale) {
        boolean isValid = true;
        if (priceSale.getPrice() == null || priceSale.getCampaign() == null || priceSale.getProduct() == null) {
            isValid = false;
        }
        if (isValid) {
            priceSaleList.add(priceSale);
        }
    }

    private void checkPriceBuy(PriceBuyPreliminarily price) {
        boolean isValid = true;
        if (price.getProduct() == null || price.getCounterAgent() == null || price.getCampaign() == null) {
            isValid = false;
        }
        if (price.getPriceBuy() == null) {
            isValid = false;
        }
        if (isValid) {
            priceBuyList.add(price);
        }
    }

    private void checkBareCode(BareCode bareCode) {
        if (bareCode.getEan13() != null) {
            if (!bareCodeService.exists(bareCode.getProduct().getIdProduct(), bareCode.getEan13())) {
                if (bareCode.getProduct() != null && bareCode.getEan13().precision() == 13) {
                    bareCodeList.add(bareCode);
                }
            }
        }
    }

    private void checkProduct(Product product) {
        boolean isValid = true;
        if (productService.exists(product.getProductName()) || StringUtils.isBlank(product.getProductName())) {
            isValid = false;
        }
        if (product.getTradeMark() == null || product.getOrganType() == null) {
            isValid = false;
        }
        if (product.getNumberInPack() < 1) {
            product.setNumberInPack(null);
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

    private Product getProduct(int cellIndex, Row row) {
        String name;
        if (isString(row.getCell(cellIndex))) {
            name = row.getCell(cellIndex).getStringCellValue();
        } else {
            return null;
        }
        if (productService.exists(name)) {
            return productService.findByProductName(name);
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
        if (cell.getCellType() == CellType.NUMERIC) {
            return true;
        }
        return false;
    }

    private boolean isString(Cell cell) {
        if (cell.getCellType() == CellType.STRING) {
            return true;
        }
        return false;
    }
}
