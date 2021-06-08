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
        "url": "/operationliststatuses",
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
            {"data": "idOperationListStatus", "width": "10%"},
            {"data": "operationListStatusName","width": "70%"},
            {"data": "inactive", "width": "20%"},
        ],
    "columnDefs": [
         {
                                    "render": function ( data, type, row ) {
                        return data?'Закрыт':'Открыт';
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
           return '<a  href="/operationliststatuses/edit?id='+data+'" role="button">'+
                  '<i class="fa fa-pencil" aria-hidden="true"></i></a>'+data;},
          targets: [0]
          }
          ]

        });

       $('#example tfoot th').each( function (i) {
               var title = $(this).text();
               $(this).html( '<input type="text" style="  width: 100%;box-sizing: border-box;" placeholder="Поиск" />' );

//               $( 'input', this ).on( 'keyup change', function () {
//                   if ( table.column(i).search() !== this.value ) {
//                       table
//                           .column(i)
//                           .search( this.value )
//                           .draw();
//                   }
//               } );
       } );
    }