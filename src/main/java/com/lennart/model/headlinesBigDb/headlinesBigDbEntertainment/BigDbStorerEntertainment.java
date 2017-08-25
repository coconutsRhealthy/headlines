package com.lennart.model.headlinesBigDb.headlinesBigDbEntertainment;

import com.lennart.model.headlinesBigDb.BigDbStorer;
import com.lennart.model.headlinesBuzzDb.JsoupElementsProcessor;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by LPO21630 on 25-8-2017.
 */
public class BigDbStorerEntertainment extends BigDbStorer {

    private double numberOfSites = 59.0;

    @Override
    protected void updateDatabase(int number) throws Exception {
        for(int i = 1; i <= 60; i++) {
            initializeDocuments(i);
        }

        List<String> aTexts = new JsoupElementsProcessor().getTextFromAllAElements(getListOfAllDocuments());
        updateAllOldATextsDatabase(aTexts);

        Map<String, Integer> occurrenceMapMultiple = getOccurrenceMapMultiple();
        Map<String, Integer> occurrenceMapSingle = getOccurrenceMapSingle();

        Map<String, List<Integer>> combinedMap = joinMaps(occurrenceMapMultiple, occurrenceMapSingle);

        initializeDbConnection();
        for (Map.Entry<String, List<Integer>> entry : combinedMap.entrySet()) {
            double avNoOccurrences = entry.getValue().get(0) / numberOfSites;
            double avPercentageSites = entry.getValue().get(1) / numberOfSites;

            storeOrUpdateWordInDatabase("entertainment_news_words_update", entry.getKey(), avNoOccurrences, avPercentageSites);
        }
        closeDbConnection();
    }

    @Override
    protected void updateAllOldATextsDatabase(List<String> aTexts) throws Exception {
        initializeDbConnection();

        for(String aText : aTexts) {
            String correctedAText = doStringReplacementsForDb(aText);

            Statement st = con.createStatement();
            try {
                st.executeUpdate("INSERT INTO entertainment_a_texts_update (atext) VALUES ('" + correctedAText + "')");
            } catch (Exception e) {

            }
            st.close();
        }
        closeDbConnection();
    }

    @Override
    protected void renameAtextUpdatedTableToAtexts() throws Exception {
        initializeDbConnection();
        Statement st = con.createStatement();
        st.executeUpdate("ALTER TABLE entertainment_a_texts_update RENAME TO entertainment_a_texts");
        st.close();
        closeDbConnection();
    }

    @Override
    protected void renameAtextDummyTableToAtextsUpdate() throws Exception {
        initializeDbConnection();
        Statement st = con.createStatement();
        st.executeUpdate("ALTER TABLE entertainment_a_texts_dummy RENAME TO entertainment_a_texts_update");
        st.close();
        closeDbConnection();
    }

    @Override
    protected void renameAtextOldTableToDummy() throws Exception {
        initializeDbConnection();
        Statement st = con.createStatement();
        st.executeUpdate("ALTER TABLE entertainment_a_texts RENAME TO entertainment_a_texts_dummy");
        st.close();
        closeDbConnection();
    }

    @Override
    protected void renameUpdatedTableToNewsWords() throws Exception {
        initializeDbConnection();
        Statement st = con.createStatement();
        st.executeUpdate("ALTER TABLE entertainment_news_words_update RENAME TO entertainment_news_words");
        st.close();
        closeDbConnection();
    }

    @Override
    protected void renameDummyTableToNewsWordsUpdate() throws Exception {
        initializeDbConnection();
        Statement st = con.createStatement();
        st.executeUpdate("ALTER TABLE entertainment_news_words_dummy RENAME TO entertainment_news_words_update");
        st.close();
        closeDbConnection();
    }

    @Override
    protected void renameOldTableToDummy() throws Exception {
        initializeDbConnection();
        Statement st = con.createStatement();
        st.executeUpdate("ALTER TABLE entertainment_news_words RENAME TO entertainment_news_words_dummy");
        st.close();
        closeDbConnection();
    }

