$(document).ready(function() {
      $('#rows-table').dataTable( {
        "searching": false,
           "scrollY": 300,
           "paging":   false,
           "ordering": false,
           "info":     false,
           "responsive": true
        });
    initOperationRowsTable();
    $('#add-row-button').click(function() {
    addRow();
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