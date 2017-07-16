package com.lennart.controller;

import com.lennart.model.headlinesBigDb.BigDbStorer;
import com.lennart.model.headlinesBuzzDb.BuzzWordsManager;
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
        new BuzzWordsManager().overallMethodServer();
    }

    @RequestMapping(value = "/getBuzzWords", method = RequestMethod.GET)
    public @ResponseBody List<BuzzWord> sendBuzzWordsToClient() throws Exception {
        List<BuzzWord> buzzWords = new RetrieveBuzzwords().retrieveBuzzWordsFromDbInitial("buzzwords_new");
        return buzzWords;
    }

    @RequestMapping(value = "/loadMoreBuzzWords", method = RequestMethod.POST)
    public @ResponseBody List<BuzzWord> sendMoreBuzzWordsToClient(@RequestBody String lastWord) throws Exception {
        List<BuzzWord> buzzWords = new RetrieveBuzzwords().retrieveExtraBuzzWordsFromDb("buzzwords_new", lastWord);
        return buzzWords;
    }
}
