package com.example.song_finder_fx.Session;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CallWeb {


    public static final String BASE_URL = "http://localhost:8080/music/";


    public static String callWebApp(String url) {
        String result = "";
        try {

            URL url1 = new URL(url);


            HttpURLConnection conn = (HttpURLConnection) url1.openConnection();


            conn.setRequestMethod("GET");


            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuffer content = new StringBuffer();


                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }


                in.close();
                result = content.toString();
            } else {
                System.out.println("GET request failed with response code: " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
