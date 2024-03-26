let dialogState;
let componenteLoading = ".loading";

$(document).on("keyup", function(event){
	var button = $(".botaoEntrar");
	if(event.keyCode === 13 && button) {
		event.preventDefault();
		$(button).trigger("click");
	}
});

function saveDialogState(componenteClasse) {
	dialogState = $("."+componenteClasse).find(".scrollable");
}

var dialogAjuste = function(){
	
};

var loading = function(){
	
	let LOADING = {
		on: on,
		off: off
	}
	
	function on() {
		getLoading().removeClass("hidden");
	}
	
	function off() {
		getLoading().addClass("hidden");
	}
	
	function getLoading() {
		return $(componenteLoading);
	}
	
	return LOADING;
}();

$(window).on("resize", function(){
	dialogAjuste();
}).on("mouseover", function(){
	dialogAjuste();
});