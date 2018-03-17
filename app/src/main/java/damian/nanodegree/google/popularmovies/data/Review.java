package damian.nanodegree.google.popularmovies.data;

/**
 * Created by robert_damian on 17.03.2018.
 */

public class Review {

    public static final int LIMITED_CONTENT_LENGTH = 97;

    private String username;
    private String reviewText;
    private boolean isExpanded;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public String getLimitedReviewText() {
        if (reviewText.length() <= LIMITED_CONTENT_LENGTH) {
            return reviewText;
        }

        return reviewText.substring(0, LIMITED_CONTENT_LENGTH) + "...";
    }
}
