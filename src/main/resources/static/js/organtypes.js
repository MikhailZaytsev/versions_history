$(document).ready(function() {
    initTradeMarksTable();
});

function initTradeMarksTable() {
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
        "url": "/organtypes",
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
            {"data": "idOrganType", "width": "10%"},
            {"data": "organTypeName","width": "50%"},
            {"data": "organTypeComment", "width": "30%"},
            {"data": "inactive", "width": "10%"}
        ],
    "columnDefs": [
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
        return '<a  href="/organtypes/edit?id='+data+'" role="button">'+
        '<i class="fa fa-pencil" aria-hidden="true"></i></a> <a href="/organtypes/delete?id='+data+'" class="pl-1 pr-3" role="button"><i class="fa fa-trash" aria-hidden="true"></i></a>'+data;},
        targets: [0]
        }
        ]

        });

        $('#example tfoot th').each( function (i) {
                var title = $(this).text();
                $(this).html( '<input type="text" style="  width: 100%;box-sizing: border-box;" placeholder="Искать '+title+'" />' );

                $( 'input', this ).on( 'keyup change', function () {
                    if ( table.column(i).search() !== this.value ) {
                        table
                            .column(i)
                            .search( this.value )
                            .draw();
                    }
                } );
        } );
    }