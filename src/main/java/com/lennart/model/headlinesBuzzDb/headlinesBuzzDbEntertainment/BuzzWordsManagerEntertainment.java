package com.lennart.model.headlinesBuzzDb.headlinesBuzzDbEntertainment;

import com.lennart.model.headlinesBigDb.headlinesBigDbEntertainment.BigDbStorerEntertainment;
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
 * Created by LPO21630 on 25-8-2017.
 */
public class BuzzWordsManagerEntertainment extends BuzzWordsManager {

    @Override
    protected void deleteEntriesOlderThan24Hours() throws Exception {
        Date date = new Date();
        date = DateUtils.addHours(date, 2);
        long currentDate = date.getTime();

        initializeDbConnection();
        ResultSet rs = getResultSetFromQuery("SELECT * FROM entertainment_buzzwords_new");

        while(rs.next()) {
            String s = rs.getString("date");
            Date parsedDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(s);

            if(parsedDateTime.getTime() < currentDate - TimeUnit.HOURS.toMillis(24)) {
                String wordToRemove = rs.getString("word");

                Statement st = con.createStatement();
                st.executeUpdate("DELETE FROM entertainment_buzzwords_new WHERE word = '" + wordToRemove + "';");
                st.close();
            }
        }

        rs.close();
        closeDbConnection();
    }

    @Override
    protected Map<String, Map<String, List<String>>> compareCurrentWithLastDbEntry() throws Exception {
        BigDbStorerEntertainment bigDbStorerEntertainment = new BigDbStorerEntertainment();

        try {
            for(int i = 1; i <= 60; i++) {
                bigDbStorerEntertainment.initializeDocuments(i);
            }
        } catch (Exception e) {
            return null;
        }

        initializeDbConnection();

        Map<String, Double> buzzWords = getBuzzWords(getTop50HighestIncreaseWordCountCurrent("entertainment_news_words", bigDbStorerEntertainment),
                getTop50HighestIncreaseSiteCountCurrent("entertainment_news_words", bigDbStorerEntertainment));

        Map<String, Map<String, List<String>>> dataForAllBuzzWords = new DataForAllBuzzWordsProvider().getDataForAllBuzzWords(buzzWords, bigDbStorerEntertainment);
        closeDbConnection();

        return dataForAllBuzzWords;
    }
}
