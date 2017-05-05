function draw(){
	$.get('data.csv', function(csv) {
	    $('#salaryContainer').highcharts({
	        chart: {
	        	type: 'line',
	        	zoomType: 'x',
	        },
	        data: {
	            csv: csv,
	        },
	        title: {
				text: 'Salary'
			},
			xAxis: {
    			type: 'datetime'
			},
			yAxis: {
				title: {
					text: 'Salary in €'
				},
				labels: {
		            formatter: function() {
		                return '€' + this.value;
		            }
		        }
			}
	    });
	});
	
	$.get('holidays.csv', function(holidays) {
	    $('#holidaysContainer').highcharts({
	        chart: {
	        	type: 'line',
	        	zoomType: 'x',
	        },
	        data: {
	            csv: holidays
	        },
	        title: {
				text: 'Holidays'
			},
			xAxis: {
    			type: 'datetime'
			},
			yAxis: {
				title: {
					text: 'Days of holidays'
				}
			}
	    });
	});
	
	$.get('leaves.csv', function(leaves) {
	    $('#leavesContainer').highcharts({
	        chart: {
	        	type: 'line',
	        	zoomType: 'x',
	        },
	        data: {
	            csv: leaves
	        },
	        title: {
				text: 'Leaves'
			},
			xAxis: {
    			type: 'datetime'
			},
			yAxis: {
				title: {
					text: 'Hours of leaves'
				}
			}
	    });
	});
};

$(document).ready(draw());