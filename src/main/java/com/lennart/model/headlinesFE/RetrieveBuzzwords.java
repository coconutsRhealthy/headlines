package com.lennart.model.headlinesFE;

import org.apache.commons.lang3.time.DateUtils;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by LennartMac on 25/06/17.
 */
public class RetrieveBuzzwords {

    private Connection con;

    public List<BuzzWord> retrieveBuzzWordsFromDbInitialNewByHeadlineNumber(String database) throws Exception {
        List<BuzzWord> buzzWords = new ArrayList<>();

        long currentDate = new Date().getTime();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM " + database + " ORDER BY no_of_headlines DESC;");

        int counter = 0;

        while(rs.next()) {
            if(counter < 21) {
                String s = rs.getString("date");
                Date parsedDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(s);

                if(parsedDateTime.getTime() > currentDate - TimeUnit.HOURS.toMillis(3)) {
                    counter++;
                    buzzWords = addBuzzWordToListFromResultSet(buzzWords, rs);
                }
            } else {
                break;
            }
        }
        return buzzWords;
    }


    public List<BuzzWord> retrieveBuzzWordsFromDbInitial(String database) throws Exception {
        List<BuzzWord> buzzWords = new ArrayList<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM " + database + " ORDER BY entry DESC;");

        int counter = 0;

        while(rs.next()) {
            if(counter >= 21) {
                break;
            }

            counter++;
            buzzWords = addBuzzWordToListFromResultSet(buzzWords, rs);
        }

        rs.close();
        st.close();
        closeDbConnection();

        //Collections.reverse(buzzWords);
        return buzzWords;
    }

    public List<BuzzWord> retrieveExtraBuzzWordsFromDbNewByHeadlineNumber(String database, String latestWord) throws Exception {
        List<BuzzWord> buzzWords = new ArrayList<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM " + database + " ORDER BY no_of_headlines DESC;");

        boolean latestWordHasBeenPassed = false;
        int counter = 0;

        while(rs.next()) {
            if(!latestWordHasBeenPassed) {
                if(rs.getString("word").equals(latestWord)) {
                    latestWordHasBeenPassed = true;
                }
            } else {
                if(counter < 20) {
                    counter++;
                    buzzWords = addBuzzWordToListFromResultSet(buzzWords, rs);
                }
            }
        }

        rs.close();
        st.close();
        closeDbConnection();

        return buzzWords;
    }

    public List<BuzzWord> retrieveExtraBuzzWordsFromDb(String database, String latestWord) throws Exception {
        List<BuzzWord> buzzWords = new ArrayList<>();

        initializeDbConnection();

        Statement stFirst = con.createStatement();
        ResultSet rsFirst = stFirst.executeQuery("SELECT * FROM " + database + " WHERE word = '" + latestWord + "';");

        rsFirst.next();

        int latestWordOnSiteEntry = rsFirst.getInt("entry");

        stFirst.close();
        rsFirst.close();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM " + database);

        int targetEntryNumber = latestWordOnSiteEntry - 20;

        while(rs.next()) {
            int rsEntryNumber = rs.getInt("entry");

            if(rsEntryNumber < latestWordOnSiteEntry && rsEntryNumber >= targetEntryNumber) {
                buzzWords = addBuzzWordToListFromResultSet(buzzWords, rs);
            }
        }

        rs.close();
        st.close();
        closeDbConnection();

        Collections.reverse(buzzWords);
        return buzzWords;
    }

    public List<BuzzWord> retrieveBuzzWordsFromDbUntillHour(String database, int numberOfHours) throws Exception {
        List<BuzzWord> buzzWords = new ArrayList<>();
        long currentDate = new Date().getTime();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM " + database + " ORDER BY entry DESC;");

        while(rs.next()) {
            String s = rs.getString("date");
            Date parsedDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(s);

            if(parsedDateTime.getTime() < currentDate - TimeUnit.HOURS.toMillis(numberOfHours)) {
                buzzWords = addBuzzWordToListFromResultSet(buzzWords, rs);
            }
        }

        rs.close();
        st.close();
        closeDbConnection();

        return buzzWords;
    }

