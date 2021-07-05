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
    let result = "";
    let product = [];
    let bareCode = [];
    let priceBuy = [];
    let priceSale = [];
    let check = document.getElementById('productSwitch');
    if (check.checked) {
        let elements = document.querySelectorAll('select.productExcel');
        for (var i = 0; i < elements.length; i++) {
            product.push(elements[i].value);
        }
    }
    check = document.getElementById('bareCodeSwitch');
    if (check.checked) {
        let elements = document.querySelectorAll('select.bareCodeExcel');
        for (var i = 0; i < elements.length; i++) {
            bareCode.push(elements[i].value);
        }
    }
    check = document.getElementById('priceBuySwitch');
    if (check.checked) {
        let elements = document.querySelectorAll('select.priceBuyExcel');
        for (var i = 0; i < elements.length; i++) {
            priceBuy.push(elements[i].value);
        }
        let agentId = $("#counterAgentId").val();
        priceBuy.push(agentId);
    }
    check = document.getElementById('priceSaleSwitch');
    if (check.checked) {
        let elements = document.querySelectorAll('select.priceSaleExcel');
        for (var i = 0; i < elements.length; i++) {
            priceSale.push(elements[i].value);
        }
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
    let result = await response.text();
    if ($(result).find('.error-text').length) {
        form.replaceWith(result);
        init();
    } else {
        location.href = "/apache";
    }
}