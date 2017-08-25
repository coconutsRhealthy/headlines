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


    //je hebt 2 mogelijkheden:
        //woord is nog niet in database
            //check voor elke urlLink van je woord of deze al ergens in de db voorkomt.
                //zoja, check welk woord meer urlLinks heeft, jouw woord of het geidentificeerde woord
                    //indien jouw woord
                        //verwijder het geidentificeerde woord uit db en begin de urlLink check opnieuw voor jouw woord
                    //indien geidentificeerde woord
                        //voeg jouw woord niet toe aan db
                //zonee, voeg het nieuwe woord toe aan de db

        //woord is al wel in database
            //check voor elke urlLink van je woord of deze al ergens, buiten jouw buzzword, in de db voorkomt.
                //zoja, check welk woord meer urlLinks heeft, jouw woord of het geidentificeerde woord
                    //indien jouw woord
                        //verwijder het geidentificeerde woord uit db en begin de urlLink check opnieuw voor jouw woord
                    //indien geidentificeerde woord
                        //verwijder jouw woord uit db
                //zonee, voeg eventuele nieuwe urlLinks toe aan de db

    public void storeBuzzwordsInDb(String database, Map<String, Map<String, List<String>>> dataForAllBuzzwords) throws Exception {
        initializeDbConnection();

        for (Map.Entry<String, Map<String, List<String>>> entry : dataForAllBuzzwords.entrySet()) {
            List<String> headlinesForWord = entry.getValue().get("rawHeadlines");
            List<String> linksForWord = entry.getValue().get("hrefs");

            if(!isWordInDatabase(database, entry.getKey())) {
                if(!earlierWordsWithSame3Headlines(database, headlinesForWord)) {
                    addNewBuzzwordToDb(database, entry.getKey(), headlinesForWord, linksForWord);
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

        st.executeUpdate("INSERT INTO " + database + " (entry, date, word, headlines, links, no_of_headlines, group_number) VALUES ('" + (getHighestIntEntry(database) + 1) + "', '" + getCurrentDateTime() + "', '" + buzzWord + "', '" + headlinesAsOneString + "', '" + linksAsOneString + "', '" + headlines.size() + "', '" + 0 + "')");

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

    private Map<String, List<String>> getAllUrlsPerWordFromDatabase(String database) throws Exception {
        Map<String, List<String>> allUrlsPerWord = new HashMap<>();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM " + database + ";");

        while(rs.next()) {
            String word = rs.getString("word");
            List<String> links = Arrays.asList(rs.getString("links").split(" ---- "));
            allUrlsPerWord.put(word, links);
        }

        rs.close();
        st.close();

        return allUrlsPerWord;
    }
    
    private List<String> getWordsWithSameUrls(String database, List<String> linksForWord) throws Exception {
        List<String> wordsWithSameUrls = new ArrayList<>();
        Map<String, List<String>> allUrlsPerWord = getAllUrlsPerWordFromDatabase(database);

        for(String link : linksForWord) {
            for (Map.Entry<String, List<String>> entry : allUrlsPerWord.entrySet()) {
                if(entry.getValue().contains(link)) {
                    wordsWithSameUrls.add(entry.getKey());
                    break;
                }
            }
        }
        return wordsWithSameUrls;
    }

    private void replaceExistingBuzzwordInDbIfNecessary(String database, String buzzWord, List<String> headlines,
                                                        List<String> links, List<String> wordsWithSameUrls) throws Exception {
        for(String wordWithSameUrls : wordsWithSameUrls) {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM " + database + " WHERE word = '" + wordWithSameUrls + "';");
            rs.next();
            List<String> linksOfWordWithSameUrls = Arrays.asList(rs.getString("links").split(" ---- "));

            rs.close();
            st.close();

            if(links.size() < linksOfWordWithSameUrls.size()) {
                return;
            }
        }

        //if you come here, apparently the new buzzword has the most links and should replace older buzzwords in db
        for(String wordWithSameUrls : wordsWithSameUrls) {
            deleteWordFromDb(database, wordWithSameUrls);
        }

        addNewBuzzwordToDb(database, buzzWord, headlines, links);
    }

    private void updateLinksIfNecessaryAndRemoveBuzzwordsWithDuplicateLinks(String database, String buzzWord, List<String> headlines,
                                                                            List<String> links, List<String> wordsWithSameUrls) throws Exception {
        for(String wordWithSameUrls : wordsWithSameUrls) {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM " + database + " WHERE word = '" + wordWithSameUrls + "';");
            rs.next();
            List<String> linksOfWordWithSameUrls = Arrays.asList(rs.getString("links").split(" ---- "));

            rs.close();
            st.close();

            if(links.size() > linksOfWordWithSameUrls.size()) {
                deleteWordFromDb(database, wordWithSameUrls);
            } else {
                deleteWordFromDb(database, buzzWord);
                return;
            }
        }
        updateLinksOfWordIfNecessary(database, buzzWord, links, headlines);
    }

    private void deleteWordFromDb(String database, String word) throws Exception {
        Statement st = con.createStatement();
        st.executeUpdate("DELETE FROM " + database + "WHERE word = '" + word + "';");
        st.close();
    }

    private void updateLinksOfWordIfNecessary(String database, String word, List<String> linksForWord,
                                              List<String> headlinesForWord) throws Exception {
        for(int i = 0; i < linksForWord.size(); i++) {
            if(!isLinkInDatabase(database, word, linksForWord.get(i))) {
                try {
                    addHeadlineAndLinkToExistingBuzzword(database, word, headlinesForWord.get(i), linksForWord.get(i));
                } catch (Exception e) {

                }
            }
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
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/words", "root", "Vuurwerk00");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }

}
