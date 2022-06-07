package myweb.ctrl;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;

//import mylib.RemlaUtil;

@Controller
public class HelloWorldController {

	private ArrayList<Integer> tfidfCorrectTimes;
	private ArrayList<Integer> tfidfIncorrectTimes;
	private ArrayList<Integer> mybagCorrectTimes;
	private ArrayList<Integer> mybagIncorrectTimes;

	private int totalTfidfCorrectTimes = 0;
	private int totalTfidfIncorrectTimes = 0;
	private int totalMybagCorrectTimes = 0;
	private int totalMybagIncorrectTimes = 0;


	public HelloWorldController(Environment env) {
		tfidfCorrectTimes = new ArrayList<>();
		tfidfIncorrectTimes = new ArrayList<>();
		mybagCorrectTimes = new ArrayList<>();
		mybagIncorrectTimes = new ArrayList<>();
	}

	public void setModelMetrics(int tfidfcorrect, int tfidfincorrect, int mybagcorrect, int mybagincorrect) {
		tfidfCorrectTimes.add(tfidfcorrect);
		tfidfIncorrectTimes.add(tfidfincorrect);
		mybagCorrectTimes.add(mybagcorrect);
		mybagIncorrectTimes.add(mybagincorrect);

		totalTfidfCorrectTimes += tfidfcorrect;
		totalTfidfIncorrectTimes += tfidfincorrect;
		totalMybagIncorrectTimes += mybagincorrect;
		totalMybagCorrectTimes += mybagcorrect;
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

		sb.append("# HELP totalTfidfCorrectTimes Total Number of times tfidf is correct\n");
		sb.append("# TYPE totalTfidfCorrectTimes counter\n");
		sb.append("totalTfidfCorrectTimes ").append(totalTfidfCorrectTimes).append("\n\n");

		sb.append("# HELP totalTfidfIncorrectTimes Total Number of times tfidf is incorrect\n");
		sb.append("# TYPE totalTfidfIncorrectTimes counter\n");
		sb.append("totalTfidfIncorrectTimes ").append(totalTfidfIncorrectTimes).append("\n\n");

		sb.append("# HELP totalMybagCorrectTimes Total Number of times mybag is correct\n");
		sb.append("# TYPE totalMybagCorrectTimes counter\n");
		sb.append("totalMybagCorrectTimes ").append(totalMybagCorrectTimes).append("\n\n");

		sb.append("# HELP  Total Number of times mybag is correct\n");
		sb.append("# TYPE totalMybagIncorrectTimes counter\n");
		sb.append("totalMybagIncorrectTimes ").append(totalMybagIncorrectTimes).append("\n\n");

		return sb.toString();
	}


	public class TagCorrection {
		public String[] mybag_correct;
		public String[] mybag_incorrect;

		public String[] tfidf_correct;
		public String[] tfidf_incorrect;

		public TagCorrection(String[] mybag_correct, String[] mybag_incorrect, String[] tfidf_correct, String[] tfidf_incorrect)
		{
			this.mybag_correct = mybag_correct;
			this.mybag_incorrect = mybag_incorrect;
			this.tfidf_correct = tfidf_correct;
			this.tfidf_incorrect = tfidf_incorrect;
		}
	}
}