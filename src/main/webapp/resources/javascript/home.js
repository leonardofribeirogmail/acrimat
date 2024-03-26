var monitor = function(){
	var STATUS = {
		start: start,
		stop: stop
	};
	
	function start() {
		PF('blockUIWidget').block();
	}
	
	function stop() {
		PF('blockUIWidget').unblock();
	}
	
	return STATUS;
}();