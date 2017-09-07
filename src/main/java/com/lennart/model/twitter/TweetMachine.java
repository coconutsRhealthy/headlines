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

    public void postTweetForNewBuzzword(String word, List<String> headlines) throws Exception {
        if(!word.matches("[0-9]+")) {
            String tweetText = getTweetText(word, headlines);

            if(tweetText != null && tweetText.length() > 50 && tweetText.length() < 135) {
                postTweet(tweetText);
            }
        }

        deleteEntriesOlderThan24Hours();
    }

    private void postTweet(String tweetText) {
        String consumerKey = "i9Rkxihee7YFLhBbBdIyvIrdA";
        String consumerSecret = "EHhxP4TSE81G4Dn15uaHcPQOE2fOrTLFsppz1PIrliplR3WqYU";
        String accessToken = "892103185606877185-9eN7Sj2buwRynB6iYSBHe7JpVyMnMSz";
        String accessSecret = "GPtbDjVXWML1Nlda2D7EsFQxZbZbLalvOsCTLXQqMh6Rd";

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
        headlines = removeHeadlinesThatWereAlreadyUsedInTweets(headlines);

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

    private List<String> removeHeadlinesThatWereAlreadyUsedInTweets(List<String> headlines) throws Exception {
        List<String> headlinesAlreadyUsed = getHeadlinesThatWereAlreadyUsedInTweetsFromDb();

        List<String> headlinesToRemove = new ArrayList<>();

        for(String headline : headlines) {
            if(headlinesAlreadyUsed.contains(headline)) {
                headlinesToRemove.add(headline);
            }
        }

        headlines.removeAll(headlinesToRemove);

        return headlines;
    }

    private List<String> getHeadlinesThatWereAlreadyUsedInTweetsFromDb() throws Exception {
        List<String> headlinesAlreadyUsed = new ArrayList<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM tweet_headlines");

        while(rs.next()) {
            headlinesAlreadyUsed.add(rs.getString("headline"));
        }

        rs.close();
        st.close();
        closeDbConnection();

        return headlinesAlreadyUsed;
    }

    private void addHeadlineToAlreadyUsedHeadlinesInTweetsDb(String headline) throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        st.executeUpdate("INSERT INTO tweet_headlines (date, headline) VALUES ('" + getCurrentDateTime() + "', '" + headline + "')");
        st.close();

        closeDbConnection();
    }

    protected void deleteEntriesOlderThan24Hours() throws Exception {
        Date date = new Date();
        date = DateUtils.addHours(date, 2);
        long currentDate = date.getTime();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM tweet_headlines");

        while(rs.next()) {
            String s = rs.getString("date");
            Date parsedDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(s);

            if(parsedDateTime.getTime() < currentDate - TimeUnit.HOURS.toMillis(12)) {
                String headlineToRemove = rs.getString("headline");

                Statement st2 = con.createStatement();
                st2.executeUpdate("DELETE FROM tweet_headlines WHERE headline = '" + headlineToRemove + "';");
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
        wordsSortedByFrequencyFromHeadlines = clearNumbersFromHashTagWords(wordsSortedByFrequencyFromHeadlines);

        headlinesToChooseFrom = convertHeadlinesToNonSpecialCharactersAndLowerCase(headlinesToChooseFrom);

        String headlineWithBestHashTags = getHeadlineWhereYouCanPutMostHashTags(headlinesToChooseFrom,
                wordsSortedByFrequencyFromHeadlines);

        try {
            addHeadlineToAlreadyUsedHeadlinesInTweetsDb(headlineWithBestHashTags);
        } catch (Exception e) {

        }

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

    private List<String> clearNumbersFromHashTagWords(List<String> hashTagWords) {
        List<String> hashTagWordsToReturn = new ArrayList<>();

        for(String hashTagWord : hashTagWords) {
            if(!hashTagWord.matches("[0-9]+")) {
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
            headlinesCorrected.add(correctedHeadline);
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
