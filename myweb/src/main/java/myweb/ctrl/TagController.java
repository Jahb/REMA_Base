package myweb.ctrl;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import myweb.data.Tag;

@Controller
@RequestMapping(path = "/tag")
public class TagController {

	private String modelHost;

	private RestTemplateBuilder rest;

	public TagController(RestTemplateBuilder rest, Environment env) {
		this.rest = rest;
		modelHost = env.getProperty("MODEL_HOST");
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