    private List<BuzzWord> addBuzzWordToListFromResultSet(List<BuzzWord> buzzWords, ResultSet rs) throws Exception {
        String dateTime = rs.getString("date").split(" ")[1];
        dateTime = getCorrectTimeString(dateTime);
        String word = rs.getString("word");
        List<String> headlines = Arrays.asList(rs.getString("headlines").split(" ---- "));
        headlines = removeEmptyStrings(headlines);
        List<String> links = Arrays.asList(rs.getString("links").split(" ---- "));
        links = removeEmptyStrings(links);
        List<String> sites = getNewsSitesFromLinks(links);

        int entry = rs.getInt("entry");

        buzzWords.add(new BuzzWord(entry, dateTime, word, headlines, links, sites));

        return buzzWords;
    }

    private List<String> removeEmptyStrings(List<String> strings) {
        List<String> nonEmptyStrings = new ArrayList<>();

        for(String string : strings) {
            if(!string.isEmpty()) {
                nonEmptyStrings.add(string);
            }
        }
        return nonEmptyStrings;
    }

    private String getCorrectTimeString(String rawDateTime) {
        String correctTime = rawDateTime.substring(0, rawDateTime.lastIndexOf(":"));

        correctTime = addYtoTimeStringIfWordIsFromYesterday(correctTime);

        //correctTime = correctTime + " CEST";
        return correctTime;
    }

    private String addYtoTimeStringIfWordIsFromYesterday(String timeString) {
        Integer hourOfGivenTimeString = Integer.valueOf(timeString.split(":")[0]);
        Integer minuteOfGivenTimeString = Integer.valueOf(timeString.split(":")[1]);

        Date currentDate = new Date();
        currentDate = DateUtils.addHours(currentDate, 2);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateAsString = dateFormat.format(currentDate);
        String timeOfCurrentDate = currentDateAsString.split(" ")[1];

        Integer currentHour = Integer.valueOf(timeOfCurrentDate.split(":")[0]);
        Integer currentMinute = Integer.valueOf(timeOfCurrentDate.split(":")[1]);

        if(hourOfGivenTimeString > currentHour) {
            timeString = timeString + " (y)";
        } else if(hourOfGivenTimeString == currentHour) {
            if(minuteOfGivenTimeString > currentMinute) {
                timeString = timeString + " (y)";
            }
        }
        return timeString;
    }

