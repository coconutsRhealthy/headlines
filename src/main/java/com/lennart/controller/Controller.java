package com.lennart.controller;

import com.lennart.model.BigDbStorer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;


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
        for(int i = 1; i <= 60; i++) {
            new BigDbStorer().overallMethodServer();
        }
    }
}
