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
        "url": "/pricebuys",
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
            {"data": "idPriceBuy", "width": "10%"},
            {"data": "priceBuy","width": "20%"},
            {"data": "priceBuyDate", "width": "20%"},
            {"data": "product.productName", "width": "20%"},
            {"data": "counterAgent.counterAgentName", "width": "10%"},
            {"data": "campaign.campaignName", "width": "10%"},
            {"data": "priceBuyComment", "width": "10%"}
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
          return '<a  href="/pricebuys/edit?id='+data+'" role="button">'+
          '<i class="fa fa-pencil"></i></a>'+' '+data;},
          targets: [0]
          }
          ]

        });

       $('#example tfoot th').each( function (i) {
               if (i == 2) {
                           $(this).html('<input type="text" id="from" style="  width: 50%;box-sizing: border-box;" placeholder="дд/мм/гггг" /> ' +
                               '<input type="text" id="to" style="  width: 50%;box-sizing: border-box;" placeholder="дд/мм/гггг" />');
                       } else {
                           $(this).html('<input type="text" style="  width: 100%;box-sizing: border-box;" placeholder="Поиск" />');
                       }
       } );
    }



