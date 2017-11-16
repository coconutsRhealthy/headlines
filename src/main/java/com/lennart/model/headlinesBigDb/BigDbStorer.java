package com.lennart.model.headlinesBigDb;

import com.lennart.model.headlinesBuzzDb.JsoupElementsProcessor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by LennartMac on 05/06/17.
 */
public class BigDbStorer {

    protected Connection con;
    private double numberOfSites = 59.0;

    protected Document document1;
    protected Document document2;
    protected Document document3;
    protected Document document4;
    protected Document document5;
    protected Document document6;
    protected Document document7;
    protected Document document8;
    protected Document document9;
    protected Document document10;
    protected Document document11;
    protected Document document12;
    protected Document document13;
    protected Document document14;
    protected Document document15;
    protected Document document16;
    protected Document document17;
    protected Document document18;
    protected Document document19;
    protected Document document20;
    protected Document document21;
    protected Document document22;
    protected Document document23;
    protected Document document24;
    protected Document document25;
    protected Document document26;
    protected Document document27;
    protected Document document28;
    protected Document document29;
    protected Document document30;
    protected Document document31;
    protected Document document32;
    protected Document document33;
    protected Document document34;
    protected Document document35;
    protected Document document36;
    protected Document document37;
    protected Document document38;
    protected Document document39;
    protected Document document40;
    protected Document document41;
    protected Document document42;
    protected Document document43;
    protected Document document44;
    protected Document document45;
    protected Document document46;
    protected Document document47;
    protected Document document48;
    protected Document document49;
    protected Document document50;
    protected Document document51;
    protected Document document52;
    protected Document document53;
    protected Document document54;
    protected Document document55;
    protected Document document56;
    protected Document document57;
    protected Document document58;
    protected Document document59;
    protected Document document60;

    public void overallMethodServer(String page) {
        while(true) {
            if(page.equals("finance")) {
                try {
                    TimeUnit.MINUTES.sleep(12);
                } catch (Exception e) {

                }
            } else if(page.equals("sport")) {
                try {
                    TimeUnit.MINUTES.sleep(24);
                } catch (Exception e) {

                }
            } else if(page.equals("entertainment")) {
                try {
                    TimeUnit.MINUTES.sleep(36);
                } catch (Exception e) {

                }
            } else if(page.equals("crypto")) {
                try {
                    TimeUnit.MINUTES.sleep(48);
                } catch (Exception e) {

                }
            }

            //rename current production table to dummy
            try {
                renameOldTableToDummy();
                renameAtextOldTableToDummy();
            } catch (Exception e) {

            }

            //rename updated table to news_words -> production table
            try {
                renameUpdatedTableToNewsWords();
                renameAtextUpdatedTableToAtexts();
            } catch (Exception e) {

            }

            //rename the previous production table (now dummy) to news_words_update
            try {
                renameDummyTableToNewsWordsUpdate();
                renameAtextDummyTableToAtextsUpdate();
            } catch (Exception e) {

            }


            //empty the the update table
            try {
                clearNewsWordsUpdateTable();
                clearAtextsUpdateTable();
            } catch (Exception e) {
                //overallMethodServer();
            }

            //update the update table
            try {
//                for(int i = 1; i <= 60; i++) {
                    updateDatabase(1);
            } catch (Exception e) {
                //overallMethodServer();
            }

            //wait for 40 minutes
            if(page.equals("main")) {
                try {
                    TimeUnit.MINUTES.sleep(53);
                } catch (Exception e) {

                }
            } else if(page.equals("finance")) {
                try {
                    TimeUnit.MINUTES.sleep(41);
                } catch (Exception e) {

                }
            } else if(page.equals("sport")) {
                try {
                    TimeUnit.MINUTES.sleep(29);
                } catch (Exception e) {

                }
            } else if(page.equals("entertainment")) {
                try {
                    TimeUnit.MINUTES.sleep(17);
                } catch (Exception e) {

                }
            } else if(page.equals("crypto")) {
                try {
                    TimeUnit.MINUTES.sleep(5);
                } catch (Exception e) {

                }
            }
        }
    }

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

