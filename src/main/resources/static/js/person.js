$(document).ready(function() {
    initTable();
});

function initTable(){
    $.fn.dataTable.ext.classes.sPageButton = 'btn btn-light btn-sm';
    var table = $('#example').DataTable({
        "dom": '<"top"i>rt<"bottom"lp><"clear">',
        language: {
            url: '../localization/russia.json'
        },
        "responsive": true,
        "processing": true,
        "serverSide": true,
        "ajax": {
            "url": "/admin",
            "type": "POST",
            "dataType": "json",
            "contentType": "application/json",
            "data": function (d) {
                return JSON.stringify(d);
            }
        },
        colReorder: true,
        colReorder: {
            fixedColumnsLeft: 1
        },
        autoFill: true,
        "order": [[ 0, "desc" ]],
        "columns": [
            {"data": "personId", "width": "10%"},
            {"data": "personName","width": "30%"},
            {"data": "roles", "width": "60%"}
        ],
        "columnDefs": [
        {
            "render": function ( data, type, row ) {
            let roles = '';
                data.forEach((role) => {
                    roles = roles + role.description + ' ';
                });
                return roles;
            },
            targets: [2]
        } ,
        {
            targets: [1,2],
            render: function(data, type, row) {
            if ( type === 'display') {
                return $.fn.dataTable.render.ellipsis(20)(data, type, row);
            }
                return data;
            }
        } ,
        {
            render: function(data){
            return '<a  href="/admin/edit?id='+data+'" role="button">'+
            '<i class="fa fa-pencil"></i></a>'+' '+data;},
            targets: [0]
        }
        ]
    });
}