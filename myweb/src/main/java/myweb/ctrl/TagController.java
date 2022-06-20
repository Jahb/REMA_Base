package myweb.ctrl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;

import myweb.data.Correction;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import myweb.data.Tag;

@Controller
@RequestMapping(path = "/tag")
public class TagController {

	private String modelHost;

	private String[] testarray;

	private RestTemplateBuilder rest;

	private HelloWorldController hw;

	public TagController(RestTemplateBuilder rest, Environment env, HelloWorldController hw) {
		this.rest = rest;
		modelHost = env.getProperty("MODEL_HOST");
		this.hw = hw;
	}

	@GetMapping("/")
	public String index(Model m) {
		m.addAttribute("hostname", modelHost);
		return "tag/index";
	}

	@PostMapping("/")
	@ResponseBody
	public Tag predict(@RequestBody Tag tag) {
		Predictions pred = getPrediction(tag);
		tag.mybag_predictions = pred.mybag_predictions;
		tag.tfidf_predictions = pred.tfidf_predictions;
		return tag;
	}

	@PostMapping("/correct")
	@ResponseBody
	public Correction correctPrediction(@RequestBody Correction corr) {
		int tfidfGoodNum = 0;
		int tfidfBadNum = 0;
		int myBagGoodNum = 0;
		int myBagBadNum = 0;
		int missed = 0;
		HashMap<String, TagMetrics> map = new HashMap<>();
		System.out.println("bad");
		for(String str: corr.tfidfBadTags){System.out.println(str);}
		System.out.println("good");
		for(String str: corr.tfidfGoodTags){System.out.println(str);}
		if(corr.myBagBadTags != null) {
			myBagBadNum += corr.myBagBadTags.length;
			for(int i=0; i<corr.myBagBadTags.length; i++){
				if(map.containsKey(corr.myBagBadTags[i])){
					map.get(corr.myBagBadTags[i]).mybagincorrect++;
				}
				else{
					map.put(corr.myBagBadTags[i], new TagMetrics(0,0,0,1,0));
				}
			}
		}
		if(corr.myBagGoodTags != null) {
			myBagGoodNum += corr.myBagGoodTags.length;
			for(int i=0; i<corr.myBagGoodTags.length; i++){
				if(map.containsKey(corr.myBagGoodTags[i])){
					map.get(corr.myBagGoodTags[i]).mybagcorrect++;
				}
				else{
					map.put(corr.myBagGoodTags[i], new TagMetrics(0,1,0,0,0));
				}
			}
		}
		if(corr.tfidfBadTags != null) {
			tfidfBadNum += corr.tfidfBadTags.length;
			for(int i=0; i<corr.tfidfBadTags.length; i++){
				if(map.containsKey(corr.tfidfBadTags[i])){
					map.get(corr.tfidfBadTags[i]).tfidfincorrect++;
				}
				else{
					map.put(corr.tfidfBadTags[i], new TagMetrics(0,0,1,0,0));
				}
			}
		}
		if(corr.tfidfGoodTags != null) {
			tfidfGoodNum += corr.tfidfGoodTags.length;
			for(int i=0; i<corr.tfidfGoodTags.length; i++){
				if(map.containsKey(corr.tfidfGoodTags[i])){
					map.get(corr.tfidfGoodTags[i]).tftidfcorrect++;
				}
				else{
					map.put(corr.tfidfGoodTags[i], new TagMetrics(1,0,0,0,0));
				}
			}
		}
		
		if(corr.missed != null && corr.missed[0]!="") {
			missed += corr.missed.length;
			for(int i=0; i<corr.missed.length; i++){
				if(map.containsKey(corr.missed[i])){
					map.get(corr.missed[i]).missed++;
				}
				else{
					if(corr.missed[i]!="") map.put(corr.missed[i], new TagMetrics(0,0,0,0,1));
				}
			}
		}

		for (String name: map.keySet()) {
			String key = name.toString();
			String value = map.get(name).toString();
			System.out.println(key + " " + value);
		}

		hw.setModelMetrics(tfidfGoodNum, tfidfBadNum, myBagGoodNum, myBagBadNum, missed, map);
		System.out.println(Arrays.toString(corr.myBagGoodTags));
		System.out.println(Arrays.toString(corr.myBagBadTags));
		System.out.println(Arrays.toString(corr.tfidfGoodTags));
		System.out.println(Arrays.toString(corr.tfidfBadTags));
		return corr;
	}


	private Predictions getPrediction(Tag tag) {
		try {
			var url = new URI(modelHost + "/predict");
			var c = rest.build().postForEntity(url, tag, Tag.class);
			Predictions pred = new Predictions(c.getBody().mybag_predictions, c.getBody().tfidf_predictions);
			return pred;
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	public class TagMetrics{
		public int tftidfcorrect;
		public int mybagcorrect;
		public int tfidfincorrect;
		public int mybagincorrect;
		public int missed;

		public int precisionTfidf;
		public int precisionMybag;

		public int recallTfidf;
		public int recallMybag;

		public int totalTfidf;
		public int totalMybag;


		public TagMetrics(int tftidfcorrect, int mybagcorrect, int tfidfincorrect, int mybagincorrect, int missed){
			this.tftidfcorrect = tftidfcorrect; //tp
			this.mybagcorrect = mybagcorrect; //tp
			this.tfidfincorrect = tfidfincorrect; //fp
			this.mybagincorrect = mybagincorrect; //fp
			this.missed = missed; //fn
		}

		public void combine(TagMetrics metrics2){
			tftidfcorrect+=metrics2.tftidfcorrect;
			mybagcorrect+=metrics2.mybagcorrect;
			tfidfincorrect+=metrics2.tfidfincorrect;
			mybagincorrect+=metrics2.mybagincorrect;
			missed+=metrics2.missed;
		}

		public void computePrecisionRecallTfidf(){
			if(this.tftidfcorrect!=0){
				this.precisionTfidf = this.tftidfcorrect/(this.tftidfcorrect+this.tfidfincorrect);
				this.recallTfidf = this.tftidfcorrect/(this.tftidfcorrect+this.missed);
			}else{
				this.precisionTfidf = 0;
				this.recallTfidf = 0;	
			}

			this.totalTfidf = this.tftidfcorrect + this.tfidfincorrect +this.missed;

		}

		public void computePrecisionRecallmybag(){
			if(this.mybagcorrect!=0){
				this.precisionMybag = this.mybagcorrect/(this.mybagcorrect+this.mybagincorrect);
				this.recallMybag = this.mybagcorrect/(this.mybagcorrect+this.missed);	
			} else{
				this.precisionMybag = 0;
				this.recallMybag = 0;
			}

			this.totalMybag = this.mybagcorrect + this.mybagincorrect +this.missed;
		}

		

		public String toString(){
			return "("+tftidfcorrect + "," + mybagcorrect+ "," + tfidfincorrect+ "," + mybagincorrect+ "," +  missed+")";
		}
	}

	public class Predictions{
		public String[] mybag_predictions;
		public String[] tfidf_predictions;

		public Predictions(String[] mybag_predictions, String[] tfidf_predictions)
		{
			this.mybag_predictions = mybag_predictions;
			this.tfidf_predictions = tfidf_predictions;
		}
	}

}