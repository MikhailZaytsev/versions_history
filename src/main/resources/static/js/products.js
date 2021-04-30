
$(document).ready(function() {

  var trigger = $('.hamburger'),
      overlay = $('.overlay'),
     isClosed = false;

    trigger.click(function () {
      hamburger_cross();
    });

    function hamburger_cross() {

      if (isClosed == true) {
        overlay.hide();
        trigger.removeClass('is-open');
        trigger.addClass('is-closed');
        isClosed = false;
      } else {
        overlay.show();
        trigger.removeClass('is-closed');
        trigger.addClass('is-open');
        isClosed = true;
      }
  }

  $('[data-toggle="offcanvas"]').click(function () {
        $('#wrapper').toggleClass('toggled');
  });




$.fn.dataTable.ext.classes.sPageButton = 'btn btn-light btn-sm';

//  Setup - add a text input to each footer cell
//    $('#example thead tr').clone(true).appendTo( '#example thead' );
    $('#example tfoot th').each( function (i) {
        var title = $(this).text();
        $(this).html( '<input type="text" style="  width: 100%;box-sizing: border-box;" placeholder="Искать '+title+'" />' );

        $( 'input', this ).on( 'keyup change', function () {
            if ( table.column(i).search() !== this.value ) {
                table
                    .column(i)
                    .search( this.value )
                    .draw();
            }
        } );
    } );

    var table = $('#example').DataTable({
     "dom": '<"top"i>rt<"bottom"lp><"clear">',
    language: {
                url: '../localization/products/russia.json'
            },
             responsive: true,
            "processing": true,
            "serverSide": true,
            "ajax": {
                "url": "/products",
                "type": "POST",
                "dataType": "json",
                "contentType": "application/json",
                "data": function (d) {
                 return JSON.stringify(d);
                }
            },
             "order": [
             [ 0, "desc" ]
             ],
                   "columns": [
                {"data": "idProduct", "width": "10%"},
                {"data": "productName","width": "20%"},
                {"data": "tradeMark.tradeMarkName", "width": "20%"},
                {"data": "organType.organTypeName", "width": "10%"},
                {"data": "numberInPack", "width": "10%"},
                {"data": "productComment", "width": "20%"},
                {"data": "inactive", "width": "10%"}
                ],
                columnDefs: [ {
                        targets: [4,5],
                        render: function(data, type, row) {
                                          if ( type === 'display') {
                                            return $.fn.dataTable.render.ellipsis(20)(data, type, row);
                                          }
                                          return data;
                                        }
                    } ,
                    {

                    render: function(data){
                     return '<a  href="/products/edit?id='+data+'" role="button">'+
                     '<i class="fa fa-pencil" aria-hidden="true"></i></a> <a href="/products/delete?id='+data+'" class="pl-1 pr-3" role="button"><i class="fa fa-trash" aria-hidden="true"></i></a>'+data;},
                     targets: [0]}
                    ]

        });

} );


/**
 * This data rendering helper method can be useful for cases where you have
 * potentially large data strings to be shown in a column that is restricted by
 * width. The data for the column is still fully searchable and sortable, but if
 * it is longer than a give number of characters, it will be truncated and
 * shown with ellipsis. A browser provided tooltip will show the full string
 * to the end user on mouse hover of the cell.
 *
 * This function should be used with the `dt-init columns.render` configuration
 * option of DataTables.
 *
 * It accepts three parameters:
 *
 * 1. `-type integer` - The number of characters to restrict the displayed data
 *    to.
 * 2. `-type boolean` (optional - default `false`) - Indicate if the truncation
 *    of the string should not occur in the middle of a word (`true`) or if it
 *    can (`false`). This can allow the display of strings to look nicer, at the
 *    expense of showing less characters.
 * 2. `-type boolean` (optional - default `false`) - Escape HTML entities
 *    (`true`) or not (`false` - default).
 *
 *  @name ellipsis
 *  @summary Restrict output data to a particular length, showing anything
 *      longer with ellipsis and a browser provided tooltip on hover.
 *  @author [Allan Jardine](http://datatables.net)
 *  @requires DataTables 1.10+
 *
 * @returns {Number} Calculated average
 *
 *  @example
 *    // Restrict a column to 17 characters, don't split words
 *    $('#example').DataTable( {
 *      columnDefs: [ {
 *        targets: 1,
 *        render: $.fn.dataTable.render.ellipsis( 17, true )
 *      } ]
 *    } );
 *
 *  @example
 *    // Restrict a column to 10 characters, do split words
 *    $('#example').DataTable( {
 *      columnDefs: [ {
 *        targets: 2,
 *        render: $.fn.dataTable.render.ellipsis( 10 )
 *      } ]
 *    } );
 */

jQuery.fn.dataTable.render.ellipsis = function ( cutoff, wordbreak, escapeHtml ) {
	var esc = function ( t ) {
		return t
			.replace( /&/g, '&amp;' )
			.replace( /</g, '&lt;' )
			.replace( />/g, '&gt;' )
			.replace( /"/g, '&quot;' );
	};

	return function ( d, type, row ) {
		// Order, search and type get the original data
		if ( type !== 'display' ) {
			return d;
		}

		if ( typeof d !== 'number' && typeof d !== 'string' ) {
			return d;
		}

		d = d.toString(); // cast numbers

		if ( d.length <= cutoff ) {
			return d;
		}

		var shortened = d.substr(0, cutoff-1);

		// Find the last white space character in the string
		if ( wordbreak ) {
			shortened = shortened.replace(/\s([^\s]*)$/, '');
		}

		// Protect against uncontrolled HTML input
		if ( escapeHtml ) {
			shortened = esc( shortened );
		}

		return '<span class="ellipsis" title="'+esc(d)+'">'+shortened+'&#8230;</span>';
	};
};