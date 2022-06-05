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

		handleResult({mybag_predictions: ["Tag1", "Tag2", "Tag3", "Tag4"], tfidf_predictions: ["Tag3", "Tag4", "Tag5", "Tag6", "Tag7"]})

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

	const myBagTagSet = new Set();
	const tfidfTagSet = new Set();
	const unionSet = new Set();
	const badTags = new Set();

	function handleResult(res) {
		const wasRight = true
		cleanResult()


		$("#result").addClass(wasRight ? "normal" : "normal")
		// $("#result").html("The predicted tags are " + res.result)

		buildSets(res.mybag_predictions, res.tfidf_predictions)

		proccessTags(unionSet);

		$("#result").show()
		$("#resultSection").show();

	}

	function buildSets(mybagArr, tfidfArr) {
		mybagArr.forEach(tag => {
			myBagTagSet.add(tag);
			unionSet.add(tag);
		})

		tfidfArr.forEach(tag => {
			tfidfTagSet.add(tag);
			unionSet.add(tag)
		})

	}

	function proccessTags(tagSet) {

		let resultHTML = '';

		tagSet.forEach(tag => {
			resultHTML = resultHTML + `<span id="${tag}ID" class="badge text-bg-primary">${tag}</span>`
		})
		$("#result").html(resultHTML)
		tagSet.forEach(tag => {
			$(`#${tag}ID`).click(() => {
				if(badTags.has(tag)) {
					$(`#${tag}ID`).removeClass("text-bg-danger")
					$(`#${tag}ID`).addClass("text-bg-primary")
					badTags.delete(tag);
				} else {
					$(`#${tag}ID`).removeClass("text-bg-primary")
					$(`#${tag}ID`).addClass("text-bg-danger")
					badTags.add(tag)
				}
				if(badTags.size === 0) {
					$("#sendCorrection").hide();
				} else{
					$("#sendCorrection").show();
				}
			})
		})
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