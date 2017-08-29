package com.lennart.model.twitter;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by LennartMac on 29/08/2017.
 */
public class TweetMachine {

    public void postTweet() {

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

        try
        {
            TwitterFactory factory = new TwitterFactory(cb.build());
            Twitter twitter = factory.getInstance();

            System.out.println(twitter.getScreenName());
            twitter.updateStatus("hello this is a test2222");
            //Status status = twitter.updateStatus(latestStatus);
            //System.out.println("Successfully updated the status to [" + status.getText() + "].");
        }catch (TwitterException te) {
            te.printStackTrace();
            System.exit(-1);
        }
    }

}
