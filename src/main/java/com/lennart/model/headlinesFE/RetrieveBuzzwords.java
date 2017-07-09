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
            List<String> links = Arrays.asList(rs.getString("links").split(" ---- "));

            buzzWords.add(new BuzzWord(dateTime, word, headlines, links));
        }

        rs.close();
        st.close();
        closeDbConnection();

        Collections.reverse(buzzWords);
        return buzzWords;
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/words", "root", "Vuurwerk00");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
