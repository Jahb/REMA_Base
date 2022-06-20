package myweb.ctrl;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import myweb.ctrl.TagController.TagMetrics;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;

//import mylib.RemlaUtil;

@Controller
public class HelloWorldController {

	private ArrayList<Integer> tfidfCorrectTimes;
	private ArrayList<Integer> tfidfIncorrectTimes;
	private ArrayList<Integer> mybagCorrectTimes;
	private ArrayList<Integer> mybagIncorrectTimes;
	private HashMap<String, TagMetrics> metricsMap;

	private int totalTfidfCorrectTimes = 0;
	private int totalTfidfIncorrectTimes = 0;
	private int totalMybagCorrectTimes = 0;
	private int totalMybagIncorrectTimes = 0;
	private int totalMissed =0;

	private int it_count=0;

	public HelloWorldController(Environment env) {
		tfidfCorrectTimes = new ArrayList<>();
		tfidfIncorrectTimes = new ArrayList<>();
		mybagCorrectTimes = new ArrayList<>();
		mybagIncorrectTimes = new ArrayList<>();
		metricsMap = new HashMap<>();
	}

	public void setModelMetrics(int tfidfcorrect, int tfidfincorrect, int mybagcorrect, int mybagincorrect, int missed, HashMap<String, TagMetrics> map) {
		tfidfCorrectTimes.add(tfidfcorrect);
		tfidfIncorrectTimes.add(tfidfincorrect);
		mybagCorrectTimes.add(mybagcorrect);
		mybagIncorrectTimes.add(mybagincorrect);

		totalTfidfCorrectTimes += tfidfcorrect;
		totalTfidfIncorrectTimes += tfidfincorrect;
		totalMybagIncorrectTimes += mybagincorrect;
		totalMybagCorrectTimes += mybagcorrect;
		totalMissed +=missed;

		it_count++;

		for(String key: map.keySet()){
			if(metricsMap.containsKey(key)){
				metricsMap.get(key).combine(map.get(key));
			}
			else{
				metricsMap.put(key, map.get(key));
			}
		}
	}

	@GetMapping("/")
	@ResponseBody
	public String index() {
		var sb = new StringBuilder();
		sb.append("Hello World!<br /><br />");

		//sb.append("Model host: ").append(modelHost).append("<br/>");
		//sb.append("Hostname: ").append(RemlaUtil.getHostName()).append("<br/>");
		//sb.append("Version: ").append(RemlaUtil.getUtilVersion()).append("<br/>");

		return sb.toString();
	}

	@GetMapping(value = "/metrics", produces = "text/plain")
	@ResponseBody
	public String metrics() {
		var sb = new StringBuilder();
		
		sb.append("# HELP it_count A counter for the number of preditcions\n");
		sb.append("# TYPE it_count counter\n");
		sb.append("it_count ").append(it_count).append("\n\n");

		sb.append("# HELP my_random A random number - used for debugging\n");
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

		sb.append("# HELP totalMybagIncorrectTimes Total Number of times mybag is correct\n");
		sb.append("# TYPE totalMybagIncorrectTimes counter\n");
		sb.append("totalMybagIncorrectTimes ").append(totalMybagIncorrectTimes).append("\n\n");

		sb.append("# HELP totalMissed Total Number of times tag was missing\n");
		sb.append("# TYPE totalMissed counter\n");
		sb.append("totalMissed ").append(totalMissed).append("\n\n");

		sb.append("# HELP tagMetric Counts the appearances of each tag\n");
		sb.append("# TYPE tagMetric counter\n");
        
		// for (String name: metricsMap.keySet()) {
		// 	String key = name.toString();
		// 	String value = metricsMap.get(name).toString();
		// 	System.out.println(key + " " + value);
		// }
		
		// System.out.println("map size");
		// System.out.println(metricsMap.keySet().size());
		// System.out.println("metrics map");
		for(String key: metricsMap.keySet()){
			sb.append("tagMetric{tag_name=\"").append(key)
			.append("\",model=\"TF-IDF\",correctness=\"correct\"} ").append(metricsMap.get(key).tftidfcorrect).append("\n");
			sb.append("tagMetric{tag_name=\"").append(key)
			.append("\",model=\"TF-IDF\",correctness=\"incorrect\"} ").append(metricsMap.get(key).tfidfincorrect).append("\n");
			sb.append("tagMetric{tag_name=\"").append(key)
			.append("\",model=\"Bag-of-words\",correctness=\"correct\"} ").append(metricsMap.get(key).mybagcorrect).append("\n");
			sb.append("tagMetric{tag_name=\"").append(key)
			.append("\",model=\"Bag-of-words\",correctness=\"incorrect\"} ").append(metricsMap.get(key).mybagincorrect).append("\n");
			sb.append("tagMetric{tag_name=\"").append(key)
			.append("\",model=\"both\",correctness=\"missed\"} ").append(metricsMap.get(key).missed).append("\n\n"); //might need to make it a single new line
		}

		// sb.append("# HELP tagMetric1 Counts the appearances of each tag\n");
		// sb.append("# TYPE tagMetric1 counter\n");
		// sb.append("tagMetric1{tag_name=\"java\",model=\"TF-IDF\",correctness=\"correct\"} ").append(javatf).append("\n");
		// sb.append("tagMetric1{tag_name=\"java\",model=\"TF-IDF\",correctness=\"incorrect\"} ").append(javatfin).append("\n");

		// sb.append("tagMetric1{tag_name=\"c\",model=\"TF-IDF\",correctness=\"correct\"} ").append(ctf).append("\n");
		// sb.append("tagMetric1{tag_name=\"c\",model=\"TF-IDF\",correctness=\"incorrect\"} ").append(ctfin).append("\n");

		buildTags(sb);
		return sb.toString();
	}

	public static void buildTags(StringBuilder sb){
		

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