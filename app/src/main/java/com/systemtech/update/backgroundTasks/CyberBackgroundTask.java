package com.systemtech.update.backgroundTasks;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.systemtech.update.database.AppDatabase;
import com.systemtech.update.database.Article;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;

public class CyberBackgroundTask extends Worker {
    private static final String TAG = "BackgroundTask";
    private ArrayList<Article> articles;
    public CyberBackgroundTask(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    // function to execute in the background when the app is loaded
    @NonNull
    @Override
    public Result doWork() {

        articles = new ArrayList<>();

        InputStream inputStream = null;
        try {
            inputStream = getInputStream();

            // this catch block is specifically for a network timeout for an example that the person's data has finished or is slow
        } catch (SocketTimeoutException e) {
            return Result.failure();

        }
        if (null == inputStream){
            return Result.failure();
        }

        try {
            articles = initXMLPullParser(inputStream);
        } catch (XmlPullParserException | IOException e) {
            Log.d(TAG, "doWork: error parsing the XML file");
            throw new RuntimeException(e);
        }

//         convert my arraylist to json format
//        Gson gson = new Gson();
//        String jsonArticles = gson.toJson(articles);
        Log.d(TAG, "doWork: articles pulled from site: " + articles.size());

        // clear old data in app database after getting fresh data from internet
        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        db.articleDao().deleteArticlesByCategory("CyberSecurity");
        db.articleDao().insertAll(articles);

        return Result.success();
//        // send the data back to the activity that calls it
//        Data outputData = new Data.Builder()
//                .putString(OUTPUT_DATA, jsonArticles)
//                .build();


    }


    // function to parse through the XML file to retrieve the information needed on articles
    private ArrayList<Article> initXMLPullParser(InputStream inputStream) throws XmlPullParserException, IOException {
        Log.d(TAG, "initXMLPullParser: Initiating XML pull parser");
        XmlPullParser parser = Xml.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(inputStream, null);
        parser.next();

        // Make sure the parser is at the beginning of the XML file
        parser.require(XmlPullParser.START_TAG, null, "rss");

        // Loop through the whole XML file till it reaches the rss end tag
        while (parser.next() != XmlPullParser.END_TAG){
            if (parser.getEventType() != XmlPullParser.START_TAG){
                continue;
            }

            parser.require(XmlPullParser.START_TAG, null, "channel");
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                        continue;
                }

                if (parser.getName().equals("item")){
                    parser.require(XmlPullParser.START_TAG, null, "item");

                    String title = "";
                    String description = "";
                    String date = "";
                    String link = "";

                    while (parser.next() != XmlPullParser.END_TAG){
                        if (parser.getEventType() != XmlPullParser.START_TAG) {
                            continue;
                        }

                        String tagName = parser.getName();
                        switch (tagName) {
                            case "title":
                                title = getContent(parser, "title");
                                break;
                            case "description":
                                description = getContent(parser, "description");
                                break;
                            case "link":
                                link = getContent(parser, "link");
                                break;
                            case "pubDate":
                                date = getContent(parser, "pubDate");
                                break;
                            default:
                                skipTag(parser);
                                break;
                        }

                    }

                    Article article = new Article(title, date, description, link, "CyberSecurity");
                    articles.add(article);
                }
                else{
                    skipTag(parser);
                }
            }

        }
        Log.d(TAG, "initXMLPullParser: End of XML parser");
        return articles;
    }

    // function to get the data from the tag in the XML file
    private String getContent(XmlPullParser parser, String tagName) throws XmlPullParserException, IOException {
        String content = "";
        parser.require(XmlPullParser.START_TAG, null, tagName);
        if (parser.next() == XmlPullParser.TEXT){
            content = parser.getText();
            parser.next();
        }

        return content;
    }

    private void skipTag(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG){
            throw new IllegalStateException("not in a new tag");
        }

        int number = 1;

        while (number != 0) {
            switch (parser.next()) {
                case XmlPullParser.START_TAG:
                    number++;
                    break;
                case XmlPullParser.END_TAG:
                    number--;
                    break;
                default:
                    break;
            }
        }

    }

    // getting the XML file for the rss url for the blog site
    private InputStream getInputStream() throws java.net.SocketTimeoutException{
        try {
            URL url = new URL("https://feeds.feedburner.com/TheHackersNews");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setReadTimeout(30000);

            Log.i(TAG, "getInputStream: successfully reached API and gotten expected response");

            return connection.getInputStream();

        } catch (IOException e) {
            Log.d(TAG, "getInputStream: error connecting to url");
            throw new RuntimeException(e);
        }
    }
}
