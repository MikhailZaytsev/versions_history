$(document).ready(function() {
    let elems = document.querySelectorAll("input, textarea");
    let i = 0;

    $(document).on('keyup', function (e) {
        if (e.key === 'Tab' || e.keyCode === 9) {
            console.log(elems);
            elems[i].focus();
            console.log(elems[i]);
            i++;
            if (i >= elems.length) {
                console.log(i);
                i = 0;
            }
        }
    });

    $('input').on('keydown', function(e) {
        if (e.key === 'Enter' || e.keyCode === 13) {
            e.preventDefault();
        }
    });
});