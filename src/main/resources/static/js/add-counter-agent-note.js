$(document).ready(function () {
    init();

});

async function sendCounterAgent(e) {
    e.preventDefault();
    let table = $('#counter-agents-notes-table').DataTable();
    let form = $('#counter-agent-form');
    let counterAgentNotes = [];

    table.rows().every(function () {
        let data = this.data();
        if (data.length > 0) {
            let counterAgentNote = {};
            counterAgentNote.idCounterAgentNote = data[0];
            counterAgentNote.note = data[1];
            counterAgentNotes.push(counterAgentNote);
        }
    });
    let url = form.attr("action");
    let response = await fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json;charset=utf-8'
        },
        body: JSON.stringify({
            counterAgentProfile: $('#counterAgentProfile').val(),
            counterAgentPhone: $('#counterAgentPhone').val(),
            counterAgentName: $('#counterAgentName').val(),
            counterAgentType: {
                idCounterAgentType: $('#counterAgentType').find(":selected").val()
            },
            counterAgentNotes: counterAgentNotes
        })
    });
    let result = await response.text();
    if ($(result).find('.error-text').length) {
        form.replaceWith(result);
        init();
    } else {
        location.href = "/counteragents/all";
    }

}

function addCounterAgentNote(counterAgentNoteNum) {

    let idCounterAgentNote = $('#idCounterAgentNote').val();
    let note = $('#note').val();
    let table = $('#counter-agents-notes-table').DataTable();
    let data = [
        idCounterAgentNote,
        note];
    if (counterAgentNoteNum === "") {
        table.row.add(data).draw(true);
    } else {
        table.row(counterAgentNoteNum).data(data).draw(true);
    }

}

function deleteCounterAgentNote() {
   $('#counter-agents-notes-table').DataTable().row('.selected').remove().draw(true);
   $('#counter-agent-note-edit-btn').addClass("disabled");
   $('#counter-agent-note-delete-btn').addClass("disabled");
}

function editCounterAgentNote() {
    var row = $('#counter-agents-notes-table').DataTable().row('.selected');
    var data = row.data();
    var index = row.index();
    $('#idCounterAgentNote').val(data[0]);
    $('#note').val(data[1]);
    $('#counter-agent-note-num').val(index);
    $('#add-counter-agent-note-modal').modal('show');
}

function init() {

    let table = $('#counter-agents-notes-table').DataTable({
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
    $('#counter-agents-notes-table tbody').on('click', 'tr', function () {
        var table = $('#counter-agents-notes-table').DataTable();
        if ($(this).hasClass('selected')) {
            $(this).removeClass('selected');
            //remove buttons
            $('#counter-agent-note-edit-btn').addClass("disabled");
            $('#counter-agent-note-delete-btn').addClass("disabled");
        } else {
            table.$('tr.selected').removeClass('selected');
            $(this).addClass('selected');
            //activate buttons
            $('#counter-agent-note-edit-btn').removeClass("disabled");
            $('#counter-agent-note-delete-btn').removeClass("disabled");
        }
    });

    $('body').on('hidden.bs.modal', '.modal', function () {
        $('#note').val('');
        $('#counter-agent-note-num').val(null);
    });

    $('#counter-agent-note-form').on('submit', function (e) {
        e.preventDefault();
        addCounterAgentNote($('#counter-agent-note-num').val());
        $('#add-counter-agent-note-modal').modal('hide');

    });

    $('#counter-agent-note-delete-btn').on('click', function () {
        deleteCounterAgentNote();
    });

    $('#counter-agent-note-edit-btn').on('click', function () {
        editCounterAgentNote();
    });


    $('#counter-agent-form').on('submit', function (e) {
        sendCounterAgent(e);
    });

}