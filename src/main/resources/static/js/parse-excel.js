$(document).ready(function() {

    $("#parseButton").click(function(e){
        if ($('#counterAgentProfileSelect').is(":hidden") || $('#counterAgentProfile').val() === 'default') {
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
    let form = $('#excel-form');
    let response = await fetch('/apache/start', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json;charset=utf-8'
        },
        body: JSON.stringify({
            tempFileName: $('#tempFileName').val(),
            headers: headers,
            campaign:{idCampaign: $('#campaignPicker').val()},
            tradeMark: {tradeMarkName: $('#trademarkPicker option:selected').text()},
            organType:{organTypeName: $('#organTypePicker option:selected').text()},
            counterAgent:{counterAgentName: $('#counterAgentName').val(),
                counterAgentPhone: $('#counterAgentPhone').val(),
                counterAgentProfile: $('#counterAgentProfile').val()}
        })
    });
    let result = await response.text();
    document.write(result);
//    document.location.reload();
}