package com.lennart.model.headlinesBuzzDb;

import java.sql.*;
import java.util.*;

/**
 * Created by LennartMac on 15/08/2017.
 */
public class RelatedBuzzwordsIdentifier {

    private Connection con;

    public void updateGroupsInDb(String database) throws Exception {
        List<String> headlinesFromDb = getAllHeadlinesFromDb(database);
        headlinesFromDb = filterOutDoubleHeadlinesFromList(headlinesFromDb);
        Map<String, Set<String>> groupPerHeadline = getGroupPerHeadline(headlinesFromDb);
        Set<Set<String>> allGroups = getAllGroups(groupPerHeadline);
        List<Set<String>> allGroupsSortedBySize = sortAllGroupsBySize(allGroups);
        Map<Integer, Set<String>> finalGroupMap = getTheFinalGroupMap(allGroupsSortedBySize);
        Map<String, Integer> buzzWordGroupMap = getBuzzwordGroupMap(database, finalGroupMap);
        Map<String, Integer> correctFinalMap = setCorrectGroupNumbersInBuzzwordMap(buzzWordGroupMap);
        doDatabaseUpdate(database, correctFinalMap);
    }

    private Map<String, Integer> getBuzzwordGroupMap(String database, Map<Integer, Set<String>> finalGroupMap) throws Exception {
        Map<String, Integer> buzzwordGroupMap = new HashMap<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM " + database + ";");

        while(rs.next()) {
            List<String> headlines = Arrays.asList(rs.getString("headlines").split(" ---- "));

            for (Map.Entry<Integer, Set<String>> entry : finalGroupMap.entrySet()) {
                List<String> headlinesCopy = new ArrayList<>();
                headlinesCopy.addAll(headlines);

                headlinesCopy.retainAll(entry.getValue());

                if(!headlinesCopy.isEmpty()) {
                    buzzwordGroupMap.putIfAbsent(rs.getString("word"), entry.getKey());
                    break;
                }
            }
        }

        rs.close();
        st.close();

        closeDbConnection();

        buzzwordGroupMap = deleteEntriesFromMapWithValuesThatOccurOnlyOnce(buzzwordGroupMap);
        return buzzwordGroupMap;
    }

    private Map<String, Integer> deleteEntriesFromMapWithValuesThatOccurOnlyOnce(Map<String, Integer> initialMap) {
        Map<String, Integer> correctMap = new HashMap<>();

        for (Map.Entry<String, Integer> entry : initialMap.entrySet()) {
            for (Map.Entry<String, Integer> entry2 : initialMap.entrySet()) {
                if(!entry.getKey().equals(entry2.getKey())) {
                    if(entry.getValue() == entry2.getValue()) {
                        correctMap.put(entry.getKey(), entry.getValue());
                        break;
                    }
                }
            }
        }
        return correctMap;
    }

    private Map<String, Integer> setCorrectGroupNumbersInBuzzwordMap(Map<String, Integer> initialMap) {
        Map<Integer, Integer> frequencyMap = getFrequencyMap(initialMap);
        Map<Integer, Integer> counterMap = convertFrequencyMapToCounterMap(frequencyMap);
        Map<String, Integer> correctFinalMap = setCorrectValuesOfInitialGroupMap(initialMap, counterMap);
        correctFinalMap = sortByValueLowToHigh(correctFinalMap);
        return correctFinalMap;
    }

    private Map<Integer, Integer> getFrequencyMap(Map<String, Integer> mapToAnalyse) {
        Map<Integer, Integer> frequencyMap = new HashMap<>();
        List<Integer> valuesAsList = new ArrayList<>(mapToAnalyse.values());

        for (Map.Entry<String, Integer> entry : mapToAnalyse.entrySet()) {
            int frequency =  Collections.frequency(valuesAsList, entry.getValue());
            frequencyMap.put(entry.getValue(), frequency);
        }
        return frequencyMap;
    }

