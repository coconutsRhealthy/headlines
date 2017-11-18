package com.lennart.model.headlinesBigDb.headlinesBigDbCrypto;

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
public class BigDbStorerCrypto extends BigDbStorer {

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

            storeOrUpdateWordInDatabase("crypto_news_words_update", entry.getKey(), avNoOccurrences, avPercentageSites);
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
                st.executeUpdate("INSERT INTO crypto_a_texts_update (atext) VALUES ('" + correctedAText + "')");
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
        st.executeUpdate("ALTER TABLE crypto_a_texts_update RENAME TO crypto_a_texts");
        st.close();
        closeDbConnection();
    }

    @Override
    protected void renameAtextDummyTableToAtextsUpdate() throws Exception {
        initializeDbConnection();
        Statement st = con.createStatement();
        st.executeUpdate("ALTER TABLE crypto_a_texts_dummy RENAME TO crypto_a_texts_update");
        st.close();
        closeDbConnection();
    }

    @Override
    protected void renameAtextOldTableToDummy() throws Exception {
        initializeDbConnection();
        Statement st = con.createStatement();
        st.executeUpdate("ALTER TABLE crypto_a_texts RENAME TO crypto_a_texts_dummy");
        st.close();
        closeDbConnection();
    }

    @Override
    protected void renameUpdatedTableToNewsWords() throws Exception {
        initializeDbConnection();
        Statement st = con.createStatement();
        st.executeUpdate("ALTER TABLE crypto_news_words_update RENAME TO crypto_news_words");
        st.close();
        closeDbConnection();
    }

    @Override
    protected void renameDummyTableToNewsWordsUpdate() throws Exception {
        initializeDbConnection();
        Statement st = con.createStatement();
        st.executeUpdate("ALTER TABLE crypto_news_words_dummy RENAME TO crypto_news_words_update");
        st.close();
        closeDbConnection();
    }

    @Override
    protected void renameOldTableToDummy() throws Exception {
        initializeDbConnection();
        Statement st = con.createStatement();
        st.executeUpdate("ALTER TABLE crypto_news_words RENAME TO crypto_news_words_dummy");
        st.close();
        closeDbConnection();
    }

    @Override
    protected void clearNewsWordsUpdateTable() throws Exception {
        initializeDbConnection();
        Statement st = con.createStatement();
        st.executeUpdate("DELETE FROM crypto_news_words_update");
        st.close();
        closeDbConnection();
    }

    @Override
    protected void clearAtextsUpdateTable() throws Exception {
        initializeDbConnection();
        Statement st = con.createStatement();
        st.executeUpdate("DELETE FROM crypto_a_texts_update");
        st.close();
        closeDbConnection();
    }

    @Override
    public List<String> retrieveAllOldAtexts() throws Exception{
        List<String> allOldATexts = new ArrayList<>();

        initializeDbConnection();
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM crypto_a_texts;");

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
                document1 = readSite("https://news.bitcoin.com/");
                break;
            case 2:
                document2 = readSite("https://www.geekwrapped.com/cryptocurrency");
                break;
            case 3:
                document3 = readSite("https://cointelegraph.com/");
                break;
            case 4:
                document4 = readSite("https://www.ethnews.com/");
                break;
            case 5:
                document5 = readSite("http://www.coindesk.com/");
                break;
            case 6:
                document6 = readSite("http://bitcoinist.com/");
                break;
            case 7:
                document7 = readSite("https://coincenter.org/");
                break;
            case 8:
                document8 = readSite("https://bitcoinmagazine.com/");
                break;
            case 9:
                document9 = readSite("http://www.newsbtc.com/");
                break;
            case 10:
                document10 = readSite("http://themerkle.com/");
                break;
            case 11:
                document11 = readSite("https://99bitcoins.com/");
                break;
            case 12:
                document12 = readSite("https://bitcoinwarrior.net/");
                break;
            case 13:
                document13 = readSite("https://blog.coinspectator.com/");
                break;
            case 14:
                document14 = readSite("http://bitcoinprices.today/");
                break;
            case 15:
                document15 = readSite("https://www.cryptocoinsnews.com/");
                break;
            case 16:
                document16 = readSite("https://cryptoinsider.com/content/index.html");
                break;
            case 17:
                document17 = readSite("https://cryptoreader.com/");
                break;
            case 18:
                document18 = readSite("https://www.crypto-news.net/");
                break;
            case 19:
                document19 = readSite("https://blog.blockchain.com/");
                break;
            case 20:
                document20 = readSite("http://forklog.net/");
                break;
            case 21:
                document21 = readSite("http://abitco.in/");
                break;
            case 22:
                document22 = readSite("https://www.coinstaker.com/bitcoin-cryptocurrency-news/");
                break;
            case 23:
                document23 = readSite("http://www.abitofnews.com/");
                break;
            case 24:
                document24 = readSite("https://cryptopotato.com/");
                break;
            case 25:
                document25 = readSite("https://btcmanager.com/");
                break;
            case 26:
                document26 = readSite("http://insidebitcoins.com/news");
                break;
            case 27:
                document27 = readSite("http://bitcoinschannel.com/");
                break;
            case 28:
                document28 = readSite("http://blocktribune.com");
                break;
            case 29:
                document29 = readSite("https://cryptovest.com");
                break;
            case 30:
                document30 = readSite("https://investfeededge.com/");
                break;
            case 31:
                document31 = readSite("http://typeboard.com");
                break;
            case 32:
                document32 = readSite("http://www.trustnodes.com");
                break;
            case 33:
                document33 = readSite("https://bit.news/eng");
                break;
            case 34:
                document34 = readSite("https://www.cryptoninjas.net");
                break;
            case 35:
                document35 = readSite("http://bitcoingarden.org");
                break;
            case 36:
                document36 = readSite("https://www.kickico.com/");
                break;
            case 37:
                document37 = readSite("http://bitcoinchaser.com");
                break;
            case 38:
                document38 = readSite("http://coinjournal.net");
                break;
            case 39:
                document39 = readSite("http://www.the-blockchain.com/");
                break;
            case 40:
                document40 = readSite("https://coinidol.com/");
                break;
            case 41:
                document41 = readSite("http://ethereumworldnews.com/");
                break;
            case 42:
                document42 = readSite("http://www.coinnewsasia.com");
                break;
            case 43:
                document43 = readSite("https://coingeek.com");
                break;
            case 44:
                document44 = null;
                break;
            case 45:
                document45 = null;
                break;
            case 46:
                document46 = null;
                break;
            case 47:
                document47 = null;
                break;
            case 48:
                document48 = null;
                break;
            case 49:
                document49 = null;
                break;
            case 51:
                document51 = readSite("http://coinmarketcal.com/");
                break;
            case 52:
                document52 = readSite("https://tokenmarket.net/");
                break;
            case 53:
                document53 = null;
                break;
            case 54:
                document54 = null;
                break;
            case 55:
                document55 = readSite("https://usethebitcoin.com");
                break;
            case 56:
                document56 = readSite("https://coinscage.com/");
                break;
            case 57:
                document57 = readSite("http://bitcoin.xyz");
                break;
            case 58:
                document58 = readSite("http://www.xbt.money");
                break;
            case 59:
                document59 = readSite("http://helenabitcoinmining.com/category/bitcoin-news");
                break;
            case 60:
                document60 = readSite("http://zempafy.com");
                break;
        }
    }
}
