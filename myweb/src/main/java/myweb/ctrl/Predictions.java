package myweb.ctrl;

public class Predictions{
    public String[] mybag_predictions;
    public String[] tfidf_predictions;

    public Predictions(String[] mybag_predictions, String[] tfidf_predictions)
    {
        this.mybag_predictions = mybag_predictions;
        this.tfidf_predictions = tfidf_predictions;
    }
}