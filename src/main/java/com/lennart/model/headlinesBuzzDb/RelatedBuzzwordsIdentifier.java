package com.lennart.model.headlinesBuzzDb;

import java.sql.*;
import java.util.*;

/**
 * Created by LennartMac on 15/08/2017.
 */
public class RelatedBuzzwordsIdentifier {

    private Connection con;

    //new approach (17 aug evening)
        //we gaan toch vergelijken op basis van headlines zelf, ipv op basis van urls

        //eerst store je gewoon al je buzzwords

        //daarna doe je update van groups in db

        //retrieve uit db alle headlines, doe deze in een grote list

        //maak hier set van, en dan weer list

        //nu ga je bepalen welke headlines van hetzelfde onderwerp zijn

        //dit doe je door woordanalyse (analoog aan wat je al hebt bij buzzwords)

        //maak een map headlineKeyRelatedHeadlineValue:
        // <String>, <List<String>> waarbij de key een headline is, en de value related headlines



        //hierna heb je eerste fase groepen.

        //maak nu een nieuwe map firstPhaseGroups:
        // <Integer>, <List<String>> met als key int van 1 tot n en value de key en
        //value van vorige map samengevoegd

        //maak nog een nieuwe map, combinedGroups: <Integer>, <Set<String>>

        //loop nu door firstPhaseGroups:
        //



        //van headlineKeyRelatedHeadlineValue moet je op de een of andere manier groepen gaan maken

        //maak een nieuwe map: theMap: <Integer, List<String>>

        //loop in headlineKeyRelatedHeadlineValue door elke value String heen per key. Elke value String
        //moet ook een key zijn in de map.




                    //"aap is vies"
                        //"nootje ook"
                            //"de bezige bij loopt op straat"
                            //"hij is lekker bezig"
                        //"verkerk is niet vies hoor"
                        //"maar de beestenboel wel"

                    //getRelatedHeadlinesThatAreNotYetInSet(String headline, Set<String> relatedHeadlines)
                        //dit moet iets van een recursieve methode worden



    //************

    //retrieve alle headlines uit db

    //maak hier set van en dan weer list

    //maak een nieuwe map: groupPerHeadline: <String, Set<String>>

    //loop door alle headlines uit headlinesFromDb heen. Add headline als key in groupPerHeadline
    //en als value getGroup

    //maak een set van sets: allGroups: <Set<Set<String>>

    //loop door groupPerHeadline heen. Add de group aan allGroups.

    //verwijder uit allGroups groups met size 0 en groups met size 1

    //maak een list: groupList: List<Set<String>>. voeg daar alle sets van allGroups aan toe.

    //sorteer groupList op size van de Set<Strings>, van groot naar klein

    //Maak een map: theMap: <Integer, Set<String>>

    //loop door groupList heen en voeg de sets een voor een toe aan theMap, met int counter starting at 1 als key

    //nu heb je het. Nu kun je in db het group field updaten per buzzword

    public static void main(String[] args) throws Exception {
        RelatedBuzzwordsIdentifier relatedBuzzwordsIdentifier = new RelatedBuzzwordsIdentifier();
        relatedBuzzwordsIdentifier.updateGroupsInDb("buzzwords_new");
    }



