$(document).ready(function() {
let globalAgents = new Array();
let second = new Array();
    $('#choose-counter-agent-modal').on('show.bs.modal', function (e) {
        $("#counterAgentPhoneSelect").hide();
        $("#counterAgentProfileSelect").hide();
        $("#submitCounterAgent").attr("disabled", true);
        let agents = $("#counterAgentName>option").map(function() { return $(this).val(); });
        globalAgents = Array.from(agents);
        globalAgents.splice(0,1);
    });

    $('#counterAgentName').change(function(){
        let chosenAgent = document.getElementById("counterAgentName").value;
        $("#submitCounterAgent").attr("disabled", true);
        second = [];
        if (chosenAgent === 'default') {
            $("#counterAgentPhoneSelect").hide();
            $("#counterAgentProfileSelect").hide();
        } else {
            let name = getName(chosenAgent);
            $("#counterAgentPhoneSelect").show();
            $("#counterAgentProfileSelect").hide();
            second = globalAgents.filter(function (e) { return getName(e) === name;});
            addArray("counterAgentPhone", second);
        }
    });

    $('#counterAgentPhone').change(function(){
        let chosenAgent = document.getElementById("counterAgentPhone").value;
        $("#submitCounterAgent").attr("disabled", true);
        if (chosenAgent === 'default') {
            $("#counterAgentProfileSelect").hide();
        } else {
            let phone = getPhone(chosenAgent);
            $("#counterAgentProfileSelect").show();
            let third = second.filter(function (e) { return getPhone(e) === phone;});
            addArray("counterAgentProfile", third);
        }
    });

    $("#counterAgentProfile").change(function() {
        let chosenAgent = document.getElementById("counterAgentProfile").value;
        if (chosenAgent === 'default') {
            $("#submitCounterAgent").attr("disabled", true);
        } else {
            $("#submitCounterAgent").attr("disabled", false);
        }
    });

    $("#submitCounterAgent").click(function(){
        let chosenAgent = document.getElementById("counterAgentProfile").value;
        $("#counterAgentId").val(getId(chosenAgent));
        $("#chosenAgentName").val(getName(chosenAgent));
        $('#choose-counter-agent-modal').modal('hide');
    });
})

function getId(agentString) {
     let agentField = agentString.split(",");
     let result = agentField[0].split("=");
     let id = (result[1]);
     return id;
}

function getName(agentString) {
     let agentField = agentString.split(",");
     let result = agentField[1].split("=");
     let name = (result[1]);
     return name;
}

function getProfile(agentString) {
     let agentField = agentString.split(",");
     let result = agentField[2].split("=");
     let profile = (result[1]);
     return profile;
}

function getPhone(agentString) {
     let agentField = agentString.split(",");
     let result = agentField[3].split("=");
     let phone = (result[1]);
     return phone;
}



function addArray(selectId, values) {
    let sel = document.getElementById(selectId);
    $('#' + selectId).find('option').remove();
    sel.options[sel.options.length] = new Option('Не выбрано', 'default')
    for (let i = 0; i < values.length; i++) {
        let text;
        if (selectId === 'counterAgentPhone') {
            text = getPhone(values[i]);
        } else {
            text = getProfile(values[i]);
        }
        let a = sel.options.length;
        sel.options[a] = new Option(text, values[i]);
    }
    $('#' + selectId).selectpicker('refresh');
}