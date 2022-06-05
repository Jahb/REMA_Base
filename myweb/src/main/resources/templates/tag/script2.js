$(document).ready(function() {

    $("#after").hide();

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
        $("#result").addClass(wasRight ? "normal" : "normal")
        $("#result").html("The predicted tags are " + res.result)
        $("#result").show()
        $("#after").show();

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