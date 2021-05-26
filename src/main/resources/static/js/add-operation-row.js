$(document).ready(function() {
      $('#rows-table').dataTable( {
        "searching": false,
           "scrollY": 200,
           "paging":   false,
           "ordering": false,
           "info":     false,
           "responsive": true,
        });
    initOperationRowsTable();
    $('#rows-table tbody').on( 'click', 'tr', function () {
            var table =  $('#rows-table').DataTable();

            if ( $(this).hasClass('selected-row') ) {
                $(this).removeClass('selected-row');
            }
            else {
                table.$('tr.selected-row').removeClass('selected-row');
                $(this).addClass('selected-row');
            }
        } );
    $('#add-row-button').click(function() {
    addRow();
    });
    $('#delete-row-button').click(function() {
    deleteRow();
    });
});

function initOperationRowsTable() {
    $('#rows-table').DataTable();
};

function addRow() {
    var productName = $('#product').find(":selected").text();
    var productId = $('#product').find(":selected").val();
    var price = $('#operation-price').val();
    var quantity = $('#quantity').val();
    var table =  $('#rows-table').DataTable();
    var totalRows = table.rows().count();
    table.row.add ([
        ' <input type="text" name="operationRows['+totalRows+'].product.productName" value="'+productName+'"><input type="text" name="operationRows['+totalRows+'].product.idProduct" value="'+productId+'">',
        ' <input type="text" name="operationRows['+totalRows+'].operationPrice" value="'+price+'">',
        ' <input type="text" name="operationRows['+totalRows+'].quantity" value="'+quantity+'">'
    ]).draw(true);
}

function deleteRow() {
    var table =
    $('#rows-table').DataTable().row('.selected-row').remove().draw(true);
}