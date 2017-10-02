package com.lennart.model.headlinesFE;

import java.sql.*;
import java.util.*;

/**
 * Created by LennartMac on 02/10/2017.
 */
public class RetrieveTopics extends RetrieveBuzzwords {

    private Connection con;

    public List<Topic> retrieveAllTopicsFromDb(String database) throws Exception {
        List<Topic> allTopics = new ArrayList<>();

        List<BuzzWord> nonGroupBuzzWords = retrieveAllNonGroupBuzzWordsWithImage(database);

        for(BuzzWord nonGroupBuzzWord : nonGroupBuzzWords) {
            allTopics.add(getTopicFromNonGroupBuzzWord(nonGroupBuzzWord));
        }

        Map<Integer, List<BuzzWord>> buzzWordGroups = retrieveAllBuzzWordGroups(database);

        for (Map.Entry<Integer, List<BuzzWord>> entry : buzzWordGroups.entrySet()) {
            Topic topic = getTopicFromBuzzWordGroup(entry.getValue());

            if(topic != null) {
                allTopics.add(topic);
            }
        }

        allTopics = clearTopicListOfDoubleImages(allTopics);

        return allTopics;
    }

    private Map<Integer, List<BuzzWord>> retrieveAllBuzzWordGroups(String database) throws Exception {
        List<BuzzWord> allGroupBuzzWords = getAllGroupBuzzWords(database);
        return getMapOfGroupBuzzWords(allGroupBuzzWords);
    }

    private Topic getTopicFromBuzzWordGroup(List<BuzzWord> buzzWordGroup) {
        String imageLink = getNewestImageLinkFromBuzzWordGroup(buzzWordGroup);

        if(imageLink != null) {
            List<String> allLinks = new ArrayList<>();
            List<String> allHeadlines = new ArrayList<>();
            List<String> allSites = new ArrayList<>();

            for(BuzzWord buzzWord : buzzWordGroup) {
                List<String> buzzWordLinks = new ArrayList<>();
                buzzWordLinks.addAll(buzzWord.getLinks());
                Collections.reverse(buzzWordLinks);

                List<String> buzzWordHeadlines = new ArrayList<>();
                buzzWordHeadlines.addAll(buzzWord.getHeadlines());
                Collections.reverse(buzzWordHeadlines);

                List<String> buzzWordSites = new ArrayList<>();
                buzzWordSites.addAll(buzzWord.getSites());
                Collections.reverse(buzzWordSites);

                for(int i = 0; i < buzzWordLinks.size(); i++) {
                    if(!allLinks.contains(buzzWordLinks.get(i))) {
                        allLinks.add(buzzWordLinks.get(i));
                        allHeadlines.add(buzzWordHeadlines.get(i));
                        allSites.add(buzzWordSites.get(i));
                    }
                }
            }

            int entry = getNewestEntryFromBuzzWordList(buzzWordGroup);
            String dateTime = getDateTimeFromBuzzWord(entry, buzzWordGroup);

            return new Topic(entry, dateTime, allHeadlines, allLinks, allSites, imageLink);
        }
        return null;
    }

    private String getNewestImageLinkFromBuzzWordGroup(List<BuzzWord> buzzWords) {
        Map<Integer, String> imageLinksFromBuzzWordGroup = getImageLinksFromBuzzWordGroup(buzzWords);

        if(!imageLinksFromBuzzWordGroup.isEmpty()) {
            Map.Entry<Integer, String> entry = imageLinksFromBuzzWordGroup.entrySet().iterator().next();
            return entry.getValue();
        }
        return null;
    }

    private Map<Integer, String> getImageLinksFromBuzzWordGroup(List<BuzzWord> buzzWords) {
        Map<Integer, String> imageLinksFromBuzzWordGroup = new TreeMap<>(Collections.reverseOrder());

        for(BuzzWord buzzWord : buzzWords) {
            if(!buzzWord.getImageLink().equals("-")) {
                imageLinksFromBuzzWordGroup.put(buzzWord.getEntry(), buzzWord.getImageLink());
            }
        }
        return imageLinksFromBuzzWordGroup;
    }

    private int getNewestEntryFromBuzzWordList(List<BuzzWord> buzzWords) {
        int highestEntry = 0;

        for(BuzzWord buzzWord : buzzWords) {
            if(buzzWord.getEntry() > highestEntry) {
                highestEntry = buzzWord.getEntry();
            }
        }
        return highestEntry;
    }

