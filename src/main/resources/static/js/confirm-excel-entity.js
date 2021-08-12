$(document).ready(function() {
    console.log("ready");
    $("#confirmParse").click(function(e){
        console.log("click");
        let excelEntity = JSON.parse($("#excel-entity-confirm").val());
        console.log(excelEntity);
    });
});

//async function sendExcelEntity() {
//    let response = await fetch('/apache/load', {
//            method: 'POST',
//            headers: {
//                'Content-Type': 'application/json;charset=utf-8'
//            },
//            body: JSON.stringify({
//                products: $('#products').val(),
//                existsProducts: $('#existsProducts').val(),
//                bareCodes: $('#bareCodes').val(),
//                priceBuyPreliminarilyMap: $('#tempFileName').val(),
//                priceSales: $('#tempFileName').val()
//            })
//        });
//    let result = await response.text();
//    document.write(result);
//}