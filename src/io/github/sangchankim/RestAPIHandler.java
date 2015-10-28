package io.github.sangchankim;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Handles sending GET requests
 * @author sangchankim
 * @version 1.0
 */
public class RestAPIHandler {

    /**
     * Makes a GET request to a URL and returns the response as a string
     * @param url api endpoint url
     * @return the response text
     * @throws IOException
     */
    public static String getRequest(URL url) throws IOException{

        HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();

        // Set request type to GET
        httpConnection.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        // Construct response
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }
}
