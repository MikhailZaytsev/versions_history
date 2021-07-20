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
            idCounterAgent: $('#counterAgentId').val(),
            tempFileName: $('#tempFileName').val(),
            headers: headers,
            idCampaign: $('#campaignPicker').val(),
            idOrganType: $('#organTypePicker').val(),
            idTradeMark: $('#trademarkPicker').val()
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