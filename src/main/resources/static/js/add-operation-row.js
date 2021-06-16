$(document).ready(function () {
    init();

    let modalElements = document.getElementsByClassName("modal-focus-element");
    let a = 0;

        $("#add-row-modal").on('keydown', function(e) {
            if (e.key === 'Tab' || e.keyCode === 9) {
                e.preventDefault();
                modalElements[a].focus();
                a++;
                if (a >= modalElements.length) {
                    a = 0;
                }
            }
            });

        $('input.modal-focus-element').on('keydown', function(e) {
            if (e.key === 'Enter' || e.keyCode === 13) {
                document.getElementById("modal-row-accept").click();
            }
        });

    $('input.enter-disable').on('keydown', function(e) {
            if (e.key === 'Enter' || e.keyCode === 13) {
                e.preventDefault();
            }
        });
});

async function sendOperationList(e) {
    e.preventDefault();
    let table = $('#rows-table').DataTable();
    let form = $('#operation-list-form');
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


function addRow(rowNum) {

    let $product = $('#product');
    let productName = $product.find(":selected").text();
    let productId = $product.find(":selected").val();
    let price = $('#operation-price').val();
    let quantity = $('#quantity').val();
    let table = $('#rows-table').DataTable();
    let data = [
        productName,
        price,
        quantity,
        productId];
    if (rowNum === "") {
        table.row.add(data).draw(true);
    } else {
        table.row(rowNum).data(data).draw(true);
    }

}


function deleteRow() {
   $('#rows-table').DataTable().row('.selected').remove().draw(true);
   $('#row-edit-btn').addClass("disabled");
   $('#row-delete-btn').addClass("disabled");
}

function editRow() {
    var row = $('#rows-table').DataTable().row('.selected');
    var data = row.data();
    var index = row.index();
    $('#product').val(data[3]).trigger('change');
    $('#quantity').val(data[2]);
    $('#operation-price').val(data[1]);
    $('#row-num').val(index);
    $('#add-row-modal').modal('show');
}

function init() {
    $('#rows-table tfoot th').each(function () {
        var title = $(this).text();
        $(this).html('<input type="text" class="form-control form-control-sm enter-disable" placeholder="Искать" />');
    });

    let table = $('#rows-table').DataTable({
        language: {
            url: '../localization/russia.json'
        },
        // responsive: true,
        dom: '<"top">t<"bottom"i>',
        scrollY: '300px',
        scrollCollapse: true,
        paging: false,
        columnDefs: [
            {
                targets: [0],
                render: function (data, type, row) {
                    if (type === 'display') {
                        return $.fn.dataTable.render.ellipsis(20)(data, type, row);
                    }
                    return data;
                }
            },
            {
                "targets": [3],
                "visible": false
            }
        ],
        initComplete: function () {
            // Apply the search
            this.api().columns().every(function () {
                var that = this;

                $('input', this.footer()).on('keyup change clear', function () {
                    if (that.search() !== this.value) {
                        that
                            .search(this.value)
                            .draw();
                    }
                });
            });
        }
    });

    $('.selectpicker').select2({
        language: "ru"
    });

    $('#product').select2({
        dropdownParent: $("#add-row-modal"),
        width: '100%'
    });

    // select row
    $('#rows-table tbody').on('click', 'tr', function () {
        var table = $('#rows-table').DataTable();
        if ($(this).hasClass('selected')) {
            $(this).removeClass('selected');
            //remove buttons
            $('#row-edit-btn').addClass("disabled");
            $('#row-delete-btn').addClass("disabled");
        } else {
            table.$('tr.selected').removeClass('selected');
            $(this).addClass('selected');
            //activate buttons
            $('#row-edit-btn').removeClass("disabled");
            $('#row-delete-btn').removeClass("disabled");
        }
    });

    $('body').on('hidden.bs.modal', '.modal', function () {
        $('#product').val("").trigger('change');
        $('#quantity').val(1);
        $('#operation-price').val(0.00);
        $('#row-num').val(null);
    });

    $('#operation-row-form').on('submit', function (e) {
        e.preventDefault();
        addRow($('#row-num').val());
        $('#add-row-modal').modal('hide');

    });

    $('#row-delete-btn').on('click', function () {
        deleteRow();
    });

    $('#row-edit-btn').on('click', function () {
        editRow();
    });


    $('#submit-row-button').on('click', function (e) {
        sendOperationList(e);
    });


}