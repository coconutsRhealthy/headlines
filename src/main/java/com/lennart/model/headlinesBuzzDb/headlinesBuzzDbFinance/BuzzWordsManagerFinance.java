package com.lennart.model.headlinesBuzzDb.headlinesBuzzDbFinance;

import com.lennart.model.headlinesBigDb.headlinesBigDbFinance.BigDbStorerFinance;
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
 * Created by LPO21630 on 8-8-2017.
 */
public class BuzzWordsManagerFinance extends BuzzWordsManager {

    @Override
    protected void deleteEntriesOlderThan24Hours() throws Exception {
        Date date = new Date();
        date = DateUtils.addHours(date, 2);
        long currentDate = date.getTime();

        initializeDbConnection();
        ResultSet rs = getResultSetFromQuery("SELECT * FROM finance_buzzwords_new");

        while(rs.next()) {
            String s = rs.getString("date");
            Date parsedDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(s);

            if(parsedDateTime.getTime() < currentDate - TimeUnit.HOURS.toMillis(24)) {
                String wordToRemove = rs.getString("word");

                Statement st = con.createStatement();
                st.executeUpdate("DELETE FROM finance_buzzwords_new WHERE word = '" + wordToRemove + "';");
                st.close();
            }
        }

        rs.close();
        closeDbConnection();
    }

    @Override
    protected Map<String, Map<String, List<String>>> compareCurrentWithLastDbEntry() throws Exception {
        BigDbStorerFinance bigDbStorerFinance = new BigDbStorerFinance();

        try {
            for(int i = 1; i <= 60; i++) {
                bigDbStorerFinance.initializeDocuments(i);
            }
        } catch (Exception e) {
            return null;
        }

        initializeDbConnection();

        Map<String, Double> buzzWords = getBuzzWords(getTop50HighestIncreaseWordCountCurrent("finance_news_words", bigDbStorerFinance),
                getTop50HighestIncreaseSiteCountCurrent("finance_news_words", bigDbStorerFinance));

        Map<String, Map<String, List<String>>> dataForAllBuzzWords = new DataForAllBuzzWordsProvider().getDataForAllBuzzWords(buzzWords, bigDbStorerFinance);
        closeDbConnection();

        return dataForAllBuzzWords;
    }

}