    private Map<Integer, Integer> convertFrequencyMapToCounterMap(Map<Integer, Integer> frequencyMap) {
        Map<Integer, Integer> frequencyMapCorrectNumbers = new HashMap<>();
        Map<Integer, Integer> frequencyMapSorted = sortByValueHighToLow(frequencyMap);

        int previousNumber = -1;
        int numberToSetAsValue = 1;

        for (Map.Entry<Integer, Integer> entry : frequencyMapSorted.entrySet()) {
            if(previousNumber == -1) {
                frequencyMapCorrectNumbers.put(entry.getKey(), numberToSetAsValue);
                previousNumber = entry.getValue();
            } else if(previousNumber == entry.getValue()) {
                frequencyMapCorrectNumbers.put(entry.getKey(), numberToSetAsValue);
            } else {
                numberToSetAsValue++;
                frequencyMapCorrectNumbers.put(entry.getKey(), numberToSetAsValue);
            }
        }
        return frequencyMapCorrectNumbers;
    }

    private Map<String, Integer> setCorrectValuesOfInitialGroupMap(Map<String, Integer> initialGroupMap,
                                                                   Map<Integer, Integer> counterMap) {
        Map<String, Integer> correctMap = new HashMap<>();

        for (Map.Entry<String, Integer> entry : initialGroupMap.entrySet()) {
            correctMap.put(entry.getKey(), counterMap.get(entry.getValue()));
        }
        return correctMap;
    }

    private List<String> getAllHeadlinesFromDb(String database) throws Exception {
        List<String> allHeadlinesFromDb = new ArrayList<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM " + database + ";");

        while(rs.next()) {
            List<String> headlines = Arrays.asList(rs.getString("headlines").split(" ---- "));
            allHeadlinesFromDb.addAll(headlines);
        }

        rs.close();
        st.close();

        closeDbConnection();

        return allHeadlinesFromDb;
    }

    private List<String> filterOutDoubleHeadlinesFromList(List<String> headlines) {
        Set<String> headlinesAsSet = new HashSet<>();
        headlinesAsSet.addAll(headlines);
        headlines.clear();
        headlines.addAll(headlinesAsSet);
        return headlines;
    }

    private Map<String, Set<String>> getGroupPerHeadline(List<String> headlines) {
        Map<String, Set<String>> groupPerHeadline = new HashMap<>();

        for(String headline : headlines) {
            Set<String> group = new HashSet<>();
            groupPerHeadline.put(headline, getGroup(headline, headlines, group));
        }
        return groupPerHeadline;
    }

    private Set<Set<String>> getAllGroups(Map<String, Set<String>> groupPerHeadline) {
        Set<Set<String>> allGroups = new HashSet<>();

        for (Map.Entry<String, Set<String>> entry : groupPerHeadline.entrySet()) {
            allGroups.add(entry.getValue());
        }

        List<Set<String>> allGroupsAsList = new ArrayList<>();
        allGroupsAsList.addAll(allGroups);
        List<Set<String>> allGroupsAsListFilterZeroAndOneSizeOut = new ArrayList<>();

        for(Set<String> set : allGroupsAsList) {
            if(set.size() > 1) {
                allGroupsAsListFilterZeroAndOneSizeOut.add(set);
            }
        }

        allGroups.clear();

        for(Set<String> set : allGroupsAsListFilterZeroAndOneSizeOut) {
            allGroups.add(set);
        }

        return allGroups;
    }

    private List<Set<String>> sortAllGroupsBySize(Set<Set<String>> allGroups) {
        List<Set<String>> groupListSortedBySize = new ArrayList<>();
        groupListSortedBySize.addAll(allGroups);

        Collections.sort(groupListSortedBySize, new Comparator<Set>(){
            public int compare(Set a1, Set a2) {
                return a2.size() - a1.size();
            }
        });
        return groupListSortedBySize;
    }

