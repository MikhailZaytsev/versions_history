package ru.plantarum.core.uploading.response;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class ResultParsing {

    private int productCount;
    private int bareCodeCount;
    private int priceBuyCount;
    private int priceSaleCount;
    private ArrayList<InvalidParse> productErrors;
    private ArrayList<InvalidParse> productWarnings;
    private ArrayList<InvalidParse> bareCodeErrors;
    private ArrayList<InvalidParse> bareCodeWarnings;
    private ArrayList<InvalidParse> priceBuyErrors;
    private ArrayList<InvalidParse> priceBuyWarnings;
    private ArrayList<InvalidParse> priceSaleErrors;
    private ArrayList<InvalidParse> priceSaleWarnings;

    public ResultParsing() {
        this.productCount = 0;
        this.bareCodeCount = 0;
        this.priceBuyCount = 0;
        this.priceSaleCount = 0;
        this.productErrors = new ArrayList<>();
        this.productWarnings = new ArrayList<>();
        this.bareCodeErrors = new ArrayList<>();
        this.bareCodeWarnings = new ArrayList<>();
        this.priceBuyErrors = new ArrayList<>();
        this.priceBuyWarnings = new ArrayList<>();
        this.priceSaleErrors = new ArrayList<>();
        this.priceSaleWarnings = new ArrayList<>();
    }

    public void addError(EntityMessage entity, String error, int row) {
        switch (entity) {
            case PRODUCT: getProductErrors().add(new InvalidParse(entity.toString(), error, row)); break;
            case BARE_CODE: getBareCodeErrors().add(new InvalidParse(entity.toString(), error, row)); break;
            case PRICE_BUY_PRELIMINARILY: getPriceBuyErrors().add(new InvalidParse(entity.toString(), error, row)); break;
            case PRICE_SALE: getPriceSaleErrors().add(new InvalidParse(entity.toString(), error, row)); break;
        }
    }

    public void addWarning(EntityMessage entity, String warning, int row) {
        switch (entity) {
            case PRODUCT: getProductWarnings().add(new InvalidParse(entity.toString(), warning, row)); break;
            case BARE_CODE: getBareCodeWarnings().add(new InvalidParse(entity.toString(), warning, row)); break;
            case PRICE_BUY_PRELIMINARILY: getPriceBuyWarnings().add(new InvalidParse(entity.toString(), warning, row)); break;
            case PRICE_SALE: getPriceSaleWarnings().add(new InvalidParse(entity.toString(), warning, row)); break;
        }
    }

    public void clearResult() {
        setProductCount(0);
        setBareCodeCount(0);
        setPriceBuyCount(0);
        setPriceSaleCount(0);
        getProductErrors().clear();
        getProductWarnings().clear();
        getBareCodeErrors().clear();
        getBareCodeWarnings().clear();
        getPriceBuyErrors().clear();
        getPriceBuyWarnings().clear();
        getPriceSaleErrors().clear();
        getPriceSaleWarnings().clear();
    }
}
