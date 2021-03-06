package com.lennart.controller;

import com.lennart.model.BuzzWord;
import com.lennart.model.RetrieveBuzzwords;
import com.lennart.model.Words30;
import org.apache.commons.lang3.time.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;


@Configuration
@EnableAutoConfiguration
@RestController
public class Controller extends SpringBootServletInitializer {

    private Connection con;

    private Document document1;
    private Document document2;
    private Document document3;
    private Document document4;
    private Document document5;
    private Document document6;
    private Document document7;
    private Document document8;
    private Document document9;
    private Document document10;
    private Document document11;
    private Document document12;
    private Document document13;
    private Document document14;
    private Document document15;
    private Document document16;
    private Document document17;
    private Document document18;
    private Document document19;
    private Document document20;
    private Document document21;
    private Document document22;
    private Document document23;
    private Document document24;
    private Document document25;
    private Document document26;
    private Document document27;
    private Document document28;
    private Document document29;
    private Document document30;
    private Document document31;
    private Document document32;
    private Document document33;
    private Document document34;
    private Document document35;
    private Document document36;
    private Document document37;
    private Document document38;
    private Document document39;
    private Document document40;
    private Document document41;
    private Document document42;
    private Document document43;
    private Document document44;
    private Document document45;
    private Document document46;
    private Document document47;
    private Document document48;
    private Document document49;
    private Document document50;
    private Document document51;
    private Document document52;
    private Document document53;
    private Document document54;
    private Document document55;
    private Document document56;
    private Document document57;
    private Document document58;
    private Document document59;
    private Document document60;

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Controller.class);
    }

    public static void main(String[] args) throws Exception {
        //Controller controller = new Controller();

        //controller.testCompareMethodeWordOncePerSite();

        SpringApplication.run(Controller.class, args);
    }

