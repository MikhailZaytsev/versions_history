$(document).ready(function() {

    $("#parseButton").click(function(){
        if (!$('#counterAgentId').val()) {
            alert('Выберите контрагента!');
        } else {
        let headers = new Array();
        $(".headerPicker").each(function () {
            headers.push($(this).val());
        })
        sendExcelParse(headers);
        }
    });
});

async function sendExcelParse(headers) {
    //TODO: '500 error' appears in browser console on this line
    let response = await fetch('apache/start', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json;charset=utf-8'
        },
        body: JSON.stringify({
            tempFileName: $('#tempFileName').val(),
            headers: headers,
            campaign:{idCampaign: $('#campaignPicker').val()},
            tradeMark: {tradeMarkName:$('#trademarkPicker option:selected').text()},
            organType:{organTypeName:$('#organTypePicker option:selected').text()},
            counterAgent:{idCounterAgent: $('#counterAgentId').val()}
        })
    });
//    let result = await response.text();
//    console.log(result);
//    if ($(result).find('.error-text').length) {
//        form.replaceWith(result);
//        init();
//    } else {
        location.href = "/apache/result";
//    }
}