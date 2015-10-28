package io.github.sangchankim.objects;

/**
 * Represents an Instagram user
 * @author sangchankim
 * @version 1.0
 */

public class User {

    private String realName;
    private String username;
    private String urlStringToProfilePicture;
    private long id;

    private long numFollowing;
    private long numFollowers;
    private long numPosts;

    public User(String username, String realName, long id) {
        this.username = username;
        this.realName = realName;;
        this.id = id;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public void setUrlStringToProfilePicture(String urlStringToProfilePicture) {
        this.urlStringToProfilePicture = urlStringToProfilePicture;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setNumFollowing(long numFollowing) {
        this.numFollowing = numFollowing;
    }

    public void setNumPosts(long numPosts) {
        this.numPosts = numPosts;
    }

    public void setNumFollowers(long numFollowers) {
        this.numFollowers = numFollowers;
    }

    public String getUsername() {
        return username;
    }

    public String getRealName() {
        return realName;
    }

    public String getUrlStringToProfilePicture() {
        return urlStringToProfilePicture;
    }

    public long getId() {
        return id;
    }

    public long getNumFollowing() {
        return numFollowing;
    }

    public long getNumFollowers() {
        return numFollowers;
    }

    public long getNumPosts() {
        return numPosts;
    }

    @Override
    public String toString() {
        String result = "   username: " + username + "\n   Followers: " + numFollowers + "\n   Following: " + numFollowing + "\n   NumPosts: " + numPosts;
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if(other == null) {
            return false;
        } else if (this == other) {
            return true;
        } else if (!(other instanceof User)) {
            return false;
        } else {
            User otherUser = (User) other;
            return otherUser.id == this.id;
        }
    }

}
