window.addEventListener("load", function(){
	$(window).on("resize", function(){
		onResize();
	});
});

function onDataTableScroll() {
	var objeto = $(".ui-datatable-scrollable-body")[0];
	objeto.addEventListener("scroll", function(){
		onResize();
	});
}

var onTabChange = function(){
	var altura = $('.outputPanelAssociadoviewer').outerHeight();
	var largura = $('.outputPanelAssociadoviewer').outerWidth();
	
	$('.outputPanelAssociadoTab').outerHeight(altura);
	$('.outputPanelAssociadoTab').outerWidth(largura);
};

var onResize = function() {
	var height = window.innerHeight;
	var novoHeight = height - 152;
	novoHeight = novoHeight <= 0 ? 100 : novoHeight;
	$(".ui-datatable-scrollable-body").css({"max-height":novoHeight});
};

var monitor = function(){
	
	var MONITOR = {
		start: start,
		stop: stop
	}
	
	function start() {
		PF('blockUIDatatable').block();
	}
	
	function stop() {
		$.when(PF('blockUIDatatable').unblock()).then(function(){
			onDataTableScroll();
			onResize();
		});
	}
	
	return MONITOR;
}();

var onCloseSefaz = function(){
	var ONCLOSE = {
		ativar:ativar,
		desativar:desativar
	}
	
	function ativar() {
		$(".ui-dialog-titlebar-close").on("click", function(){
			remoteCommandVoltarSefaz();
		});
	}
	
	function desativar() {
		$(".ui-dialog-titlebar-close").unbind("click");
		$(".ui-dialog-titlebar-close").on("click", function(){
			PF('dialog').hide();
		});
	}
	
	return ONCLOSE;
}();
