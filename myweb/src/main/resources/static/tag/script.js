$(document).ready(function() {

	$("#resultSection").hide();
	$("#sendCorrection").hide();

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

		console.log("print")

		handleResult({result: ["Tag1", "Tag2", "Tag3", "Tag4"]})

		// $.ajax({
		// 	type: "POST",
		// 	url: "./",
		// 	data: JSON.stringify({"title": title}),
		// 	contentType: "application/json",
		// 	dataType: "json",
		// 	success: handleResult,
		// 	error: handleError
		// })
	})

	function handleResult(res) {
		const wasRight = true
		cleanResult()
		
		$("#result_mybag").addClass(wasRight ? "normal" : "normal")
		$("#result_mybag").html("The predicted tags by mybag are " + res.mybag_predictions)
		$("#result_mybag").show()

		$("#result").addClass(wasRight ? "normal" : "normal")
		// $("#result").html("The predicted tags are " + res.result)
		let resultHTML = '';
		const badTagsID = new Set();


		res.result.forEach(tag => {
			resultHTML = resultHTML + `<span id="${tag}ID" class="badge text-bg-primary">${tag}</span>`
		})
		$("#result").html(resultHTML)
		res.result.forEach(tag => {
			$(`#${tag}ID`).click(() => {
				if(badTagsID.has(`${tag}ID`)) {
					$(`#${tag}ID`).removeClass("text-bg-danger")
					$(`#${tag}ID`).addClass("text-bg-primary")
					badTagsID.delete(`${tag}ID`);
				} else {
					$(`#${tag}ID`).removeClass("text-bg-primary")
					$(`#${tag}ID`).addClass("text-bg-danger")
					badTagsID.add(`${tag}ID`);
				}
				if(badTagsID.size === 0) {
					$("#sendCorrection").hide();
				} else{
					$("#sendCorrection").show();
				}
			})
		})
		$("#result").html("The predicted tags by tfidf are " + res.tfidf_predictions)
		$("#result").show()
		$("#resultSection").show();

	}
	
	function handleError(e) {
		cleanResult()		
		$("#result").addClass("error")
		$("#result").html("An error occured (see log).")
		$("#result").show()
		$("#after").show();
	}
	
	$("textarea").on('keypress',function(e) {
		$("#result").hide()
	})
	
	$("input").click(function(e) {
		$("#result").hide()
	})
})