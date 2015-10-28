package io.github.sangchankim;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Handles GET requests to Instagram API endpoints and processes JSON response
 * @author sangchankim
 * @version 1.0
 */
public class InstagramAPIHandler {

    /**
     * @param tag the hashtag to query
     * @param numOfPosts the total number of posts to query
     * @param token Instagram API token required for authentication
     * @return a list of all processed io.github.sangchankim.Media objects
     * @throws InstagramAPIException
     */
    public static List<Media> getLatestPosts(String tag, int numOfPosts, String token) throws InstagramAPIException {

        List<Media> processedMediaList = new ArrayList<>(numOfPosts);

        String url = "https://api.instagram.com/v1/tags/" + tag + "/media/recent?access_token=" + token;

        try {
            return getPosts(new URL(url), processedMediaList, numOfPosts, token);
        } catch (MalformedURLException e) {
            throw new InstagramAPIException("Malformed InstagramAPI url: " + url, e);
        }
    }

    /* Instagram's JSON response provides a next_url that allows us to query for more than 20 posts
     * This is a method that makes a GET request, processed the posts, and recursively calls itself
     * using the next_url for more posts if needed
     */
    private static List<Media> getPosts(URL url, List<Media> list, int numOfPostsToRetrieve, String token) throws InstagramAPIException{
        if (numOfPostsToRetrieve <= 0) {
            return list;
        }

        try {

            String queryResults = RestAPIHandler.getRequest(url);

            JSONObject jsonObj = (JSONObject) JSONValue.parse(queryResults);

            JSONObject jsonMetaObject = (JSONObject) jsonObj.get("meta");
            long code = (long) jsonMetaObject.get("code");
            if(code != 200) {
                String jsonErrorType = (String) jsonMetaObject.get("error_type");
                if(jsonErrorType.equals("OAuthAccessTokenException")) {
                    throw new InstagramAPIException("Please use a valid API token");
                } else {
                    throw new InstagramAPIException("Unknown Instagram API error type: " + jsonErrorType);
                }
            }

            JSONArray dataArray = (JSONArray) jsonObj.get("data");

            int limit = Math.min(numOfPostsToRetrieve, 20);

            for (int i = 0; i < limit; i++) {

                Media media = new Media();

                JSONObject mediaObj = (JSONObject) dataArray.get(i);

                // Gather media author
                JSONObject jsonMediaAuthor = (JSONObject) mediaObj.get("user");
                User user = getUserFromJSON(jsonMediaAuthor, token);
                getUserInfo(user, token);
                media.setAuthor(user);

                // Gather all tags
                // TODO: Add note about how it's more efficient to predefine arraylist size
                JSONArray tagsArray = (JSONArray) mediaObj.get("tags");
                ArrayList<String> tags = new ArrayList<>(tagsArray.size());
                for (int j = 0; j < tagsArray.size(); j++) {
                    tags.add((String) tagsArray.get(j));
                }
                media.setTags(tags);


                // Gather comments
                JSONObject jsonComments = (JSONObject) mediaObj.get("comments");

                ArrayList<Comment> comments;
                try {
                    int numComments = safeLongToInt((Long) jsonComments.get("count"));
                    comments = new ArrayList<>(numComments);
                } catch (IllegalArgumentException e) {
                    comments = new ArrayList<>();
                }

                JSONArray jsonCommentsArray = (JSONArray) jsonComments.get("data");
                for (int j = 0; j < jsonCommentsArray.size(); j++) {
                    JSONObject jsonComment = (JSONObject) jsonCommentsArray.get(j);

                    JSONObject jsonCommentAuthor = (JSONObject) jsonComment.get("from");
                    User commentAuthor = getUserFromJSON(jsonCommentAuthor, token);
                    String commentText = (String) jsonComment.get("text");
                    comments.add(new Comment(commentAuthor, commentText));
                }
                media.setComments(comments);

                // Gather likes count
                JSONObject jsonLikes = (JSONObject) mediaObj.get("likes");
                long numLikes = (long) jsonLikes.get("count");
                media.setNumLikes(numLikes);


                // Gather caption
                JSONObject jsonCaption = (JSONObject) mediaObj.get("caption");
                String caption = getTaglessCaption((String) jsonCaption.get("text"));
                media.setCaption(caption);

                // Gather post id
                String mediaID = (String) mediaObj.get("id");
                media.setId(mediaID);

                // Gather url to image (standard resolution)
                JSONObject jsonImages = (JSONObject) mediaObj.get("images");
                JSONObject jsonImagesStandard = (JSONObject) jsonImages.get("standard_resolution");
                String urlStringtoMedia= (String) jsonImagesStandard.get("url");
                media.setUrlStringToMedia(urlStringtoMedia);

                // Gather the time stamp for the media
                long timeStamp = Long.parseLong((String) mediaObj.get("created_time"));
                media.setTimeStamp(timeStamp);

                list.add(media);
            }

            JSONObject paginationObject = (JSONObject) jsonObj.get("pagination");
            String newUrl = (String) paginationObject.get("next_url");

            return getPosts(new URL(newUrl), list, numOfPostsToRetrieve - limit, token);

        } catch (MalformedURLException e) {
            throw new InstagramAPIException("Malformed InstagramAPI url: " + url, e);
        } catch (IOException e) {
            throw new InstagramAPIException("IOException while performing GET request to InstagramAPI.", e);
        } catch (ClassCastException e) {
            throw new InstagramAPIException("Problem processing InstagramAPI's json", e);
        } catch (NumberFormatException e) {
            throw new InstagramAPIException("Problem with parsing InstagramAPI string values to numeric data types", e);
        }
    }

    /**
     * retrieves following, follower, and media count data about the user
     * @param user the user whose information is needed
     * @param token Instagram API token required for authentication
     * @throws IOException
     */
    public static void getUserInfo(User user, String token) throws IOException {
        String url = "https://api.instagram.com/v1/users/" + user.getId() + "?access_token=" + token;
        String queryResults = RestAPIHandler.getRequest(new URL(url));

        JSONObject jsonObj = (JSONObject) JSONValue.parse(queryResults);
        JSONObject dataObj = (JSONObject) jsonObj.get("data");
        JSONObject countsObj = (JSONObject) dataObj.get("counts");
        user.setNumPosts((long) countsObj.get("media"));
        user.setNumFollowers((long) countsObj.get("followed_by"));
        user.setNumFollowing((long) countsObj.get("follows"));
    }

    private static User getUserFromJSON(JSONObject jsonUser, String token) {
        String authorUsername = (String) jsonUser.get("username");
        String authorRealname = (String) jsonUser.get("full_name");
        long authorID = Long.parseLong((String) jsonUser.get("id"));
        return new User(authorUsername, authorRealname, authorID);
    }

    // String manipulation to remove any form of tag ("#" or "@") from the caption
    private static String getTaglessCaption(String caption) {
        Scanner s = new Scanner(caption);

        String result = "";

        while (s.hasNext()) {
            String word = s.next();
            if (!word.substring(0,1).equals("#") && !word.substring(0,1).equals("@")) {
                result += word + " ";
            }
        }

        return result;
    }

    // A safe method of converting a long to an int
    // API returns numeric values as long
    // Some method parameters require numeric values from the API as an int instead of a long
    private static int safeLongToInt(long l) {
        if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
            throw new IllegalArgumentException
                    (l + " cannot be cast to int without changing its value.");
        }
        return (int) l;
    }
}
