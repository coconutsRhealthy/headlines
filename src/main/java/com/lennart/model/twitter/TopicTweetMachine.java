package com.lennart.model.twitter;

import com.lennart.model.headlinesBuzzDb.StoreBuzzwords;
import com.lennart.model.headlinesFE.RetrieveTopics;
import com.lennart.model.headlinesFE.Topic;
import org.apache.commons.lang3.time.DateUtils;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by LennartMac on 02/11/2017.
 */
public class TopicTweetMachine {

    private Connection con;

    private void getTopics(String database) {
        RetrieveTopics retrieveTopics = new RetrieveTopics();

        while(true) {
            try {
                List<Topic> topicsFromBuzzDb = retrieveTopics.retrieveAllTopicsFromDb("buzzwords_new");
                List<Topic> topicsFromTwitterTopicsDb = retrieveAllTopicsFromTwitterTopicsDb(database);
                List<Topic> newTopics = getNewTopics(topicsFromBuzzDb, topicsFromTwitterTopicsDb);
                postTopicTweets(newTopics);
                storeNewTopicsInTwitterTopicsDb(database, newTopics);
                deleteEntriesOlderThan24Hours(database);
                TimeUnit.SECONDS.sleep(30);
            } catch (Exception e) {

            }
        }
    }

    private void postTopicTweets(List<Topic> newTopics) {
        //to implement
    }

    private List<Topic> getNewTopics(List<Topic> topicsFromBuzzDb, List<Topic> topicsFromTwitterTopicsDb) {
        List<Topic> newTopics = new ArrayList<>();

        for(Topic topicFromBuzzDb : topicsFromBuzzDb) {
            for(Topic topicFromTwitterDb : topicsFromTwitterTopicsDb) {
                if(!topicFromBuzzDb.getImageLink().equals(topicFromTwitterDb.getImageLink())) {
                    List<String> headlinesBuzzDbTopic = topicFromBuzzDb.getHeadlines();
                    List<String> headlinesTwitterDbTopic = topicFromTwitterDb.getHeadlines();

                    if(Collections.disjoint(headlinesBuzzDbTopic, headlinesTwitterDbTopic)) {
                        newTopics.add(topicFromBuzzDb);
                    }
                }
            }
        }

        return newTopics;
    }

    private void storeNewTopicsInTwitterTopicsDb(String database, List<Topic> newTopics) throws Exception {
        for(Topic topic : newTopics) {
            int entry = topic.getEntry();
            String headlinesAsOneString = StoreBuzzwords.createOneStringOfList(topic.getHeadlines());
            String imageLink = topic.getImageLink();

            Statement st = con.createStatement();

            st.executeUpdate("INSERT INTO " + database + " (entry, date, headlines, image_link) VALUES ('" +
                    entry + "', '" + StoreBuzzwords.getCurrentDateTime() + "', '" +
                    headlinesAsOneString + "', '" + imageLink + "')");

            st.close();
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
        ResultSet rs = st.executeQuery("SELECT * FROM tweet_topics;");

        while(rs.next()) {
            String s = rs.getString("date");
            Date parsedDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(s);

            if(parsedDateTime.getTime() < currentDate - TimeUnit.HOURS.toMillis(24)) {
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

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/words", "root", "Vuurwerk00");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