    private void updateGroupsInDb(String database) throws Exception {
        List<String> headlinesFromDb = getAllHeadlinesFromDb(database);
        headlinesFromDb = filterOutDoubleHeadlinesFromList(headlinesFromDb);
        Map<String, Set<String>> groupPerHeadline = getGroupPerHeadline(headlinesFromDb);
        Set<Set<String>> allGroups = getAllGroups(groupPerHeadline);
        List<Set<String>> allGroupsSortedBySize = sortAllGroupsBySize(allGroups);
        Map<Integer, Set<String>> finalGroupMap = getTheFinalGroupMap(allGroupsSortedBySize);

        Map<String, Integer> buzzWordGroupMap = getBuzzwordGroupMap(database, finalGroupMap);

        Map<String, Integer> correctFinalMap = theMapCorrectingMethod(buzzWordGroupMap);

        System.out.println("wacht");
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


    private Map<String, Integer> theMapCorrectingMethod(Map<String, Integer> initialMap) {
        //Je hebt map met woorden en bijbehorende groep

        //Je wil nu dat de woorden een nieuwe groep krijgen, op basis van hoeveel de eerdere groep voorkomt

        //

        Map<Integer, Integer> frequencyMap = getFrequencyMap(initialMap);
        Map<Integer, Integer> counterMap = convertFrequencyMapToCounterMap2(frequencyMap);
        Map<String, Integer> correctFinalMap = setCorrectValuesOfInitialGroupMap(initialMap, counterMap);
        correctFinalMap = sortByValueLowToHigh(correctFinalMap);
        return correctFinalMap;
    }

    private Map<Integer, Integer> getFrequencyMap(Map<String, Integer> mapToAnalyse) {
        Map<Integer, Integer> frequencyMap = new HashMap<>();
        List<Integer> valuesAsList = new ArrayList<Integer>(mapToAnalyse.values());

        for (Map.Entry<String, Integer> entry : mapToAnalyse.entrySet()) {
            int frequency =  Collections.frequency(valuesAsList, entry.getValue());
            frequencyMap.put(entry.getValue(), frequency);
        }
        return frequencyMap;
    }

    private Map<Integer, Integer> convertFrequencyMapToCounterMap(Map<Integer, Integer> frequencyMap) {
        Map<Integer, Integer> frequencyMapSorted = sortByValueHighToLow(frequencyMap);
        Map<Integer, Integer> frequencyMapCorrectNumbers = new HashMap<>();
        int counter = 1;

        for (Map.Entry<Integer, Integer> entry : frequencyMapSorted.entrySet()) {
            frequencyMapCorrectNumbers.put(entry.getKey(), counter);
            counter++;
        }
        return frequencyMapCorrectNumbers;
    }

    private Map<Integer, Integer> convertFrequencyMapToCounterMap2(Map<Integer, Integer> frequencyMap) {
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

//    private Map<String, Integer> setCorrectGroupNumbersForBuzzword(Map<String, Integer> initialMap) {
//
//        //je hebt de map met woorden en bijbehorende groep
//
//
//        //stop alle values in een list
//
//
//        //sorteer de list op no_of_occurr
//
//
//        //maak set van list
//
//
//        //maak list van set
//
//
//        //loop door map heen. Kijk aan welke index van list de value van map gelijk is. Verander de value in deze index
//
//
//
//        List<Integer> allValuesOfMap = new ArrayList<>();
//
//        for (Map.Entry<String, Integer> entry : initialMap.entrySet()) {
//            allValuesOfMap.add(entry.getValue());
//        }
//
//
//    }



//    private Map<String, Integer> setCorrectGroupNumbersForBuzzword(Map<String, Integer> initialMap) {
//        Map<String, Integer> correctMap = new HashMap<>();
//        Map<Integer, Integer> occurrenceMap = new HashMap<>();
//
//        for (Map.Entry<String, Integer> entry : initialMap.entrySet()) {
//            int counter = 0;
//
//            if(occurrenceMap.get(entry.getValue()) == null) {
//                for (Map.Entry<String, Integer> entry2 : initialMap.entrySet()) {
//                    if(entry.getValue() == entry2.getValue()) {
//                        counter++;
//                    }
//                }
//                occurrenceMap.put(entry.getValue(), counter);
//            }
//        }
//
//        occurrenceMap = sortByValueHighToLow(occurrenceMap);
//
//        int newCounter = 1;
//        Map<Integer, Integer> correctOccurrenceMap = new HashMap<>();
//
//
//
//        for (Map.Entry<String, Integer> entry : initialMap.entrySet()) {
//            correctMap.put(entry.getKey(), occurrenceMap.get(entry.getValue()));
//        }
//
//        return correctMap;
//    }


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










    //new approach (17 aug morning)
        //eerst store je alle woorden in db

        //dan ga je per woord de groep zetten of updaten, door:

        //haal uit de db alle urls op. Sorteer deze van meest naar minst voorkomend

        //maak hier set van en dan weer list

        //loop door deze lijst heen. Elk woord dat de eerste url heeft zit in de grootste groep

        //elk woord dat de 2e url heeft zit in de tweede groep, mits het woord niet al in de eerste groep zit.
            //als het wel al in de eerste groep zitten, dan horen url1 en url2 kennelijk bij elkaar. Dus dan
            //komt elk woord dat url2 heeft ook in groep 1.

        //dan door naar volgende url (3e), met dezelfde logica als hierboven. Als groep 2 nog niet bestaat dan
        //maak je groep 2, anders groep 3. etc.

        //zo door tot de laatste url. Als maar 1 woord de url bevat, dan geen groep maken
    




    public void updateGroups() {

        //get map met buzzwoorden en lijst met links
        Map<String, List<String>> buzzwordsAndLinksFromDb = retrieveBuzzwordsAndLinksFromDb();


        //per buzzword, ga na welke andere buzzwords related zijn


        //maak hier een map van, je hebt dus map met buzzwords als key en related words als values
        Map<String, List<String>> buzzwordsAndRelatedWords = getBuzzWordsAndRelatedWords(buzzwordsAndLinksFromDb);


        //voeg alle woorden (key en values) uit deze map samen in een lijst
        List<String> allWordsTogether = makeListOfAllWordsInMap(buzzwordsAndRelatedWords);


        //maak groepen op basis van hoeveelheid voorkomen van ieder woord (in map)
        Map<Integer, List<String>> buzzGroups = makeBuzzGroups(allWordsTogether, buzzwordsAndRelatedWords);


        //update database adhv deze map
        //updateDatabaseWithBuzzGroups(buzzGroups);


        //aap - noot, mies, steen, kerk
        //kerk - aap, steen, mies

        //stel je hebt 10 entries, als er een woord is dat 10 keer voorkomt dan is het een grote groep,
        //hoort alles bij elkaar.
        //--als er een woord is dat 4 keer voorkomt dan horen die 4 woorden bij elkaar als groep
        //---als er een woord is dat 3 keer voorkomt dan horen die 3 woorden bij elkaar als groep, MITS
        //---deze 3 woorden niet ook al gezamelijk in een grotere groep zitten



        //ga per entry van deze map na hoeveel



    }

    private Map<String, List<String>> retrieveBuzzwordsAndLinksFromDb() {
        Map<String, List<String>> theMap = new HashMap<>();

        String url1 = "www.nu.nl";
        String url2 = "www.efteling.nl";
        String url3 = "www.cda.nl";
        String url4 = "www.sgp.nl";
        String url5 = "www.nos.nl";
        String url6 = "www.telegraaf.nl";
        String url7 = "www.vvd.nl";
        String url8 = "www.vi.nl";
        String url9 = "www.marktplaats.nl";
        String url10 = "www.pvda.nl";

        List<String> aapList = new ArrayList<>();
        List<String> nootList = new ArrayList<>();
        List<String> miesList = new ArrayList<>();
        List<String> kaasList = new ArrayList<>();

        aapList.add(url1);
        aapList.add(url2);
        aapList.add(url3);
        aapList.add(url4);

        nootList.add(url3);
        nootList.add(url10);
        nootList.add(url9);

        miesList.add(url3);
        miesList.add(url9);
        miesList.add(url5);

        kaasList.add(url6);
        kaasList.add(url7);
        kaasList.add(url8);
        kaasList.add(url1);

        theMap.put("aap", aapList);
        theMap.put("noot", nootList);
        theMap.put("mies", miesList);
        theMap.put("kaas", kaasList);

        return theMap;
    }

    private Map<String, List<String>> getBuzzWordsAndRelatedWords(Map<String, List<String>> buzzWordsAndLinksFromDb) {
        Map<String, List<String>> buzzWordsAndLinksFromDbCopy = getCopyOfMapString(buzzWordsAndLinksFromDb);
        Map<String, List<String>> buzzWordsAndRelatedWords = new HashMap<>();

        for (Map.Entry<String, List<String>> entry : buzzWordsAndLinksFromDb.entrySet()) {
            buzzWordsAndRelatedWords.put(entry.getKey(), getRelatedBuzzWords(entry.getKey(), buzzWordsAndLinksFromDbCopy));
        }

        return buzzWordsAndRelatedWords;
    }

    private List<String> getRelatedBuzzWords(String word, Map<String, List<String>> buzzWordsAndLinksFromDb) {
        List<String> relatedBuzzwords = new ArrayList<>();
        List<String> linksFromBuzzword = buzzWordsAndLinksFromDb.get(word);

        for (Map.Entry<String, List<String>> entry : buzzWordsAndLinksFromDb.entrySet()) {
            if(!entry.getKey().equals(word)) {
                List<String> linksFromBuzzwordCopy = new ArrayList<>();
                linksFromBuzzwordCopy.addAll(linksFromBuzzword);

                List<String> linksFromWordToAnalyse = new ArrayList<>();
                linksFromWordToAnalyse.addAll(entry.getValue());

                linksFromBuzzwordCopy.retainAll(linksFromWordToAnalyse);

                if(!linksFromBuzzwordCopy.isEmpty()) {
                    relatedBuzzwords.add(entry.getKey());
                }
            }
        }
        return relatedBuzzwords;
    }

    private List<String> makeListOfAllWordsInMap(Map<String, List<String>> buzzwordsAndRelatedWords) {
        List<String> listOfAllWordsInMap = new ArrayList<>();

        for (Map.Entry<String, List<String>> entry : buzzwordsAndRelatedWords.entrySet()) {
            listOfAllWordsInMap.add(entry.getKey());
            listOfAllWordsInMap.addAll(entry.getValue());
        }

        return listOfAllWordsInMap;
    }

    private Set<String> convertListToSet(List<String> list) {
        Set<String> set = new HashSet<>();
        set.addAll(list);
        return set;
    }

    private List<String> convertSetToList(Set<String> set) {
        List<String> list = new ArrayList<>();
        list.addAll(set);
        return list;
    }

    private Map<Integer, List<String>> makeBuzzGroups(List<String> allWordsTogether, Map<String, List<String>> buzzwordsAndRelatedWords) {
        Map<Integer, List<String>> buzzGroupsToReturn = new HashMap<>();
        Map<Integer, List<String>> buzzGroupsInital = new HashMap<>();
        int buzzCounterInitial = 1;

        Set<String> listAsSet = convertListToSet(allWordsTogether);
        allWordsTogether = convertSetToList(listAsSet);

        for(String word : allWordsTogether) {
            List<String> allRelatedWords = getListOfBuzzWordsThatHaveDesignatedWordAsRelatedWord(word, buzzwordsAndRelatedWords);
            allRelatedWords.add(word);
            buzzGroupsInital.put(buzzCounterInitial, allRelatedWords);
            buzzCounterInitial++;
        }

        buzzGroupsInital = retainOnlyBiggestGroupEntries(buzzGroupsInital);
        buzzGroupsInital = removeGroupsThatAreSize1(buzzGroupsInital);
        buzzGroupsInital = removeGroupsOfIdenticalWords(buzzGroupsInital);
        buzzGroupsInital = sortMapByListSizeFromBigToSmall(buzzGroupsInital);

        int buzzCounter = 1;
        for (Map.Entry<Integer, List<String>> entry : buzzGroupsInital.entrySet()) {
            buzzGroupsToReturn.put(buzzCounter, entry.getValue());
            buzzCounter++;
        }

        return buzzGroupsToReturn;
    }

    private List<String> getListOfBuzzWordsThatHaveDesignatedWordAsRelatedWord(String word, Map<String, List<String>> buzzwordsAndRelatedWords) {
        List<String> buzzWordsThatHaveDesignatedWordAsRelatedWord = new ArrayList<>();

        for (Map.Entry<String, List<String>> entry : buzzwordsAndRelatedWords.entrySet()) {
            if(!entry.getKey().equals(word)) {
                for (String relatedWord : entry.getValue()) {
                    if(word.equals(relatedWord)) {
                        buzzWordsThatHaveDesignatedWordAsRelatedWord.add(entry.getKey());
                        break;
                    }

                }
            }
        }
        return buzzWordsThatHaveDesignatedWordAsRelatedWord;
    }

    private Map<Integer, List<String>> retainOnlyBiggestGroupEntries(Map<Integer, List<String>> buzzGroupsInital) {
        Map<Integer, List<String>> clearedMap = getCopyOfMapInteger(buzzGroupsInital);
        List<Integer> entriesThatMustBeRemoved = new ArrayList<>();

        for (Map.Entry<Integer, List<String>> entry : buzzGroupsInital.entrySet()) {
            List<String> wordListOfEntry = entry.getValue();

            for (Map.Entry<Integer, List<String>> entry2 : buzzGroupsInital.entrySet()) {
                if(!entry.getKey().equals(entry2.getKey())) {
                    if(entry2.getValue().containsAll(wordListOfEntry) && entry2.getValue().size() > wordListOfEntry.size()) {
                        entriesThatMustBeRemoved.add(entry.getKey());
                        break;
                    }
                }
            }
        }

        for(int i : entriesThatMustBeRemoved) {
            clearedMap.remove(i);
        }
        return clearedMap;
    }

    private Map<Integer, List<String>> removeGroupsThatAreSize1(Map<Integer, List<String>> buzzGroupsInital) {
        Map<Integer, List<String>> clearedMap = getCopyOfMapInteger(buzzGroupsInital);
        List<Integer> entriesThatMustBeRemoved = new ArrayList<>();

        for (Map.Entry<Integer, List<String>> entry : buzzGroupsInital.entrySet()) {
            if(entry.getValue().size() == 1) {
                entriesThatMustBeRemoved.add(entry.getKey());
            }
        }

        for(int i : entriesThatMustBeRemoved) {
            clearedMap.remove(i);
        }
        return clearedMap;
    }

    private Map<Integer, List<String>> removeGroupsOfIdenticalWords(Map<Integer, List<String>> buzzGroupsInital) {
        Map<Integer, List<String>> mapToReturn = new HashMap<>();
        Set<List<String>> setOfLists = new HashSet<>();

        for (Map.Entry<Integer, List<String>> entry : buzzGroupsInital.entrySet()) {
            List<String> listCopy = new ArrayList<>();
            listCopy.addAll(entry.getValue());
            Collections.sort(listCopy);
            setOfLists.add(listCopy);
        }

        List<List<String>> listOfLists = new ArrayList<>();
        listOfLists.addAll(setOfLists);

        int counter = 1;

        for(List<String> list : listOfLists) {
            mapToReturn.put(counter, list);
        }
        return mapToReturn;
    }

    private Map<Integer, List<String>> sortMapByListSizeFromBigToSmall(Map<Integer, List<String>> mapToSort) {
        Map<Integer, List<String>> mapToReturn = new HashMap<>();
        List<List<String>> listOfLists = new ArrayList<>();

        for (Map.Entry<Integer, List<String>> entry : mapToSort.entrySet()) {
            listOfLists.add(entry.getValue());
        }

        Collections.sort(listOfLists, new Comparator<List>(){
            public int compare(List a1, List a2) {
                return a2.size() - a1.size(); // assumes you want biggest to smallest
            }
        });

        int counter = 1;

        for(List<String> list : listOfLists) {
            mapToReturn.put(counter, list);
            counter++;
        }

        return mapToReturn;
    }

    private void updateDatabaseWithBuzzGroups(Map<Integer, List<String>> buzzGroups) {

    }

    private Map<String, List<String>> getCopyOfMapString(Map<String, List<String>> mapToCopy) {
        Map<String, List<String>> mapCopy = new HashMap<>();

        for (Map.Entry<String, List<String>> entry : mapToCopy.entrySet()) {
            String key = entry.getKey();
            List<String> listCopy = new ArrayList<>();
            listCopy.addAll(entry.getValue());

            mapCopy.put(key, listCopy);
        }
        return mapCopy;
    }

    private Map<Integer, List<String>> getCopyOfMapInteger(Map<Integer, List<String>> mapToCopy) {
        Map<Integer, List<String>> mapCopy = new HashMap<>();

        for (Map.Entry<Integer, List<String>> entry : mapToCopy.entrySet()) {
            Integer key = entry.getKey();
            List<String> listCopy = new ArrayList<>();
            listCopy.addAll(entry.getValue());

            mapCopy.put(key, listCopy);
        }
        return mapCopy;
    }












    //elk buzzword krijgt nieuw veld met related buzzwords

    //een groep is een groep woorden waarvan elk woord related is aan ieder ander woord in de groep



           //aap

    //noot         //mies



    //aap - noot, mies
    //noot - aap
    //mies - aap



    public void setMemberOfGroup(Map<String, Map<String, List<String>>> dataForAllNewBuzzwords) {

    }




    private Map<String, List<String>> getBuzzWordsAndLinksFromDb() {
        return null;
    }

    public List<String> getRelatedBuzzWords(String buzzWord) {

        return null;
    }

//    private Map<String, List<String>> getBuzzWordsAndLinksFromDataForAllBuzzwords(Map<String, Map<String, List<String>>>
//                                                                                          dataForAllNewBuzzwords) {
//        return null;
//    }


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
