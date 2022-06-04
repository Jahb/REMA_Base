$(document).ready(function() {

	function getTitle() {
		return $("textarea").val().trim()
	}

	function cleanResult() {
		$("#result").removeClass("correct")
		$("#result").removeClass("incorrect")
		$("#result").removeClass("error")
		$("#result").html()
	}

	$("button").click(function (e) {
		e.stopPropagation()
		e.preventDefault()

		var title = getTitle()

		$.ajax({
			type: "POST",
			url: "./",
			data: JSON.stringify({"title": title}),
			contentType: "application/json",
			dataType: "json",
			success: handleResult,
			error: handleError	
		})
	})

	function handleResult(res) {
		const wasRight = true

		cleanResult()
		
		$("#result_mybag").addClass(wasRight ? "normal" : "normal")
		$("#result_mybag").html("The predicted tags by mybag are " + res.mybag_predictions)
		$("#result_mybag").show()

		$("#result").addClass(wasRight ? "normal" : "normal")
		$("#result").html("The predicted tags by tfidf are " + res.tfidf_predictions)
		$("#result").show()
	}
	
	function handleError(e) {
		cleanResult()		
		$("#result").addClass("error")
		$("#result").html("An error occured (see log).")
		$("#result").show()
	}
	
	$("textarea").on('keypress',function(e) {
		$("#result").hide()
	})
	
	$("input").click(function(e) {
		$("#result").hide()
	})
})