            storeOrUpdateWordInDatabase("news_words_update", entry.getKey(), avNoOccurrences, avPercentageSites);
        }
        closeDbConnection();
    }

    protected void updateAllOldATextsDatabase(List<String> aTexts) throws Exception {
        initializeDbConnection();

        for(String aText : aTexts) {
            String correctedAText = doStringReplacementsForDb(aText);

            Statement st = con.createStatement();
            try {
                st.executeUpdate("INSERT INTO a_texts_update (atext) VALUES ('" + correctedAText + "')");
            } catch (Exception e) {

            }
            st.close();
        }
        closeDbConnection();
    }

    protected void renameAtextUpdatedTableToAtexts() throws Exception {
        initializeDbConnection();
        Statement st = con.createStatement();
        st.executeUpdate("ALTER TABLE a_texts_update RENAME TO a_texts");
        st.close();
        closeDbConnection();
    }

    protected void renameAtextDummyTableToAtextsUpdate() throws Exception {
        initializeDbConnection();
        Statement st = con.createStatement();
        st.executeUpdate("ALTER TABLE a_texts_dummy RENAME TO a_texts_update");
        st.close();
        closeDbConnection();
    }

    protected void renameAtextOldTableToDummy() throws Exception {
        initializeDbConnection();
        Statement st = con.createStatement();
        st.executeUpdate("ALTER TABLE a_texts RENAME TO a_texts_dummy");
        st.close();
        closeDbConnection();
    }

    protected void renameUpdatedTableToNewsWords() throws Exception {
        initializeDbConnection();
        Statement st = con.createStatement();
        st.executeUpdate("ALTER TABLE news_words_update RENAME TO news_words");
        st.close();
        closeDbConnection();
    }

    protected void renameDummyTableToNewsWordsUpdate() throws Exception {
        initializeDbConnection();
        Statement st = con.createStatement();
        st.executeUpdate("ALTER TABLE news_words_dummy RENAME TO news_words_update");
        st.close();
        closeDbConnection();
    }

    protected void renameOldTableToDummy() throws Exception {
        initializeDbConnection();
        Statement st = con.createStatement();
        st.executeUpdate("ALTER TABLE news_words RENAME TO news_words_dummy");
        st.close();
        closeDbConnection();
    }

    protected void clearNewsWordsUpdateTable() throws Exception {
        initializeDbConnection();
        Statement st = con.createStatement();
        st.executeUpdate("DELETE FROM news_words_update");
        st.close();
        closeDbConnection();
    }

    protected void clearAtextsUpdateTable() throws Exception {
        initializeDbConnection();
        Statement st = con.createStatement();
        st.executeUpdate("DELETE FROM a_texts_update");
        st.close();
        closeDbConnection();
    }

    protected void storeOrUpdateWordInDatabase(String database, String word, double avNoOccurrences, double avNoSites) throws Exception {
        if(isWordInDatabase(database, word)) {
            updateWordInDatabase(database, word, avNoOccurrences, avNoSites);
        } else {
            storeWordInDatabase(database, word, avNoOccurrences, avNoSites);
        }
    }

    public void storeWordInDatabase(String database, String word, double avNoOccurrences, double avNoSites) throws Exception {
        Statement st = con.createStatement();
        st.executeUpdate("INSERT INTO " + database + " (entry, word, av_no_occurr_site, av_no_sites) VALUES ('" + (getHighestIntEntry(database) + 1) + "', '" + word + "', '" + avNoOccurrences + "', '" + avNoSites + "')");
        st.close();
    }

    private boolean isWordInDatabase(String database, String word) throws Exception{
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM " + database + " WHERE word = '" + word + "';");

        if(rs.next()) {
            rs.close();
            st.close();
            return true;
        }
        rs.close();
        st.close();
        return false;
    }

    private void updateWordInDatabase(String database, String word, double avNoOccurrences, double avNoSites) throws Exception {
        Statement st = con.createStatement();
        st.executeUpdate("UPDATE " + database + " SET av_no_occurr_site = '" + avNoOccurrences + "', av_no_sites = '" + avNoSites + "' WHERE word = '" + word + "'");
        st.close();
    }

    protected void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/words", "root", "Vuurwerk00");
    }

    protected void closeDbConnection() throws SQLException {
        con.close();
    }

    private int getHighestIntEntry(String database) throws Exception {
        Statement st = con.createStatement();
        String sql = ("SELECT * FROM " + database + " ORDER BY entry DESC;");
        ResultSet rs = st.executeQuery(sql);

        if(rs.next()) {
            int highestIntEntry = rs.getInt("entry");
            st.close();
            rs.close();
            return highestIntEntry;
        }
        st.close();
        rs.close();
        return 0;
    }

    protected Map<String, List<Integer>> joinMaps(Map<String, Integer> noOccurrences, Map<String, Integer> noSites) {
        Map<String, List<Integer>> joinedMaps = new HashMap<>();

        for (Map.Entry<String, Integer> entry : noOccurrences.entrySet()) {
            List<Integer> listOccurrencesAndSites = new ArrayList<>();

            if(noSites.get(entry.getKey()) != null) {
                listOccurrencesAndSites.add(entry.getValue());
                listOccurrencesAndSites.add(noSites.get(entry.getKey()));
            }

            joinedMaps.put(entry.getKey(), listOccurrencesAndSites);
        }
        return joinedMaps;
    }

    public List<String> retrieveAllOldAtexts() throws Exception{
        List<String> allOldATexts = new ArrayList<>();

        initializeDbConnection();
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM a_texts;");

        while(rs.next()) {
            allOldATexts.add(rs.getString("atext"));
        }
        rs.close();
        st.close();
        closeDbConnection();

        return allOldATexts;
    }

    public Map<String, Integer> getOccurrenceMapSingle() throws Exception {
        Set<String> cbc = getSetOfWordsFromDocument(document1);
        Set<String> theStar = getSetOfWordsFromDocument(document2);
        Set<String> nyTimes = getSetOfWordsFromDocument(document3);
        Set<String> washingtonPost = getSetOfWordsFromDocument(document4);
        Set<String> huffingtonPost = getSetOfWordsFromDocument(document5);
        Set<String> laTimes = getSetOfWordsFromDocument(document6);
        Set<String> cnn = getSetOfWordsFromDocument(document7);
        Set<String> foxNews = getSetOfWordsFromDocument(document8);
        Set<String> usaToday = getSetOfWordsFromDocument(document9);
        Set<String> wsj = getSetOfWordsFromDocument(document10);
        Set<String> cnbc = getSetOfWordsFromDocument(document11);
        Set<String> nbc = getSetOfWordsFromDocument(document12);
        Set<String> theYucatanTimes = getSetOfWordsFromDocument(document13);
        Set<String> theNewsMx = getSetOfWordsFromDocument(document14);
        Set<String> rioTimesOnline = getSetOfWordsFromDocument(document15);
        Set<String> folha = getSetOfWordsFromDocument(document16);
        Set<String> buenosAiresHerald = getSetOfWordsFromDocument(document17);
        Set<String> theGuardian = getSetOfWordsFromDocument(document18);
        Set<String> bbc = getSetOfWordsFromDocument(document19);
        Set<String> ft = getSetOfWordsFromDocument(document20);
        Set<String> theTimes = getSetOfWordsFromDocument(document21);
        Set<String> theSun = getSetOfWordsFromDocument(document22);
        Set<String> irishTimes = getSetOfWordsFromDocument(document23);
        Set<String> telegraphFr = getSetOfWordsFromDocument(document24);
        Set<String> mediaPartFr = getSetOfWordsFromDocument(document25);
        Set<String> spiegel = getSetOfWordsFromDocument(document26);
        Set<String> telegraphDe = getSetOfWordsFromDocument(document27);
        Set<String> elPais = getSetOfWordsFromDocument(document28);
        Set<String> ansaIt = getSetOfWordsFromDocument(document29);
        Set<String> rt = getSetOfWordsFromDocument(document30);
        Set<String> theMoscowTimes = getSetOfWordsFromDocument(document31);
        Set<String> dailySun = getSetOfWordsFromDocument(document32);
        Set<String> timesLive = getSetOfWordsFromDocument(document33);
        Set<String> vanguardNgr = getSetOfWordsFromDocument(document34);
        Set<String> gulfNews = getSetOfWordsFromDocument(document35);
        Set<String> dailySabah = getSetOfWordsFromDocument(document36);
        Set<String> teheranTimes = getSetOfWordsFromDocument(document37);
        Set<String> ynetNews = getSetOfWordsFromDocument(document38);
        Set<String> timesOfOman = getSetOfWordsFromDocument(document39);
        Set<String> timesOfIndia = getSetOfWordsFromDocument(document40);
        Set<String> indianExpress = getSetOfWordsFromDocument(document41);
        Set<String> chinaDaily = getSetOfWordsFromDocument(document42);
        Set<String> shanghaiDaily = getSetOfWordsFromDocument(document43);
        Set<String> xinHuanet = getSetOfWordsFromDocument(document44);
        Set<String> globalTimesCn = getSetOfWordsFromDocument(document45);
        Set<String> scmp = getSetOfWordsFromDocument(document46);
        Set<String> japanTimes = getSetOfWordsFromDocument(document47);
        Set<String> japanNews = getSetOfWordsFromDocument(document48);
        Set<String> japanToday = getSetOfWordsFromDocument(document49);
        //List<String> chinaDailyHk = getSetOfWordsFromDocument(document50);
        Set<String> hongKongFp = getSetOfWordsFromDocument(document51);
        Set<String> bangKokPost = getSetOfWordsFromDocument(document52);
        Set<String> vietnamNews = getSetOfWordsFromDocument(document53);
        Set<String> jakartaPost = getSetOfWordsFromDocument(document54);
        Set<String> abcAu = getSetOfWordsFromDocument(document55);
        Set<String> theAustralian = getSetOfWordsFromDocument(document56);
        Set<String> nzHerald = getSetOfWordsFromDocument(document57);
        Set<String> alJazeera = getSetOfWordsFromDocument(document58);
        Set<String> bloomberg = getSetOfWordsFromDocument(document59);
        Set<String> reuters = getSetOfWordsFromDocument(document60);

        List<String> combinedList = new ArrayList<>();
        combinedList.addAll(cbc);
        combinedList.addAll(theStar);
        combinedList.addAll(nyTimes);
        combinedList.addAll(washingtonPost);
        combinedList.addAll(huffingtonPost);
        combinedList.addAll(laTimes);
        combinedList.addAll(cnn);
        combinedList.addAll(foxNews);
        combinedList.addAll(usaToday);
        combinedList.addAll(wsj);
        combinedList.addAll(cnbc);
        combinedList.addAll(nbc);
        combinedList.addAll(theYucatanTimes);
        combinedList.addAll(theNewsMx);
        combinedList.addAll(rioTimesOnline);
        combinedList.addAll(folha);
        combinedList.addAll(buenosAiresHerald);
        combinedList.addAll(theGuardian);
        combinedList.addAll(bbc);
        combinedList.addAll(ft);
        combinedList.addAll(theTimes);
        combinedList.addAll(theSun);
        combinedList.addAll(irishTimes);
        combinedList.addAll(telegraphFr);
        combinedList.addAll(mediaPartFr);
        combinedList.addAll(spiegel);
        combinedList.addAll(telegraphDe);
        combinedList.addAll(elPais);
        combinedList.addAll(ansaIt);
        combinedList.addAll(rt);
        combinedList.addAll(theMoscowTimes);
        combinedList.addAll(dailySun);
        combinedList.addAll(timesLive);
        combinedList.addAll(vanguardNgr);
        combinedList.addAll(gulfNews);
        combinedList.addAll(dailySabah);
        combinedList.addAll(teheranTimes);
        combinedList.addAll(ynetNews);
        combinedList.addAll(timesOfOman);
        combinedList.addAll(timesOfIndia);
        combinedList.addAll(indianExpress);
        combinedList.addAll(chinaDaily);
        combinedList.addAll(shanghaiDaily);
        combinedList.addAll(xinHuanet);
        combinedList.addAll(globalTimesCn);
        combinedList.addAll(scmp);
        combinedList.addAll(japanTimes);
        combinedList.addAll(japanNews);
        combinedList.addAll(japanToday);
        //combinedList.addAll(chinaDailyHk);
        combinedList.addAll(hongKongFp);
        combinedList.addAll(bangKokPost);
        combinedList.addAll(vietnamNews);
        combinedList.addAll(jakartaPost);
        combinedList.addAll(abcAu);
        combinedList.addAll(theAustralian);
        combinedList.addAll(nzHerald);
        combinedList.addAll(alJazeera);
        combinedList.addAll(bloomberg);
        combinedList.addAll(reuters);

        Map<String, Integer> occurrenceMapAll = new HashMap<>();

        for(String word : combinedList) {
            if(occurrenceMapAll.get(word) == null) {
                int frequency = Collections.frequency(combinedList, word);
                occurrenceMapAll.put(word, frequency);
            }
        }

        return sortByValue(occurrenceMapAll);
    }

    public Map<String, Integer> getOccurrenceMapMultiple() throws Exception {
        List<String> cbc = getListOfWordsFromDocument(document1);
        List<String> theStar = getListOfWordsFromDocument(document2);
        List<String> nyTimes = getListOfWordsFromDocument(document3);
        List<String> washingtonPost = getListOfWordsFromDocument(document4);
        List<String> huffingtonPost = getListOfWordsFromDocument(document5);
        List<String> laTimes = getListOfWordsFromDocument(document6);
        List<String> cnn = getListOfWordsFromDocument(document7);
        List<String> foxNews = getListOfWordsFromDocument(document8);
        List<String> usaToday = getListOfWordsFromDocument(document9);
        List<String> wsj = getListOfWordsFromDocument(document10);
        List<String> cnbc = getListOfWordsFromDocument(document11);
        List<String> nbc = getListOfWordsFromDocument(document12);
        List<String> theYucatanTimes = getListOfWordsFromDocument(document13);
        List<String> theNewsMx = getListOfWordsFromDocument(document14);
        List<String> rioTimesOnline = getListOfWordsFromDocument(document15);
        List<String> folha = getListOfWordsFromDocument(document16);
        List<String> buenosAiresHerald = getListOfWordsFromDocument(document17);
        List<String> theGuardian = getListOfWordsFromDocument(document18);
        List<String> bbc = getListOfWordsFromDocument(document19);
        List<String> ft = getListOfWordsFromDocument(document20);
        List<String> theTimes = getListOfWordsFromDocument(document21);
        List<String> theSun = getListOfWordsFromDocument(document22);
        List<String> irishTimes = getListOfWordsFromDocument(document23);
        List<String> telegraphFr = getListOfWordsFromDocument(document24);
        List<String> mediaPartFr = getListOfWordsFromDocument(document25);
        List<String> spiegel = getListOfWordsFromDocument(document26);
        List<String> telegraphDe = getListOfWordsFromDocument(document27);
        List<String> elPais = getListOfWordsFromDocument(document28);
        List<String> ansaIt = getListOfWordsFromDocument(document29);
        List<String> rt = getListOfWordsFromDocument(document30);
        List<String> theMoscowTimes = getListOfWordsFromDocument(document31);
        List<String> dailySun = getListOfWordsFromDocument(document32);
        List<String> timesLive = getListOfWordsFromDocument(document33);
        List<String> vanguardNgr = getListOfWordsFromDocument(document34);
        List<String> gulfNews = getListOfWordsFromDocument(document35);
        List<String> dailySabah = getListOfWordsFromDocument(document36);
        List<String> teheranTimes = getListOfWordsFromDocument(document37);
        List<String> ynetNews = getListOfWordsFromDocument(document38);
        List<String> timesOfOman = getListOfWordsFromDocument(document39);
        List<String> timesOfIndia = getListOfWordsFromDocument(document40);
        List<String> indianExpress = getListOfWordsFromDocument(document41);
        List<String> chinaDaily = getListOfWordsFromDocument(document42);
        List<String> shanghaiDaily = getListOfWordsFromDocument(document43);
        List<String> xinHuanet = getListOfWordsFromDocument(document44);
        List<String> globalTimesCn = getListOfWordsFromDocument(document45);
        List<String> scmp = getListOfWordsFromDocument(document46);
        List<String> japanTimes = getListOfWordsFromDocument(document47);
        List<String> japanNews = getListOfWordsFromDocument(document48);
        List<String> japanToday = getListOfWordsFromDocument(document49);
        //List<String> chinaDailyHk = getListOfWordsFromDocument(document50);
        List<String> hongKongFp = getListOfWordsFromDocument(document51);
        List<String> bangKokPost = getListOfWordsFromDocument(document52);
        List<String> vietnamNews = getListOfWordsFromDocument(document53);
        List<String> jakartaPost = getListOfWordsFromDocument(document54);
        List<String> abcAu = getListOfWordsFromDocument(document55);
        List<String> theAustralian = getListOfWordsFromDocument(document56);
        List<String> nzHerald = getListOfWordsFromDocument(document57);
        List<String> alJazeera = getListOfWordsFromDocument(document58);
        List<String> bloomberg = getListOfWordsFromDocument(document59);
        List<String> reuters = getListOfWordsFromDocument(document60);

        List<String> combinedList = new ArrayList<>();
        combinedList.addAll(cbc);
        combinedList.addAll(theStar);
        combinedList.addAll(nyTimes);
        combinedList.addAll(washingtonPost);
        combinedList.addAll(huffingtonPost);
        combinedList.addAll(laTimes);
        combinedList.addAll(cnn);
        combinedList.addAll(foxNews);
        combinedList.addAll(usaToday);
        combinedList.addAll(wsj);
        combinedList.addAll(cnbc);
        combinedList.addAll(nbc);
        combinedList.addAll(theYucatanTimes);
        combinedList.addAll(theNewsMx);
        combinedList.addAll(rioTimesOnline);
        combinedList.addAll(folha);
        combinedList.addAll(buenosAiresHerald);
        combinedList.addAll(theGuardian);
        combinedList.addAll(bbc);
        combinedList.addAll(ft);
        combinedList.addAll(theTimes);
        combinedList.addAll(theSun);
        combinedList.addAll(irishTimes);
        combinedList.addAll(telegraphFr);
        combinedList.addAll(mediaPartFr);
        combinedList.addAll(spiegel);
        combinedList.addAll(telegraphDe);
        combinedList.addAll(elPais);
        combinedList.addAll(ansaIt);
        combinedList.addAll(rt);
        combinedList.addAll(theMoscowTimes);
        combinedList.addAll(dailySun);
        combinedList.addAll(timesLive);
        combinedList.addAll(vanguardNgr);
        combinedList.addAll(gulfNews);
        combinedList.addAll(dailySabah);
        combinedList.addAll(teheranTimes);
        combinedList.addAll(ynetNews);
        combinedList.addAll(timesOfOman);
        combinedList.addAll(timesOfIndia);
        combinedList.addAll(indianExpress);
        combinedList.addAll(chinaDaily);
        combinedList.addAll(shanghaiDaily);
        combinedList.addAll(xinHuanet);
        combinedList.addAll(globalTimesCn);
        combinedList.addAll(scmp);
        combinedList.addAll(japanTimes);
        combinedList.addAll(japanNews);
        combinedList.addAll(japanToday);
        //combinedList.addAll(chinaDailyHk);
        combinedList.addAll(hongKongFp);
        combinedList.addAll(bangKokPost);
        combinedList.addAll(vietnamNews);
        combinedList.addAll(jakartaPost);
        combinedList.addAll(abcAu);
        combinedList.addAll(theAustralian);
        combinedList.addAll(nzHerald);
        combinedList.addAll(alJazeera);
        combinedList.addAll(bloomberg);
        combinedList.addAll(reuters);

        Map<String, Integer> occurrenceMapAll = new HashMap<>();

        for(String word : combinedList) {
            if(occurrenceMapAll.get(word) == null) {
                int frequency = Collections.frequency(combinedList, word);
                occurrenceMapAll.put(word, frequency);
            }
        }

        return sortByValue(occurrenceMapAll);
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

    protected String doStringReplacementsForDb(String string) {
        String correctString = string.replace("'", "''");
        correctString = correctString.replace("\"", "\\\"");
        return correctString;
    }

    private Set<String> getSetOfWordsFromDocument(Document document) {
        if(document != null) {
            String allText = document.text();
            allText = allText.replaceAll("[^A-Za-z0-9 ]", "");
            allText = allText.toLowerCase();

            List<String> listOfWordsTemp = Arrays.asList(allText.split(" "));
            List<String> listOfWords = new ArrayList<>();

            listOfWords.addAll(listOfWordsTemp);

            Set<String> setOfWords = new HashSet<>();
            setOfWords.addAll(listOfWords);

            return setOfWords;
        } else {
            return new HashSet<>();
        }
    }

    private List<String> getListOfWordsFromDocument(Document document) {
        if(document != null) {
            String allText = document.text();
            allText = allText.replaceAll("[^A-Za-z0-9 ]", "");
            allText = allText.toLowerCase();

            List<String> listOfWordsTemp = Arrays.asList(allText.split(" "));
            List<String> listOfWords = new ArrayList<>();

            listOfWords.addAll(listOfWordsTemp);

            return listOfWords;
        } else {
            return new ArrayList<>();
        }
    }

    public void initializeDocuments(int number) throws IOException {
        switch(number) {
            case 1:
                document1 = readSite("http://nypost.com");
                break;
            case 2:
                document2 = readSite("http://www.nydailynews.com");
                break;
            case 3:
                document3 = readSite("https://www.nytimes.com");
                break;
            case 4:
                document4 = readSite("https://www.washingtonpost.com");
                break;
            case 5:
                document5 = readSite("http://www.huffingtonpost.com");
                break;
            case 6:
                document6 = readSite("http://www.latimes.com");
                break;
            case 7:
                document7 = readSite("http://www.cnn.com");
                break;
            case 8:
                document8 = readSite("http://www.foxnews.com");
                break;
            case 9:
                document9 = readSite("https://www.usatoday.com");
                break;
            case 10:
                document10 = readSite("https://www.wsj.com");
                break;
            case 11:
                document11 = readSite("http://www.cnbc.com");
                break;
            case 12:
                document12 = readSite("http://www.nbcnews.com");
                break;
            case 13:
                document13 = readSite("http://www.chicagotribune.com");
                break;
            case 14:
                document14 = readSite("http://www.chron.com");
                break;
            case 15:
                document15 = readSite("http://www.azcentral.com");
                break;
            case 16:
                document16 = readSite("https://www.dallasnews.com/");
                break;
            case 17:
                document17 = readSite("https://www.cbsnews.com");
                break;
            case 18:
                document18 = readSite("http://abcnews.go.com");
                break;
            case 19:
                document19 = readSite("http://www.newsmax.com");
                break;
            case 20:
                document20 = readSite("http://observer.com");
                break;
            case 21:
                document21 = readSite("http://www.newsday.com");
                break;
            case 22:
                document22 = readSite("http://www.sfchronicle.com");
                break;
            case 23:
                document23 = readSite("https://www.bostonglobe.com");
                break;
            case 24:
                document24 = readSite("http://www.nj.com/starledger");
                break;
            case 25:
                document25 = readSite("http://www.ajc.com");
                break;
            case 26:
                document26 = readSite("http://www.startribune.com");
                break;
            case 27:
                document27 = readSite("http://www.oregonlive.com");
                break;
            case 28:
                document28 = readSite("http://www.sandiegouniontribune.com");
                break;
            case 29:
                document29 = readSite("http://www.ocregister.com");
                break;
            case 30:
                document30 = readSite("http://www.sacbee.com");
                break;
            case 31:
                document31 = readSite("http://www.stltoday.com");
                break;
            case 32:
                document32 = readSite("http://www.miamiherald.com");
                break;
            case 33:
                document33 = readSite("http://www.indystar.com");
                break;
            case 34:
                document34 = readSite("http://www.kansascity.com");
                break;
            case 35:
                document35 = readSite("http://www.denverpost.com");
                break;
            case 36:
                document36 = readSite("http://rockymountainnews.com");
                break;
            case 37:
                document37 = readSite("http://www.mysanantonio.com");
                break;
            case 38:
                document38 = readSite("http://www.baltimoresun.com");
                break;
            case 39:
                document39 = readSite("http://www.mercurynews.com");
                break;
            case 40:
                document40 = readSite("http://www.jsonline.com");
                break;
            case 41:
                document41 = readSite("http://www.tampabay.com");
                break;
            case 42:
                document42 = readSite("http://www.orlandosentinel.com");
                break;
            case 43:
                document43 = readSite("https://www.seattletimes.com");
                break;
            case 44:
                document44 = readSite("http://www.dispatch.com");
                break;
            case 45:
                document45 = readSite("http://www.courier-journal.com");
                break;
            case 46:
                document46 = readSite("http://www.charlotteobserver.com");
                break;
            case 47:
                document47 = readSite("http://www.post-gazette.com");
                break;
            case 48:
                document48 = readSite("http://www.star-telegram.com");
                break;
            case 49:
                document49 = readSite("http://www.detroitnews.com");
                break;
            case 51:
                document51 = readSite("http://www.bostonherald.com");
                break;
            case 52:
                document52 = readSite("http://www.twincities.com");
                break;
            case 53:
                document53 = readSite("http://www.richmond.com");
                break;
            case 54:
                document54 = readSite("http://www.omaha.com");
                break;
            case 55:
                document55 = readSite("https://pilotonline.com");
                break;
            case 56:
                document56 = readSite("http://www.arkansasonline.com");
                break;
            case 57:
                document57 = readSite("http://buffalonews.com");
                break;
            case 58:
                document58 = readSite("http://www.tennessean.com");
                break;
            case 59:
                document59 = readSite("https://www.bloomberg.com");
                break;
            case 60:
                document60 = readSite("http://www.reuters.com");
                break;
        }
    }

    protected Document readSite(String url) {
        try {
            return Jsoup.connect(url).get();
        } catch (Exception e) {
            return null;
        }
    }

    public List<Document> getListOfAllDocuments() {
        List<Document> listOfAllDocuments = new ArrayList<>();

        listOfAllDocuments.add(document1);
        listOfAllDocuments.add(document2);
        listOfAllDocuments.add(document3);
        listOfAllDocuments.add(document4);
        listOfAllDocuments.add(document5);
        listOfAllDocuments.add(document6);
        listOfAllDocuments.add(document7);
        listOfAllDocuments.add(document8);
        listOfAllDocuments.add(document9);
        listOfAllDocuments.add(document10);
        listOfAllDocuments.add(document11);
        listOfAllDocuments.add(document12);
        listOfAllDocuments.add(document13);
        listOfAllDocuments.add(document14);
        listOfAllDocuments.add(document15);
        listOfAllDocuments.add(document16);
        listOfAllDocuments.add(document17);
        listOfAllDocuments.add(document18);
        listOfAllDocuments.add(document19);
        listOfAllDocuments.add(document20);
        listOfAllDocuments.add(document21);
        listOfAllDocuments.add(document22);
        listOfAllDocuments.add(document23);
        listOfAllDocuments.add(document24);
        listOfAllDocuments.add(document25);
        listOfAllDocuments.add(document26);
        listOfAllDocuments.add(document27);
        listOfAllDocuments.add(document28);
        listOfAllDocuments.add(document29);
        listOfAllDocuments.add(document30);
        listOfAllDocuments.add(document31);
        listOfAllDocuments.add(document32);
        listOfAllDocuments.add(document33);
        listOfAllDocuments.add(document34);
        listOfAllDocuments.add(document35);
        listOfAllDocuments.add(document36);
        listOfAllDocuments.add(document37);
        listOfAllDocuments.add(document38);
        listOfAllDocuments.add(document39);
        listOfAllDocuments.add(document40);
        listOfAllDocuments.add(document41);
        listOfAllDocuments.add(document42);
        listOfAllDocuments.add(document43);
        listOfAllDocuments.add(document44);
        listOfAllDocuments.add(document45);
        listOfAllDocuments.add(document46);
        listOfAllDocuments.add(document47);
        listOfAllDocuments.add(document48);
        listOfAllDocuments.add(document49);
        listOfAllDocuments.add(document51);
        listOfAllDocuments.add(document52);
        listOfAllDocuments.add(document53);
        listOfAllDocuments.add(document54);
        listOfAllDocuments.add(document55);
        listOfAllDocuments.add(document56);
        listOfAllDocuments.add(document57);
        listOfAllDocuments.add(document58);
        listOfAllDocuments.add(document59);
        listOfAllDocuments.add(document60);

        return listOfAllDocuments;
    }
}