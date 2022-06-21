package myweb.data;

/**
 *  Data class
 *  Objects of type Tag are formed after the predictions of the model backend
 */
public class Tag {
	public String[] mybag_predictions;
	public String[] tfidf_predictions;
	public String title;
}