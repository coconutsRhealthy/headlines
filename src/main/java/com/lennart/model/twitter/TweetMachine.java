package com.lennart.model.twitter;

import com.lennart.model.headlinesBuzzDb.DataForAllBuzzWordsProvider;
import com.lennart.model.headlinesFE.BuzzWord;
import com.lennart.model.headlinesFE.RetrieveBuzzwords;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.util.*;

/**
 * Created by LennartMac on 29/08/2017.
 */
public class TweetMachine {

    public void postTweetForNewBuzzword(String word, List<String> headlines) {
        String tweetText = getTweetText(word, headlines);

        if(tweetText != null && tweetText.length() > 80) {
            postTweet(tweetText);
        }
    }

    private List<BuzzWord> getBuzzwordsToBeTweeted() throws Exception {
        List<BuzzWord> buzzWords = new RetrieveBuzzwords().retrieveBuzzWordsFromDbInitialNewByHeadlineNumber("buzzwords_new", 5, "home");
        return buzzWords;
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

    private String getTweetText(String word, List<String> headlines) {
        String tweetWithoutHashTags = "Buzzword: " + word + "\n" + "Sample headline: " + getHeadlineForBuzzword(headlines, word) + "\n" + "More at newsbuzzwords.com" + "\n";
        String tweet =  tweetWithoutHashTags + getHashTags(word, headlines, 140 - tweetWithoutHashTags.length());
        return tweet;
    }

    private String getHeadlineForBuzzword(List<String> headlines, String buzzWord) {
        String headlineToReturn = "No sample headline Identified";

        for(String headline : headlines) {
            String correctedHeadline = convertHeadlineToCorrectedFormat(headline);
            correctedHeadline = correctedHeadline.substring(0, 37);
            correctedHeadline = correctedHeadline + "...";

            if(correctedHeadline.contains(buzzWord)) {
                headlineToReturn = headline.substring(0, 37);
                headlineToReturn = headlineToReturn + "...";
                break;
            }
        }

        if(headlineToReturn.equals("No sample headline Identified")) {
            String lastResortHeadlineToReturn = convertHeadlineToCorrectedFormat(headlines.get(0));
            lastResortHeadlineToReturn = lastResortHeadlineToReturn.substring(0, 37);
            lastResortHeadlineToReturn = lastResortHeadlineToReturn + "...";
            headlineToReturn = lastResortHeadlineToReturn;
        }

        return headlineToReturn;
    }

    private String getHashTags(String buzzword, List<String> headlines, int numberOfCharactersRemaining) {
        StringBuilder hashTags = new StringBuilder();

        if(numberOfCharactersRemaining > 0) {
            String buzzwordHashtag = "#" + buzzword + " ";

            if(buzzwordHashtag.length() < numberOfCharactersRemaining) {
                hashTags.append(buzzwordHashtag);
            }

            if(hashTags.length() < numberOfCharactersRemaining) {
                List<String> frequencyWordsFromHeadlines = getWordsSortedByFrequencyFromHeadlines(headlines, buzzword);

                for(String frequencyWord : frequencyWordsFromHeadlines) {
                    if(hashTags.length() >= numberOfCharactersRemaining) {
                        break;
                    }

                    String hashTagToAdd = "#" + frequencyWord + " ";
                    String hashTagsTryOutTotal = hashTags.toString() + hashTagToAdd;

                    if(hashTagsTryOutTotal.length() < numberOfCharactersRemaining) {
                        hashTags.append(hashTagToAdd);
                    }
                }
            }
        }
        return hashTags.toString();
    }

    private String convertHeadlineToCorrectedFormat(String rawHeadline) {
        String correctedHeadline = rawHeadline.toLowerCase();
        correctedHeadline = correctedHeadline.replaceAll("[^A-Za-z0-9 ]", "");
        return correctedHeadline;
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
        if(buzzword.equals("condemnation")) {
            System.out.println("wacht");
        }

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
}
