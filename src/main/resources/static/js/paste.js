$(document).ready(function() {
$("#note").bind("paste", function(e){
 e.preventDefault();
    // access the clipboard using the api
    var pastedData = e.originalEvent.clipboardData.getData('text');
    var href = $(this).attr('href');
     var maxLength = $(this).attr('maxlength');
               console.log(maxLength);
               if (pastedData.length > maxLength) {
                   $(this).val(pastedData.substring(0, maxLength));
               }
               $.post(href, { note: $(this).val() } );

} );

});