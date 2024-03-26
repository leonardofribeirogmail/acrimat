$(".inputMaskTelefone").on("keypress", function(event){
	if(event.keyCode === 13){
		$(".commandButtonAdicionar").trigger("click");
	}
});