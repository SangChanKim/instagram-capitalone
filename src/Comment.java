/**
 * Represents a comment left on an Instagram media post
 * @author sangchankim
 * @version 1.0
 */

public class Comment {
    private User author;
    private String text;

    public Comment(User author, String text) {
        this.author = author;
        this.text = text;
    }

    public User getAuthor() {
        return author;
    }

    public String getText() {
        return text;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public void setText(String text) {
        this.text = text;
    }
}
