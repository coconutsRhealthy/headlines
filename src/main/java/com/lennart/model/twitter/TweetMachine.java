package com.lennart.model.twitter;

import com.lennart.model.headlinesBuzzDb.DataForAllBuzzWordsProvider;
import org.apache.commons.lang3.time.DateUtils;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by LennartMac on 29/08/2017.
 */
public class TweetMachine {

    private Connection con;

    public void overallMethodServer() {
        while(true) {
            //0
            postTweet1Hour();
            postTweet3Hour();
            postTweet6Hour();
            postTweet12Hour();
            postTweet24Hour();

            //1
            waitOneHour();
            postTweet1Hour();

            //2
            waitOneHour();
            postTweet1Hour();

            //3
            waitOneHour();
            postTweet1Hour();
            postTweet3Hour();

            //4
            waitOneHour();
            postTweet1Hour();

            //5
            waitOneHour();
            postTweet1Hour();

            //6
            waitOneHour();
            postTweet1Hour();
            postTweet3Hour();
            postTweet6Hour();

            //7
            waitOneHour();
            postTweet1Hour();

            //8
            waitOneHour();
            postTweet1Hour();

            //9
            waitOneHour();
            postTweet1Hour();
            postTweet3Hour();

            //10
            waitOneHour();
            postTweet1Hour();

            //11
            waitOneHour();
            postTweet1Hour();

            //12
            waitOneHour();
            postTweet1Hour();
            postTweet3Hour();
            postTweet6Hour();
            postTweet12Hour();

            //13
            waitOneHour();
            postTweet1Hour();

            //14
            waitOneHour();
            postTweet1Hour();

            //15
            waitOneHour();
            postTweet1Hour();
            postTweet3Hour();

            //16
            waitOneHour();
            postTweet1Hour();

            //17
            waitOneHour();
            postTweet1Hour();

            //18
            waitOneHour();
            postTweet1Hour();
            postTweet3Hour();
            postTweet6Hour();

            //19
            waitOneHour();
            postTweet1Hour();

            //20
            waitOneHour();
            postTweet1Hour();

            //21
            waitOneHour();
            postTweet1Hour();
            postTweet3Hour();

            //22
            waitOneHour();
            postTweet1Hour();

            //23
            waitOneHour();
            postTweet1Hour();
        }
    }

    private void postTweet1Hour() {
        try {
            postTweetNewStyle("buzzwords_new", 1);
        } catch (Exception e) {

        }

        try {
            postTweetNewStyle("finance_buzzwords_new", 1);
        } catch (Exception e) {

        }

        try {
            postTweetNewStyle("sport_buzzwords_new", 1);
        } catch (Exception e) {

        }

        try {
            postTweetNewStyle("entertainment_buzzwords_new", 1);
        } catch (Exception e) {

        }
    }

    private void postTweet3Hour() {
        try {
            postTweetNewStyle("buzzwords_new", 3);
        } catch (Exception e) {

        }

        try {
            postTweetNewStyle("finance_buzzwords_new", 3);
        } catch (Exception e) {

        }

        try {
            postTweetNewStyle("sport_buzzwords_new", 3);
        } catch (Exception e) {

        }

        try {
            postTweetNewStyle("entertainment_buzzwords_new", 3);
        } catch (Exception e) {

        }
    }

    private void postTweet6Hour() {
        try {
            postTweetNewStyle("buzzwords_new", 6);
        } catch (Exception e) {

        }

        try {
            postTweetNewStyle("finance_buzzwords_new", 6);
        } catch (Exception e) {

        }

        try {
            postTweetNewStyle("sport_buzzwords_new", 6);
        } catch (Exception e) {

        }

        try {
            postTweetNewStyle("entertainment_buzzwords_new", 6);
        } catch (Exception e) {

        }
    }

    private void postTweet12Hour() {
        try {
            postTweetNewStyle("buzzwords_new", 12);
        } catch (Exception e) {

        }

        try {
            postTweetNewStyle("finance_buzzwords_new", 12);
        } catch (Exception e) {

        }

        try {
            postTweetNewStyle("sport_buzzwords_new", 12);
        } catch (Exception e) {

        }

        try {
            postTweetNewStyle("entertainment_buzzwords_new", 12);
        } catch (Exception e) {

        }
    }

