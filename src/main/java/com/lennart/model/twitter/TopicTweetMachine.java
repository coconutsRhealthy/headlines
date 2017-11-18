package com.lennart.model.twitter;

import com.lennart.model.headlinesBuzzDb.StoreBuzzwords;
import com.lennart.model.headlinesFE.RetrieveTopics;
import com.lennart.model.headlinesFE.Topic;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.DateUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by LennartMac on 02/11/2017.
 */
public class TopicTweetMachine extends TweetMachine {

    private Connection con;

    @Override
    public void overallMethodServer() {
        while(true) {
            String buzzWordDb = "buzzwords_new";
            String tweetDb = "tweet_topics";

            try {
                List<Topic> topicsFromBuzzDb = new RetrieveTopics().retrieveAllTopicsFromDb(buzzWordDb);
                List<Topic> topicsFromTwitterTopicsDb = retrieveAllTopicsFromTwitterTopicsDb(tweetDb);
                List<Topic> newTopics = getNewTopics(topicsFromBuzzDb, topicsFromTwitterTopicsDb);
                postTopicTweets(newTopics, buzzWordDb);
                storeNewTopicsInTwitterTopicsDb(tweetDb, newTopics);
                deleteEntriesOlderThan24Hours(tweetDb);
            } catch (Exception e) {
                storeStackTraceInDb(ExceptionUtils.getStackTrace(e));
            }

            buzzWordDb = "sport_buzzwords_new";
            tweetDb = "sport_tweet_topics";

            try {
                List<Topic> topicsFromBuzzDb = new RetrieveTopics().retrieveAllTopicsFromDb(buzzWordDb);
                List<Topic> topicsFromTwitterTopicsDb = retrieveAllTopicsFromTwitterTopicsDb(tweetDb);
                List<Topic> newTopics = getNewTopics(topicsFromBuzzDb, topicsFromTwitterTopicsDb);
                postTopicTweets(newTopics, buzzWordDb);
                storeNewTopicsInTwitterTopicsDb(tweetDb, newTopics);
                deleteEntriesOlderThan24Hours(tweetDb);
            } catch (Exception e) {
                storeStackTraceInDb(ExceptionUtils.getStackTrace(e));
            }

            buzzWordDb = "entertainment_buzzwords_new";
            tweetDb = "entertainment_tweet_topics";

            try {
                List<Topic> topicsFromBuzzDb = new RetrieveTopics().retrieveAllTopicsFromDb(buzzWordDb);
                List<Topic> topicsFromTwitterTopicsDb = retrieveAllTopicsFromTwitterTopicsDb(tweetDb);
                List<Topic> newTopics = getNewTopics(topicsFromBuzzDb, topicsFromTwitterTopicsDb);
                postTopicTweets(newTopics, buzzWordDb);
                storeNewTopicsInTwitterTopicsDb(tweetDb, newTopics);
                deleteEntriesOlderThan24Hours(tweetDb);
            } catch (Exception e) {
                storeStackTraceInDb(ExceptionUtils.getStackTrace(e));
            }

            buzzWordDb = "finance_buzzwords_new";
            tweetDb = "finance_tweet_topics";

            try {
                List<Topic> topicsFromBuzzDb = new RetrieveTopics().retrieveAllTopicsFromDb(buzzWordDb);
                List<Topic> topicsFromTwitterTopicsDb = retrieveAllTopicsFromTwitterTopicsDb(tweetDb);
                List<Topic> newTopics = getNewTopics(topicsFromBuzzDb, topicsFromTwitterTopicsDb);
                postTopicTweets(newTopics, buzzWordDb);
                storeNewTopicsInTwitterTopicsDb(tweetDb, newTopics);
                deleteEntriesOlderThan24Hours(tweetDb);
                TimeUnit.SECONDS.sleep(20);
            } catch (Exception e) {
                storeStackTraceInDb(ExceptionUtils.getStackTrace(e));
            }
        }
    }

    private void storeStackTraceInDb(String stackTrace) {
        try {
            initializeDbConnection();
            Statement st = con.createStatement();

            st.executeUpdate("INSERT INTO tweet_stacktraces (stacktrace) VALUES ('" + stackTrace + "')");

            st.close();
            closeDbConnection();
        } catch (Exception e) {
            storeStackTraceInDb(ExceptionUtils.getStackTrace(e));
        }
    }

    private void postTopicTweets(List<Topic> newTopics, String database) {
        for(Topic topic : newTopics) {
            postTopicTweet(topic, database);
        }
    }

    private void postTopicTweet(Topic topic, String database) {
        try {
            readAndSaveImageToDisc(topic.getImageLink());
        } catch (Exception e) {
            storeStackTraceInDb(ExceptionUtils.getStackTrace(e));
            return;
        }

        String topicTweetText = getTopicTweetText(topic.getHeadlines(), database);

        if(topicTweetText != null && topicTweetText.length() > 5 && topicTweetText.length() < 135) {
            postTopicTweet(topicTweetText, database);
        }
    }

    private String getTopicTweetText(List<String> headlines, String database) {
        String headlineToUse = getHeadlineToUse(headlines);
        String hashTags = getHashTags(headlines);
        String lastLine = getLastLine(database);

        String tweetText = headlineToUse + "\n" + hashTags + "\n" + "\n" + lastLine;
        return tweetText;
    }

