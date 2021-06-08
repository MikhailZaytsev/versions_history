$(document).ready(function () {

    let inputs = document.querySelectorAll("input");
    let i = 0;

    $('#search-button').on('click', function () {
            buttonSearch();
        });

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


function getTable() {
let table = $('#example').DataTable();
return table;
}

function buttonClear() {
        $(':input').val('');
        location.reload();
}


function buttonSearch() {
            let table = getTable();
            search(table);
}

function search(table) {
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
