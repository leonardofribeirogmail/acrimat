var marcarDesmarcar = function(){

	let MARCARDESMARCAR = {
		init: init,
		marcarTodos: marcarTodos,
		desmarcarTodos: desmarcarTodos
	}
	
	let opcaoA = "Selecionar todos";
	let opcaoB = "Desmarcar todos";
	
	function init(){
		debugger;
		if(deveMarcarOuDesmarcar()){
			getSpan().text(opcaoA);
			PF("selectBooleanCheckBoxSelecionarTodos").uncheck();
			getBotao().on("click", function(){
				marcarDesmarcar.marcarTodos();
			});
		} else {
			getSpan().text(opcaoB);
			PF("selectBooleanCheckBoxSelecionarTodos").check();
			getBotao().on("click", function(){
				marcarDesmarcar.desmarcarTodos();
			});
		}
	}
	
	function marcarTodos(){
		marcarOuDesmacarTodos(true);
		getSpan().text(opcaoB);
		getBotao().on("click", function(){
			marcarDesmarcar.desmarcarTodos();
		});
	}
	
	function desmarcarTodos(){
		marcarOuDesmacarTodos(false);
		getSpan().text(opcaoA);
		getBotao().on("click", function(){
			marcarDesmarcar.marcarTodos();
		});
	}
	
	function getBotao() {
		return $(".selectBooleanCheckBoxSelecionarTodos");
	}
	
	function getSpan(){
		let botao = getBotao();
		return botao.find(".ui-chkbox-label");
	}
	
	function marcarOuDesmacarTodos(marcar){
		let inputs = PF("selectManyCheckBoxOpcao").inputs;
		$(inputs).each(function(index){
		    let input = $(this);
		    let checado = input.is(":checked");
		    if((marcar && !checado) || (!marcar && checado)) {
		    	input.trigger("click");
		    }
		});
	}
	
	function deveMarcarOuDesmarcar() {
		let inputs = PF("selectManyCheckBoxOpcao").inputs;
		let quantidade = 0;
		$(inputs).each(function(index){
		    let input = $(this);
		    let checado = input.is(":checked");
		    if(checado){
		    	quantidade++;
		    }
		});
		
		return !(quantidade === inputs.length);
	}
	
	return MARCARDESMARCAR;
}();

function setarLoadingRelatorio() {
	componenteLoading = ".loading-relatorio";
}

function retornarLoading() {
	componenteLoading = ".loading";
}