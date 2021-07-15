$(document).ready(function () {
    let allSelects = null;//$('.narrow-select'); //получение всех "узких" селектов (класс narrow-select)
    let notChosen = 'Не выбран'; //на случай отмены контрагента
    let defaultValue = "default"; //значение, не привязанное к контрагенту

    /*
    * установка параметров для select
    */
    $('#counterAgentName').select2({
        language: "ru",
        dropdownParent: $('.modal-body', '#choose-counter-agent-modal'), //не совсем понятно зачем
        width: '100%'
    });
    $('.add-counter-agent-search-select').select2({
        language: "ru",
        dropdownParent: $('.modal-body', '#choose-counter-agent-modal'), //не совсем понятно зачем
        templateResult: formatResults, //не понятно зачем
        width: '100%'
    });

    function formatResults(value) {

        if (value.text === notChosen || !value.id) { //если НЕ выбран контрагент
            return value.text; //возвращается отображаемый текст, не значение
        }
        let searchBy = $(value.element).data('search').toString(); //переменная, по которой будут искаться значения следующих select
        let prevSelect = $(value.element).parent().parent().prev().children('select').first().val(); //видимо все значения следущего select
        return searchBy === prevSelect ? value.text : null; //если есть совпадение с searchBy, то будет отображено
    }

    $('#choose-counter-agent-modal').on('show.bs.modal', function (e) { //при запуске модальног окна
        $(".narrow-select-span").hide(); //скрыть элементы под классом 'narrow-select-SPAN'
        $("#submitCounterAgent").attr("disabled", true); //отключить кнопку подтверждения
    });

    $('#counterAgentName').change(function () {
        let search = $(this).find(":selected").text();
        $("#submitCounterAgent").attr("disabled", true);
        $('#counterAgentProfile').select2({
            language: "ru",
            dropdownParent: $('.modal-body', '#choose-counter-agent-modal'), //не совсем понятно зачем
            width: '100%'
        });
        $('#counterAgentPhone').select2({
            language: "ru",
            dropdownParent: $('.modal-body', '#choose-counter-agent-modal'), //не совсем понятно зачем
            width: '100%'
        });
        $('#counterAgentPhoneSelect').hide();
        $('#counterAgentProfileSelect').hide();
        if (search !== notChosen) {
            $('#counterAgentPhoneSelect').show();
        }
    });

     $('#counterAgentPhone').change(function () {
        let search = $(this).find(":selected").text();
        $("#submitCounterAgent").attr("disabled", true);
        $('#counterAgentProfile').select2({
            language: "ru",
            dropdownParent: $('.modal-body', '#choose-counter-agent-modal'), //не совсем понятно зачем
            width: '100%'
        });
        $('#counterAgentProfileSelect').hide();
        if (search !== notChosen) {
            $('#counterAgentProfileSelect').show();
        }
     });

     $('#counterAgentProfile').change(function () {
        let search = $(this).find(":selected").text();
        if (search === notChosen) {
            $("#submitCounterAgent").attr("disabled", true);
        } else {
            $("#submitCounterAgent").attr("disabled", false);
        }
     });

    allSelects.change(function () {
      //  console.log("change"); //распечатать в консоле текст
        let search = $(this).find(":selected").text(); //получить текст выбранного значения
        let nextElements = $(this).parent().nextAll('.narrow-select-span'); //коллекция всех открытых span  с селектами
//        console.log(nextElements.length); //распечатать длину массива селектов
//        console.log("nextElements", nextElements); //распечатать весь массив селектов
        if (nextElements.length > 0) { //если массив не пустой
            $("#submitCounterAgent").attr("disabled", true); //кнопка подтверждения не активна
        } else {
            $("#submitCounterAgent").attr("disabled", false); //кнопка подтверждения активна
        }
        if (search === notChosen) {

//        let selectId = $(this).attr('id');
//            if (selectId === 'counterAgentName') {
//                $('#counterAgentPhone').select2("val", defaultValue);
//                $('#counterAgentProfile').select2("val", defaultValue);
//            } else if(selectId === 'counterAgentPhone') {
//                $('#counterAgentProfile').select2("val", defaultValue);
//            }
            //hide all next narrow-select-spans
//            nextElements
            nextElements.hide();
//            nextElements.forEach(element => element.find("select").fn.select2.defaults.reset());
            $("#submitCounterAgent").attr("disabled", true); //кнопка подтверждения не активна
            //TODO clear input
        } else {
            nextElements.first().show(); //отобразить следующий select
//            console.log("show", nextElements.first()); распечатать следующий селект
        }
    });

})
