$(document).ready(function () {
    init();

    $("#add-bare-code-modal").on('keydown', function(e) {
            if (e.key === 'Enter' || e.keyCode === 13) {
                document.getElementById("accept-bare-code").click();
            }
        });

});

async function sendProduct(e) {
    e.preventDefault();
    let table = $('#bare-codes-table').DataTable();
    let form = $('#product-form');
    let bareCodes = [];

    table.rows().every(function () {
        let data = this.data();
        if (data.length > 0) {
            let bareCode = {};
            bareCode.idBareCode = data[0];
            bareCode.ean13 = data[1];
            bareCodes.push(bareCode);
        }
    });
    let url = form.attr("action");
    let response = await fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json;charset=utf-8'
        },
        body: JSON.stringify({
            productName: $('#productName').val(),
            numberInPack: $('#numberInPack').val(),
            tradeMark: {
                idTradeMark: $('#tradeMark').find(":selected").val()
            },
            organType: {
                idOrganType: $('#organType').find(":selected").val()
            },
            productComment: $('#productComment').val(),
            bareCodes: bareCodes
        })
    });
    let result = await response.text();
    if ($(result).find('.error-text').length) {
        form.replaceWith(result);
        init();
    } else {
        location.href = "/products/all";
    }

}

function addBareCode(bareCodeNum) {

    let ean13 = $('#bareCode').val();
    let idBareCode = $('#idBareCode').val();
    let table = $('#bare-codes-table').DataTable();
    let data = [
        idBareCode,
        ean13];
    if (bareCodeNum === "") {
        table.row.add(data).draw(true);
    } else {
        table.row(bareCodeNum).data(data).draw(true);
    }

}

function deleteBareCode() {
   $('#bare-codes-table').DataTable().row('.selected').remove().draw(true);
   $('#bare-code-edit-btn').addClass("disabled");
   $('#bare-code-delete-btn').addClass("disabled");
}

function editBareCode() {
    var row = $('#bare-codes-table').DataTable().row('.selected');
    var data = row.data();
    var index = row.index();
    $('#bareCode').val(data[1]);
    $('#idBareCode').val(data[0]);
    $('#bare-code-num').val(index);
    $('#add-bare-code-modal').modal('show');
}

function init() {

    let table = $('#bare-codes-table').DataTable({
        language: {
            url: '../localization/russia.json'
        },
        // responsive: true,
        dom: '<"top">t<"bottom"i>',
        scrollY: '300px',
        scrollCollapse: true,
        paging: false,
        order: [[0, "desc"]],
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
            { "width": "10%", "targets": 0 },
            { "width": "90%", "targets": 1 },
        ],
        initComplete: function () {
            this.api().columns().every(function () {
                var that = this;

            });
        }
    });

    $('.selectpicker').select2({
        language: "ru"
    });

    // select row
    $('#bare-codes-table tbody').on('click', 'tr', function () {
        var table = $('#bare-codes-table').DataTable();
        if ($(this).hasClass('selected')) {
            $(this).removeClass('selected');
            //remove buttons
            $('#bare-code-edit-btn').addClass("disabled");
            $('#bare-code-delete-btn').addClass("disabled");
        } else {
            table.$('tr.selected').removeClass('selected');
            $(this).addClass('selected');
            //activate buttons
            $('#bare-code-edit-btn').removeClass("disabled");
            $('#bare-code-delete-btn').removeClass("disabled");
        }
    });

    $('body').on('hidden.bs.modal', '.modal', function () {
        $('#bareCode').val('');
        $('#idBareCode').val('');
        $('#bare-code-num').val(null);
    });

    $('#bare-code-form').on('submit', function (e) {
        e.preventDefault();
        addBareCode($('#bare-code-num').val());
        $('#add-bare-code-modal').modal('hide');

    });

    $('#bare-code-delete-btn').on('click', function () {
        deleteBareCode();
    });

    $('#bare-code-edit-btn').on('click', function () {
        editBareCode();
    });


    $('#submit-product-button').on('click', function (e) {
        sendProduct(e);
    });

}