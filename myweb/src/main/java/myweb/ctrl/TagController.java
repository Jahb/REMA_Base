package myweb.ctrl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

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

		if(corr.myBagBadTags != null) {
			myBagBadNum += corr.myBagBadTags.length;
		}
		if(corr.myBagGoodTags != null) {
			myBagGoodNum += corr.myBagGoodTags.length;
		}
		if(corr.tfidfBadTags != null) {
			tfidfBadNum += corr.tfidfBadTags.length;
		}
		if(corr.tfidfGoodTags != null) {
			tfidfGoodNum += corr.tfidfGoodTags.length;
		}

		hw.setModelMetrics(tfidfGoodNum, tfidfBadNum, myBagGoodNum, myBagBadNum);
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