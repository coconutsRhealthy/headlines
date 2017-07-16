package com.lennart.model.headlinesBuzzDb;

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

    public void storeBuzzwordsInDb(Map<String, Map<String, List<String>>> dataForAllBuzzwords) throws Exception {
        String database = "buzzwords_new";

        initializeDbConnection();

        List<String> headlinesForWord22 = new ArrayList<>();
        List<String> linksForWord22 = new ArrayList<>();

        headlinesForWord22.add("Test headline 1");
        headlinesForWord22.add("Test headline 2");

        linksForWord22.add("www.nu.nl");
        linksForWord22.add("www.efteling.nl");

        addNewBuzzwordToDb(database, "noWords", headlinesForWord22, linksForWord22);

        for (Map.Entry<String, Map<String, List<String>>> entry : dataForAllBuzzwords.entrySet()) {
            List<String> headlinesForWord = entry.getValue().get("rawHeadlines");
            List<String> linksForWord = entry.getValue().get("hrefs");

            if(!isWordInDatabase(database, entry.getKey())) {
                addNewBuzzwordToDb(database, entry.getKey(), headlinesForWord, linksForWord);
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

    public void storeBuzzwordsInDeclinedDb(String wordAndHeadline) throws Exception {
        String database = "buzzwords_new_declined";

        initializeDbConnection();
        Statement st = con.createStatement();

        st.executeUpdate("INSERT INTO " + database + " (date, word_headline) VALUES ('" + getCurrentDateTime() + "', '" + wordAndHeadline + "')");

        st.close();
        closeDbConnection();
    }

    public void addNewBuzzwordToDb(String database, String buzzWord, List<String> headlines, List<String> links) throws Exception {
        String headlinesAsOneString = createOneStringOfList(headlines);
        String linksAsOneString = createOneStringOfList(links);

        headlinesAsOneString = doStringReplacementsForDb(headlinesAsOneString);
        linksAsOneString = doStringReplacementsForDb(linksAsOneString);

        Statement st = con.createStatement();

        st.executeUpdate("INSERT INTO " + database + " (entry, date, word, headlines, links) VALUES ('" + (getHighestIntEntry(database) + 1) + "', '" + getCurrentDateTime() + "', '" + buzzWord + "', '" + headlinesAsOneString + "', '" + linksAsOneString + "')");

        st.close();
    }

    private String doStringReplacementsForDb(String string) {
        String correctString = string.replace("'", "''");
        correctString = correctString.replace("\"", "\\\"");
        return correctString;
    }

    private void addHeadlineAndLinkToExistingBuzzword(String database, String word, String headlineToAdd, String linkToAdd) throws Exception {
        String headlines = retrieveHeadlinesOrLinksFromDatabase(database, word, "headlines");
        headlines = headlines + headlineToAdd + " ---- ";

        String links = retrieveHeadlinesOrLinksFromDatabase(database, word, "links");
        links = links + linkToAdd + " ---- ";

        headlines = doStringReplacementsForDb(headlines);
        links = doStringReplacementsForDb(links);

        Statement st = con.createStatement();
        st.executeUpdate("UPDATE " + database + " SET headlines = '" + headlines + "', links = '" + links + "' WHERE word = '" + word + "'");
        st.close();
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
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/words", "root", "Vuurwerk00");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }

}
