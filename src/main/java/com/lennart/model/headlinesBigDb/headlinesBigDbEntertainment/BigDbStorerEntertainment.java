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
                document1 = readSite("https://www.yahoo.com/celebrity");
                break;
            case 2:
                document2 = readSite("http://www.tmz.com");
                break;
            case 3:
                document3 = readSite("http://www.eonline.com");
                break;
            case 4:
                document4 = readSite("http://people.com");
                break;
            case 5:
                document5 = readSite("http://www.usmagazine.com");
                break;
            case 6:
                document6 = readSite("http://www.wonderwall.com");
                break;
            case 7:
                document7 = readSite("http://www.zimbio.com");
                break;
            case 8:
                document8 = readSite("http://perezhilton.com");
                break;
            case 9:
                document9 = readSite("http://hollywoodlife.com");
                break;
            case 10:
                document10 = readSite("http://radaronline.com");
                break;
            case 11:
                document11 = readSite("https://www.popsugar.com");
                break;
            case 12:
                document12 = readSite("http://www.wetpaint.com");
                break;
            case 13:
                document13 = readSite("http://mediatakeout.com");
                break;
            case 14:
                document14 = readSite("http://toofab.com");
                break;
            case 15:
                document15 = readSite("http://ohnotheydidnt.livejournal.com");
                break;
            case 16:
                document16 = readSite("http://dlisted.com");
                break;
            case 17:
                document17 = readSite("http://www.thesuperficial.com");
                break;
            case 18:
                document18 = readSite("https://www.gofugyourself.com");
                break;
            case 19:
                document19 = readSite("http://www.celebitchy.com");
                break;
            case 20:
                document20 = readSite("http://www.celebslam.com");
                break;
            case 21:
                document21 = readSite("http://www.egotastic.com");
                break;
            case 22:
                document22 = readSite("http://www.hellomagazine.com");
                break;
            case 23:
                document23 = readSite("http://idly.craveonline.com");
                break;
            case 24:
                document24 = readSite("http://www.infdaily.org");
                break;
            case 25:
                document25 = readSite("http://www.justjared.com");
                break;
            case 26:
                document26 = readSite("http://www.socialitelife.com");
                break;
            case 27:
                document27 = readSite("https://www.wesmirch.com");
                break;
            case 28:
                document28 = readSite("http://www.wwtdd.com");
                break;
            case 29:
                document29 = readSite("http://x17online.com");
                break;
            case 30:
                document30 = readSite("https://www.thehollywoodgossip.com");
                break;
            case 31:
                document31 = readSite("http://www.celebritygossip.com");
                break;
            case 32:
                document32 = readSite("http://www.digitalspy.com");
                break;
            case 33:
                document33 = readSite("http://www.latimes.com/entertainment");
                break;
            case 34:
                document34 = readSite("http://variety.com");
                break;
            case 35:
                document35 = readSite("http://www.bet.com");
                break;
            case 36:
                document36 = readSite("http://theshaderoom.com");
                break;
            case 37:
                document37 = readSite("http://www.lifeandstylemag.com");
                break;
            case 38:
                document38 = readSite("http://www.glamourmagazine.co.uk");
                break;
            case 39:
                document39 = readSite("http://www.cambio.com");
                break;
            case 40:
                document40 = readSite("https://www.vanityfair.com");
                break;
            case 41:
                document41 = readSite("http://www.intouchweekly.com");
                break;
            case 42:
                document42 = readSite("http://www.entertainmentdaily.co.uk");
                break;
            case 43:
                document43 = readSite("http://www.accesshollywood.com");
                break;
            case 44:
                document44 = readSite("http://www.celebuzz.com");
                break;
            case 45:
                document45 = readSite("https://www.reddit.com/r/entertainment");
                break;
            case 46:
                document46 = readSite("https://www.buzzfeed.com/celebrity");
                break;
            case 47:
                document47 = readSite("http://deadline.com");
                break;
            case 48:
                document48 = readSite("http://globalnews.ca/entertainment");
                break;
            case 49:
                document49 = readSite("http://thefix.nine.com.au");
                break;
            case 51:
                document51 = readSite("http://celebrityinsider.org");
                break;
            case 52:
                document52 = readSite("http://stylecaster.com");
                break;
            case 53:
                document53 = readSite("https://www.thesun.co.uk/tvandshowbiz");
                break;
            case 54:
                document54 = readSite("https://bossip.com");
                break;
            case 55:
                document55 = readSite("http://www.laineygossip.com");
                break;
            case 56:
                document56 = readSite("http://www.ok.co.uk");
                break;
            case 57:
                document57 = readSite("https://www.gossipcop.com");
                break;
            case 58:
                document58 = readSite("http://www.fame10.com");
                break;
            case 59:
                document59 = readSite("http://etcanada.com");
                break;
            case 60:
                document60 = readSite("http://sandrarose.com");
                break;
        }
    }
}
