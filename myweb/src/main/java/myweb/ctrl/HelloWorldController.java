package myweb.ctrl;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

//import mylib.RemlaUtil;

@Controller
public class HelloWorldController {

	private int tfidfCorrectTimes;

	//public HelloWorldController(Environment env) {
	//	modelHost = env.getProperty("MODEL_HOST");
	//}

	public void addTfidfCorrect() {
		tfidfCorrectTimes++;
	}

	@GetMapping("/")
	@ResponseBody
	public String index() {
		var sb = new StringBuilder();
		sb.append("Hello World!<br /><br />");

	//	sb.append("Model host: ").append(modelHost).append("<br/>");
		//sb.append("Hostname: ").append(RemlaUtil.getHostName()).append("<br/>");
		//sb.append("Version: ").append(RemlaUtil.getUtilVersion()).append("<br/>");

		return sb.toString();
	}

	@GetMapping(value = "/metrics", produces = "text/plain")
	@ResponseBody
	public String metrics() {
		var sb = new StringBuilder();

		sb.append("# HELP my_random A random number\n");
		sb.append("# TYPE my_random gauge\n");
		sb.append("my_random ").append(Math.random()).append("\n\n");

		sb.append("# HELP tfidf_correct Number of times tfidf is correct\n");
		sb.append("# TYPE tfidf_correct counter\n");
		sb.append("tfidf_correct ").append(tfidfCorrectTimes).append("\n\n");

		return sb.toString();
	}
}