    private void postTweet24Hour() {
        try {
            postTweetNewStyle("buzzwords_new", 24);
        } catch (Exception e) {

        }

        try {
            postTweetNewStyle("finance_buzzwords_new", 24);
        } catch (Exception e) {

        }

        try {
            postTweetNewStyle("sport_buzzwords_new", 24);
        } catch (Exception e) {

        }

        try {
            postTweetNewStyle("entertainment_buzzwords_new", 24);
        } catch (Exception e) {

        }
    }

    private void waitOneHour() {
        try {
            TimeUnit.MINUTES.sleep(60);
        } catch (Exception e) {

        }
    }

    private void postTweetNewStyle(String database, int numberOfHours) throws Exception {
        String tweetText = getTweetTextNewStyle(database, numberOfHours);

        if(tweetText != null && tweetText.length() > 5 && tweetText.length() < 135) {
            postTweet(tweetText, database);
        }
    }

    public void postTweetForNewBuzzword(String word, List<String> headlines, String database) throws Exception {
        if(wordIsFromNewGroup(database, word)) {
            if(!word.matches("[0-9]+")) {
                String tweetText = getTweetText(word, headlines);

                if(tweetText != null && tweetText.length() > 50 && tweetText.length() < 135) {
                    postTweet(tweetText, database);
                    addWordToTweetedWordsDb(word, database);
                }
            }
        }

        deleteEntriesOlderThan24Hours(database);
    }

    private void postTweet(String tweetText, String database) {
        String consumerKey = getConsumerKey(database);
        String consumerSecret = getConsumerSecret(database);
        String accessToken = getAccessToken(database);
        String accessSecret = getAccessSecret(database);

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(consumerKey)
                .setOAuthConsumerSecret(consumerSecret)
                .setOAuthAccessToken(accessToken)
                .setOAuthAccessTokenSecret(accessSecret);

        try {
            TwitterFactory factory = new TwitterFactory(cb.build());
            Twitter twitter = factory.getInstance();
            twitter.updateStatus(tweetText);
        } catch (TwitterException te) {

        }
    }

    private String getConsumerKey(String database) {
        String consumerKey = "";

        if(database.equals("buzzwords_new")) {
            consumerKey = "i9Rkxihee7YFLhBbBdIyvIrdA";
        } else if(database.equals("finance_buzzwords_new")) {
            consumerKey = "dB1cpsM6d4UJmYBC4Xu3eZz7a";
        } else if(database.equals("sport_buzzwords_new")) {
            consumerKey = "Yqo4ciNiAPJEFkojhu0wLiXpj";
        } else if(database.equals("entertainment_buzzwords_new")) {
            consumerKey = "wBAxIuT7hKwhECsBTM3VHwkjS";
        }
        return consumerKey;
    }

    private String getConsumerSecret(String database) {
        String consumerSecret = "";

        if(database.equals("buzzwords_new")) {
            consumerSecret = "EHhxP4TSE81G4Dn15uaHcPQOE2fOrTLFsppz1PIrliplR3WqYU";
        } else if(database.equals("finance_buzzwords_new")) {
            consumerSecret = "fmUa0v6959ftkYRpc1WQjry6iM17TqvH71z1YidQAvlLcDMTP3";
        } else if(database.equals("sport_buzzwords_new")) {
            consumerSecret = "M9A8XesBdE1Tc4OROp7qitl3DK23UJ2D9LLeRxIXNpMPVtW4sH";
        } else if(database.equals("entertainment_buzzwords_new")) {
            consumerSecret = "od5q7BKzv5iNEWbm7JCsvh3LfPQcDDOIkLCNtKZzNXnXqmnikY";
        }
        return consumerSecret;
    }

