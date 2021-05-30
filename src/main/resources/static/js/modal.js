$(document).ready(function() {
$("#deleteModal").on("show.bs.modal", function(e) {
//$(this).find(".btn-ok").attr("href", $(e.relatedTarget).data("href"));
//alert($(e.relatedTarget).data('id'));
var href = document.getElementById('delButton').href;
alert(href);
$("#accept").click(function() {
window.location = href;
});
} );
});