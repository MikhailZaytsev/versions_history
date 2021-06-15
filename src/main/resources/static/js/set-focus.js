$(document).ready(function () {

    let inputs = document.querySelectorAll("input");
    let i = 0;

    $(document).on('keydown', function (e) {
    if (e.key === 'Tab' || e.keyCode === 9) {
    if (!($('.modal').hasClass('show'))) {
        e.preventDefault();
        inputs[i].focus();
        i++;
        if (i >= inputs.length) {
        i = 0;
        }
    }
    }
    });
});