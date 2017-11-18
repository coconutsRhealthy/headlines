package com.lennart.model.twitter;

import com.lennart.model.headlinesBuzzDb.DataForAllBuzzWordsProvider;
import org.apache.commons.lang3.time.DateUtils;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.io.File;
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
            postTweetNewStyle("crypto_buzzwords_new", 1);
        } catch (Exception e) {

        }
    }

    private void postTweet3Hour() {
        try {
            postTweetNewStyle("crypto_buzzwords_new", 3);
        } catch (Exception e) {

        }
    }

    private void postTweet6Hour() {
        try {
            postTweetNewStyle("crypto_buzzwords_new", 6);
        } catch (Exception e) {

        }
    }

    private void postTweet12Hour() {
        try {
            postTweetNewStyle("crypto_buzzwords_new", 12);
        } catch (Exception e) {

        }
    }

    private void postTweet24Hour() {
        try {
            postTweetNewStyle("crypto_buzzwords_new", 24);
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

    protected void postTopicTweet(String tweetText, String database) {
        File imagefile = new File(System.getProperty("user.home") + "/output22.png");

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

            UploadedMedia media = twitter.uploadMedia(imagefile);
            long mediaId = media.getMediaId();

            StatusUpdate statusUpdate = new StatusUpdate(tweetText);
            statusUpdate.setMediaIds(mediaId);
            twitter.updateStatus(statusUpdate);
        } catch (TwitterException te) {

        }
    }

    private String getConsumerKey(String database) {
        String consumerKey = "";

        if(database.equals("buzzwords_new")) {
            consumerKey = "Hig2msvOUlhdGPEAz8nLHeZ3w";
        } else if(database.equals("finance_buzzwords_new")) {
            consumerKey = "xoWF7uruxZztzCQZq44fxIZ90";
        } else if(database.equals("sport_buzzwords_new")) {
            consumerKey = "dDySXH1ZKqBTPdocu7XL2ovz4";
        } else if(database.equals("entertainment_buzzwords_new")) {
            consumerKey = "VFDFkHRE6amyiDlZLoDl6vPi8";
        } else if(database.equals("crypto_buzzwords_new")) {
            consumerKey = "1eYTjOuwDEbUXWbQRqJsHSRzt";
        }
        return consumerKey;
    }

    private String getConsumerSecret(String database) {
        String consumerSecret = "";

        if(database.equals("buzzwords_new")) {
            consumerSecret = "9f98SygJO87Pe9sQq4uaqkMUMVzIvFby55ZOULnvVo12PqG3Ts";
        } else if(database.equals("finance_buzzwords_new")) {
            consumerSecret = "9VcRz0iZmfe21q2puuoOb3Qg6PNm9ld0b09bjL2kfR9HT9Vjl5";
        } else if(database.equals("sport_buzzwords_new")) {
            consumerSecret = "wrx82er9k7vzFjZyMQPrldiDdtBuEPdDG8Gj7vxEhFhGrv2ga6";
        } else if(database.equals("entertainment_buzzwords_new")) {
            consumerSecret = "AKxR68eVSz1H5OimFWNUz9k6pbAUt1vL5dMGASxAyQJlxWoCak";
        } else if(database.equals("crypto_buzzwords_new")) {
            consumerSecret = "B3J7Vn8olAzmMKph0BDdUB3mI01U6BTcgNENXXctCh9UZeR2wt";
        }
        return consumerSecret;
    }

    private String getAccessToken(String database) {
        String accessToken = "";

        if(database.equals("buzzwords_new")) {
            accessToken = "927177830936563712-gB6KGXsKl52m0GSosQnNDsHNn6HbDti";
        } else if(database.equals("finance_buzzwords_new")) {
            accessToken = "927959694454476800-maHKk6TAuSaosExARl7mshAdIyxl7sX";
        } else if(database.equals("sport_buzzwords_new")) {
            accessToken = "927186780125884416-2fNzfjmvofPvnKTZ7eVXSpqyQNokMRO";
        } else if(database.equals("entertainment_buzzwords_new")) {
            accessToken = "927958288351842304-r7C4xXJtLBD6vEfpv4S89NSDpqjJKrw";
        } else if(database.equals("crypto_buzzwords_new")) {
            accessToken = "931807475837358080-F36MKT4DYRuRA99H3OKXXuQsFNVZ1cr";
        }
        return accessToken;
    }

    private String getAccessSecret(String database) {
        String accessSecret = "";

        if(database.equals("buzzwords_new")) {
            accessSecret = "ZzjKRigz5iltv6DQxVZO9ZfaiZHHZmjFT8sG1OaikKaPm";
        } else if(database.equals("finance_buzzwords_new")) {
            accessSecret = "SUZBcpXiwiCPjxiDgN2ACkzGYaqmCPSb5dt9q4SQlxPko";
        } else if(database.equals("sport_buzzwords_new")) {
            accessSecret = "fueDoV8yFGLQsAz7j8WjAXrajS5ho03G7fJolwtkWhy8g";
        } else if(database.equals("entertainment_buzzwords_new")) {
            accessSecret = "f6QmyHlWyqnTQi7tstS4RGMy5Kgp3ocp3D7ShP55tWUSL";
        } else if(database.equals("crypto_buzzwords_new")) {
            accessSecret = "KsMpGzw5P267JtZ0inM761ZVgWhHAzsK4GIFFMovyWcDw";
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
            if(!database.contains("crypto")) {
                tweetText = getFirstLine(database, numberOfHours, wordsList.size()) + getWordsBelowEachOther(wordsList, true) +
                        getLastLine(database);
            } else {
                tweetText = getFirstLine(database, numberOfHours, wordsList.size()) + getWordsBelowEachOther(wordsList, false) +
                        getCryptoHashTags() + getLastLine(database);
            }
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
        } else if(database.equals("crypto_buzzwords_new")) {
            if(numberOfHours == 1) {
                firstLine = "Trending crypto " + wordOrWords + " of last hour:\n\n";
            } else {
                firstLine = "Trending crypto " + wordOrWords + " of last " + numberOfHours + " hours:\n\n";
            }
        }
        return firstLine;
    }

    private String getWordsBelowEachOther(List<String> wordsList, boolean isCrypto) throws Exception {
        StringBuilder wordsBelowEachOther = new StringBuilder();

        if(!wordsList.isEmpty()) {
            for(int i = 0; i < wordsList.size(); i++) {
                if(i == 0) {
                    if(!isCrypto) {
                        wordsBelowEachOther.append("#");
                    }
                    wordsBelowEachOther.append(wordsList.get(i));
                } else {
                    wordsBelowEachOther.append("\n");
                    if(!isCrypto) {
                        wordsBelowEachOther.append("#");
                    }
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
        } else if(database.equals("crypto_buzzwords_new")) {
            lastLine = "\n\ncryptobuzzwords.com";
        }
        return lastLine;
    }

    private String getCryptoHashTags() {
        return "\n\n#crypto #btc #bitcoin #blockchain";
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

    protected List<String> clearNumbersAndEmptyStringsFromHashTagWords(List<String> hashTagWords) {
        List<String> hashTagWordsToReturn = new ArrayList<>();

        for(String hashTagWord : hashTagWords) {
            if(!hashTagWord.equals("") && !hashTagWord.matches("[0-9]+")) {
                hashTagWordsToReturn.add(hashTagWord);
            }
        }
        return hashTagWordsToReturn;
    }

    public List<String> convertHeadlinesToNonSpecialCharactersAndLowerCase(List<String> headlinesRaw) {
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

    public List<String> getWordsSortedByFrequencyFromHeadlines(List<String> headlines, String buzzword) {
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
