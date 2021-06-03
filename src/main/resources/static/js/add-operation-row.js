$(document).ready(function () {
    init();
});

async function sendOperationList(e) {
    let table = $('#rows-table').DataTable();
    let form = $('#operation-list-form');
    e.preventDefault();
    let operationRows = [];

    table.rows().every(function () {
        let data = this.data();
        if (data.length > 0) {
            let row = {};
            row.product = {
                productName: data[0],
                idProduct: data[3],
            };
            row.operationPrice = data[1];
            row.quantity = data[2];
            operationRows.push(row);
        }
    });
    let url = form.attr("action");
    let response = await fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json;charset=utf-8'
        },
        body: JSON.stringify({
            operationType: {
                idOperationType: $('#operationType').find(":selected").val()
            },
            operationListStatus: {
                idOperationListStatus: $('#operationListStatus').find(":selected").val()
            },
            operationListComment: $('#operationListComment').val(),
            counterAgent: {
                idCounterAgent: $('#counterAgent').find(":selected").val()
            },
            operationRows: operationRows
        })
    });
    let result = await response.text();
    if ($(result).find('.error-text').length) {
        form.replaceWith(result);
        init();
    } else {
        location.href = "/operationlists/all";
    }

}


function addRow() {
    let $product = $('#product');
    let productName = $product.find(":selected").text();
    let productId = $product.find(":selected").val();
    let price = $('#operation-price').val();
    let quantity = $('#quantity').val();
    let table = $('#rows-table').DataTable();
    table.row.add([
        productName,
        price,
        quantity,
        productId
    ]).draw(true);

}

function deleteRow() {
    $('#rows-table').DataTable().row('.selected').remove().draw(true);
}

function init() {
    let table = $('#rows-table').DataTable({
        language: {
            url: '../localization/russia.json'
        },
        responsive: true,
        dom: '<"top">t<"bottom"i>',
        scrollY: '200px',
        scrollCollapse: true,
        paging: false,
        columnDefs: [

            {
                "targets": [3],
                "visible": false
            }
        ]
    });

    $('.selectpicker').select2({
        language: "ru"
    });

    $('#product').select2({
        dropdownParent: $("#add-row-modal"),
        width: '100%'
    });

    $('#rows-table tbody').on('click', 'tr', function () {
        let table = $('#rows-table').DataTable();

        if ($(this).hasClass('selected')) {
            $(this).removeClass('selected');
        } else {
            table.$('tr.selected').removeClass('selected');
            $(this).addClass('selected');
        }
    });

    $('#operation-row-form').on('submit', function (e) {
        e.preventDefault();
        addRow();
        $('#add-row-modal').modal('hide');

    });

    $('#delete-row-button').on('click', function () {
        deleteRow();
    });

    $('#operation-list-form').on('submit', function (e) {
        sendOperationList(e);
    });
}