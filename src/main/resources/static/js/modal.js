$(document).ready(function () {
    $("#deleteModal").on("show.bs.modal", function (e) {

        var href = document.getElementById('delButton').href;
        alert(href);
        $("#accept").click(function () {
            window.location = href;
        });
    });


    $("#clear-search").click(function () {
        $(':input').val('');
        location.reload();
    });

});
