$(document).ready(function() {
$('#note').on('paste', function (e) {
//    e.preventDefault();
    var pastedData = e.originalEvent.clipboardData.getData('text');
    alert(pastedData);
//    document.execCommand('inserttext', false, pastedData);
});
});