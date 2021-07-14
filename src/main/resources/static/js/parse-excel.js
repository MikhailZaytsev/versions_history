$(document).ready(function() {
    $("#productMarkSelect").selectpicker('hide');
    $("#productOrganSelect").selectpicker('hide');
    $(".selectpicker").selectpicker('toggle');
    window.scrollTo(0,0);

    $("#parseButton").click(function(){
        startParsing();
    });

    $("#productMarkSwitch").change(function() {
        if(this.checked) {
            $("#productMarkSelect").selectpicker('show');
            $("#productMarkSelect").addClass("productExcel");
            $("#tradeMarksDb").selectpicker('hide');
            $("#tradeMarksDb").removeClass("productExcel");
        } else {
            $("#productMarkSelect").selectpicker('hide');
            $("#productMarkSelect").removeClass("productExcel");
            $("#tradeMarksDb").selectpicker('show');
            $("#tradeMarksDb").addClass("productExcel");
        }
    });

    $("#productOrganSwitch").change(function() {
        if(this.checked) {
            $("#productOrganSelect").selectpicker('show');
            $("#productOrganSelect").addClass("productExcel");
            $("#organTypesDb").selectpicker('hide');
            $("#organTypesDb").removeClass("productExcel");
        } else {
            $("#productOrganSelect").selectpicker('hide');
            $("#productOrganSelect").removeClass("productExcel");
            $("#organTypesDb").selectpicker('show');
            $("#organTypesDb").addClass("productExcel");
        }
    });
});

function productShowing(check) {
    if (check.checked) {
        $("#productFields").show();
    } else {
        $("#productFields").hide();
    }
}

function bareCodeShowing(check) {
    if (check.checked) {
        $("#bareCodeFields").show();
    } else {
        $("#bareCodeFields").hide();
    }
}

function priceBuyShowing(check) {
    if (check.checked) {
        $("#priceBuyFields").show();
    } else {
        $("#priceBuyFields").hide();
    }
}

function priceSaleShowing(check) {
    if (check.checked) {
        $("#priceSaleFields").show();
    } else {
        $("#priceSaleFields").hide();
    }
}

function startParsing() {
    let product = [];
    let bareCode = [];
    let priceBuy = [];
    let priceSale = [];

    let productName = $('#productNameSelect').val();
    let productMark;
    if ($('#productMarkSwitch').prop('checked')) {
        productMark = $('#productMarkSelect').val();
    } else {
        productMark = $('#tradeMarksDb').val();
    }
    let productOrgan;
    if ($('#productMarkSwitch').prop('checked')) {
        productOrgan = $('#productOrganSelect').val();
    } else {
        productOrgan = $('#organTypesDb').val();
    }
    let productQuantity = $('#productCountSelect').val();

    let ean13 = $('#bareCodeSelect').val();

    let price_in = $('#PriceBuySelect').val();
    let campaign = $('#campaignSelect').val();
    let counterAgent = $('#counterAgentId').val();

    let price_out = $('#priceSaleSelect').val();

    let check = document.getElementById('productSwitch');
    if (check.checked) {
        product.push(productName);
        product.push(productMark);
        product.push(productOrgan);
        product.push(productQuantity);
    }
    check = document.getElementById('bareCodeSwitch');
    if (check.checked) {
        bareCode.push(productName);
        bareCode.push(productMark);
        bareCode.push(productQuantity);
        bareCode.push(ean13);
    }
    check = document.getElementById('priceBuySwitch');
    if (check.checked) {
        priceBuy.push(productName);
        priceBuy.push(productMark);
        priceBuy.push(productQuantity);
        priceBuy.push(price_in);
        priceBuy.push(campaign);
        priceBuy.push(counterAgent);
    }
    check = document.getElementById('priceSaleSwitch');
    if (check.checked) {
        priceSale.push(productName);
        priceSale.push(productMark);
        priceSale.push(productQuantity);
        priceSale.push(campaign);
        priceSale.push(price_out);
    }
    sendExcelParse(product, bareCode, priceBuy, priceSale);
}

async function sendExcelParse(product, bareCode, priceBuy, priceSale) {
    let response = await fetch('apache/start', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json;charset=utf-8'
        },
        body: JSON.stringify({
            products: product,
            bareCodes: bareCode,
            priceBuys: priceBuy,
            priceSales: priceSale
        })
    });
//    let result = await response.text();
//    if ($(result).find('.error-text').length) {
//        form.replaceWith(result);
//        init();
//    } else {
        location.href = "/apache/result";
//    }
}