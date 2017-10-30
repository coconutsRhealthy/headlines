package com.lennart.model.headlinesFE;

import com.lennart.model.headlinesBuzzDb.DataForAllBuzzWordsProvider;
import com.lennart.model.twitter.TweetMachine;
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

    //in the future this could be implementend as a static class variable, which is refreshed every 30 seconds or so
    //private static List<Topic> allTopics;

    public List<Topic> retrieveAllTopicsFromDb(String database) throws Exception {
        List<Topic> allTopics = new ArrayList<>();

        List<BuzzWord> buzzWordsWithImage = retrieveAllBuzzWordsWithImage(database);

        for(BuzzWord buzzWordWithImage : buzzWordsWithImage) {
            Topic topicFromBuzzWord = getTopicFromBuzzWord(buzzWordWithImage, database);

            if(topicFromBuzzWord != null) {
                allTopics.add(topicFromBuzzWord);
            }
        }

        allTopics = clearTopicListOfDoubleImages(allTopics);
        allTopics = removeTopicsThatAreSameTopic(allTopics);

        return allTopics;
    }

    List<String> retainHeadlinesThatAreTrulyRelatedForTopic(List<String> headlines, String database) {
        DataForAllBuzzWordsProvider dataForAllBuzzWordsProvider = new DataForAllBuzzWordsProvider();
        List<String> correctFormatHeadlines = new TweetMachine().convertHeadlinesToNonSpecialCharactersAndLowerCase(headlines);
        List<Map<String, String>> headlinesCorrectFormatKeyRawValueInList = new ArrayList<>();

        for(int i = 0; i < headlines.size(); i++) {
            headlinesCorrectFormatKeyRawValueInList.add(new HashMap<>());
            headlinesCorrectFormatKeyRawValueInList.get(i).put(correctFormatHeadlines.get(i), headlines.get(i));
        }

        Map<String, Integer> wordsRankedByOccurenceTwoOrMore = dataForAllBuzzWordsProvider.getWordsRankedByOccurrence(correctFormatHeadlines, "", 2);

        List<String> headlinesToRemove;

        if(database.equals("entertainment_buzzwords_new")) {
            headlinesToRemove = dataForAllBuzzWordsProvider.getHeadlinesThatAreUnrelated(correctFormatHeadlines, wordsRankedByOccurenceTwoOrMore, 4);
        } else {
            headlinesToRemove = dataForAllBuzzWordsProvider.getHeadlinesThatAreUnrelated(correctFormatHeadlines, wordsRankedByOccurenceTwoOrMore, 3);
        }

        List<Map<String, String>> listOfMapsToRemove = new ArrayList<>();

        for(String headlineToRemove : headlinesToRemove) {
            for(Map<String, String> map : headlinesCorrectFormatKeyRawValueInList) {
                String headlineCorrectFormat = map.entrySet().iterator().next().getKey();

                if(headlineToRemove.equals(headlineCorrectFormat)) {
                    listOfMapsToRemove.add(map);
                }
            }
        }

        headlinesCorrectFormatKeyRawValueInList.removeAll(listOfMapsToRemove);

        List<String> headlinesToRetain = new ArrayList<>();

        for(Map<String, String> map : headlinesCorrectFormatKeyRawValueInList) {
            headlinesToRetain.add(map.entrySet().iterator().next().getValue());
        }

        return headlinesToRetain;
    }

    private List<BuzzWord> retrieveAllBuzzWordsWithImage(String database) throws Exception {
        List<BuzzWord> buzzWordsWithImage = new ArrayList<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM " + database + " WHERE image_link <> '-' ORDER BY entry DESC;");

        while(rs.next()) {
            buzzWordsWithImage.add(getBuzzWordFromResultSet(rs));
        }

        rs.close();
        st.close();
        closeDbConnection();

        return buzzWordsWithImage;
    }

    private Topic getTopicFromBuzzWord(BuzzWord buzzWord, String database) throws Exception {
        List<String> headlines = retainHeadlinesThatAreTrulyRelatedForTopic(buzzWord.getHeadlines(), database);

        if(headlines.size() >= 3) {
            return new Topic(buzzWord.getEntry(), getDateTimeForTopic(buzzWord.getDateTime()), headlines, buzzWord.getLinks(),
                    buzzWord.getSites(), buzzWord.getImageLink());
        }
        return null;
    }

    private List<Topic> removeTopicsThatAreSameTopic(List<Topic> allTopics) {
        List<Topic> topicsToRemove = new ArrayList<>();

        List<Topic> allTopicsCopyOuter = new ArrayList<>();
        List<Topic> allTopicsCopyInner = new ArrayList<>();

        allTopicsCopyOuter.addAll(allTopics);
        allTopicsCopyInner.addAll(allTopics);

        DataForAllBuzzWordsProvider dataForAllBuzzWordsProvider = new DataForAllBuzzWordsProvider();

        for(Topic topicOuter : allTopicsCopyOuter) {
            List<String> headlines = topicOuter.getHeadlines();

            List<String> correctFormatHeadlinesOuter = new TweetMachine().convertHeadlinesToNonSpecialCharactersAndLowerCase(headlines);
            Map<String, Integer> wordsRankedByOccurenceTwoOrMore = dataForAllBuzzWordsProvider.getWordsRankedByOccurrence(correctFormatHeadlinesOuter, "", 2);

            for(Topic topicInner : allTopicsCopyInner) {
                if(!topicsToRemove.contains(topicInner)) {
                    List<String> innerHeadlines = topicInner.getHeadlines();
                    List<String> correctFormatHeadlinesInner = new TweetMachine().convertHeadlinesToNonSpecialCharactersAndLowerCase(innerHeadlines);

                    for(String headline : correctFormatHeadlinesInner) {
                        int counter = 0;

                        for (Map.Entry<String, Integer> entry : wordsRankedByOccurenceTwoOrMore.entrySet()) {
                            if(headline.contains(entry.getKey())) {
                                counter++;

                                if(counter >= 3) {
                                    break;
                                }
                            }
                        }

                        if(counter >= 3) {
                            if(topicOuter.getEntry() != topicInner.getEntry()) {
                                if(topicInner.getEntry() > topicOuter.getEntry()) {
                                    if(!topicsToRemove.contains(topicOuter)) {
                                        topicsToRemove.add(topicOuter);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        allTopicsCopyOuter.removeAll(topicsToRemove);

        return allTopicsCopyOuter;
    }

    private BuzzWord getBuzzWordFromResultSet(ResultSet rs) throws Exception {
        String dateTime = rs.getString("date");
        String word = rs.getString("word");
        List<String> headlines = Arrays.asList(rs.getString("headlines").split(" ---- "));
        headlines = replaceEmptyStringByMinus(headlines);
        List<String> links = Arrays.asList(rs.getString("links").split(" ---- "));
        links = replaceEmptyStringByMinus(links);
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