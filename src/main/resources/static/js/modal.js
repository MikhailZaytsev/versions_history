$(document).ready(function () {

let url = "#";

    $("#deleteModal").on("show.bs.modal", function (e) {
        let elem = document.getElementById("accept-btn");
          elem.setAttribute('href', getUrl());
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
