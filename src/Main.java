import java.util.List;

/**
 * @author Sang Chan Kim
 * @version 1.0
 */
public class Main {

    /**
     * Main function
     * @param args the number of posts to collect, Instagram token, Alchemy API key
     */
    public static void main(String[] args) {

        if (args.length != 3) {
            System.out.println("Please input exactly 3 arguments- the number of posts to collect, Instagram token, and" +
                    "Alchemy API key");
        } else {
            // Get user input
            int numberOfPosts;
            try {
                numberOfPosts = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                // If user inputs an invalid number of posts, set default to 20
                numberOfPosts = 20;
            }
            String instagramAPIToken = args[1];
            String alchemyAPIKey = args[2];

            List<Media> mediaList = null;

            try {

                // Get the NUM_MEDIA amount of #capitalone tagged Instagram posts
                mediaList = InstagramAPIHandler.getLatestPosts("capitalone", numberOfPosts, instagramAPIToken);


                // Track the number of positive, negative, and neutral posts
                int numPositive = 0, numNegative = 0, numNeutral = 0, numNaN = 0;

                for (int i = 0; i < mediaList.size(); i++) {

                    Media media = mediaList.get(i);

                    try {

                        // Calculate sentiment score
                        media.setSentimentScore(AlchemyAPIHandler.calculateSentimentScore(media.getCaption(), alchemyAPIKey));

                        // Only track numbers for the 20 latest posts
                        if (i < 20) {

                            if (media.getSentimentScore() > 0) {

                                numPositive++;

                            } else if (media.getSentimentScore() < 0) {

                                numNegative++;

                            } else if (media.getSentimentScore() == 0){

                                numNeutral++;

                            } else {

                                numNaN++;

                            }
                        }
                    } catch (AlchemyAPIException e) {
                        System.out.println("Instagram post " + media.getId() + ": " + e.getMessage());
                    }
                }

                // Get the 20 latest Instagram posts and prints each post's information
                // If allMedia contains less than 20, print that smaller amount out

                System.out.println("DELIVERABLE 1 AND 2");

                int limit = Math.min(numberOfPosts, 20);

                for (int i = 0; i < limit; i++) {

                    System.out.println(mediaList.get(i).toString());

                }

                // Print out number of positive/negative/neutral posts

                System.out.println("DELIVERABLE 3");
                System.out.println(numPositive + " positive #capitalone posts");
                System.out.println(numNegative + " negative #capitalone posts");
                System.out.println(numNeutral + " neutral #capitalone posts");
                System.out.println(numNaN + " posts were not able to receive a valid sentiment analysis.");

            } catch (InstagramAPIException e) {
                e.printStackTrace();
                System.out.println("Due to problem with the InstagramAPI, the program will end.");
            }
        }


    }

}