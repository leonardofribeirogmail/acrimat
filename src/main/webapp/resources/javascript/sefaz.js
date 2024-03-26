function selecionar() {
	var form = document.getElementById("formDocumento");
	form.numrDoct.value = numrIe;
	form.pagn.value = "FormularioListaEnviar";
	form.target = "_blank";
	form.submit();
	document.form.numrDoct.clear();
}

function submeterInscricaoEstadual(inscricao) {
	$("#formSubmitDocumentoSefaz").remove();
	$(".numrDoct").val(inscricao.replace(/[^\d]/g, ''));
	let form = $(".formSubmitDocumentoSefaz").clone();
	form.attr("id", "formSubmitDocumentoSefaz");
	document.body.appendChild(form[0]);
	$("#formSubmitDocumentoSefaz").submit();
}