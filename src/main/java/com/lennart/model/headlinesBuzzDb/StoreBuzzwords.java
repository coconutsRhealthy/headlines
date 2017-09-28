package com.lennart.model.headlinesBuzzDb;

import com.lennart.model.twitter.TweetMachine;
import org.apache.commons.lang3.time.DateUtils;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by LennartMac on 24/06/17.
 */
public class StoreBuzzwords {

    private Connection con;

    public void storeBuzzwordsInDb(String database, Map<String, Map<String, List<String>>> dataForAllBuzzwords) throws Exception {
        initializeDbConnection();

        for (Map.Entry<String, Map<String, List<String>>> entry : dataForAllBuzzwords.entrySet()) {
            List<String> headlinesForWord = entry.getValue().get("rawHeadlines");
            List<String> linksForWord = entry.getValue().get("hrefs");
            String imageLink = entry.getValue().get("imageLink").get(0);

            if(!isWordInDatabase(database, entry.getKey())) {
                if(!earlierWordsWithSame3Headlines(database, headlinesForWord)) {
                    addNewBuzzwordToDb(database, entry.getKey(), headlinesForWord, linksForWord, imageLink);
                    updateGroupsInDb(database);
                    //postTweet(entry.getKey(), headlinesForWord, database);
                }
            } else {
                for(int i = 0; i < linksForWord.size(); i++) {
                    if(!isLinkInDatabase(database, entry.getKey(), linksForWord.get(i))) {
                        try {
                            addHeadlineAndLinkToExistingBuzzword(database, entry.getKey(), headlinesForWord.get(i), linksForWord.get(i));
                        } catch (Exception e) {

                        }
                    }
                }
            }
        }
        closeDbConnection();
    }

//    private void postTweet(String buzzWord, List<String> headlines, String database) {
//        try {
//            new TweetMachine().postTweetForNewBuzzword(buzzWord, headlines, database);
//        } catch (Exception e) {
//
//        }
//    }

    private void updateGroupsInDb(String database) {
        try {
            new RelatedBuzzwordsIdentifier().updateGroupsInDb(database);
        } catch (Exception e) {

        }
    }

    public void addNewBuzzwordToDb(String database, String buzzWord, List<String> headlines, List<String> links, String imageLink) throws Exception {
        String headlinesAsOneString = createOneStringOfList(headlines);
        String linksAsOneString = createOneStringOfList(links);

        headlinesAsOneString = doStringReplacementsForDb(headlinesAsOneString);
        linksAsOneString = doStringReplacementsForDb(linksAsOneString);

        Statement st = con.createStatement();

        st.executeUpdate("INSERT INTO " + database + " (entry, date, word, headlines, links, no_of_headlines, group_number, image_link) VALUES ('" + (getHighestIntEntry(database) + 1) + "', '" + getCurrentDateTime() + "', '" + buzzWord + "', '" + headlinesAsOneString + "', '" + linksAsOneString + "', '" + headlines.size() + "', '" + 0 + "', '" + imageLink + "')");

        st.close();
    }

    private String doStringReplacementsForDb(String string) {
        String correctString = string.replace("'", "''");
        correctString = correctString.replace("\"", "\\\"");
        return correctString;
    }

    private void addHeadlineAndLinkToExistingBuzzword(String database, String word, String headlineToAdd, String linkToAdd) throws Exception {
        String links = retrieveHeadlinesOrLinksFromDatabase(database, word, "links");

        if(!linksAlreadyContainLinkOfThisSite(links, linkToAdd)) {
            links = links + linkToAdd + " ---- ";

            String headlines = retrieveHeadlinesOrLinksFromDatabase(database, word, "headlines");
            headlines = headlines + headlineToAdd + " ---- ";

            headlines = doStringReplacementsForDb(headlines);
            links = doStringReplacementsForDb(links);

            int numberOfHeadlines = headlines.split(" ---- ").length;

            Statement st = con.createStatement();
            st.executeUpdate("UPDATE " + database + " SET headlines = '" + headlines + "', links = '" + links + "', no_of_headlines = " + numberOfHeadlines + " WHERE word = '" + word + "'");
            st.close();
        }
    }

    private boolean linksAlreadyContainLinkOfThisSite(String links, String linkToAdd) {
        String site;

        if(linkToAdd.contains("www.")) {
            site = linkToAdd.split("\\.")[1];
        } else {
            site = linkToAdd.split("\\.")[0];
        }

        if(site != null) {
            return links.contains(site);
        }
        return false;
    }

    private String createOneStringOfList(List<String> list) {
        StringBuilder stringBuilder = new StringBuilder();

        for(String s : list) {
            stringBuilder.append(s);
            stringBuilder.append(" ---- ");
        }
        return stringBuilder.toString();
    }

    private boolean isWordInDatabase(String database, String word) throws Exception{
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM " + database + " WHERE word = '" + word + "';");

        if(rs.next()) {
            rs.close();
            st.close();
            return true;
        }
        rs.close();
        st.close();
        return false;
    }

    private boolean isLinkInDatabase(String database, String word, String link) throws Exception {
        String allLinksAsOneString = retrieveHeadlinesOrLinksFromDatabase(database, word, "links");
        List<String> links = Arrays.asList(allLinksAsOneString.split(" ---- "));

        boolean linkIsInDatabase = false;

        for(String linkInDb : links) {
            if(linkInDb.equals(link)) {
                linkIsInDatabase = true;
            }
        }
        return linkIsInDatabase;
    }

    private boolean earlierWordsWithSame3Headlines(String database, List<String> headlinesForWord) {
        try {
            boolean thereIsEarlierWordWithSame3Headlines = false;

            Collections.sort(headlinesForWord);

            if(headlinesForWord.size() == 3) {
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM " + database + ";");

                while(rs.next()) {
                    List<String> headlinesForWordFromDb = Arrays.asList(rs.getString("headlines").split(" ---- "));

                    if(headlinesForWordFromDb.size() == 3) {
                        Collections.sort(headlinesForWordFromDb);

                        if(headlinesForWord.equals(headlinesForWordFromDb)) {
                            thereIsEarlierWordWithSame3Headlines = true;
                            break;
                        }
                    }
                }

                rs.close();
                st.close();
            }
            return thereIsEarlierWordWithSame3Headlines;
        } catch (Exception e) {
            return false;
        }
    }

    private String retrieveHeadlinesOrLinksFromDatabase(String database, String word, String headlinesOrLinks) throws Exception {
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM " + database + " WHERE word = '" + word + "';");

        rs.next();
        String linksAsOneString = rs.getString(headlinesOrLinks);

        rs.close();
        st.close();

        return linksAsOneString;
    }

    private int getHighestIntEntry(String database) throws Exception {
        Statement st = con.createStatement();
        String sql = ("SELECT * FROM " + database + " ORDER BY entry DESC;");
        ResultSet rs = st.executeQuery(sql);

        if(rs.next()) {
            int highestIntEntry = rs.getInt("entry");
            st.close();
            rs.close();
            return highestIntEntry;
        }
        st.close();
        rs.close();
        return 0;
    }

    private String getCurrentDateTime() {
        java.util.Date date = new java.util.Date();
        date = DateUtils.addHours(date, 2);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/words?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }

}
