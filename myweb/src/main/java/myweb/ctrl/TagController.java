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

/**
 * Manages the tag endpoint. It is mainly related to the working of the GUI.
 */
@Controller
@RequestMapping(path = "/tag")
public class TagController {

	private String modelHost;

	private RestTemplateBuilder rest;

	private MainController hw;

	/**
	 * Constructor to creates the controller
	 * @param rest needed by Spring
	 * @param env environment
	 * @param hw
	 */
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

	/**
	 * Process the resultls depending on the user input in the GUI
	 * @param corr The response received from the GUI formatted according to the data class Correction.
	 * @return
	 */
	@PostMapping("/correct")
	@ResponseBody
	public Correction correctPrediction(@RequestBody Correction corr) {
		int tfidfGoodNum = 0;
		int tfidfBadNum = 0;
		int myBagGoodNum = 0;
		int myBagBadNum = 0;
		int missed = 0;

		// Holds the data for each tag
		// The data is formatted according to the TagMetrics format
		HashMap<String, TagMetrics> map = new HashMap<>();

		// created because of some issue in the js GUI code
		// used to hold the correct tags for both models instead of the response entity
		ArrayList<String> tfidfGoodTagsholder = new ArrayList<>();
		ArrayList<String> myBagGoodTagsholder = new ArrayList<>();

		//construct the two arraylists for the correct tags
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

		// control prints (could be deleted without impacting the code)
		System.out.println("good list");
		for(int i=0;i<corr.tfidfGoodTags.length;i++)System.out.println(corr.tfidfGoodTags[i]);
		System.out.println("bad list");
		for(int i=0;i<corr.tfidfBadTags.length;i++)System.out.println(corr.tfidfBadTags[i]);
		System.out.println("holder");
		for(int i=0;i<tfidfGoodTagsholder.size();i++)System.out.println(tfidfGoodTagsholder.get(i));

		// increments the counter and constructs the tag map according to all tags that Bag-of-words classified incorrectly
		// All of those updates follow the same pattern
		// 1. Update the counter
		// 2. If the map contains that tag, just increment the TagMetrics data depending on the model and correctness
		// (this one is doing it for Bag-of-words and incorrect prediction)
		// 3. If the map does not contain that tag, add it and Create new TagMetrics depending on the model and correctness
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

		// increments the counter and constructs the tag map according to all tags that Bag-of-words classified correctly
		if(myBagGoodTagsholder.size()>0) {
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

		// increments the counter and constructs the tag map according to all tags that TF-IDF classified incorrectly
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

		// increments the counter and constructs the tag map according to all tags that TF-IDF classified correctly
		if(tfidfGoodTagsholder.size()>0) {
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

		// increments the counter and constructs the tag map according to all tags that both models missed
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

		//Update the TagMetrics for the tag in the case where only TF-IDF included the tag
		for(String tag: tfidfGoodTagsholder){
			if(!myBagGoodTagsholder.contains(tag))map.get(tag).missedbymybag++;
		}

		//Update the TagMetrics for the tag in the case where only Bag-of-words included the tag
		for(String tag: myBagGoodTagsholder){
			if(!tfidfGoodTagsholder.contains(tag))map.get(tag).missedbytfidf++;
		}

		// Printing used for debugging (could be deleted without causing issues)
		for (String name: map.keySet()) {
			String key = name.toString();
			String value = map.get(name).toString();
			System.out.println(key + " " + value);
		}

		// calls the data update based on the user input and the predictions after their processing above
		hw.setModelMetrics(tfidfGoodNum, tfidfBadNum, myBagGoodNum, myBagBadNum, missed, map);
		System.out.println(Arrays.toString(corr.myBagGoodTags));
		System.out.println(Arrays.toString(corr.myBagBadTags));
		System.out.println(Arrays.toString(corr.tfidfGoodTags));
		System.out.println(Arrays.toString(corr.tfidfBadTags));
		return corr;
	}

	/**
	 * Gets the predictions from the predict endpoint, which serves the models
	 * @param tag Object according to the Tag format
	 * @return the response entity
	 */
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

}