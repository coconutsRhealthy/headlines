package com.lennart.model.headlinesFE;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by LennartMac on 25/06/17.
 */
public class RetrieveBuzzwords {

    private Connection con;

    public List<BuzzWord> retrieveBuzzWordsFromDb(String database) throws Exception {
        List<BuzzWord> buzzWords = new ArrayList<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM " + database);

        while(rs.next()) {
            String dateTime = rs.getString("date").split(" ")[1];
            String word = rs.getString("word");
            List<String> headlines = Arrays.asList(rs.getString("headlines").split(" ---- "));
            headlines = removeEmptyStrings(headlines);
            List<String> links = Arrays.asList(rs.getString("links").split(" ---- "));
            links = removeEmptyStrings(links);

            buzzWords.add(new BuzzWord(dateTime, word, headlines, links));
        }

        rs.close();
        st.close();
        closeDbConnection();

        Collections.reverse(buzzWords);
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

    private List<String> getNewsSitesFromLinks(List<String> links) {
        List<String> newsSites = new ArrayList<>();

        for(String link : links) {
            if(link.contains("cbc.")) {
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
            }

        }
        return null;
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/words", "root", "Vuurwerk00");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
