package myweb.data;

/**
 *  Data class
 *  Objects of type Correction are used by the GUI to send information to the Java backend.
 */
public class Correction {

    public String[] tfidfBadTags;
    public String[] tfidfGoodTags;

    public String[] myBagBadTags;
    public String[] myBagGoodTags;

    public String[] missed;
}