    private String getHeadlineToUse(List<String> headlines) {
        String headlineToUse = "";

        for(String headline : headlines) {
            if(headline.length() <= 75) {
                headlineToUse = headline;
                break;
            }
        }

        return headlineToUse;
    }

    private String getHashTags(List<String> headlines) {
        StringBuilder hashTags = new StringBuilder();

        List<String> wordsSortedByFrequencyFromHeadlines = getWordsSortedByFrequencyFromHeadlines(headlines, "");
        wordsSortedByFrequencyFromHeadlines = clearNumbersAndEmptyStringsFromHashTagWords(wordsSortedByFrequencyFromHeadlines);

        int counter = 0;

        for(String hashTagWord : wordsSortedByFrequencyFromHeadlines) {
            if(counter < 4) {
                if(hashTags.length() + hashTagWord.length() <= 25) {
                    hashTags.append("#");
                    hashTags.append(hashTagWord);
                    hashTags.append(" ");
                    counter++;
                }
            }
        }

        String hashTagsAsString = hashTags.toString();
        hashTagsAsString = hashTagsAsString.substring(0, hashTagsAsString.length() - 1);

        return hashTagsAsString;
    }

    private String getLastLine(String database) {
        String lastLine = "";

        if(database.equals("buzzwords_new")) {
            lastLine = "headl1nes.com";
        } else if(database.equals("finance_buzzwords_new")) {
            lastLine = "headl1nes.com/business.html";
        } else if(database.equals("sport_buzzwords_new")) {
            lastLine = "headl1nes.com/sports.html";
        } else if(database.equals("entertainment_buzzwords_new")) {
            lastLine = "headl1nes.com/entertainment.html";
        }
        return lastLine;
    }

    private List<Topic> getNewTopics(List<Topic> topicsFromBuzzDb, List<Topic> topicsFromTwitterTopicsDb) {
        List<Topic> newTopics = new ArrayList<>();

        loop: for(Topic topicFromBuzzDb : topicsFromBuzzDb) {
            for(Topic topicFromTwitterDb : topicsFromTwitterTopicsDb) {
                if(topicFromBuzzDb.getImageLink().equals(topicFromTwitterDb.getImageLink())) {
                    continue loop;
                }

                List<String> headlinesBuzzDbTopic = topicFromBuzzDb.getHeadlines();
                List<String> headlinesTwitterDbTopic = topicFromTwitterDb.getHeadlines();

                if(!Collections.disjoint(headlinesBuzzDbTopic, headlinesTwitterDbTopic)) {
                    continue loop;
                }
            }
            newTopics.add(topicFromBuzzDb);
        }
        return newTopics;
    }

    private void storeNewTopicsInTwitterTopicsDb(String database, List<Topic> newTopics) throws Exception {
        for(Topic topic : newTopics) {
            int entry = topic.getEntry();
            String headlinesAsOneString = StoreBuzzwords.createOneStringOfList(topic.getHeadlines());
            headlinesAsOneString = StoreBuzzwords.doStringReplacementsForDb(headlinesAsOneString);
            String imageLink = topic.getImageLink();

            initializeDbConnection();
            Statement st = con.createStatement();

            st.executeUpdate("INSERT INTO " + database + " (entry, date, headlines, image_link) VALUES ('" +
                    entry + "', '" + StoreBuzzwords.getCurrentDateTime() + "', '" +
                    headlinesAsOneString + "', '" + imageLink + "')");

            st.close();
            closeDbConnection();
        }
    }

    private List<Topic> retrieveAllTopicsFromTwitterTopicsDb(String database) throws Exception {
        List<Topic> allTopicsFromTwitterTopicsDb = new ArrayList<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM " + database + ";");

        while(rs.next()) {
            int entry = rs.getInt("entry");
            String dateTime = rs.getString("date");
            List<String> headlines = Arrays.asList(rs.getString("headlines").split(" ---- "));
            String imageLink = rs.getString("image_link");

            Topic topic = new Topic(entry, dateTime, headlines, null, null, imageLink);
            allTopicsFromTwitterTopicsDb.add(topic);
        }

        rs.close();
        st.close();
        closeDbConnection();

        return allTopicsFromTwitterTopicsDb;
    }

    private void deleteEntriesOlderThan24Hours(String database) throws Exception {
        Date date = new Date();
        date = DateUtils.addHours(date, 2);
        long currentDate = date.getTime();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM " + database + ";");

        while(rs.next()) {
            String s = rs.getString("date");
            Date parsedDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(s);

            if(parsedDateTime.getTime() < currentDate - TimeUnit.HOURS.toMillis(30)) {
                int entryToRemove = rs.getInt("entry");

                Statement st2 = con.createStatement();
                st2.executeUpdate("DELETE FROM " + database + " WHERE entry = " + entryToRemove + ";");
                st2.close();
            }
        }

        st.close();
        rs.close();
        closeDbConnection();
    }

    private void readAndSaveImageToDisc(String imageUrl) throws Exception {
        BufferedImage bimg = ImageIO.read(new URL(imageUrl));
        File outputfile = new File(System.getProperty("user.home") + "/output22.png");

        if(bimg != null) {
            ImageIO.write(bimg, "png", outputfile);
        }
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/words", "root", "Vuurwerk00");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
