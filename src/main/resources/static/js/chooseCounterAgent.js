$(document).ready(function () {
    let allSelects = $('.narrow-select');
    let notChosen = 'Не выбран';

    $('#counterAgentName').select2({
        language: "ru",
        dropdownParent: $('.modal-body', '#choose-counter-agent-modal'),
        width: '100%'
    });
    $('.add-counter-agent-search-select').select2({
        language: "ru",
        dropdownParent: $('.modal-body', '#choose-counter-agent-modal'),
        templateResult: formatResults,
        width: '100%'
    });

    function formatResults(value) {

        if (value.text === notChosen || !value.id) {
            return value.text;
        }
        let searchBy = $(value.element).data('search').toString();
        let prevSelect = $(value.element).parent().parent().prev().children('select').first().val();
        return searchBy === prevSelect ? value.text : null;
    }

    $('#choose-counter-agent-modal').on('show.bs.modal', function (e) {
        $(".narrow-select-div").hide();
        $("#submitCounterAgent").attr("disabled", true);
    });

    allSelects.change(function () {
        console.log("change");
        let search = $(this).find(":selected").text();
        let nextElements = $(this).parent().nextAll('.narrow-select-span');
        console.log("nextElements", nextElements);
        if (nextElements.length > 0) {
            $("#submitCounterAgent").attr("disabled", false);
        } else {
            $("#submitCounterAgent").attr("disabled", true);
        }
        if (search === notChosen) {
            //hide all next narrow-select-divs
            nextElements.hide();

            //TODO clear input

        } else {
            nextElements.first().show();
            console.log("show", nextElements.first());
        }
    });

})