    private Map<Integer, Set<String>> getTheFinalGroupMap(List<Set<String>> groupListSortedBySize) {
        Map<Integer, Set<String>> theFinalGroupMap = new HashMap<>();
        int counter = 1;

        for(Set<String> group : groupListSortedBySize) {
            theFinalGroupMap.put(counter, group);
            counter++;
        }
        return theFinalGroupMap;
    }

    private Set<String> getGroup(String initialHeadline, List<String> allHeadlines, Set<String> group) {
        List<String> relatedHeadlines = getRelatedHeadlines(initialHeadline, allHeadlines);

        relatedHeadlines.removeAll(group);

        group.add(initialHeadline);
        group.addAll(relatedHeadlines);

        for(String headline : relatedHeadlines) {
            getGroup(headline, allHeadlines, group);
        }
        return group;
    }

    private List<String> getRelatedHeadlines(String headlineToAnalyse, List<String> allHeadlines) {
        List<String> relatedHeadlines = new ArrayList<>();

        String headlineToAnalyseCorrectFormat = headlineToAnalyse.toLowerCase();
        headlineToAnalyseCorrectFormat = headlineToAnalyseCorrectFormat.replaceAll("[^A-Za-z0-9 ]", "");

        List<String> wordsFromHeadlineToAnalyse = new ArrayList<>();
        wordsFromHeadlineToAnalyse.addAll(Arrays.asList(headlineToAnalyseCorrectFormat.split(" ")));
        wordsFromHeadlineToAnalyse = DataForAllBuzzWordsProvider.removeBlackListWords(wordsFromHeadlineToAnalyse);
        Set<String> wordsFromHeadlineToAnalyseAsSet = new HashSet<>();
        wordsFromHeadlineToAnalyseAsSet.addAll(wordsFromHeadlineToAnalyse);

        for(String headline : allHeadlines) {
            String headlineCorrectFormat = headline.toLowerCase();
            headlineCorrectFormat = headlineCorrectFormat.replaceAll("[^A-Za-z0-9 ]", "");

            List<String> wordsFromHeadline = new ArrayList<>();
            wordsFromHeadline.addAll(Arrays.asList(headlineCorrectFormat.split(" ")));
            wordsFromHeadline = DataForAllBuzzWordsProvider.removeBlackListWords(wordsFromHeadline);
            Set<String> wordsFromHeadlineAsSet = new HashSet<>();
            wordsFromHeadlineAsSet.addAll(wordsFromHeadline);

            Set<String> wordsFromHeadlineToAnalyseAsSetCopy = new HashSet<>();
            wordsFromHeadlineToAnalyseAsSetCopy.addAll(wordsFromHeadlineToAnalyseAsSet);

            wordsFromHeadlineToAnalyseAsSetCopy.retainAll(wordsFromHeadlineAsSet);

            if(wordsFromHeadlineToAnalyseAsSetCopy.size() >= 4) {
                relatedHeadlines.add(headline);
            }
        }

        Set<String> relatedHeadlinesAsSet = new HashSet<>();
        relatedHeadlinesAsSet.addAll(relatedHeadlines);
        relatedHeadlines.clear();
        relatedHeadlines.addAll(relatedHeadlinesAsSet);

        return relatedHeadlines;
    }

    private void doDatabaseUpdate(String database, Map<String, Integer> buzzwordGroups) throws Exception {
        initializeDbConnection();

        for (Map.Entry<String, Integer> entry : buzzwordGroups.entrySet()) {
            Statement st = con.createStatement();
            st.executeUpdate("UPDATE " + database + " SET group_number = " + entry.getValue() + " WHERE word = '" + entry.getKey() + "'");
            st.close();
        }

        closeDbConnection();
    }

    private <K, V extends Comparable<? super V>> Map<K, V> sortByValueHighToLow(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>( map.entrySet() );
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o2.getValue() ).compareTo( o1.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    private <K, V extends Comparable<? super V>> Map<K, V> sortByValueLowToHigh(Map<K, V> map) {
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

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/words?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