    private String getAccessToken(String database) {
        String accessToken = "";

        if(database.equals("buzzwords_new")) {
            accessToken = "892103185606877185-9eN7Sj2buwRynB6iYSBHe7JpVyMnMSz";
        } else if(database.equals("finance_buzzwords_new")) {
            accessToken = "907314515175505921-c0NC52yxL9x0eVypVnqaIkx05jdyIYP";
        } else if(database.equals("sport_buzzwords_new")) {
            accessToken = "907324019682267136-PQh1O5C9tXKauzE3A8BAVCZcalXEszF";
        } else if(database.equals("entertainment_buzzwords_new")) {
            accessToken = "907326731001966593-8U914IvxGcsmZoXgFrpN4D1oNjn1pLo";
        }
        return accessToken;
    }

    private String getAccessSecret(String database) {
        String accessSecret = "";

        if(database.equals("buzzwords_new")) {
            accessSecret = "GPtbDjVXWML1Nlda2D7EsFQxZbZbLalvOsCTLXQqMh6Rd";
        } else if(database.equals("finance_buzzwords_new")) {
            accessSecret = "GKAUZfwWS0EmAYlKsKHvMPxRWGPqUKyRa3AEN1j4fphat";
        } else if(database.equals("sport_buzzwords_new")) {
            accessSecret = "OhAMbrKk3hPk5ALQ3mlp9B3URvzTNWbtJ0W7sdkZQxVzb";
        } else if(database.equals("entertainment_buzzwords_new")) {
            accessSecret = "NfWSS7McTIqdTAXOFTWWS5gWvRIksP4EGPePxVESD7aES";
        }
        return accessSecret;
    }

    private boolean wordIsFromNewGroup(String database, String buzzWord) throws Exception {
        boolean wordIsFromNewGroup = false;
        int groupOfNewBuzzWord = getGroupOfWord(database, buzzWord);

        if(groupOfNewBuzzWord == 0) {
            wordIsFromNewGroup = true;
        } else {
            List<Integer> groupsOfWordsAlreadyTweeted = getGroupsOfWordsAlreadyTweeted(database);

            if(!groupsOfWordsAlreadyTweeted.contains(groupOfNewBuzzWord)) {
                wordIsFromNewGroup = true;
            }
        }

        return wordIsFromNewGroup;
    }

    private List<Integer> getGroupsOfWordsAlreadyTweeted(String database) throws Exception {
        List<Integer> groupsOfWordsAlreadyTweeted = new ArrayList<>();
        List<String> wordsAlreadyTweeted = getWordsAlreadyTweeted(database);

        for(String word : wordsAlreadyTweeted) {
            groupsOfWordsAlreadyTweeted.add(getGroupOfWord(database, word));
        }
        return groupsOfWordsAlreadyTweeted;
    }


    private List<String> getWordsAlreadyTweeted(String database) throws Exception {
        List<String> wordsAlreadyTweeted = new ArrayList<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM " + getTweetDbNameFromBuzzDbName(database));

        while(rs.next()) {
            wordsAlreadyTweeted.add(rs.getString("word"));
        }

        rs.close();
        st.close();
        closeDbConnection();

        return wordsAlreadyTweeted;
    }

    private int getGroupOfWord(String database, String word) throws Exception {
        int groupOfWord;

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM " + database + " WHERE word = '" + word + "';");

        rs.next();

        groupOfWord = rs.getInt("group_number");

        rs.close();
        st.close();
        closeDbConnection();

        return groupOfWord;
    }

    private void addWordToTweetedWordsDb(String word, String database) throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        st.executeUpdate("INSERT INTO " + getTweetDbNameFromBuzzDbName(database) + " (date, word) VALUES ('" + getCurrentDateTime() + "', '" + word + "')");
        st.close();

