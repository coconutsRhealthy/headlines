package com.lennart.model.headlinesFE;

import org.apache.commons.lang3.time.DateUtils;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

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
        buzzWordGroups = retainBuzzWordsWithSameImagesInGroupsMap(buzzWordGroups);

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

    private Map<Integer, List<BuzzWord>> retainBuzzWordsWithSameImagesInGroupsMap(Map<Integer, List<BuzzWord>> buzzWordGroupsMap) {
        Map<Integer, List<BuzzWord>> cleanedMap = new HashMap<>();

        for (Map.Entry<Integer, List<BuzzWord>> entry : buzzWordGroupsMap.entrySet()) {
            String mostFrequentImageUrlFromGroup = getMostFrequentImageUrlFromGroup(entry.getValue());

            if(mostFrequentImageUrlFromGroup != null) {
                List<BuzzWord> cleanedGroup = retainBuzzWordsWithSameImageInGroup(entry.getValue(), mostFrequentImageUrlFromGroup);
                cleanedMap.put(entry.getKey(), cleanedGroup);
            }
        }
        return cleanedMap;
    }

    private String getMostFrequentImageUrlFromGroup(List<BuzzWord> group) {
        List<String> allImageLinks = new ArrayList<>();

        for(BuzzWord buzzWord : group) {
            if(!buzzWord.getImageLink().equals("-")) {
                allImageLinks.add(buzzWord.getImageLink());
            }
        }

        Map<Integer, String> frequencyMap = new TreeMap<>(Collections.reverseOrder());

        for(String imageLink : allImageLinks) {
            frequencyMap.put(Collections.frequency(allImageLinks, imageLink), imageLink);
        }

        if(frequencyMap.entrySet().iterator().hasNext()) {
            return frequencyMap.entrySet().iterator().next().getValue();
        }
        return null;
    }

    private List<BuzzWord> retainBuzzWordsWithSameImageInGroup(List<BuzzWord> group, String imageUrlToRetain) {
        List<BuzzWord> buzzWordsToRetain = new ArrayList<>();

        for(BuzzWord buzzWord : group) {
            if(buzzWord.getImageLink().equals(imageUrlToRetain)) {
                buzzWordsToRetain.add(buzzWord);
            }
        }
        return buzzWordsToRetain;
    }

    private Topic getTopicFromBuzzWordGroup(List<BuzzWord> buzzWordGroup) throws Exception {
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
            String dateTimeForTopic = getDateTimeForTopic(dateTime);

            return new Topic(entry, dateTimeForTopic, allHeadlines, allLinks, allSites, imageLink);
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

    private Topic getTopicFromNonGroupBuzzWord(BuzzWord buzzWord) throws Exception {
        return new Topic(buzzWord.getEntry(), getDateTimeForTopic(buzzWord.getDateTime()), buzzWord.getHeadlines(), buzzWord.getLinks(),
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
        String dateTime = rs.getString("date");
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

        List<Topic> clearedList = new ArrayList<>(imageLinkTopicMap.values());
        Collections.sort(clearedList, getTopicComparator());
        return clearedList;
    }

    private String getDateTimeForTopic(String dateTimeOfBuzzWord) throws Exception {
        Date currentDate = new java.util.Date();
        currentDate = DateUtils.addHours(currentDate, 2);
        long currentTime = currentDate.getTime();

        long entryTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateTimeOfBuzzWord).getTime();
        long timeDifference = currentTime - entryTime;
        double timeDifferenceInHours = timeDifference / (double) 3_600_000;

        String topicTimeString;

        if(timeDifferenceInHours < 1) {
            topicTimeString = "Less than 1 hour ago";
        } else if(timeDifferenceInHours < 2) {
            topicTimeString = "1 hour ago";
        } else if(timeDifferenceInHours < 3) {
            topicTimeString = "2 hour ago";
        } else if(timeDifferenceInHours < 4) {
            topicTimeString = "3 hour ago";
        } else if(timeDifferenceInHours < 5) {
            topicTimeString = "4 hour ago";
        } else if(timeDifferenceInHours < 6) {
            topicTimeString = "5 hour ago";
        } else if(timeDifferenceInHours < 7) {
            topicTimeString = "6 hour ago";
        } else if(timeDifferenceInHours < 8) {
            topicTimeString = "7 hour ago";
        } else if(timeDifferenceInHours < 9) {
            topicTimeString = "8 hour ago";
        } else if(timeDifferenceInHours < 10) {
            topicTimeString = "9 hour ago";
        } else if(timeDifferenceInHours < 11) {
            topicTimeString = "10 hour ago";
        } else if(timeDifferenceInHours < 12) {
            topicTimeString = "11 hour ago";
        } else if(timeDifferenceInHours < 13) {
            topicTimeString = "12 hour ago";
        } else if(timeDifferenceInHours < 14) {
            topicTimeString = "13 hour ago";
        } else if(timeDifferenceInHours < 15) {
            topicTimeString = "14 hour ago";
        } else if(timeDifferenceInHours < 16) {
            topicTimeString = "15 hour ago";
        } else if(timeDifferenceInHours < 17) {
            topicTimeString = "16 hour ago";
        } else if(timeDifferenceInHours < 18) {
            topicTimeString = "17 hour ago";
        } else if(timeDifferenceInHours < 19) {
            topicTimeString = "18 hour ago";
        } else if(timeDifferenceInHours < 20) {
            topicTimeString = "19 hour ago";
        } else if(timeDifferenceInHours < 21) {
            topicTimeString = "20 hour ago";
        } else if(timeDifferenceInHours < 22) {
            topicTimeString = "21 hour ago";
        } else if(timeDifferenceInHours < 23) {
            topicTimeString = "22 hour ago";
        } else {
            topicTimeString = "23 hour ago";
        }
        return topicTimeString;
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
