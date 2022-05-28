package myweb.ctrl;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

//import mylib.RemlaUtil;

@Controller
public class HelloWorldController {

	//private String modelHost;

	//public HelloWorldController(Environment env) {
	//	modelHost = env.getProperty("MODEL_HOST");
	//}

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
}