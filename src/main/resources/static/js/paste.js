$(document).ready(function() {
$("#note").bind("paste", function(e){
 e.preventDefault();
    // access the clipboard using the api
    var pastedData = e.originalEvent.clipboardData.getData('text');

     var maxLength = $(this).attr('maxlength');
               if (pastedData.length > maxLength) {
                   $(this).val(pastedData.substring(0, maxLength));
               }

} );
 $("#add-note").click(function(){
 var textarea =$("#note");
 var href =textarea.attr('href');
        var posting = $.post(href, { note: textarea.val() } );
        posting.done(function( data ) {
                      location.reload(); // then reload the page.(3)
          });
    });
});