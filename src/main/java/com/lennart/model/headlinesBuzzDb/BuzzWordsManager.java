package com.lennart.model.headlinesBuzzDb;

import com.lennart.model.headlinesBigDb.BigDbStorer;
import org.apache.commons.lang3.time.DateUtils;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by LennartMac on 05/06/17.
 */
public class BuzzWordsManager {

    protected Connection con;
    private double numberOfSites = 59.0;

    public void overallMethodServer(String database) {
        while(true) {
            try {
                deleteEntriesOlderThan24Hours();
            } catch(Exception e) {
                //overallMethodServer();
            }

            try {
                Map<String, Map<String, List<String>>> dataForAllBuzzWords = compareCurrentWithLastDbEntry();

                if(dataForAllBuzzWords != null) {
                    new StoreBuzzwords().storeBuzzwordsInDb(database, dataForAllBuzzWords);
                }
            } catch (Exception q) {

            }

            try {
                new RelatedBuzzwordsIdentifier().updateGroupsInDb(database);
            } catch (Exception e) {

            }
        }
    }

    protected void deleteEntriesOlderThan24Hours() throws Exception {
        Date date = new Date();
        date = DateUtils.addHours(date, 2);
        long currentDate = date.getTime();

        initializeDbConnection();
        ResultSet rs = getResultSetFromQuery("SELECT * FROM buzzwords_new");

        while(rs.next()) {
            String s = rs.getString("date");
            Date parsedDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(s);

            if(parsedDateTime.getTime() < currentDate - TimeUnit.HOURS.toMillis(24)) {
                String wordToRemove = rs.getString("word");

                Statement st = con.createStatement();
                st.executeUpdate("DELETE FROM buzzwords_new WHERE word = '" + wordToRemove + "';");
                st.close();
            }
        }

        rs.close();
        closeDbConnection();
    }

    protected void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/words", "root", "Vuurwerk00");
    }

    protected void closeDbConnection() throws SQLException {
        con.close();
    }

    protected Map<String, Double> getBuzzWords(Map<String, Double> wordMap, Map<String, Double> siteMap) {
        Map<String, Double> combined = new HashMap<>();

        for (Map.Entry<String, Double> entry : wordMap.entrySet()) {
            if(siteMap.get(entry.getKey()) != null) {
                combined.put(entry.getKey(), entry.getValue());
            }
        }

        for (Map.Entry<String, Double> entry : siteMap.entrySet()) {
            if(combined.get(entry.getKey()) == null) {
                if(wordMap.get(entry.getKey()) != null) {
                    combined.put(entry.getKey(), entry.getValue());
                }
            }
        }

        return sortByValue(combined);
    }

    protected ResultSet getResultSetFromQuery(String query) throws SQLException {
        Statement st = con.createStatement();
        return st.executeQuery(query);
    }

    protected Map<String, Map<String, List<String>>> compareCurrentWithLastDbEntry() throws Exception {
        BigDbStorer bigDbStorer = new BigDbStorer();

        try {
            for(int i = 1; i <= 60; i++) {
                bigDbStorer.initializeDocuments(i);
            }
        } catch (Exception e) {
            return null;
        }

        initializeDbConnection();

        Map<String, Double> buzzWords = getBuzzWords(getTop50HighestIncreaseWordCountCurrent("news_words", bigDbStorer),
                getTop50HighestIncreaseSiteCountCurrent("news_words", bigDbStorer));

        Map<String, Map<String, List<String>>> dataForAllBuzzWords = new DataForAllBuzzWordsProvider().getDataForAllBuzzWords(buzzWords, bigDbStorer);
        closeDbConnection();

        return dataForAllBuzzWords;
    }

    protected Map<String, Double> getTop50HighestIncreaseWordCountCurrent(String database, BigDbStorer bigDbStorer) throws Exception {
        Map<String, Double> wordIncreaseMap = new HashMap<>();

        initializeDbConnection();
        ResultSet rs = getResultSetFromQuery("SELECT * FROM " + database + ";");

        Map<String, Double> map1 = new HashMap<>();

        while(rs.next()) {
            map1.put(rs.getString("word"), rs.getDouble("av_no_occurr_site"));
        }

        rs.close();
        closeDbConnection();

        Map<String, Double> map2 = convertIntegerMapToDoubleWithSiteDivide(bigDbStorer.getOccurrenceMapMultiple());

        for (Map.Entry<String, Double> entry : map2.entrySet()) {
            String word = entry.getKey();

            double oldWordPerSite;

            if(map1.get(entry.getKey()) != null) {
                oldWordPerSite = map1.get(entry.getKey());
            } else {
                oldWordPerSite = 0;
            }

            double newWordPerSite = entry.getValue();

            if(oldWordPerSite >= 0.033 || newWordPerSite >= 0.033) {
                wordIncreaseMap.put(word, newWordPerSite / oldWordPerSite);
            }
        }

        Map<String, Double> filteredMap = new HashMap<>();

        for (Map.Entry<String, Double> entry : wordIncreaseMap.entrySet()) {
            if(entry.getValue() >= 1.3) {
                filteredMap.put(entry.getKey(), entry.getValue());
            }
        }

        return sortByValue(filteredMap);
    }

    protected Map<String, Double> getTop50HighestIncreaseSiteCountCurrent(String database, BigDbStorer bigDbStorer) throws Exception {
        Map<String, Double> wordIncreaseMap = new HashMap<>();

        initializeDbConnection();
        ResultSet rs = getResultSetFromQuery("SELECT * FROM " + database + ";");

        Map<String, Double> map1 = new HashMap<>();

        while(rs.next()) {
            map1.put(rs.getString("word"), rs.getDouble("av_no_sites"));
        }

        rs.close();
        closeDbConnection();

        Map<String, Double> map2 = convertIntegerMapToDoubleWithSiteDivide(bigDbStorer.getOccurrenceMapSingle());

        for (Map.Entry<String, Double> entry : map2.entrySet()) {
            String word = entry.getKey();

            double oldWordPerSite;

            if(map1.get(entry.getKey()) != null) {
                oldWordPerSite = map1.get(entry.getKey());
            } else {
                oldWordPerSite = 0;
            }

            double newWordPerSite = entry.getValue();

            if(newWordPerSite >= 0.033) {
                wordIncreaseMap.put(word, newWordPerSite / oldWordPerSite);
            }
        }

        Map<String, Double> filteredMap = new HashMap<>();

        for (Map.Entry<String, Double> entry : wordIncreaseMap.entrySet()) {
            if(entry.getValue() >= 1.3) {
                filteredMap.put(entry.getKey(), entry.getValue());
            }
        }

        return sortByValue(filteredMap);
    }

    private Map<String, Double> convertIntegerMapToDoubleWithSiteDivide(Map<String, Integer> integerMap) {
        Map<String, Double> doubleMap = new HashMap<>();

        for (Map.Entry<String, Integer> entry : integerMap.entrySet()) {
            double d = (double) entry.getValue();
            doubleMap.put(entry.getKey(), d / numberOfSites);
        }
        return doubleMap;
    }

    private <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>( map.entrySet() );
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o1.getValue() ).compareTo( o2.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
}