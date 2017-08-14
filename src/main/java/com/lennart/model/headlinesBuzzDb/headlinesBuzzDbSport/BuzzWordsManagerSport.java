package com.lennart.model.headlinesBuzzDb.headlinesBuzzDbSport;

import com.lennart.model.headlinesBigDb.headlinesBigDbSport.BigDbStorerSport;
import com.lennart.model.headlinesBuzzDb.BuzzWordsManager;
import com.lennart.model.headlinesBuzzDb.DataForAllBuzzWordsProvider;
import org.apache.commons.lang3.time.DateUtils;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by LennartMac on 14/08/2017.
 */
public class BuzzWordsManagerSport extends BuzzWordsManager {

    @Override
    protected void deleteEntriesOlderThan24Hours() throws Exception {
        Date date = new Date();
        date = DateUtils.addHours(date, 2);
        long currentDate = date.getTime();

        initializeDbConnection();
        ResultSet rs = getResultSetFromQuery("SELECT * FROM sport_buzzwords_new");

        while(rs.next()) {
            String s = rs.getString("date");
            Date parsedDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(s);

            if(parsedDateTime.getTime() < currentDate - TimeUnit.HOURS.toMillis(24)) {
                String wordToRemove = rs.getString("word");

                Statement st = con.createStatement();
                st.executeUpdate("DELETE FROM sport_buzzwords_new WHERE word = '" + wordToRemove + "';");
                st.close();
            }
        }

        rs.close();
        closeDbConnection();
    }

    @Override
    protected Map<String, Map<String, List<String>>> compareCurrentWithLastDbEntry() throws Exception {
        BigDbStorerSport bigDbStorerSport = new BigDbStorerSport();

        try {
            for(int i = 1; i <= 60; i++) {
                bigDbStorerSport.initializeDocuments(i);
            }
        } catch (Exception e) {
            return null;
        }

        initializeDbConnection();

        Map<String, Double> buzzWords = getBuzzWords(getTop50HighestIncreaseWordCountCurrent("sport_news_words", bigDbStorerSport),
                getTop50HighestIncreaseSiteCountCurrent("sport_news_words", bigDbStorerSport));

        Map<String, Map<String, List<String>>> dataForAllBuzzWords = new DataForAllBuzzWordsProvider().getDataForAllBuzzWords(buzzWords, bigDbStorerSport);
        closeDbConnection();

        return dataForAllBuzzWords;
    }
}