        closeDbConnection();
    }

    private String getTweetTextNewStyle(String database, int numberOfHours) throws Exception {
        String tweetText;

        List<String> wordsList = getMax5WordsOfLastXHoursFromDb(database, numberOfHours);

        if(!wordsList.isEmpty()) {
            tweetText = getFirstLine(database, numberOfHours, wordsList.size()) + getWordsBelowEachOther(wordsList) +
                    getLastLine(database);
        } else {
            tweetText = null;
        }
        return tweetText;
    }

    private String getFirstLine(String database, int numberOfHours, int numberOfWords) {
        String firstLine = "";

        String wordOrWords;

        if(numberOfWords == 1) {
            wordOrWords = "word";
        } else {
            wordOrWords = "words";
        }

        if(database.equals("buzzwords_new")) {
            if(numberOfHours == 1) {
                firstLine = "#Trending news " + wordOrWords + " of last hour:\n\n";
            } else {
                firstLine = "#Trending news " + wordOrWords + " of last " + numberOfHours + " hours:\n\n";
            }
        } else if(database.equals("finance_buzzwords_new")) {
            if(numberOfHours == 1) {
                firstLine = "#Trending fin. market " + wordOrWords + " of last hour:\n\n";
            } else {
                firstLine = "#Trending fin. market " + wordOrWords + " of last " + numberOfHours + " hours:\n\n";
            }
        } else if(database.equals("sport_buzzwords_new")) {
            if(numberOfHours == 1) {
                firstLine = "#Trending sport " + wordOrWords + " of last hour:\n\n";
            } else {
                firstLine = "#Trending sport " + wordOrWords + " of last " + numberOfHours + " hours:\n\n";
            }
        } else if(database.equals("entertainment_buzzwords_new")) {
            if(numberOfHours == 1) {
                firstLine = "#Trending gossip " + wordOrWords + " of last hour:\n\n";
            } else {
                firstLine = "#Trending gossip " + wordOrWords + " of last " + numberOfHours + " hours:\n\n";
            }
        }
        return firstLine;
    }

    private String getWordsBelowEachOther(List<String> wordsList) throws Exception {
        StringBuilder wordsBelowEachOther = new StringBuilder();

        if(!wordsList.isEmpty()) {
            for(int i = 0; i < wordsList.size(); i++) {
                if(i == 0) {
                    wordsBelowEachOther.append("#");
                    wordsBelowEachOther.append(wordsList.get(i));
                } else {
                    wordsBelowEachOther.append("\n");
                    wordsBelowEachOther.append("#");
                    wordsBelowEachOther.append(wordsList.get(i));
                }
            }
        }
        return wordsBelowEachOther.toString();
    }

    private String getLastLine(String database) {
        String lastLine = "";

        if(database.equals("buzzwords_new")) {
            lastLine = "\n\nnewsbuzzwords.com";
        } else if(database.equals("finance_buzzwords_new")) {
            lastLine = "\n\nnewsbuzzwords.com/finance.html";
        } else if(database.equals("sport_buzzwords_new")) {
            lastLine = "\n\nnewsbuzzwords.com/sport.html";
        } else if(database.equals("entertainment_buzzwords_new")) {
            lastLine = "\n\nnewsbuzzwords.com/entertainment.html";
        }
        return lastLine;
    }

    private List<String> getMax5WordsOfLastXHoursFromDb(String database, int numberOfHours) throws Exception {
        List<String> wordsOfLastXHours = new ArrayList<>();

        Date date = new Date();
        date = DateUtils.addHours(date, 2);
        long currentDate = date.getTime();

        initializeDbConnection();
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM " + database + " ORDER BY no_of_headlines DESC;");

        int counter = 0;

        while(rs.next()) {
            String s = rs.getString("date");
            Date parsedDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(s);

            if(parsedDateTime.getTime() > currentDate - TimeUnit.HOURS.toMillis(numberOfHours)) {
                String word = rs.getString("word");

                if(!word.matches("[0-9]+")) {
                    if(counter < 5) {
                        wordsOfLastXHours.add(word);
                        counter++;
                    }
                }
            }
        }

        st.close();
        rs.close();
        closeDbConnection();

        return wordsOfLastXHours;
    }

    private String getTweetText(String word, List<String> headlines) throws Exception {
        String headlineToUse = getHeadlineToUse(headlines, word);
        String tweetText;

        if(!headlineToUse.isEmpty()) {
            tweetText = headlineToUse + "\n" + "\n" + "Buzzword: " + word + "\n" + "\n" + "newsbuzzwords.com";
        } else {
            tweetText = null;
        }
        return tweetText;
    }

    private String getHeadlineToUse(List<String> headlines, String buzzWord) throws Exception {
        headlines = convertHeadlinesToNonSpecialCharactersAndLowerCase(headlines);
        headlines = removeSpecificWordsFromHeadlines(headlines);

        String headlineToUse = "";

        if(!headlines.isEmpty()) {
            List<String> headlinesToChooseFrom = getHeadlinesBetween40and76Chars(headlines);
            headlinesToChooseFrom = getHeadlinesThatContainBuzzword(headlinesToChooseFrom, buzzWord);

            if(!headlinesToChooseFrom.isEmpty()) {
                headlineToUse = getHeadlineWithBestHashTagWords(headlines, headlinesToChooseFrom, buzzWord);
            } else {
                headlineToUse = getHeadlineWithBestHashTagWords(headlines, headlines, buzzWord);
            }
        }
        return headlineToUse;
    }

    private void deleteEntriesOlderThan24Hours(String database) throws Exception {
        Date date = new Date();
        date = DateUtils.addHours(date, 2);
        long currentDate = date.getTime();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM " + getTweetDbNameFromBuzzDbName(database));

        while(rs.next()) {
            String s = rs.getString("date");
            Date parsedDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(s);

            if(parsedDateTime.getTime() < currentDate - TimeUnit.HOURS.toMillis(24)) {
                String wordToRemove = rs.getString("word");

                Statement st2 = con.createStatement();
                st2.executeUpdate("DELETE FROM " + getTweetDbNameFromBuzzDbName(database) + " WHERE word = '" + wordToRemove + "';");
                st2.close();
            }
        }

        st.close();
        rs.close();
        closeDbConnection();
    }

    private List<String> getHeadlinesBetween40and76Chars(List<String> headlines) {
        List<String> headlinesBetween40and76Chars = new ArrayList<>();

        for(String headline : headlines) {
            if(headline.length() >= 40 && headline.length() <= 76) {
                headlinesBetween40and76Chars.add(headline);
            }
        }
        return headlinesBetween40and76Chars;
    }

    private List<String> getHeadlinesThatContainBuzzword(List<String> headlines, String buzzWord) {
        List<String> headlinesToReturn = new ArrayList<>();

        for(String headline : headlines) {
            if(headline.contains(buzzWord)) {
                headlinesToReturn.add(headline);
            }
        }
        return headlinesToReturn;
    }

    private String getHeadlineWithBestHashTagWords(List<String> baseHeadlines, List<String> headlinesToChooseFrom,
                                                   String buzzWord) {
        List<String> wordsSortedByFrequencyFromHeadlines = getWordsSortedByFrequencyFromHeadlines(baseHeadlines, buzzWord);
        wordsSortedByFrequencyFromHeadlines = clearNumbersAndEmptyStringsFromHashTagWords(wordsSortedByFrequencyFromHeadlines);

        headlinesToChooseFrom = convertHeadlinesToNonSpecialCharactersAndLowerCase(headlinesToChooseFrom);

        String headlineWithBestHashTags = getHeadlineWhereYouCanPutMostHashTags(headlinesToChooseFrom,
                wordsSortedByFrequencyFromHeadlines);

        headlineWithBestHashTags = putHashTagsInHeadline(headlineWithBestHashTags, wordsSortedByFrequencyFromHeadlines, buzzWord);

        return headlineWithBestHashTags;
    }

    private String getHeadlineWhereYouCanPutMostHashTags(List<String> headlines, List<String> hashTagWords) {
        Map<String, Integer> numberOfHashTagsPerHeadline = new HashMap<>();

        for(String headline : headlines) {
            int counter = 0;

            for(String hashTagWord : hashTagWords) {
                if(headline.contains(hashTagWord)) {
                    counter++;
                }
            }
            numberOfHashTagsPerHeadline.put(headline, counter);
        }

        numberOfHashTagsPerHeadline = sortByValueHighToLow(numberOfHashTagsPerHeadline);

        Map.Entry<String,Integer> entry = numberOfHashTagsPerHeadline.entrySet().iterator().next();

        return entry.getKey();
    }

    private String putHashTagsInHeadline(String headline, List<String> hashTagWords, String buzzWord) {
        if(hashTagWords.size() > 4) {
            hashTagWords = hashTagWords.subList(0, 4);
        }

        for(String hashTagWord : hashTagWords) {
            headline = headline.replaceFirst(hashTagWord, "#" + hashTagWord);
        }

        headline = headline.replaceFirst(buzzWord, "#" + buzzWord);

        headline = doCorrectionStringReplacements(headline);

        return headline;
    }

    private String doCorrectionStringReplacements(String headline) {
        headline = headline.replaceAll("##", "#");
        headline = headline.replaceAll("#", " #");
        headline = headline.replaceAll("  ", " ");

        if(headline.charAt(0) == ' ') {
            headline = headline.substring(1, headline.length());
        }
        return headline;
    }

    private List<String> clearNumbersAndEmptyStringsFromHashTagWords(List<String> hashTagWords) {
        List<String> hashTagWordsToReturn = new ArrayList<>();

        for(String hashTagWord : hashTagWords) {
            if(!hashTagWord.equals("") && !hashTagWord.matches("[0-9]+")) {
                hashTagWordsToReturn.add(hashTagWord);
            }
        }
        return hashTagWordsToReturn;
    }

    private List<String> convertHeadlinesToNonSpecialCharactersAndLowerCase(List<String> headlinesRaw) {
        List<String> headlinesCorrected = new ArrayList<>();

        for(String rawHeadline : headlinesRaw) {
            String correctedHeadline = rawHeadline.toLowerCase();
            correctedHeadline = correctedHeadline.replaceAll("[^A-Za-z0-9 ]", "");
            correctedHeadline = correctedHeadline.replaceAll("  ", " ");
            headlinesCorrected.add(correctedHeadline);
        }
        return headlinesCorrected;
    }

    private List<String> removeSpecificWordsFromHeadlines(List<String> headlines) {
        List<String> headlinesCorrected = new ArrayList<>();

        for(String headline : headlines) {
            if(headline.contains("read comments")) {
                String correctedHeadline = headline.replaceAll("read comments", "");
                headlinesCorrected.add(correctedHeadline);
            } else {
                headlinesCorrected.add(headline);
            }
        }
        return headlinesCorrected;
    }

    private List<String> getWordsSortedByFrequencyFromHeadlines(List<String> headlines, String buzzword) {
        List<String> wordsSortedByFrequency = new ArrayList<>();

        List<String> correctFormatHeadlines = convertHeadlinesToNonSpecialCharactersAndLowerCase(headlines);

        Map<String, Integer> frequencyMap = new DataForAllBuzzWordsProvider().getWordsRankedByOccurrence
                (correctFormatHeadlines, buzzword, 0);

        frequencyMap = sortByValueHighToLow(frequencyMap);

        for (Map.Entry<String, Integer> entry : frequencyMap.entrySet()) {
            wordsSortedByFrequency.add(entry.getKey());
        }

        return wordsSortedByFrequency;
    }

    private <K, V extends Comparable<? super V>> Map<K, V> sortByValueHighToLow(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>( map.entrySet() );
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o2.getValue() ).compareTo( o1.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    private String getTweetDbNameFromBuzzDbName(String buzzDbName) {
        String tweetDbName = "";

        if(buzzDbName.equals("buzzwords_new")) {
            tweetDbName = "tweet_words";
        } else if(buzzDbName.equals("finance_buzzwords_new")) {
            tweetDbName = "finance_tweet_words";
        } else if(buzzDbName.equals("sport_buzzwords_new")) {
            tweetDbName = "sport_tweet_words";
        } else if(buzzDbName.equals("entertainment_buzzwords_new")) {
            tweetDbName = "entertainment_tweet_words";
        }
        return tweetDbName;
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
