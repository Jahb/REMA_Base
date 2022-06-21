package myweb.ctrl;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import myweb.ctrl.TagController.TagMetrics;

import java.util.ArrayList;
import java.util.HashMap;

//import mylib.RemlaUtil;

@Controller
public class MainController {

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

	public MainController(Environment env) {
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
        
		int tfidfApp = 0;
		float tfidfRecall = 0;
		float tfidfPrecision = 0;

		int mybagApp = 0;
		float mybagRecall = 0;
		float mybagPrecision = 0;

		for(String key: metricsMap.keySet()){
			TagMetrics active = metricsMap.get(key);
			active.computePrecisionRecallTfidf();
			active.computePrecisionRecallmybag();
			sb.append("tagMetric{tag_name=\"").append(key)
			.append("\",model=\"TF-IDF\",correctness=\"correct\"} ").append(active.tftidfcorrect).append("\n");
			sb.append("tagMetric{tag_name=\"").append(key)
			.append("\",model=\"TF-IDF\",correctness=\"incorrect\"} ").append(active.tfidfincorrect).append("\n");
			sb.append("tagMetric{tag_name=\"").append(key)
			.append("\",model=\"Bag-of-words\",correctness=\"correct\"} ").append(active.mybagcorrect).append("\n");
			sb.append("tagMetric{tag_name=\"").append(key)
			.append("\",model=\"Bag-of-words\",correctness=\"incorrect\"} ").append(active.mybagincorrect).append("\n");
			sb.append("tagMetric{tag_name=\"").append(key)
			.append("\",model=\"both\",correctness=\"missed\"} ").append(active.missed).append("\n\n");

			tfidfApp+=active.totalTfidf;
			mybagApp+=active.totalMybag;

			tfidfRecall += active.totalTfidf*active.recallTfidf;
			tfidfPrecision += active.totalTfidf*active.precisionTfidf;

			mybagPrecision += active.totalMybag*active.precisionMybag;
			mybagRecall += active.totalMybag*active.recallMybag;
		}

		if(tfidfApp!=0){
			tfidfRecall = tfidfRecall/tfidfApp;
			tfidfPrecision = tfidfPrecision/tfidfApp;
		}else{
			tfidfRecall = 0;
			tfidfPrecision = 0;
		}

		if(mybagApp!=0){
			mybagPrecision = mybagPrecision/mybagApp;
			mybagRecall = mybagRecall/mybagApp;
		}else{
			mybagPrecision = 0;
			mybagRecall = 0;
		}

		float tfidfF1;
		float mybagF1;

		if(tfidfPrecision + tfidfRecall ==0){
			tfidfF1 =0;
		} else{
			tfidfF1 = 2 * (tfidfPrecision * tfidfRecall) / (tfidfPrecision + tfidfRecall);
		}

		if(mybagPrecision + mybagRecall ==0){
			mybagF1 = 0;
		} else{
			mybagF1 = 2 * (mybagPrecision * mybagRecall) / (mybagPrecision + mybagRecall);
		}


		sb.append("# HELP tfidfPrecision Precision of the tfidf\n");
		sb.append("# TYPE tfidfPrecision gauge\n");
		sb.append("tfidfPrecision ").append(tfidfPrecision).append("\n\n");

		sb.append("# HELP tfidfRecall Recall of the tfidf\n");
		sb.append("# TYPE tfidfRecall gauge\n");
		sb.append("tfidfRecall ").append(tfidfRecall).append("\n\n");

		sb.append("# HELP tfidfF1 F1 of the tfidf\n");
		sb.append("# TYPE tfidfF1 gauge\n");
		sb.append("tfidfF1 ").append(tfidfF1).append("\n\n");

		sb.append("# HELP mybagPrecision Precision of the mybag\n");
		sb.append("# TYPE mybagPrecision gauge\n");
		sb.append("mybagPrecision ").append(mybagPrecision).append("\n\n");

		sb.append("# HELP mybagRecall Recall of the mybag\n");
		sb.append("# TYPE mybagRecall gauge\n");
		sb.append("mybagRecall ").append(mybagRecall).append("\n\n");

		sb.append("# HELP mybagF1 F1 of the mybag\n");
		sb.append("# TYPE mybagF1 gauge\n");
		sb.append("mybagF1 ").append(mybagF1).append("\n\n");

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