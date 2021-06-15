$(document).ready(function () {

let url = "#";

    $("#deleteModal").on("show.bs.modal", function (e) {
          document.getElementById("accept-btn").setAttribute('href', getUrl());
    });

    $("#deleteModal").on('keydown', function(e) {
        if (e.key === 'Enter' || e.keyCode === 13) {
            document.getElementById("accept-btn").click();
        }
    });
});

function setUrl(s) {
url = s;
}

function getUrl() {
return url;
}

function pressed(el) {
    setUrl(el.getAttribute("href"));
}