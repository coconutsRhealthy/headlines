package com.lennart.model.headlinesBigDb.headlinesBigDbSport;

import com.lennart.model.headlinesBigDb.BigDbStorer;
import com.lennart.model.headlinesBuzzDb.JsoupElementsProcessor;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by LennartMac on 14/08/2017.
 */
public class BigDbStorerSport extends BigDbStorer {

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

            storeOrUpdateWordInDatabase("sport_news_words_update", entry.getKey(), avNoOccurrences, avPercentageSites);
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
                st.executeUpdate("INSERT INTO sport_a_texts_update (atext) VALUES ('" + correctedAText + "')");
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
        st.executeUpdate("ALTER TABLE sport_a_texts_update RENAME TO sport_a_texts");
        st.close();
        closeDbConnection();
    }

    @Override
    protected void renameAtextDummyTableToAtextsUpdate() throws Exception {
        initializeDbConnection();
        Statement st = con.createStatement();
        st.executeUpdate("ALTER TABLE sport_a_texts_dummy RENAME TO sport_a_texts_update");
        st.close();
        closeDbConnection();
    }

    @Override
    protected void renameAtextOldTableToDummy() throws Exception {
        initializeDbConnection();
        Statement st = con.createStatement();
        st.executeUpdate("ALTER TABLE sport_a_texts RENAME TO sport_a_texts_dummy");
        st.close();
        closeDbConnection();
    }

    @Override
    protected void renameUpdatedTableToNewsWords() throws Exception {
        initializeDbConnection();
        Statement st = con.createStatement();
        st.executeUpdate("ALTER TABLE sport_news_words_update RENAME TO sport_news_words");
        st.close();
        closeDbConnection();
    }

    @Override
    protected void renameDummyTableToNewsWordsUpdate() throws Exception {
        initializeDbConnection();
        Statement st = con.createStatement();
        st.executeUpdate("ALTER TABLE sport_news_words_dummy RENAME TO sport_news_words_update");
        st.close();
        closeDbConnection();
    }

    @Override
    protected void renameOldTableToDummy() throws Exception {
        initializeDbConnection();
        Statement st = con.createStatement();
        st.executeUpdate("ALTER TABLE sport_news_words RENAME TO sport_news_words_dummy");
        st.close();
        closeDbConnection();
    }

    @Override
    protected void clearNewsWordsUpdateTable() throws Exception {
        initializeDbConnection();
        Statement st = con.createStatement();
        st.executeUpdate("DELETE FROM sport_news_words_update");
        st.close();
        closeDbConnection();
    }

    @Override
    protected void clearAtextsUpdateTable() throws Exception {
        initializeDbConnection();
        Statement st = con.createStatement();
        st.executeUpdate("DELETE FROM sport_a_texts_update");
        st.close();
        closeDbConnection();
    }

    @Override
    public List<String> retrieveAllOldAtexts() throws Exception{
        List<String> allOldATexts = new ArrayList<>();

        initializeDbConnection();
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM sport_a_texts;");

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
                document1 = readSite("https://sports.yahoo.com");
                break;
            case 2:
                document2 = readSite("http://www.espn.com");
                break;
            case 3:
                document3 = readSite("http://www.bleacherreport.com");
                break;
            case 4:
                document4 = readSite("https://www.cbssports.com");
                break;
            case 5:
                document5 = readSite("https://www.si.com");
                break;
            case 6:
                document6 = readSite("http://www.nbcsports.com");
                break;
            case 7:
                document7 = readSite("https://www.sbnation.com");
                break;
            case 8:
                document8 = readSite("http://www.foxsports.com");
                break;
            case 9:
                document9 = readSite("http://www.rantsports.com");
                break;
            case 10:
                document10 = readSite("http://deadspin.com");
                break;
            case 11:
                document11 = readSite("http://www.thepostgame.com");
                break;
            case 12:
                document12 = readSite("http://www.sportingnews.com");
                break;
            case 13:
                document13 = readSite("http://www.scout.com");
                break;
            case 14:
                document14 = readSite("http://www.yardbarker.com");
                break;
            case 15:
                document15 = readSite("http://www.goal.com/en");
                break;
            case 16:
                document16 = readSite("http://www.skysports.com");
                break;
            case 17:
                document17 = readSite("http://www.eurosport.com");
                break;
            case 18:
                document18 = readSite("http://www.espnfc.com");
                break;
            case 19:
                document19 = readSite("http://www.bbc.com/sport");
                break;
            case 20:
                document20 = readSite("https://www.theguardian.com/uk/sport");
                break;
            case 21:
                document21 = readSite("http://www.independent.co.uk/sport");
                break;
            case 22:
                document22 = readSite("http://www.mirror.co.uk/sport");
                break;
            case 23:
                document23 = readSite("http://www.football365.com");
                break;
            case 24:
                document24 = readSite("http://www.tennisnow.com");
                break;
            case 25:
                document25 = readSite("https://www.reuters.com/news/sports");
                break;
            case 26:
                document26 = readSite("https://www.thestar.com/sports.html");
                break;
            case 27:
                document27 = readSite("https://www.nytimes.com/section/sports");
                break;
            case 28:
                document28 = readSite("https://www.washingtonpost.com/sports");
                break;
            case 29:
                document29 = readSite("http://www.huffingtonpost.com/section/sports");
                break;
            case 30:
                document30 = readSite("http://www.latimes.com/sports/");
                break;
            case 31:
                document31 = readSite("http://edition.cnn.com/sport");
                break;
            case 32:
                document32 = readSite("http://www.thenews.mx/sports");
                break;
            case 33:
                document33 = readSite("http://riotimesonline.com/brazil-news/category/rio-sports");
                break;
            case 34:
                document34 = readSite("https://www.thesun.co.uk/sport");
                break;
            case 35:
                document35 = readSite("https://www.irishtimes.com/sport");
                break;
            case 36:
                document36 = readSite("http://www.telegraph.co.uk/sport");
                break;
            case 37:
                document37 = readSite("https://www.rt.com/sport");
                break;
            case 38:
                document38 = readSite("http://www.dailysun.co.za/sport");
                break;
            case 39:
                document39 = readSite("http://www.vanguardngr.com/category/sports");
                break;
            case 40:
                document40 = readSite("http://gulfnews.com/sport");
                break;
            case 41:
                document41 = readSite("https://www.dailysabah.com/sports");
                break;
            case 42:
                document42 = readSite("http://www.tehrantimes.com/service/sports");
                break;
            case 43:
                document43 = readSite("http://timesofoman.com/Sports");
                break;
            case 44:
                document44 = readSite("http://timesofindia.indiatimes.com/sports");
                break;
            case 45:
                document45 = readSite("http://indianexpress.com/section/sports");
                break;
            case 46:
                document46 = readSite("http://www.chinadaily.com.cn/sports");
                break;
            case 47:
                document47 = readSite("http://www.shanghaidaily.com/sports");
                break;
            case 48:
                document48 = readSite("http://www.xinhuanet.com/english/sports");
                break;
            case 49:
                document49 = readSite("http://www.globaltimes.cn/sports");
                break;
            case 51:
                document51 = readSite("http://www.scmp.com/sport");
                break;
            case 52:
                document52 = readSite("http://www.abc.net.au/news/sport");
                break;
            case 53:
                document53 = readSite("http://www.theaustralian.com.au/sport");
                break;
            case 54:
                document54 = readSite("http://www.nzherald.co.nz/sport");
                break;
            case 55:
                document55 = readSite("http://www.aljazeera.com/topics/categories/sport.html");
                break;
            case 56:
                document56 = readSite("http://www.japantimes.co.jp/sports");
                break;
            case 57:
                document57 = readSite("http://www.bangkokpost.com/news/sports");
                break;
            case 58:
                document58 = readSite("https://japantoday.com/category/sports");
                break;
            case 59:
                document59 = readSite("http://www.express.co.uk/sport");
                break;
            case 60:
                document60 = readSite("https://www.foxsports.com");
                break;
        }
    }
}