    @Override
    protected void clearNewsWordsUpdateTable() throws Exception {
        initializeDbConnection();
        Statement st = con.createStatement();
        st.executeUpdate("DELETE FROM entertainment_news_words_update");
        st.close();
        closeDbConnection();
    }

    @Override
    protected void clearAtextsUpdateTable() throws Exception {
        initializeDbConnection();
        Statement st = con.createStatement();
        st.executeUpdate("DELETE FROM entertainment_a_texts_update");
        st.close();
        closeDbConnection();
    }

    @Override
    public List<String> retrieveAllOldAtexts() throws Exception{
        List<String> allOldATexts = new ArrayList<>();

        initializeDbConnection();
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM entertainment_a_texts;");

        while(rs.next()) {
            allOldATexts.add(rs.getString("atext"));
        }
        rs.close();
        st.close();
        closeDbConnection();

        return allOldATexts;
    }

    public void initializeDocuments(int number) throws IOException {
        switch(number) {
            case 1:
                document1 = readSite("...");
                break;
            case 2:
                document2 = readSite("...");
                break;
            case 3:
                document3 = readSite("...");
                break;
            case 4:
                document4 = readSite("...");
                break;
            case 5:
                document5 = readSite("...");
                break;
            case 6:
                document6 = readSite("...");
                break;
            case 7:
                document7 = readSite("...");
                break;
            case 8:
                document8 = readSite("...");
                break;
            case 9:
                document9 = readSite("...");
                break;
            case 10:
                document10 = readSite("...");
                break;
            case 11:
                document11 = readSite("...");
                break;
            case 12:
                document12 = readSite("...");
                break;
            case 13:
                document13 = readSite("...");
                break;
            case 14:
                document14 = readSite("...");
                break;
            case 15:
                document15 = readSite("...");
                break;
            case 16:
                document16 = readSite("...");
                break;
            case 17:
                document17 = readSite("...");
                break;
            case 18:
                document18 = readSite("...");
                break;
            case 19:
                document19 = readSite("...");
                break;
            case 20:
                document20 = readSite("...");
                break;
            case 21:
                document21 = readSite("...");
                break;
            case 22:
                document22 = readSite("...");
                break;
            case 23:
                document23 = readSite("...");
                break;
            case 24:
                document24 = readSite("...");
                break;
            case 25:
                document25 = readSite("...");
                break;
            case 26:
                document26 = readSite("...");
                break;
            case 27:
                document27 = readSite("...");
                break;
            case 28:
                document28 = readSite("...");
                break;
            case 29:
                document29 = readSite("...");
                break;
            case 30:
                document30 = readSite("...");
                break;
            case 31:
                document31 = readSite("...");
                break;
            case 32:
                document32 = readSite("...");
                break;
            case 33:
                document33 = readSite("...");
                break;
            case 34:
                document34 = readSite("...");
                break;
            case 35:
                document35 = readSite("...");
                break;
            case 36:
                document36 = readSite("...");
                break;
            case 37:
                document37 = readSite("...");
                break;
            case 38:
                document38 = readSite("...");
                break;
            case 39:
                document39 = readSite("...");
                break;
            case 40:
                document40 = readSite("...");
                break;
            case 41:
                document41 = readSite("...");
                break;
            case 42:
                document42 = readSite("...");
                break;
            case 43:
                document43 = readSite("...");
                break;
            case 44:
                document44 = readSite("...");
                break;
            case 45:
                document45 = readSite("...");
                break;
            case 46:
                document46 = readSite("...");
                break;
            case 47:
                document47 = readSite("...");
                break;
            case 48:
                document48 = readSite("...");
                break;
            case 49:
                document49 = readSite("...");
                break;
            case 51:
                document51 = readSite("...");
                break;
            case 52:
                document52 = readSite("...");
                break;
            case 53:
                document53 = readSite("...");
                break;
            case 54:
                document54 = readSite("...");
                break;
            case 55:
                document55 = readSite("...");
                break;
            case 56:
                document56 = readSite("...");
                break;
            case 57:
                document57 = readSite("...");
                break;
            case 58:
                document58 = readSite("...");
                break;
            case 59:
                document59 = readSite("...");
                break;
            case 60:
                document60 = readSite("...");
                break;
        }
    }
}
