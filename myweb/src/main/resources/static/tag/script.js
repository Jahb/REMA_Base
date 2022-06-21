$(document).ready(function() {

	$("#resultSection").hide();
	$("#thanks").hide();
	//$("#sendCorrection").hide();
	$("#loading").hide();

	function getTitle() {
		return $("#inputField").val().trim();
	}

	function arrayRemove(arr, value) { 
    
        return arr.filter(function(ele){ 
            return ele != value; 
        });
    }

	function getNewTags() {
		const input = $("#newTags").val().trim();
		const tagArray = input.toLowerCase().split(", ");
		console.log(tagArray);
		return tagArray;
	}

	function cleanResult() {
		$("#result").removeClass("correct")
		$("#result").removeClass("incorrect")
		$("#result").removeClass("error")
		$("#result").html()
	}

	$("#predictButton").click(function (e) {
		e.stopPropagation()
		e.preventDefault()

		$("#predictButton").hide();
		$("#loading").show();


		var title = getTitle()
		myBagTagSet.clear();
		tfidfTagSet.clear();
		unionSet.clear();
		badTags.clear();
		goodTags.clear();


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

	//TODO send out the corrected predections.
	$("#sendCorrection").click((e) => {
		e.stopPropagation()
		e.preventDefault()
		$("#resultSection").hide();
		$("#thanks").show();

		var myBagBadTags = [];
		var tfidfBadTags = [];

		var myBagGoodTags = [];
		var tfidfGoodTags = [];

		badTags.forEach(tag => {
			if(myBagTagSet.has(tag)) {
				myBagBadTags.push(tag);
			}
			if(tfidfTagSet.has(tag)) {
				tfidfBadTags.push(tag);
			}
		});

		goodTags.forEach(tag => {
			if(myBagTagSet.has(tag)) {
				myBagGoodTags.push(tag);
			}
			if(tfidfTagSet.has(tag)) {
				tfidfGoodTags.push(tag);
			}
		});

		missed = getNewTags();
		console.log("Bad Tags myBag:")
		console.log(myBagBadTags);
		console.log("Bad Tags tfidfBadTags:")
		console.log(tfidfBadTags);


		console.log("Good Tags myBag:")
		console.log(myBagGoodTags);
		console.log("Good Tags tfidfBadTags:")
		console.log(tfidfGoodTags);

		tfidfBadTags.forEach(tag => {
			if(tfidfGoodTags.includes(tag)) {
				arrayRemove(tfidfGoodTags,tag);
			}
		});

		// for (index = 0; index < tfidfBadTags.length; index++) {
		// 	console.log(array[index]);
		// }

		myBagBadTags.forEach(tag => {
			if(myBagGoodTags.includes(tag)) {
				arrayRemove(myBagGoodTags,tag);
			}
		});
		
		$.ajax({
			type: "POST",
			dataType: 'json',
			contentType: 'application/json',
			url: "/tag/correct",
			data: JSON.stringify({"tfidfBadTags": tfidfBadTags, "tfidfGoodTags" : tfidfGoodTags, "myBagBadTags" : myBagBadTags, "myBagGoodTags" : myBagGoodTags, "missed": missed}),
			success:function(data){
				console.log(data);
			}
		})
	})

	const myBagTagSet = new Set();
	const tfidfTagSet = new Set();
	const unionSet = new Set();
	const badTags = new Set();
	const goodTags = new Set();

	function handleResult(res) {
		$("#predictButton").show();
		$("#loading").hide();

		const wasRight = true
		cleanResult()


		$("#result").addClass(wasRight ? "normal" : "normal")
		// $("#result").html("The predicted tags are " + res.result)

		buildSets(res.mybag_predictions, res.tfidf_predictions)

		proccessTags(unionSet);

		badTags.forEach(element => {
			if(goodTags.has(element)){
				goodTags.delete(element);
			}
		  });

		$("#result").show()
		$("#thanks").hide();
		$("#resultSection").show();

	}

	function buildSets(mybagArr, tfidfArr) {
		mybagArr.forEach(tag => {
			myBagTagSet.add(tag);
			unionSet.add(tag);
		})

		tfidfArr.forEach(tag => {
			tfidfTagSet.add(tag);
			unionSet.add(tag);
		})

		console.log(mybagArr);
		console.log(tfidfArr);

	}

	function proccessTags(tagSet) {

		let resultHTML = '';

		tagSet.forEach(tag => {
			resultHTML = resultHTML + `<span id="${tag}ID" class="badge text-bg-primary my-2 mx-1">${tag}</span>`
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
				// if(badTags.size === 0) {
				// 	$("#sendCorrection").hide();
				// } else{
				//$("#sendCorrection").show(); //might need to comment out
				// }
			})
		})

		tagSet.forEach(tag => {
			if(!badTags.has(tag)) {
				goodTags.add(tag);
			}
		})
	}
	
	function handleError(e) {
		cleanResult()		
		$("#result").addClass("error")
		$("#result").html("An error occured (see log).")
		$("#result").show();
		$("#after").show();
	}
	
	// $("#inputField").on('keypress',function(e) {
	// 	$("#result").hide()
	// })
	
	// $("input").click(function(e) {
	// 	$("#result").hide()
	// })
})