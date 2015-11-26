package com.comsysto.netflix.loadtest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestingRunnable implements Runnable {

    private final String getUrl;
    private final URL url;
    private final List<Integer> resultList;

    public TestingRunnable(List<Integer> resultList) throws IOException {
        this.resultList = resultList;

        getUrl = "http://localhost:10000/report";
        url = new URL(getUrl);
    }

    @Override
    public void run() {
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            int reponseCode = httpURLConnection.getResponseCode();

            if (reponseCode == HttpURLConnection.HTTP_OK) {
                // success
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        httpURLConnection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                httpURLConnection.disconnect();

//                analyzeResponse(response.toString());



                String fakeString = "<span id=\"reportAge\">1234711</span>";
                analyzeResponse(fakeString);
            } else {
                // error
            }
        } catch (IOException e) {
            ;
        }
    }

    private void analyzeResponse(String response) {
        System.out.println("analyzing " + response);
        final String patternString = "<span id=\"reportAge\">(.*?)</span>";
        final Pattern pattern = Pattern.compile(patternString);
        final Matcher matcher = pattern.matcher(response);
        matcher.find();
        String foundValue = matcher.group(1);
        System.out.println(foundValue);
        resultList.add(Integer.parseInt(foundValue));
        System.out.println("done");
    }


}