    private String getDateTimeFromBuzzWord(int entry, List<BuzzWord> buzzWords) {
        String dateTime = "";

        for(BuzzWord buzzWord : buzzWords) {
            if(buzzWord.getEntry() == entry) {
                dateTime = buzzWord.getDateTime();
                break;
            }
        }
        return dateTime;
    }

    private List<BuzzWord> retrieveAllNonGroupBuzzWordsWithImage(String database) throws Exception {
        List<BuzzWord> nonGroupBuzzWordsWithImage = new ArrayList<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM " + database + " WHERE image_link <> '-' AND group_number = 0 ORDER BY entry DESC;");

        while(rs.next()) {
            nonGroupBuzzWordsWithImage.add(getBuzzWordFromResultSet(rs));
        }

        rs.close();
        st.close();
        closeDbConnection();

        return nonGroupBuzzWordsWithImage;
    }

    private Topic getTopicFromNonGroupBuzzWord(BuzzWord buzzWord) {
        return new Topic(buzzWord.getEntry(), buzzWord.getDateTime(), buzzWord.getHeadlines(), buzzWord.getLinks(),
                buzzWord.getSites(), buzzWord.getImageLink());
    }

    private List<BuzzWord> getAllGroupBuzzWords(String database) throws Exception {
        List<BuzzWord> allGroupBuzzWords = new ArrayList<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM " + database + " WHERE group_number <> 0 ORDER BY entry DESC;");

        while(rs.next()) {
            allGroupBuzzWords.add(getBuzzWordFromResultSet(rs));
        }

        rs.close();
        st.close();
        closeDbConnection();

        return allGroupBuzzWords;
    }

    private Map<Integer, List<BuzzWord>> getMapOfGroupBuzzWords(List<BuzzWord> allGroupBuzzWords) {
        Map<Integer, List<BuzzWord>> mapOfGroupBuzzWords = new HashMap<>();

        for(BuzzWord groupBuzzWord : allGroupBuzzWords) {
            if(mapOfGroupBuzzWords.get(groupBuzzWord.getGroup()) == null) {
                mapOfGroupBuzzWords.put(groupBuzzWord.getGroup(), new ArrayList<>());
            }
            mapOfGroupBuzzWords.get(groupBuzzWord.getGroup()).add(groupBuzzWord);
        }
        return mapOfGroupBuzzWords;
    }

    private BuzzWord getBuzzWordFromResultSet(ResultSet rs) throws Exception {
        String dateTime = rs.getString("date").split(" ")[1];
        dateTime = getCorrectTimeString(dateTime);
        String word = rs.getString("word");
        List<String> headlines = Arrays.asList(rs.getString("headlines").split(" ---- "));
        headlines = removeEmptyStrings(headlines);
        List<String> links = Arrays.asList(rs.getString("links").split(" ---- "));
        links = removeEmptyStrings(links);
        List<String> sites = getNewsSitesFromLinks(links, null);

        int entry = rs.getInt("entry");
        int group = rs.getInt("group_number");

        String imageLink = rs.getString("image_link");

        return new BuzzWord(entry, dateTime, word, headlines, links, sites, group, imageLink);
    }

    private List<Topic> clearTopicListOfDoubleImages(List<Topic> initialTopicList) {
        Map<String, Topic> imageLinkTopicMap = new HashMap<>();

        for(Topic topic : initialTopicList) {
            if(imageLinkTopicMap.get(topic.getImageLink()) == null) {
                imageLinkTopicMap.put(topic.getImageLink(), topic);
            }
        }

        List<Topic> clearedList = new ArrayList<Topic>(imageLinkTopicMap.values());
        Collections.sort(clearedList, getTopicComparator());
        return clearedList;
    }

    public Comparator<Topic> getTopicComparator() {
        return new Comparator<Topic>() {
            @Override
            public int compare(Topic topic1, Topic topic2) {
                if(topic2.getEntry() > topic1.getEntry()) {
                    return 1;
                } else if(topic2.getEntry() == topic1.getEntry()) {
                    return 0;
                } else {
                    return -1;
                }
            }
        };
    }

    protected void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/words", "root", "Vuurwerk00");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
