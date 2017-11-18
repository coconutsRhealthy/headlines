package com.lennart.controller;

import com.lennart.model.headlinesBigDb.BigDbStorer;
import com.lennart.model.headlinesBigDb.headlinesBigDbCrypto.BigDbStorerCrypto;
import com.lennart.model.headlinesBigDb.headlinesBigDbEntertainment.BigDbStorerEntertainment;
import com.lennart.model.headlinesBigDb.headlinesBigDbFinance.BigDbStorerFinance;
import com.lennart.model.headlinesBigDb.headlinesBigDbSport.BigDbStorerSport;
import com.lennart.model.headlinesBuzzDb.BuzzWordsManager;
import com.lennart.model.headlinesBuzzDb.headlinesBuzzDbCrypto.BuzzWordsManagerCrypto;
import com.lennart.model.headlinesBuzzDb.headlinesBuzzDbEntertainment.BuzzWordsManagerEntertainment;
import com.lennart.model.headlinesBuzzDb.headlinesBuzzDbFinance.BuzzWordsManagerFinance;
import com.lennart.model.headlinesBuzzDb.headlinesBuzzDbSport.BuzzWordsManagerSport;
import com.lennart.model.headlinesFE.BuzzWord;
import com.lennart.model.headlinesFE.RetrieveBuzzwords;
import com.lennart.model.headlinesFE.RetrieveTopics;
import com.lennart.model.headlinesFE.Topic;
import com.lennart.model.twitter.TopicTweetMachine;
import com.lennart.model.twitter.TweetMachine;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Configuration
@EnableAutoConfiguration
@RestController
public class Controller extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Controller.class);
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Controller.class, args);
    }

    @RequestMapping(value = "/updateBigDb", method = RequestMethod.GET)
    public void updateBigDb() throws Exception {
        new BigDbStorer().overallMethodServer("main");
    }

    @RequestMapping(value = "/updateBuzzDb", method = RequestMethod.GET)
    public void updateBuzzDb() throws Exception {
        new BuzzWordsManager().overallMethodServer("buzzwords_new");
    }

    @RequestMapping(value = "/updateFinanceBigDb", method = RequestMethod.GET)
    private void updateFinanceBigDb() throws Exception {
        new BigDbStorerFinance().overallMethodServer("finance");
    }

    @RequestMapping(value = "/updateFinanceBuzzDb", method = RequestMethod.GET)
    private void updateFinanceBuzzDb() throws Exception {
        new BuzzWordsManagerFinance().overallMethodServer("finance_buzzwords_new");
    }

    @RequestMapping(value = "/updateSportBigDb", method = RequestMethod.GET)
    private void updateSportBigDb() throws Exception {
        new BigDbStorerSport().overallMethodServer("sport");
    }

    @RequestMapping(value = "/updateSportBuzzDb", method = RequestMethod.GET)
    private void updateSportBuzzDb() throws Exception {
        new BuzzWordsManagerSport().overallMethodServer("sport_buzzwords_new");
    }

    @RequestMapping(value = "/updateEntertainmentBigDb", method = RequestMethod.GET)
    private void updateEntertainmentBigDb() throws Exception {
        new BigDbStorerEntertainment().overallMethodServer("entertainment");
    }

    @RequestMapping(value = "/updateEntertainmentBuzzDb", method = RequestMethod.GET)
    private void updateEntertainmentBuzzDb() throws Exception {
        new BuzzWordsManagerEntertainment().overallMethodServer("entertainment_buzzwords_new");
    }

    @RequestMapping(value = "/updateCryptoBigDb", method = RequestMethod.GET)
    private void updateCryptoBigDb() throws Exception {
        new BigDbStorerCrypto().overallMethodServer("crypto");
    }

    @RequestMapping(value = "/updateCryptoBuzzDb", method = RequestMethod.GET)
    private void updateCryptoBuzzDb() throws Exception {
        new BuzzWordsManagerCrypto().overallMethodServer("crypto_buzzwords_new");
    }


    @RequestMapping(value = "/postTweets", method = RequestMethod.GET)
    private void postTweets() throws Exception {
        new TopicTweetMachine().overallMethodServer();
    }

    @RequestMapping(value = "/postCryptoTweets", method = RequestMethod.GET)
    private void postCryptoTweets() throws Exception {
        new TweetMachine().overallMethodServer();
    }

    @RequestMapping(value = "/getBuzzWords", method = RequestMethod.POST)
    public @ResponseBody List<BuzzWord> sendBuzzWordsToClient(@RequestBody int numberOfHours) throws Exception {
        List<BuzzWord> buzzWords = new RetrieveBuzzwords().retrieveBuzzWordsFromDbInitialNewByHeadlineNumber("buzzwords_new", numberOfHours, "home");
        return buzzWords;
    }

    @RequestMapping(value = "/loadMoreBuzzWords", method = RequestMethod.POST)
    public @ResponseBody List<BuzzWord> sendMoreBuzzWordsToClient(@RequestBody String combinedData) throws Exception {
        String[] combinedDataAsArray = combinedData.split(" ---- ");
        int numberOfHours = Integer.valueOf(combinedDataAsArray[0]);
        int numberOfWordsPresentOnSite = Integer.valueOf(combinedDataAsArray[1]);

        List<BuzzWord> buzzWords = new RetrieveBuzzwords().retrieveExtraBuzzWordsFromDbNewByHeadlineNumber
                ("buzzwords_new", numberOfHours, "home", numberOfWordsPresentOnSite);
        return buzzWords;
    }

    @RequestMapping(value = "/getFinanceBuzzWords", method = RequestMethod.POST)
    public @ResponseBody List<BuzzWord> sendFinanceBuzzWordsToClient(@RequestBody int numberOfHours) throws Exception {
        List<BuzzWord> buzzWords = new RetrieveBuzzwords().retrieveBuzzWordsFromDbInitialNewByHeadlineNumber("finance_buzzwords_new", numberOfHours, "finance");
        return buzzWords;
    }

    @RequestMapping(value = "/loadMoreFinanceBuzzWords", method = RequestMethod.POST)
    public @ResponseBody List<BuzzWord> sendMoreFinanceBuzzWordsToClient(@RequestBody String combinedData) throws Exception {
        String[] combinedDataAsArray = combinedData.split(" ---- ");
        int numberOfHours = Integer.valueOf(combinedDataAsArray[0]);
        int numberOfWordsPresentOnSite = Integer.valueOf(combinedDataAsArray[1]);

        List<BuzzWord> buzzWords = new RetrieveBuzzwords().retrieveExtraBuzzWordsFromDbNewByHeadlineNumber
                ("finance_buzzwords_new", numberOfHours, "finance", numberOfWordsPresentOnSite);
        return buzzWords;
    }

    @RequestMapping(value = "/getSportBuzzWords", method = RequestMethod.POST)
    public @ResponseBody List<BuzzWord> sendSportBuzzWordsToClient(@RequestBody int numberOfHours) throws Exception {
        List<BuzzWord> buzzWords = new RetrieveBuzzwords().retrieveBuzzWordsFromDbInitialNewByHeadlineNumber("sport_buzzwords_new", numberOfHours, "sport");
        return buzzWords;
    }

    @RequestMapping(value = "/loadMoreSportBuzzWords", method = RequestMethod.POST)
    public @ResponseBody List<BuzzWord> sendMoreSportBuzzWordsToClient(@RequestBody String combinedData) throws Exception {
        String[] combinedDataAsArray = combinedData.split(" ---- ");
        int numberOfHours = Integer.valueOf(combinedDataAsArray[0]);
        int numberOfWordsPresentOnSite = Integer.valueOf(combinedDataAsArray[1]);

        List<BuzzWord> buzzWords = new RetrieveBuzzwords().retrieveExtraBuzzWordsFromDbNewByHeadlineNumber
                ("sport_buzzwords_new", numberOfHours, "sport", numberOfWordsPresentOnSite);
        return buzzWords;
    }

    @RequestMapping(value = "/getEntertainmentBuzzWords", method = RequestMethod.POST)
    public @ResponseBody List<BuzzWord> sendEntertainmentBuzzWordsToClient(@RequestBody int numberOfHours) throws Exception {
        List<BuzzWord> buzzWords = new RetrieveBuzzwords().retrieveBuzzWordsFromDbInitialNewByHeadlineNumber("entertainment_buzzwords_new", numberOfHours, "entertainment");
        return buzzWords;
    }

    @RequestMapping(value = "/loadMoreEntertainmentBuzzWords", method = RequestMethod.POST)
    public @ResponseBody List<BuzzWord> sendMoreEntertainmentBuzzWordsToClient(@RequestBody String combinedData) throws Exception {
        String[] combinedDataAsArray = combinedData.split(" ---- ");
        int numberOfHours = Integer.valueOf(combinedDataAsArray[0]);
        int numberOfWordsPresentOnSite = Integer.valueOf(combinedDataAsArray[1]);

        List<BuzzWord> buzzWords = new RetrieveBuzzwords().retrieveExtraBuzzWordsFromDbNewByHeadlineNumber
                ("entertainment_buzzwords_new", numberOfHours, "entertainment", numberOfWordsPresentOnSite);
        return buzzWords;
    }

    @RequestMapping(value = "/getCryptoBuzzWords", method = RequestMethod.POST)
    public @ResponseBody List<BuzzWord> sendCryptoBuzzWordsToClient(@RequestBody int numberOfHours) throws Exception {
        List<BuzzWord> buzzWords = new RetrieveBuzzwords().retrieveBuzzWordsFromDbInitialCrypto("crypto_buzzwords_new", numberOfHours);
        return buzzWords;
    }


    //below here the new style methods (with images)

    @RequestMapping(value = "/getWorldNewsTopics", method = RequestMethod.POST)
    public @ResponseBody List<Topic> sendWorldNewsTopicsToClient() throws Exception {
        List<Topic> worldNewsTopics = new RetrieveTopics().retrieveAllTopicsFromDb("buzzwords_new");
        return worldNewsTopics;
    }

    @RequestMapping(value = "/getFinanceTopics", method = RequestMethod.POST)
    public @ResponseBody List<Topic> sendFinanceTopicsToClient() throws Exception {
        List<Topic> financeTopics = new RetrieveTopics().retrieveAllTopicsFromDb("finance_buzzwords_new");
        return financeTopics;
    }

    @RequestMapping(value = "/getSportTopics", method = RequestMethod.POST)
    public @ResponseBody List<Topic> sendSportTopicsToClient() throws Exception {
        List<Topic> sportTopics = new RetrieveTopics().retrieveAllTopicsFromDb("sport_buzzwords_new");
        return sportTopics;
    }

    @RequestMapping(value = "/getEntertainmentTopics", method = RequestMethod.POST)
    public @ResponseBody List<Topic> sendEntertainmentTopicsToClient() throws Exception {
        List<Topic> entertainmentTopics = new RetrieveTopics().retrieveAllTopicsFromDb("entertainment_buzzwords_new");
        return entertainmentTopics;
    }
}
