$(document).ready(function () {
    initTable();
});

function initTable() {
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
            "url": "/products",
            "type": "POST",
            "dataType": "json",
            "contentType": "application/json",
            "data": function (d) {
                return JSON.stringify(d);
            }
        },
        autoFill: true,
        "order": [[0, "desc"]],
        "columns": [
            {"data": "idProduct", "width": "10%"},
            {"data": "productName", "width": "20%"},
            {"data": "tradeMark.tradeMarkName", "width": "20%"},
            {"data": "organType.organTypeName", "width": "10%"},
            {"data": "numberInPack", "width": "10%"},
            {"data": "productComment", "width": "20%"},
            {"data": "inactive", "width": "10%"}
        ],
        "columnDefs": [
            {
                targets: [1, 5],
                render: function (data, type, row) {
                    if (type === 'display') {
                        return $.fn.dataTable.render.ellipsis(20)(data, type, row);
                    }
                    return data;
                }
            },
            {
                render: function (data) {
                    return '<a  href="/products/edit?id=' + data + '" role="button">' +
                        '<i class="fa fa-pencil" aria-hidden="true"></i></a> <a href="/products/delete?id=' + data + '" class="pl-1 pr-3" role="button" data-toggle="modal" data-target="#deleteModal" id="delButton"><i class="fa fa-trash" aria-hidden="true"></i></a>' + data;
                },
                targets: [0]
            }
        ]

    });

    $('#example tfoot th').each(function (i) {

        if (i == 6) {
            $(this).html('<input type="text" id="from" style="  width: 100%;box-sizing: border-box;" placeholder="Поиск" /> ' +
                '<input type="text" id="to" style="  width: 100%;box-sizing: border-box;" placeholder="Поиск" />');
        } else {
            $(this).html('<input type="text" style="  width: 100%;box-sizing: border-box;" placeholder="Поиск" />');
        }


    });

    $('#search-button').on('click', function () {
        let table = $('#example').DataTable();
        let lastRowNum = table.rows().count();
        let row = table.row(lastRowNum);
        for (let i = 0; i < row.columns().count(); i++) {
            if (table.column(i).search() !== this.value) {
                let inputs = $(table.column(i).footer()).find('input');
                let query="";
                inputs.each(function (j) {
                    let val = $(this).val();
                    if (val) {
                        if (j === 0) {
                            query +=val;
                        } else {
                            query +=';'+val;
                        }

                    }else{
                        query+="";
                    }
                });
                table
                    .column(i)
                    .search(query);
            }
        }
        table.draw();


    });
}



