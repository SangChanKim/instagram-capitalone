import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;

/**
 * Handles calls to AlchemyAPI endpoints and processes JSON response
 * @author sangchankim
 * @version 1.0
 */
public class AlchemyAPIHandler {

    /**
     *
     * @param text the text that needs to be analyzed
     * @return a double that represents the post's polarity- positive, neutral, negative
     * @throws AlchemyAPIException
     */
    public static double calculateSentimentScore(String text, String token) throws AlchemyAPIException {

        String url = "";

        try {

            // Removes all ASCII values
            text = text.replaceAll("[^\\x00-\\x7F]", "");

            // Makes sure AlchemyAPI understands any weird text by encoding it in UTF-8
            text = URLEncoder.encode(text, "UTF-8");

            url = "http://gateway-a.watsonplatform.net/calls/text/TextGetTextSentiment?apikey=" + token
                + "&outputMode=json" + "&text=" + text;

            String queryResult = RestAPIHandler.getRequest(new URL(url));

            JSONObject obj = (JSONObject) JSONValue.parse(queryResult);
            String status = (String) obj.get("status");
            if (status.equals("OK")) {
                JSONObject sentimentObj = (JSONObject) obj.get("docSentiment");
                String  sentimentType = (String) sentimentObj.get("type");
                if (!sentimentType.equals("neutral")) {
                    double sentimentScore = Double.parseDouble((String) sentimentObj.get("score"));
                    return sentimentScore;
                } else {
                    return 0;
                }

            } else if(status.equals("ERROR")){
                String statusInfo = (String) obj.get("statusInfo");
                if (statusInfo.equals("unsupported-text-language")) {
                    throw new AlchemyAPIException("statusInfo: " + statusInfo + ". Please make sure your text's language is in an AlchemyAPI supported language.");
                } else if(statusInfo.equals("invalid-api-key")) {
                    throw new AlchemyAPIException("statusInfo: " + statusInfo + ". Invalid API key.");
                } else if(statusInfo.equals("content-is-empty")) {
                    throw new AlchemyAPIException("statusInfo: " + statusInfo + ". Your text is empty.");
                } else {
                    throw new AlchemyAPIException("Unknown status info returned by AlchemyAPI.");
                }
            } else {
                throw new AlchemyAPIException("Unknown status returned by AlchemyAPI.");
            }
        } catch (UnsupportedEncodingException e) {
            throw new AlchemyAPIException("Unsupported encoding parameter used to encode AlchemyAPI url.", e);
        } catch (MalformedURLException e) {
            throw new AlchemyAPIException("Malformed AlchemyAPI url: " + url, e);
        } catch (IOException e) {
            throw new AlchemyAPIException("IOException while performing GET request to AlchemyAPI.", e);
        }
    }
}
