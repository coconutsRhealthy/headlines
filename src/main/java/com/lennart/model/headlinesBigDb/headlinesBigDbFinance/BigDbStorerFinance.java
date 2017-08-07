package com.lennart.model.headlinesBigDb.headlinesBigDbFinance;

import com.lennart.model.headlinesBigDb.BigDbStorer;
import com.lennart.model.headlinesBuzzDb.JsoupElementsProcessor;

import java.io.IOException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

/**
 * Created by LPO21630 on 7-8-2017.
 */
public class BigDbStorerFinance extends BigDbStorer {

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

            storeOrUpdateWordInDatabase("finance_news_words_update", entry.getKey(), avNoOccurrences, avPercentageSites);
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
                st.executeUpdate("INSERT INTO finance_a_texts_update (atext) VALUES ('" + correctedAText + "')");
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
        st.executeUpdate("ALTER TABLE finance_a_texts_update RENAME TO finance_a_texts");
        st.close();
        closeDbConnection();
    }

    @Override
    protected void renameAtextDummyTableToAtextsUpdate() throws Exception {
        initializeDbConnection();
        Statement st = con.createStatement();
        st.executeUpdate("ALTER TABLE finance_a_texts_dummy RENAME TO finance_a_texts_update");
        st.close();
        closeDbConnection();
    }

    @Override
    protected void renameAtextOldTableToDummy() throws Exception {
        initializeDbConnection();
        Statement st = con.createStatement();
        st.executeUpdate("ALTER TABLE finance_a_texts RENAME TO finance_a_texts_dummy");
        st.close();
        closeDbConnection();
    }

    @Override
    protected void renameUpdatedTableToNewsWords() throws Exception {
        initializeDbConnection();
        Statement st = con.createStatement();
        st.executeUpdate("ALTER TABLE finance_news_words_update RENAME TO finance_news_words");
        st.close();
        closeDbConnection();
    }

    @Override
    protected void renameDummyTableToNewsWordsUpdate() throws Exception {
        initializeDbConnection();
        Statement st = con.createStatement();
        st.executeUpdate("ALTER TABLE finance_news_words_dummy RENAME TO finance_news_words_update");
        st.close();
        closeDbConnection();
    }

    @Override
    protected void renameOldTableToDummy() throws Exception {
        initializeDbConnection();
        Statement st = con.createStatement();
        st.executeUpdate("ALTER TABLE finance_news_words RENAME TO finance_news_words_dummy");
        st.close();
        closeDbConnection();
    }

    @Override
    protected void clearNewsWordsUpdateTable() throws Exception {
        initializeDbConnection();
        Statement st = con.createStatement();
        st.executeUpdate("DELETE FROM finance_news_words_update");
        st.close();
        closeDbConnection();
    }

    @Override
    protected void clearAtextsUpdateTable() throws Exception {
        initializeDbConnection();
        Statement st = con.createStatement();
        st.executeUpdate("DELETE FROM finance_a_texts_update");
        st.close();
        closeDbConnection();
    }

    public void initializeDocuments(int number) throws IOException {
        switch(number) {
            case 1:
                document1 = readSite("http://www.investopedia.com");
                break;
            case 2:
                document2 = readSite("https://finance.yahoo.com");
                break;
            case 3:
                document3 = readSite("https://www.fool.com");
                break;
            case 4:
                document4 = readSite("https://www.thestreet.com");
                break;
            case 5:
                document5 = readSite("https://www.wsj.com/news/markets");
                break;
            case 6:
                document6 = readSite("http://www.msn.com/en-us/money/markets");
                break;
            case 7:
                document7 = readSite("http://www.investorguide.com");
                break;
            case 8:
                document8 = readSite("https://seekingalpha.com");
                break;
            case 9:
                document9 = readSite("http://money.cnn.com");
                break;
            case 10:
                document10 = readSite("http://www.thisismoney.co.uk/money/index.html");
                break;
            case 11:
                document11 = readSite("http://www.marketwatch.com/");
                break;
            case 12:
                document12 = readSite("https://www.bloomberg.com/markets");
                break;
            case 13:
                document13 = readSite("https://www.bloomberg.com");
                break;
            case 14:
                document14 = readSite("http://www.nytimes.com/pages/business/dealbook/index.html");
                break;
            case 15:
                document15 = readSite("https://www.usatoday.com/money/markets");
                break;
            case 16:
                document16 = readSite("https://money.usnews.com");
                break;
            case 17:
                document17 = readSite("https://www.cnbc.com/world/?region=world");
                break;
            case 18:
                document18 = readSite("http://vestywaves.com");
                break;
            case 19:
                document19 = readSite("http://www.reuters.com/finance/markets");
                break;
            case 20:
                document20 = readSite("https://www.barchart.com");
                break;
            case 21:
                document21 = readSite("https://markets.ft.com/data");
                break;
            case 22:
                document22 = readSite("http://www.japantimes.co.jp/news/financial-markets");
                break;
            case 23:
                document23 = readSite("http://www.scmp.com/business/markets");
                break;
            case 24:
                document24 = readSite("http://www.theaustralian.com.au/business/markets");
                break;
            case 25:
                document25 = readSite("https://www.theguardian.com/business/stock-markets");
                break;
            case 26:
                document26 = readSite("https://washpost.bloomberg.com/market-news");
                break;
            case 27:
                document27 = readSite("http://finviz.com/news.ashx");
                break;
            case 28:
                document28 = readSite("https://www.ft.com/markets");
                break;
            case 29:
                document29 = readSite("http://www.cbc.ca/news/business");
                break;
            case 30:
                document30 = readSite("http://www.thestar.com.my/business");
                break;
            case 31:
                document31 = readSite("http://www.latimes.com/business");
                break;
            case 32:
                document32 = readSite("http://www.foxbusiness.com");
                break;
            case 33:
                document33 = readSite("http://www.nbcnews.com/business/markets");
                break;
            case 34:
                document34 = readSite("http://www.bbc.com/news/business");
                break;
            case 35:
                document35 = readSite("https://www.ft.com");
                break;
            case 36:
                document36 = readSite("https://www.irishtimes.com/business");
                break;
            case 37:
                document37 = readSite("http://www.telegraph.co.uk/business/markets");
                break;
            case 38:
                document38 = readSite("https://www.rt.com/business");
                break;
            case 39:
                document39 = readSite("http://gulfnews.com/business");
                break;
            case 40:
                document40 = readSite("https://www.dailysabah.com/business");
                break;
            case 41:
                document41 = readSite("http://timesofoman.com/business");
                break;
            case 42:
                document42 = readSite("http://timesofindia.indiatimes.com/business");
                break;
            case 43:
                document43 = readSite("http://indianexpress.com/business");
                break;
            case 44:
                document44 = readSite("http://www.chinadaily.com.cn/business");
                break;
            case 45:
                document45 = readSite("http://www.shanghaidaily.com/business");
                break;
            case 46:
                document46 = readSite("http://www.xinhuanet.com/english/business");
                break;
            case 47:
                document47 = readSite("http://www.globaltimes.cn/business");
                break;
            case 48:
                document48 = readSite("http://www.scmp.com/business");
                break;
            case 49:
                document49 = readSite("http://www.japantimes.co.jp/news/business");
                break;
            case 51:
                document51 = readSite("http://the-japan-news.com/news/business");
                break;
            case 52:
                document52 = readSite("https://japantoday.com/category/business");
                break;
            case 53:
                document53 = readSite("http://www.bangkokpost.com/business");
                break;
            case 54:
                document54 = readSite("http://www.thejakartapost.com/news/business");
                break;
            case 55:
                document55 = readSite("http://www.abc.net.au/news/business");
                break;
            case 56:
                document56 = readSite("http://www.theaustralian.com.au/business");
                break;
            case 57:
                document57 = readSite("http://www.nzherald.co.nz/business");
                break;
            case 58:
                document58 = readSite("http://www.aljazeera.com/topics/categories/business.html");
                break;
            case 59:
                document59 = readSite("https://www.forbes.com/markets");
                break;
            case 60:
                document60 = readSite("http://www.chicagotribune.com/business");
                break;
        }
    }
}