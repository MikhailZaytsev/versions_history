/*---------------------------
file to set up main js functions such as
- side menu
- hamburger
- cross button
---------------------------*/
$(window).on('beforeunload', function(){
   $('*').css("cursor", "wait");
});

$(document).ready(function() {
    initMainMenu();

});

function initMainMenu() {
$('[data-toggle="offcanvas"]').click(function () {
            $('#wrapper').toggleClass('toggled');
      });
var trigger = $('.hamburger');

trigger.click(function () {
var trigger = $(this),
    overlay = $('.overlay'),
    isClosed = trigger.hasClass('is-open');
        if (isClosed == true) {
            overlay.hide();
            trigger.removeClass('is-open');
            trigger.addClass('is-closed');
        } else {
            overlay.show();
            trigger.removeClass('is-closed');
            trigger.addClass('is-open');
            }
    });

 };
