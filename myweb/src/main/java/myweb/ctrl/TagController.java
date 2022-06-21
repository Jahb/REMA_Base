package myweb.ctrl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import myweb.data.Correction;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import myweb.data.Tag;

@Controller
@RequestMapping(path = "/tag")
public class TagController {

	private String modelHost;

	private RestTemplateBuilder rest;

	private MainController hw;

	public TagController(RestTemplateBuilder rest, Environment env, MainController hw) {
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

		ArrayList<String> tfidfGoodTagsholder = new ArrayList<>();
		ArrayList<String> myBagGoodTagsholder = new ArrayList<>();

        boolean flag=false;
		for(int i=0; i< corr.myBagGoodTags.length;i++){
			for(int j=0; j<corr.myBagBadTags.length; j++ ){
				if(corr.myBagBadTags[j]==corr.myBagGoodTags[i]){
					flag = true;
					break;
				}
			}
			if(flag==false)myBagGoodTagsholder.add(corr.myBagGoodTags[i]);
			flag=false;
		}

		for(int i=0; i< corr.tfidfGoodTags.length;i++){
			for(int j=0; j<corr.tfidfBadTags.length; j++ ){
				if(corr.tfidfBadTags[j]==corr.tfidfGoodTags[i]){
					flag = true;
					break;
				}
			}
			if(flag==false)tfidfGoodTagsholder.add(corr.tfidfGoodTags[i]);
			flag=false;
		}

		for(int i=0; i<corr.tfidfBadTags.length;i++){
			tfidfGoodTagsholder.remove(corr.tfidfBadTags[i]);
		}

		for(int i=0; i<corr.myBagBadTags.length;i++){
			myBagGoodTagsholder.remove(corr.myBagBadTags[i]);
		}

		System.out.println("good list");
		for(int i=0;i<corr.tfidfGoodTags.length;i++)System.out.println(corr.tfidfGoodTags[i]);
		System.out.println("bad list");
		for(int i=0;i<corr.tfidfBadTags.length;i++)System.out.println(corr.tfidfBadTags[i]);
		System.out.println("holder");
		for(int i=0;i<tfidfGoodTagsholder.size();i++)System.out.println(tfidfGoodTagsholder.get(i));

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
			myBagGoodNum += myBagGoodTagsholder.size();
			for(int i=0; i<myBagGoodTagsholder.size(); i++){
				if(map.containsKey(myBagGoodTagsholder.get(i))){
					map.get(myBagGoodTagsholder.get(i)).mybagcorrect++;
				}
				else{
					map.put(myBagGoodTagsholder.get(i), new TagMetrics(0,1,0,0,0));
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
			tfidfGoodNum += tfidfGoodTagsholder.size();
			for(int i=0; i<tfidfGoodTagsholder.size(); i++){
				if(map.containsKey(tfidfGoodTagsholder.get(i))){
					map.get(tfidfGoodTagsholder.get(i)).tftidfcorrect++;
				}
				else{
					map.put(tfidfGoodTagsholder.get(i), new TagMetrics(1,0,0,0,0));
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

		for(String tag: tfidfGoodTagsholder){
			if(!myBagGoodTagsholder.contains(tag))map.get(tag).missedbymybag++;
		}

		for(String tag: myBagGoodTagsholder){
			if(!tfidfGoodTagsholder.contains(tag))map.get(tag).missedbytfidf++;
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

		public int missedbytfidf;
		public int missedbymybag;

		public float precisionTfidf;
		public float precisionMybag;

		public float recallTfidf;
		public float recallMybag;

		public int totalTfidf;
		public int totalMybag;


		public TagMetrics(int tftidfcorrect, int mybagcorrect, int tfidfincorrect, int mybagincorrect, int missed){
			this.tftidfcorrect = tftidfcorrect; //tp
			this.mybagcorrect = mybagcorrect; //tp
			this.tfidfincorrect = tfidfincorrect; //fp
			this.mybagincorrect = mybagincorrect; //fp
			this.missed = missed; //fn
			this.missedbymybag = 0;
			this.missedbytfidf= 0;
		}

		public void combine(TagMetrics metrics2){
			tftidfcorrect+=metrics2.tftidfcorrect;
			mybagcorrect+=metrics2.mybagcorrect;
			tfidfincorrect+=metrics2.tfidfincorrect;
			mybagincorrect+=metrics2.mybagincorrect;
			missed+=metrics2.missed;
			missedbymybag+=metrics2.missedbymybag;
			missedbytfidf+= metrics2.missedbytfidf;
		}

		public void computePrecisionRecallTfidf(){
			if(this.tftidfcorrect!=0){
				this.precisionTfidf = this.tftidfcorrect/(this.tftidfcorrect+this.tfidfincorrect);
				this.recallTfidf = this.tftidfcorrect/(this.tftidfcorrect+this.missed+this.missedbytfidf);
			}else{
				this.precisionTfidf = 0;
				this.recallTfidf = 0;	
			}

			this.totalTfidf = this.tftidfcorrect + this.tfidfincorrect +this.missed;
		}

		public void computePrecisionRecallmybag(){
			if(this.mybagcorrect!=0){
				this.precisionMybag = this.mybagcorrect/(this.mybagcorrect+this.mybagincorrect);
				this.recallMybag = this.mybagcorrect/(this.mybagcorrect+this.missed+this.missedbymybag);	
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