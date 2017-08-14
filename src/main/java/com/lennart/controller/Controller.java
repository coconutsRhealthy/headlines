package com.lennart.controller;

import com.lennart.model.headlinesBigDb.BigDbStorer;
import com.lennart.model.headlinesBigDb.headlinesBigDbFinance.BigDbStorerFinance;
import com.lennart.model.headlinesBuzzDb.BuzzWordsManager;
import com.lennart.model.headlinesBuzzDb.headlinesBuzzDbFinance.BuzzWordsManagerFinance;
import com.lennart.model.headlinesFE.BuzzWord;
import com.lennart.model.headlinesFE.RetrieveBuzzwords;
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
        new BigDbStorer().overallMethodServer();
    }

    @RequestMapping(value = "/updateBuzzDb", method = RequestMethod.GET)
    public void updateBuzzDb() throws Exception {
        new BuzzWordsManager().overallMethodServer("buzzwords_new");
    }

    @RequestMapping(value = "/updateFinanceBigDb", method = RequestMethod.GET)
    private void updateFinanceBigDb() throws Exception {
        new BigDbStorerFinance().overallMethodServer();
    }

    @RequestMapping(value = "/updateFinanceBuzzDb", method = RequestMethod.GET)
    private void updateFinanceBuzzDb() throws Exception {
        new BuzzWordsManagerFinance().overallMethodServer("finance_buzzwords_new");
    }

    @RequestMapping(value = "/getBuzzWords", method = RequestMethod.POST)
    public @ResponseBody List<BuzzWord> sendBuzzWordsToClient(@RequestBody int numberOfHours) throws Exception {
        List<BuzzWord> buzzWords = new RetrieveBuzzwords().retrieveBuzzWordsFromDbInitialNewByHeadlineNumber("buzzwords_new", numberOfHours);
        return buzzWords;
    }

    @RequestMapping(value = "/loadMoreBuzzWords", method = RequestMethod.POST)
    public @ResponseBody List<BuzzWord> sendMoreBuzzWordsToClient(@RequestBody String combinedData) throws Exception {
        String[] combinedDataAsArray = combinedData.split(" ---- ");
        String lastWord = combinedDataAsArray[0];
        int numberOfHours = Integer.valueOf(combinedDataAsArray[1]);

        List<BuzzWord> buzzWords = new RetrieveBuzzwords().retrieveExtraBuzzWordsFromDbNewByHeadlineNumber("buzzwords_new", lastWord, numberOfHours);
        return buzzWords;
    }

    @RequestMapping(value = "/getFinanceBuzzWords", method = RequestMethod.POST)
    public @ResponseBody List<BuzzWord> sendFinanceBuzzWordsToClient(@RequestBody int numberOfHours) throws Exception {
        List<BuzzWord> buzzWords = new RetrieveBuzzwords().retrieveBuzzWordsFromDbInitialNewByHeadlineNumber("finance_buzzwords_new", numberOfHours);
        return buzzWords;
    }

    @RequestMapping(value = "/loadMoreFinanceBuzzWords", method = RequestMethod.POST)
    public @ResponseBody List<BuzzWord> sendMoreFinanceBuzzWordsToClient(@RequestBody String combinedData) throws Exception {
        String[] combinedDataAsArray = combinedData.split(" ---- ");
        String lastWord = combinedDataAsArray[0];
        int numberOfHours = Integer.valueOf(combinedDataAsArray[1]);

        List<BuzzWord> buzzWords = new RetrieveBuzzwords().retrieveExtraBuzzWordsFromDbNewByHeadlineNumber("finance_buzzwords_new", lastWord, numberOfHours);
        return buzzWords;
    }
}
