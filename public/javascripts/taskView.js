localOffset = (new Date()).getTimezoneOffset() * 60000;

jQuery(document).ready(function($) {
	$(".date").each(function(){
		$(this).datetimepicker();
		
		var timeInput = $(this).children(":input");
		var time = timeInput.val();
		if(time.indexOf('/') == -1){
			var d = new Date(parseInt(time));
			if(d != "Invalid Date"){
				var d2 = new Date((d.getMonth()+1)+"/"+d.getDate()+"/"+d.getFullYear()+" "+(d.getHours())+":"+(d.getMinutes())+":"+(d.getSeconds())+" UTC");
				timeInput.attr('value', (moment(d2).format('MM/DD/YYYY h:mm A')));
				timeInput.trigger('change');
			}
		}
	});
	
	$("#save").on('click', function(){
		startTime = moment($("#fakestart").val()).valueOf() + localOffset;
		endTime = moment($("#fakeend").val()).valueOf() + localOffset;
		$("#start").val(startTime);
		$("#end").val(endTime);
	});
});