    private List<String> getNewsSitesFromLinks(List<String> links) {
        List<String> newsSites = new ArrayList<>();

        for(String link : links) {
            if(link.contains("aljazeera.")) {
                newsSites.add("Al Jazeera");
            } else if(link.contains("bloomberg.")) {
                newsSites.add("Bloomberg");
            } else if(link.contains("reuters.")) {
                newsSites.add("Reuters");
            } else if(link.contains("cbc.")) {
                newsSites.add("CBC");
            } else if(link.contains("thestar.")) {
                newsSites.add("The Star");
            } else if(link.contains("nytimes.")) {
                newsSites.add("NY Times");
            } else if(link.contains("washingtonpost.")) {
                newsSites.add("Washington Post");
            } else if(link.contains("huffingtonpost.")) {
                newsSites.add("Huffington Post");
            } else if(link.contains("latimes.")) {
                newsSites.add("LA Times");
            } else if(link.contains("cnn.")) {
                newsSites.add("CNN");
            } else if(link.contains("foxnews.")) {
                newsSites.add("FOX News");
            } else if(link.contains("usatoday.")) {
                newsSites.add("USA Today");
            } else if(link.contains("wsj.")) {
                newsSites.add("Wall Street Journal");
            } else if(link.contains("cnbc.")) {
                newsSites.add("CNBC");
            } else if(link.contains("nbcnews.")) {
                newsSites.add("NBC News");
            } else if(link.contains("theyucatantimes.")) {
                newsSites.add("Yucatan Times");
            } else if(link.contains("thenews.")) {
                newsSites.add("The News MX");
            } else if(link.contains("riotimesonline.")) {
                newsSites.add("Rio Times");
            } else if(link.contains("folha.")) {
                newsSites.add("Folha de S.Paulo");
            } else if(link.contains("buenosairesherald.")) {
                newsSites.add("Buenos Aires Herald");
            } else if(link.contains("theguardian.")) {
                newsSites.add("The Guardian");
            } else if(link.contains("bbc.")) {
                newsSites.add("BBC");
            } else if(link.contains("ft.")) {
                newsSites.add("Financial Times");
            } else if(link.contains("thetimes.")) {
                newsSites.add("The Times");
            } else if(link.contains("thesun.")) {
                newsSites.add("The Sun");
            } else if(link.contains("irishtimes.")) {
                newsSites.add("Irish Times");
            } else if(link.contains("telegraph.")) {
                newsSites.add("The Telegraph");
            } else if(link.contains("mediapart.")) {
                newsSites.add("Mediapart FR");
            } else if(link.contains("spiegel.")) {
                newsSites.add("Der Spiegel");
            } else if(link.contains("elpais.")) {
                newsSites.add("El País");
            } else if(link.contains("ansa.")) {
                newsSites.add("Ansa IT");
            } else if(link.contains("rt.")) {
                newsSites.add("Russia Today");
            } else if(link.contains("themoscowtimes.")) {
                newsSites.add("Moscow Times");
            } else if(link.contains("dailysun.")) {
                newsSites.add("Daily Sun ZA");
            } else if(link.contains("timeslive.")) {
                newsSites.add("Times Live ZA");
            } else if(link.contains("vanguardngr.")) {
                newsSites.add("Vanguard NGR");
            } else if(link.contains("gulfnews.")) {
                newsSites.add("Gulf News");
            } else if(link.contains("dailysabah.")) {
                newsSites.add("Daily Sabah");
            } else if(link.contains("tehrantimes.")) {
                newsSites.add("Tehran Times");
            } else if(link.contains("ynetnews.")) {
                newsSites.add("Ynet");
            } else if(link.contains("timesofoman.")) {
                newsSites.add("Times of Oman");
            } else if(link.contains("timesofindia.")) {
                newsSites.add("Times of India");
            } else if(link.contains("indianexpress.")) {
                newsSites.add("Indian Express");
            } else if(link.contains("chinadaily.")) {
                newsSites.add("China Daily");
            } else if(link.contains("shanghaidaily.")) {
                newsSites.add("Shanghai Daily");
            } else if(link.contains("xinhuanet.")) {
                newsSites.add("Xinhua News");
            } else if(link.contains("globaltimes.")) {
                newsSites.add("Global Times");
            } else if(link.contains("scmp.")) {
                newsSites.add("South China Morning Post");
            } else if(link.contains("japantimes.")) {
                newsSites.add("Japan Times");
            } else if(link.contains("japan-news.")) {
                newsSites.add("The Japan News");
            } else if(link.contains("japantoday.")) {
                newsSites.add("Japan Today");
            } else if(link.contains("hongkongfp.")) {
                newsSites.add("Hong Kong FP");
            } else if(link.contains("bangkokpost.")) {
                newsSites.add("Bangkok Post");
            } else if(link.contains("vietnamnews.")) {
                newsSites.add("Vietnam News");
            } else if(link.contains("thejakartapost.")) {
                newsSites.add("The Jakarta Post");
            } else if(link.contains("abc.")) {
                newsSites.add("ABC");
            } else if(link.contains("theaustralian.")) {
                newsSites.add("The Australian");
            } else if(link.contains("nzherald.")) {
                newsSites.add("New Zealand Herald");
            } else {
                String site = link.split("\\.")[1];
                newsSites.add(site);
            }
        }
        return newsSites;
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/words", "root", "Vuurwerk00");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
