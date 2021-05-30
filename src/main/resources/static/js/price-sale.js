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
        "url": "/pricesales",
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
            {"data": "idPriceSale", "width": "10%"},
            {"data": "price","width": "10%"},
            {"data": "priceSaleDate", "width": "20%"},
            {"data": "product.productName", "width": "10%"},
            {"data": "propertyPrice", "width": "10%"},
            {"data": "campaign.campaignName", "width": "10%"},
            {"data": "packOnly", "width": "10%"},
            {"data": "priceSaleComment", "width": "10%"}
        ],
    "columnDefs": [
        {
                                            "render": function ( data, type, row ) {
                                return data?'Упаковками':'Штучно';
                            },
                           targets: [6]
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
          return '<a  href="/pricesales/edit?id='+data+'" role="button">'+
          '<i class="fa fa-pencil" aria-hidden="true"></i></a>'+data;},
          targets: [0]
          }
          ]

        });

       $('#example tfoot th').each( function (i) {
               var title = $(this).text();
               $(this).html( '<input type="text" style="  width: 100%;box-sizing: border-box;" placeholder="Поиск" />' );

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



