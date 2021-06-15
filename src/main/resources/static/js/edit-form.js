$(document).ready(function() {
    let elements = document.getElementsByClassName("focus-element");
    let j = 0;

    $(document).on('keydown', function (e) {
    if (e.key === 'Tab' || e.keyCode === 9) {
        if (!($('.modal').hasClass('show'))) {
            e.preventDefault();
            elements[j].focus();
            j++;
            if (j >= elements.length) {
                j = 0;
            }
        }
        }
    });

    $('input').on('keydown', function(e) {
        if (e.key === 'Enter' || e.keyCode === 13) {
            e.preventDefault();
        }
    });
});