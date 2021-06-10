$(document).ready(function () {

    let inputs = document.querySelectorAll("input");
    let i = 0;

    $(document).on('keyup', function (e) {
    if (e.key === 'Enter' || e.keyCode === 13) {
            buttonSearch();
        }
    else if (e.key === 'Tab' || e.keyCode === 9) {
        inputs[i].focus();
        i++;
        if (i >= inputs.length) {
        i = 0;
        }
    }
    else if (e.key === 'Escape' || e.keyCode === 27) {
        buttonClear();
    }
    });
});

function checkDate() {
if ($('#from').val() === '' && $('#to').val() === '') {
return null;
}
if ($('#from').val() === '' && $('#to').val() !== '') {
$('#from').val('2021-01-01T00:00:00Z');
$('#to').val(parseDate($('#to').val()));
}
else if (($('#from').val() !== '' && $('#to').val() === '')) {
let d = new Date();
let curr_date = d.getDate();
if (curr_date < 10) {
curr_date = '0' + curr_date;
}
let curr_month = d.getMonth() + 1; //Months are zero based
if (curr_month < 10) {
curr_month = '0' + curr_month;
}
let curr_year = d.getFullYear();
let hour = d.getHours();
let min = d.getMinutes();
let sec = d.getSeconds();
$('#to').val(curr_year + "-" + curr_month + "-" + curr_date + "T" + hour + ":" + min + ":" + sec + "Z");
$('#from').val(parseDate($('#from').val()));
}
else {
$('#from').val(parseDate($('#from').val()));
$('#to').val(parseDate($('#to').val()));
}
}

function parseDate(date) {
let reDate;
let a;
if (date.includes("/")) {
a = date.split("/");
}
else {
a = date.split(".");
}
reDate = a[2] + "-" + a[1] + "-" + a[0] + "T00:00:00Z";
return reDate;
}

function getTable() {
let table = $('#example').DataTable();
return table;
}

function buttonClear() {
        const inputFeilds = document.querySelectorAll("input");
        const validInputs = Array.from(inputFeilds).filter( input => input.value !== "");
        if (validInputs.length !== 0) {
        location.reload();
        }
}


function buttonSearch() {
            if (document.getElementById("from") !== null &&  document.getElementById("to") !== null) {
            checkDate();
            }
            search();
}

function search() {
            let table = $('#example').DataTable();
            let lastRowNum = table.rows().count();
            let row = table.row(lastRowNum);
            for (let i = 0; i < row.columns().count(); i++) {
                if (table.column(i).search() !== this.value) {
                    let inputs = $(table.column(i).footer()).find('input');
                    let query="";
                    inputs.each(function (j) {
                        let val = $(this).val();
                        if (val) {
                            if (j === 0) {
                                query +=val;
                            } else {
                                query +=';'+val;
                            }

                        }else{
                            query+="";
                        }
                    });
                    table
                        .column(i)
                        .search(query);
                }
            }
            table.draw();
}
