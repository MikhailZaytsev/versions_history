let defValue = 'default'; //global field for compare 'select' values
$(document).ready(function () {

    /*
    * enable searching in 'select'
    */
    $('.add-counter-agent-search-select').select2({
            language: "ru",
            dropdownParent: $('.modal-body', '#choose-counter-agent-modal'),
            width: '100%'
        });

    let counterAgentsArray = new Array();
    let agentName;
    let agentPhone;
    let agentProfile;
    /*
    * arrays for sorting 'select' values
    */
    let names = new Array();
    let phones = new Array();
    let profiles = new Array();

    $('#choose-counter-agent-modal').on('show.bs.modal', function (e) {
        $(".narrow-select-span").hide();
        $("#submitCounterAgent").attr("disabled", true);
        counterAgentsArray = JSON.parse($("#counterAgentJSON").val());
        /*
        * put in first 'select' agents names only (without duplicates)
        */
        counterAgentsArray.forEach(agent =>names.push(agent.name));
        addArray('counterAgentName', removeDuplicates(names));
    });

    $('#counterAgentName').change(function () {
        $("#submitCounterAgent").attr("disabled", true);
        $('#counterAgentProfileSelect').hide();
        /*
        * clear arrays from last search
        */
        phones = [];
        profiles = [];
        let search = $(this).val();
        if (search === defValue) {
            $('#counterAgentPhoneSelect').hide();
        } else {
            agentName = search;
            /*
            * select agents phones with the same agent name from first 'select'
            */
            for (let i = 0; i < counterAgentsArray.length; i++) {
                if (counterAgentsArray[i].name === agentName) {
                    phones.push(counterAgentsArray[i].phone);
                }
            }
            $('#counterAgentPhoneSelect').show();
            /*
            * put in second 'select' agents phones only (without duplicates)
            */
            addArray('counterAgentPhone', removeDuplicates(phones));
        }
    });

     $('#counterAgentPhone').change(function () {
        profiles = [];
        let search = $(this).val();
        $("#submitCounterAgent").attr("disabled", true);
        if (search === defValue) {
            $('#counterAgentProfileSelect').hide();
        } else {
            /*
            * select agents profiles with the same agent name and phone from first and second 'select'
            */
            agentPhone = search;
            for (let i = 0; i < counterAgentsArray.length; i++) {
                if (counterAgentsArray[i].name === agentName && counterAgentsArray[i].phone === agentPhone) {
                    profiles.push(counterAgentsArray[i].profile);
                }
            }
            $('#counterAgentProfileSelect').show();
            /*
            * put in third 'select' agents profiles only
            */
            addArray('counterAgentProfile', profiles);
        }
     });

     $('#counterAgentProfile').change(function () {
        let search = $(this).val();
        if (search === defValue) {
            $("#submitCounterAgent").attr("disabled", true);
        } else {
            /*
            * if agent is chosen - submit button activate
            */
            agentProfile = search;
            $("#submitCounterAgent").attr("disabled", false);
        }
     });

     $("#submitCounterAgent").click(function() {
        /*
        * get the agent with unique name + phone + profile
        */
        let agent = counterAgentsArray.filter(function(e) {
            return (e.name === agentName && e.phone === agentPhone && e.profile === agentProfile);
        });
        $("#counterAgentId").val(agent[0].id);
        $("#chosenAgentName").val(agent[0].name);
        $('#choose-counter-agent-modal').modal('hide');
     });
})
/*
* fill 'select' with values from sorted array
*/
function addArray(selectId, values) {
    let sel = document.getElementById(selectId);
    $('#' + selectId).find('option').remove();
    sel.options[sel.options.length] = new Option('Не выбрано', defValue, true, true);
    values.forEach(value => sel.options[sel.options.length] = new Option(value, value, false, false));
}

function removeDuplicates(values) {
    return values.filter((value, index) => values.indexOf(value) === index);
}