//    @RequestMapping(value = "/startGame", method = RequestMethod.GET)
//    public void startGame() throws Exception {
//        while(true) {
//            retrieveAndStoreNuNl();
//            TimeUnit.SECONDS.sleep(5);
//            retrieveAndStoreNos();
//            TimeUnit.SECONDS.sleep(5);
//            retrieveAndStoreTelegraaf();
//            TimeUnit.SECONDS.sleep(5);
//            retrieveAndStoreAd();
//            TimeUnit.SECONDS.sleep(5);
//            retrieveAndStoreVolkskrant();
//
//            TimeUnit.MINUTES.sleep(10);
//        }
//    }

    @RequestMapping(value = "/startGame", method = RequestMethod.GET)
    public void startGame() throws Exception {
        for(int i = 1; i <= 60; i++) {
            new Words30().overallMethodServer();
        }
    }

    @RequestMapping(value = "/getBuzzWords", method = RequestMethod.GET)
    public @ResponseBody List<BuzzWord> sendBuzzWordsToClient() throws Exception {
        List<BuzzWord> buzzWords = new RetrieveBuzzwords().retrieveBuzzWordsFromDb("buzzwords_new");
        System.out.println("buzzwords size is: " + buzzWords.size());
        return buzzWords;
    }


    private void retrieveAndStoreNuNl() {
        try {
            initializeDbConnection();
            Map<String, String> nuNlHeadlines = retrieveHeadLinesFromNuNl();
            addHeadLinesToDataBase("nu_nl_headlines", nuNlHeadlines);
            closeDbConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void retrieveAndStoreNos() {
        try {
            initializeDbConnection();
            Map<String, String> nosHeadlines = retrieveHeadLinesFromNos();
            addHeadLinesToDataBase("nos_headlines", nosHeadlines);
            closeDbConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void retrieveAndStoreTelegraaf() {
        try {
            initializeDbConnection();
            Map<String, String> telegraafHeadlines = retrieveHeadLinesFromTelegraaf();
            addHeadLinesToDataBase("telegraaf_headlines", telegraafHeadlines);
            closeDbConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void retrieveAndStoreAd() {
        try {
            initializeDbConnection();
            Map<String, String> adHeadlines = retrieveHeadLinesFromAd();
            addHeadLinesToDataBase("ad_headlines", adHeadlines);
            closeDbConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void retrieveAndStoreVolkskrant() {
        try {
            initializeDbConnection();
            Map<String, String> volkskrantHeadlines = retrieveHeadLinesFromVolkskrant();
            addHeadLinesToDataBase("volkskrant_headlines", volkskrantHeadlines);
            closeDbConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Map<String, String> retrieveHeadLinesFromNuNl() throws Exception {
        return retrieveHeadLinesFromSite("http://www.nu.nl/algemeen", "div#block-280723 div.block-content.clearfix span.title", "div#block-280723 div.block-content.clearfix a");
    }

    private Map<String, String> retrieveHeadLinesFromNos() throws Exception {
        return retrieveHeadLinesFromSite("http://www.nos.nl", "div#latest_news span.link-hover", "div#latest_news a");
    }

    private Map<String, String> retrieveHeadLinesFromTelegraaf() throws Exception {
        return retrieveHeadLinesFromSite("http://www.telegraaf.nl", "ul:nth-child(2) span.snelnieuws-tekst", "ul:nth-child(2) > li.item > a");
    }

    private Map<String, String> retrieveHeadLinesFromAd() throws Exception {
        Map<String, String> mapIncludingTime = retrieveHeadLinesFromSite("http://www.ad.nl/accept?url=http://www.ad.nl/nieuws", "div.widget.fjs-autoupdate-widget ol.articles-list.fjs-articles-list h3", "div.widget.fjs-autoupdate-widget ol.articles-list.fjs-articles-list a");
        return removeTimeFromAdHeadlines(mapIncludingTime);
    }

    private Map<String, String> retrieveHeadLinesFromVolkskrant() throws Exception {
        Map<String, String> mapIncludingSportAndOpinie = retrieveHeadLinesFromSite("http://www.volkskrant.nl/cookiewall/accept?url=http://www.volkskrant.nl/", "ol.latest-articles.latest-articles--most-recent p", "ol.latest-articles.latest-articles--most-recent a");
        return removeVolkskrantSportAndOpinieHeadlines(mapIncludingSportAndOpinie);
    }

    private Map<String, String> removeTimeFromAdHeadlines(Map<String, String> mapIncluding) {
        Map<String, String> mapExcluding = new LinkedHashMap<>();

        for (Map.Entry<String, String> entry : mapIncluding.entrySet()) {
            String headlineNoTime = entry.getKey().substring(5);
            mapExcluding.put(headlineNoTime, entry.getValue());
        }
        return mapExcluding;
    }

    private Map<String, String> removeVolkskrantSportAndOpinieHeadlines(Map<String, String> mapIncluding) {
        Map<String, String> mapExcluding = new LinkedHashMap<>();

        for (Map.Entry<String, String> entry : mapIncluding.entrySet()) {
            String fourthElementInLink = entry.getValue().split("/")[3];

            if(!fourthElementInLink.equals("sport") && !fourthElementInLink.equals("opinie")) {
                mapExcluding.put(entry.getKey(), entry.getValue());
            }
        }
        return mapExcluding;
    }

    private Map<String, String> retrieveHeadLinesFromSite(String url, String headlinesSelector,
                                                                 String hrefsSelector) throws Exception {
        Map<String, String> headLinesAndHrefs = new LinkedHashMap<>();

        List<String> headLines = new ArrayList<>();
        List<String> hRefs = new ArrayList<>();

        Document document = Jsoup.connect(url).get();
        Elements headLineTexts = document.select(headlinesSelector);

        for(Element e : headLineTexts) {
            headLines.add(e.text());
        }

        Elements headLineHrefs = document.select(hrefsSelector);
        for(Element e : headLineHrefs) {
            hRefs.add(e.attr("href"));
        }

        headLines = escapeSingleQuote(headLines);
        hRefs = escapeSingleQuote(hRefs);

        for(int i = 0; i < headLines.size(); i++) {
            headLinesAndHrefs.put(headLines.get(i), hRefs.get(i));
        }

        return headLinesAndHrefs;
    }

    private List<String> escapeSingleQuote(List<String> headLines) {
        List<String> headLinesSingleQuotesEscaped = new ArrayList<>();

        for(String headLine : headLines) {
            String correctHeadLine = headLine.replace("'", "''");
            correctHeadLine = correctHeadLine.replace("\"", "\\\"");
            headLinesSingleQuotesEscaped.add(correctHeadLine);
        }
        return headLinesSingleQuotesEscaped;
    }

    private boolean isHeadLineAlreadyInDatabase(String database, String headLine) throws Exception {
        Statement st = con.createStatement();
        String sql = ("SELECT * FROM " + database + " ORDER BY date;");
        ResultSet rs = st.executeQuery(sql);
        while(rs.next()) {
            String retrievedString = rs.getString("headline").replace("'", "''");
            if(retrievedString.equals(headLine)) {
                return true;
            }
        }
        return false;
    }

    private void addHeadLinesToDataBase(String database, Map<String, String> headLinesAndHrefs) throws Exception {
        Date date = DateUtils.addHours(new Date(), 2);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = dateFormat.format(date);

        for (Map.Entry<String, String> entry : headLinesAndHrefs.entrySet()) {
            if(!isHeadLineAlreadyInDatabase(database, entry.getKey())) {
                int entryValue = getHighestIntEntry(database) + 1;
                Statement st = con.createStatement();
                st.executeUpdate("INSERT INTO " + database + " (entry, date, headline, link) VALUES ('" + entryValue + "', '" + dateString + "', '" + entry.getKey() + "', '" + entry.getValue() + "')");
            }
        }
    }

    private int getHighestIntEntry(String database) throws Exception {
        Statement st = con.createStatement();
        String sql = ("SELECT * FROM " + database + " ORDER BY entry DESC;");
        ResultSet rs = st.executeQuery(sql);

        if(rs.next()) {
            return rs.getInt("entry");
        }
        return 0;
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/headlines", "root", "Vuurwerk00");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }

    public Map<String, Integer> testCompareMethodeWordOncePerSite() throws Exception {
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

//        Map<String, Integer> occurrenceMapOnlyTwoOrMore = new HashMap<>();
//
//        for (Map.Entry<String, Integer> entry : occurrenceMapAll.entrySet()) {
//            if(entry.getValue() >= 5) {
//                occurrenceMapOnlyTwoOrMore.put(entry.getKey(), entry.getValue());
//            }
//        }

        return sortByValue(occurrenceMapAll);
        //occurrenceMapOnlyTwoOrMore = sortByValue(occurrenceMapOnlyTwoOrMore);
    }

    public Map<String, Integer> testCompareMethodeWordMultiplePerSite() throws Exception {
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

//        Map<String, Integer> occurrenceMapOnlyTwoOrMore = new HashMap<>();
//
//        for (Map.Entry<String, Integer> entry : occurrenceMapAll.entrySet()) {
//            if(entry.getValue() >= 5) {
//                occurrenceMapOnlyTwoOrMore.put(entry.getKey(), entry.getValue());
//            }
//        }

        return sortByValue(occurrenceMapAll);
        //occurrenceMapOnlyTwoOrMore = sortByValue(occurrenceMapOnlyTwoOrMore);
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
                document1 = readSite("http://www.cbc.ca/news");
                break;
            case 2:
                document2 = readSite("https://www.thestar.com");
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
                document13 = readSite("http://www.theyucatantimes.com");
                break;
            case 14:
                document14 = readSite("http://www.thenews.mx");
                break;
            case 15:
                document15 = readSite("http://riotimesonline.com");
                break;
            case 16:
                document16 = readSite("http://www1.folha.uol.com.br/internacional/en");
                break;
            case 17:
                document17 = readSite("http://www.buenosairesherald.com/printed-edition");
                break;
            case 18:
                document18 = readSite("https://www.theguardian.com");
                break;
            case 19:
                document19 = readSite("http://www.bbc.co.uk");
                break;
            case 20:
                document20 = readSite("https://www.ft.com");
                break;
            case 21:
                document21 = readSite("https://www.thetimes.co.uk");
                break;
            case 22:
                document22 = readSite("https://www.thesun.co.uk");
                break;
            case 23:
                document23 = readSite("http://www.irishtimes.com");
                break;
            case 24:
                document24 = readSite("http://www.telegraph.co.uk/france/");
                break;
            case 25:
                document25 = readSite("https://www.mediapart.fr/en/english");
                break;
            case 26:
                document26 = readSite("http://www.spiegel.de/international");
                break;
            case 27:
                document27 = readSite("http://www.telegraph.co.uk/germany");
                break;
            case 28:
                document28 = readSite("http://elpais.com/elpais/inenglish.html");
                break;
            case 29:
                document29 = readSite("http://www.ansa.it/english");
                break;
            case 30:
                document30 = readSite("https://www.rt.com");
                break;
            case 31:
                document31 = readSite("https://themoscowtimes.com");
                break;
            case 32:
                document32 = readSite("http://www.dailysun.co.za");
                break;
            case 33:
                document33 = readSite("http://www.timeslive.co.za");
                break;
            case 34:
                document34 = readSite("http://www.vanguardngr.com");
                break;
            case 35:
                document35 = readSite("http://gulfnews.com");
                break;
            case 36:
                document36 = readSite("https://www.dailysabah.com");
                break;
            case 37:
                document37 = readSite("http://www.tehrantimes.com");
                break;
            case 38:
                document38 = readSite("https://www.ynetnews.com");
                break;
            case 39:
                document39 = readSite("http://timesofoman.com");
                break;
            case 40:
                document40 = readSite("http://timesofindia.indiatimes.com/home/headlines");
                break;
            case 41:
                document41 = readSite("http://indianexpress.com");
                break;
            case 42:
                document42 = readSite("http://www.chinadaily.com.cn");
                break;
            case 43:
                document43 = readSite("http://www.shanghaidaily.com");
                break;
            case 44:
                document44 = readSite("http://www.xinhuanet.com/english");
                break;
            case 45:
                document45 = readSite("http://www.globaltimes.cn");
                break;
            case 46:
                document46 = readSite("http://www.scmp.com/frontpage/international");
                break;
            case 47:
                document47 = readSite("http://www.japantimes.co.jp");
                break;
            case 48:
                document48 = readSite("http://the-japan-news.com");
                break;
            case 49:
                document49 = readSite("https://japantoday.com");
                break;
            case 51:
                document51 = readSite("https://www.hongkongfp.com");
                break;
            case 52:
                document52 = readSite("http://www.bangkokpost.com");
                break;
            case 53:
                document53 = readSite("http://vietnamnews.vn");
                break;
            case 54:
                document54 = readSite("http://www.thejakartapost.com");
                break;
            case 55:
                document55 = readSite("http://www.abc.net.au/news");
                break;
            case 56:
                document56 = readSite("http://www.theaustralian.com.au");
                break;
            case 57:
                document57 = readSite("http://www.nzherald.co.nz");
                break;
            case 58:
                document58 = readSite("http://www.aljazeera.com");
                break;
            case 59:
                document59 = readSite("https://www.bloomberg.com");
                break;
            case 60:
                document60 = readSite("http://www.reuters.com");
                break;
        }
    }

    private Document readSite(String url) {
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
