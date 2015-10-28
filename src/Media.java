import java.util.ArrayList;

/**
 * Represents an Instagram media post
 * @author sangchankim
 * @version 1.0
 */
public class Media {

    private User author;

    private double sentimentScore = Double.NaN;
    private long timeStamp;
    private long numLikes;

    private ArrayList<Comment> comments;
    private ArrayList<String> tags;

    private String caption;
    private String urlStringToMedia;
    private String id;

    public void setAuthor(User author) {
        this.author = author;
    }

    public void setNumLikes(long numLikes) {
        this.numLikes = numLikes;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public void setUrlStringToMedia(String urlStringToMedia) {
        this.urlStringToMedia = urlStringToMedia;
    }

    public void setSentimentScore(double sentimentScore) {
        this.sentimentScore = sentimentScore;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getAuthor() {
        return author;
    }

    public String getCaption() {
        return caption;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public long getNumLikes() {
        return numLikes;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public String getUrlStringToMedia() {
        return urlStringToMedia;
    }

    public double getSentimentScore() {
        return sentimentScore;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        String result = "Author: \n" + author.toString() + "\nCaption: "
                + caption + "\nLikes: " + numLikes
                + "\nSentiment Score: " + sentimentScore +"\nLink to media: " +urlStringToMedia + "\nid: " + id + "\nTags: ";

        for (int i = 0; i < tags.size(); i++) {
            result += "#" + tags.get(i) + " ";
        }

        result += "\nComments: ";

        for (int i = 0; i < comments.size(); i++) {
            Comment comment = comments.get(i);
            result += "\n   " + comment.getAuthor().getUsername() + ": " + comment.getText();
        }

        result += "\n";

        return result;
    }

}
