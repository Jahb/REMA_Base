package myweb.ctrl;

/**
 * Holds the metrics data for each class
 */
public class TagMetrics{
    public int tftidfcorrect;  // Number of times TF-IDF correctly predicted the tag
    public int mybagcorrect; // Number of times Bag-of-words correctly predicted the tag
    public int tfidfincorrect; // Number of times TF-IDF incorrectly predicted the tag
    public int mybagincorrect; // Number of times Bag-of-words incorrectly predicted the tag
    public int missed; // Number of times both models missed the tag
    public int missedbytfidf; // Number of times only TF-IDF missed the tag
    public int missedbymybag; // Number of times only Bag-of-words missed the tag

    public float precisionTfidf; // The precision of TF-IDF over this tag
    public float precisionMybag; // The precision of Bag-of-words over this tag

    public float recallTfidf; // The recall of TF-IDF over this tag
    public float recallMybag; // The recall of Bag-of-words over this tag

    public int totalTfidf; // sums the correct, incorrect and missed values for the TF-IDF model
    public int totalMybag; // sums the correct, incorrect and missed values for the Bag-of-words model

    public TagMetrics(int tftidfcorrect, int mybagcorrect, int tfidfincorrect, int mybagincorrect, int missed){
        this.tftidfcorrect = tftidfcorrect; //tp
        this.mybagcorrect = mybagcorrect; //tp
        this.tfidfincorrect = tfidfincorrect; //fp
        this.mybagincorrect = mybagincorrect; //fp
        this.missed = missed; //fn
        this.missedbymybag = 0;
        this.missedbytfidf= 0;
    }

    /**
     * Combines to TagMetrics objects. That's done by adding each of the values of the object
     * @param metrics2 the other TagMetrics object
     */
    public void combine(TagMetrics metrics2){
        tftidfcorrect+=metrics2.tftidfcorrect;
        mybagcorrect+=metrics2.mybagcorrect;
        tfidfincorrect+=metrics2.tfidfincorrect;
        mybagincorrect+=metrics2.mybagincorrect;
        missed+=metrics2.missed;
        missedbymybag+=metrics2.missedbymybag;
        missedbytfidf+= metrics2.missedbytfidf;
    }

    /**
     * Computes the precision and the recall of TF-IDF for this tag
     */
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

    /**
     * Computes the precision and the recall of Bag-of-words for this tag
     */
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

    /**
     * @return string representation of the object
     */
    public String toString(){
        return "("+tftidfcorrect + "," + mybagcorrect+ "," + tfidfincorrect+ "," + mybagincorrect+ "," +  missed+")";
    }
}