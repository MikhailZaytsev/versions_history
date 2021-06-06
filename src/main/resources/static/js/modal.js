$(document).ready(function() {
$("#deleteModal").on("show.bs.modal", function(e) {
//$(this).find(".btn-ok").attr("href", $(e.relatedTarget).data("href"));
//alert($(e.relatedTarget).data('id'));
var href = document.getElementById('delButton').href;
alert(href);
$("#accept").click(function() {
window.location = href;
});
});

$("#clear-search").click(function() {
$(':input').val('');
location.reload();
});
$("#search-button").click(function(e) {
    e.preventDefault();
    hardtry();
});
});
async function hardtry() {
//let start = "meow";
//alert(start);
let obj = {"draw":6, "columns":[{
            "data":"idProduct", "name":"", "searchable":true, "orderable":true, "search":{
                "value":"70", "regex":false
            }
        },{
            "data":"productName", "name":"", "searchable":true, "orderable":true, "search":{
                "value":"", "regex":false
            }
        },{
            "data":"tradeMark.tradeMarkName", "name":"", "searchable":true, "orderable":true, "search":{
                "value":"", "regex":false
            }
        },{
            "data":"organType.organTypeName", "name":"", "searchable":true, "orderable":true, "search":{
                "value":"", "regex":false
            }
        },{
            "data":"numberInPack", "name":"", "searchable":true, "orderable":true, "search":{
                "value":"", "regex":false
            }
        },{
            "data":"productComment", "name":"", "searchable":true, "orderable":true, "search":{
                "value":"", "regex":false
            }
        },{
            "data":"inactive", "name":"", "searchable":true, "orderable":true, "search":{
                "value":"", "regex":false
            }
        }],"order":[{
            "column":0, "dir":"desc"
        }],"start":0, "length":10, "search":{
            "value":"", "regex":false
        }};
let url = "/products";
    let response = await fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json;charset=utf-8'
        },
        body: JSON.stringify(obj)
    });
    let result = await response.json();
    let table = $('#example').DataTable();
    table.clear().draw();
    table.rows.add(result.data);
    table.columns.adjust().draw();
    console.log(result.data);
//let pagingRequest = JSON.stringify(obj);
//let request = new XMLHttpRequest();
//let url = "/products";
//request.open("POST", url, true);
//request.setRequestHeader("Content-Type", "application/json");
//request.onreadystatechange = function () {
// if (request.readyState === 4 && request.status === 200) {
//        const json = JSON.parse(request.responseText);
//        const table = $('#example').DataTable();
//        console.log(table, request.responseText,$('#example'));
//        table.rows.add(json.data).draw();
//    }
//}
//request.send(pagingRequest);

}