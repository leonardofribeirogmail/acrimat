var converter = function(){

	var arquivo = {
		toString: tostring
	}
	
	let toBase64 = file => new Promise((resolve, reject) => {
    	let reader = new FileReader();
    	reader.readAsDataURL(file);
    	reader.onload = () => resolve(reader.result);
    	reader.onerror = error => reject(error);
	});
	
	function tostring() {
	
		let promessa = $.Deferred();
		
		promessa.done(function(result){
			var nomeCompleto = $(".arquivo").val();
			var nomeCompletoArray = nomeCompleto.split("\\");
			var nome = nomeCompletoArray[nomeCompletoArray.length-1];
			
			const base64String = result.replace('data:', '').replace(/^.+,/, '');
		
			$("#arquivoBase64").val(base64String);
			$("#arquivoNome").val(encodeURIComponent(nome));
			$(".hiddenButtonCarregar").trigger("click");
		});
		
		promessa.fail(function(span){
			$("#arquivoBase64").val("");
			$("#arquivoNome").val("");
			$("."+span).removeClass("hidden");
			monitor.stop();
		});
	
		componente(promessa);
	}
	
	function componente(promessa) {
		$(".arquivoerro").removeClass("hidden").addClass("hidden");
		
		let arquivo = document.querySelector(".arquivo");
		if(arquivo.files.length > 0) {
		
			let file = document.querySelector(".arquivo").files[0];
			
			if(file.size <= 10485760) {
				$.when(toBase64(file))
				.done(function(result){
					promessa.resolve(result);
				}).fail(function(){
					promessa.reject('spanarquivonaoprocessado');
				});
			} else {
				promessa.reject('spanarquivoexcedido');
			}
		} else {
			promessa.reject('spanarquivoerro');
		}
	}
	
	return arquivo;
}();