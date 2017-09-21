package com.lennart.model.headlinesBuzzDb;

import com.lennart.model.headlinesBigDb.BigDbStorer;
import com.lennart.model.headlinesBigDb.headlinesBigDbEntertainment.BigDbStorerEntertainment;
import com.lennart.model.headlinesBigDb.headlinesBigDbFinance.BigDbStorerFinance;
import com.lennart.model.headlinesBigDb.headlinesBigDbSport.BigDbStorerSport;
import com.lennart.model.headlinesFE.BuzzWord;
import com.lennart.model.headlinesFE.RetrieveBuzzwords;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.*;

/**
 * Created by LPO21630 on 19-6-2017.
 */
public class DataForAllBuzzWordsProvider {

    public Map<String, Map<String, List<String>>> getDataForAllBuzzWords(Map<String, Double> buzzWords, BigDbStorer bigDbStorer) throws Exception {
        Map<String, Map<String, List<String>>> dataForAllBuzzWords = new HashMap<>();

        for (Map.Entry<String, Double> entry : buzzWords.entrySet()) {
            try {
                Map<String, List<String>> dataForBuzzword = myNewOwnCompareLastNew(entry.getKey(), bigDbStorer);

                List<String> headLinesForWord = new ArrayList<>();
                headLinesForWord.addAll(dataForBuzzword.get("rawHeadlines"));

                if(headLinesForWord.size() >= 3) {
                    headLinesForWord = removeHeadlinesThatWerePresentInPreviousIteration(headLinesForWord, bigDbStorer);
                    headLinesForWord = removeHeadlinesThatWereCoveredByBuzzWordOlderThan3Hours(headLinesForWord, bigDbStorer);

                    if(headLinesForWord.size() >= 3) {

                        if(headLinesForWord.size() < dataForBuzzword.get("rawHeadlines").size()) {
                            List<Integer> removedIndices = getIndicesThatHaveBeenRemovedFromList
                                    (dataForBuzzword.get("rawHeadlines"), headLinesForWord);

                            List<String> hrefsForWord = removeIndicesFromList(removedIndices, dataForBuzzword.get("hrefs"));
                            List<String> correctedHeadlinesForWord =
                                    removeIndicesFromList(removedIndices, dataForBuzzword.get("correctedHeadlines"));

                            dataForBuzzword.put("hrefs", hrefsForWord);
                            dataForBuzzword.put("correctedHeadlines", correctedHeadlinesForWord);
                        }

                        dataForBuzzword.put("rawHeadlines", headLinesForWord);

                        List<String> imageLinkList = getImageLinkForBuzzwordInList(entry.getKey(), dataForBuzzword.get("hrefs"));

                        if(imageLinkList != null && imageLinkList.size() == 1) {
                            dataForBuzzword.put("imageLink", imageLinkList);
                            dataForAllBuzzWords.put(entry.getKey(), dataForBuzzword);
                        }
                    }
                }
            } catch (Exception e) {

            }
        }
        return dataForAllBuzzWords;
    }

    private List<String> getImageLinkForBuzzwordInList(String buzzword, List<String> hrefs) throws Exception {
        List<String> asList = new ArrayList<>();

        String link = getImageLinkForBuzzword(buzzword, hrefs);

        if(link != null) {
            asList.add(getImageLinkForBuzzword(buzzword, hrefs));
        } else {
            asList = null;
        }
        return asList;
    }

    private String getImageLinkForBuzzword(String buzzword, List<String> hrefs) throws Exception {
        String linkToReturn;

        List<String> imageLinksContainingBuzzword = getImageLinksContainingBuzzword(buzzword, hrefs);
        Map<String, Integer> imageMapSortedBySize = getImgMapSortedBySize(imageLinksContainingBuzzword);

        if(imageMapSortedBySize.entrySet().iterator().hasNext()) {
            linkToReturn = imageMapSortedBySize.entrySet().iterator().next().getKey();
        } else {
            linkToReturn = null;
        }
        return linkToReturn;
    }

    private List<String> getImageLinksContainingBuzzword(String buzzword, List<String> hrefs) throws Exception {
        List<String> imageLinksContainingBuzzword = new ArrayList<>();

        for(String href : hrefs) {
            Document document = Jsoup.connect(href).get();
            Elements elements = document.select("img[src]");

            for (Element element : elements) {
                if (element.attr("abs:src").contains(buzzword)) {
                    imageLinksContainingBuzzword.add(element.attr("abs:src"));
                }
            }
        }
        return imageLinksContainingBuzzword;
    }

    private Map<String, Integer> getImgMapSortedBySize(List<String> images) {
        Map<String, Integer> imgSizeMap = new HashMap<>();

        for(String imageLink : images) {
            try {
                BufferedImage bimg = ImageIO.read(new URL(imageLink));

                if(bimg != null) {
                    int width          = bimg.getWidth();
                    int height         = bimg.getHeight();
                    int size = width * height;

                    if(size > 3000) {
                        imgSizeMap.put(imageLink, size);
                    }
                }
            } catch (Exception e) {

            }
        }
        imgSizeMap = sortByValueHighToLow(imgSizeMap);
        return imgSizeMap;
    }

    private List<Integer> getIndicesThatHaveBeenRemovedFromList(List<String> oldList, List<String> newList) {
        List<Integer> removedIndices = new ArrayList<>();

        for(int i = 0; i < oldList.size(); i++) {
            if(!newList.contains(oldList.get(i))) {
                removedIndices.add(i);
            }
        }
        return removedIndices;
    }

    private List<String> removeIndicesFromList(List<Integer> indicesToRemove, List<String> list) {
        for(int i : indicesToRemove) {
            list.remove(i);
        }
        return list;
    }

    private List<String> removeHeadlinesThatWerePresentInPreviousIteration(List<String> headLinesForWord, BigDbStorer bigDbStorer) throws Exception {
        List<String> headlinesThatShouldBeRemoved = new ArrayList<>();
        List<String> allOldATexts = bigDbStorer.retrieveAllOldAtexts();

        for(String headline : headLinesForWord) {
            for(String oldAText : allOldATexts) {
                if(oldAText.contains(headline)) {
                    headlinesThatShouldBeRemoved.add(headline);
                }
            }
        }
        headLinesForWord.removeAll(headlinesThatShouldBeRemoved);
        return headLinesForWord;
    }

    private List<String> removeHeadlinesThatWereCoveredByBuzzWordOlderThan3Hours(List<String> headLinesForWord, BigDbStorer bigDbStorer) throws Exception {
        List<String> headLinesForWordCopy = new ArrayList<>();
        headLinesForWordCopy.addAll(headLinesForWord);

        //get all buzzwords older than 3 hours
        String databaseTableToUse;
        if(bigDbStorer instanceof BigDbStorerFinance) {
            databaseTableToUse = "finance_buzzwords_new";
        } else if(bigDbStorer instanceof BigDbStorerSport) {
            databaseTableToUse = "sport_buzzwords_new";
        } else if(bigDbStorer instanceof BigDbStorerEntertainment) {
            databaseTableToUse = "entertainment_buzzwords_new";
        } else {
            databaseTableToUse = "buzzwords_new";
        }

        List<BuzzWord> buzzWordsOlderThan3Hours = new RetrieveBuzzwords().retrieveBuzzWordsFromDbUntillHour(databaseTableToUse, 3);

        List<String> olderHeadlinesList = new ArrayList<>();

        //check if your headline contains any of these buzzwords
        //for each buzzword that it contains, get all the headlines that belong to this buzzword
        //put all these headlines together in a list
        for(String headline : headLinesForWord) {
            for(BuzzWord buzzWord : buzzWordsOlderThan3Hours) {
                String word = buzzWord.getWord();

                if(headline.toLowerCase().contains(word.toLowerCase())) {
                    List<String> headlinesOfOlderBuzzWord = buzzWord.getHeadlines();

                    for(String olderHeadline : headlinesOfOlderBuzzWord) {
                        olderHeadlinesList.add(olderHeadline.toLowerCase());
                    }
                }
            }
        }

        List<String> headlinesToRemove = new ArrayList<>();

        if(!olderHeadlinesList.isEmpty()) {
            loop: for(String headline : headLinesForWord) {
                //maak een lijst van woorden van de headline
                List<String> currentHeadlineSplittedList = new ArrayList<>();
                currentHeadlineSplittedList.addAll(Arrays.asList(headline.toLowerCase().split(" ")));

                //verwijder uit deze lijst de blacklist woorden
                currentHeadlineSplittedList = removeBlackListWords(currentHeadlineSplittedList);

                //maak set
                Set<String> currentHeadlineSplittedListAsSet = new HashSet<>();
                currentHeadlineSplittedListAsSet.addAll(currentHeadlineSplittedList);

                for(String olderHeadline : olderHeadlinesList) {
                    List<String> olderHeadlineSplittedList = new ArrayList<>();
                    olderHeadlineSplittedList.addAll(Arrays.asList(olderHeadline.toLowerCase().split(" ")));

                    olderHeadlineSplittedList = removeBlackListWords(olderHeadlineSplittedList);

                    Set<String> olderHeadlineSplittedListAsSet = new HashSet<>();
                    olderHeadlineSplittedListAsSet.addAll(olderHeadlineSplittedList);

                    List<String> currentHeadlineBackToList = new ArrayList<>();
                    currentHeadlineBackToList.addAll(currentHeadlineSplittedListAsSet);

                    List<String> olderHeadlineBackToList = new ArrayList<>();
                    olderHeadlineBackToList.addAll(olderHeadlineSplittedListAsSet);

                    currentHeadlineBackToList.retainAll(olderHeadlineBackToList);

                    if(currentHeadlineBackToList.size() >= 4) {
                        headlinesToRemove.add(headline);
                        continue loop;
                    }
                }
            }
        }
        //if your headline contains in addition to the buzzword 3 other non blacklist words from any of these identified
        //headlines

        //your headline will be removed
        headLinesForWordCopy.removeAll(headlinesToRemove);

        return headLinesForWordCopy;
    }

    private Map<String, List<String>> myNewOwnCompareLastNew(String word, BigDbStorer bigDbStorer) throws Exception {
        //eerst verzamel je alle gegevens
        JsoupElementsProcessor jsoupElementsProcessor = new JsoupElementsProcessor();

        List<Element> elementsPerWord = jsoupElementsProcessor.getAllElementsPerWord(word, bigDbStorer);
        List<String> uncorrectedTrimmedHeadlines = jsoupElementsProcessor.getRawHeadlinesPerWord(elementsPerWord, word);
        List<String> hrefsForWord = jsoupElementsProcessor.getHrefHeadlinesPerWord(elementsPerWord, word);
        List<String> correctedHeadlinesForWord = jsoupElementsProcessor.getHeadlinesPerWord(elementsPerWord, word);

        Map<String, List<String>> dataTotalForWord = new HashMap<>();
        dataTotalForWord.put("correctedHeadlines", correctedHeadlinesForWord);
        dataTotalForWord.put("rawHeadlines", uncorrectedTrimmedHeadlines);
        dataTotalForWord.put("hrefs", hrefsForWord);

        //dan doe je de woord analyse
        Map<String, Integer> wordsRankedByOccurenceTwoOrMore = getWordsRankedByOccurrence(correctedHeadlinesForWord, word, 2);

        //dan verwijder je uit alle gegevens op basis van de woordanalyse de niet relevante entries
        dataTotalForWord = getRelevantEntries(dataTotalForWord, wordsRankedByOccurenceTwoOrMore);

        //tot slot verwijder je links die van dezelfde site afkomstig zijn
        dataTotalForWord = removeHeadlinesThatAreFromSameSite(dataTotalForWord);

        return dataTotalForWord;
    }

    private Map<String, List<String>> getRelevantEntries(Map<String, List<String>> dataTotalForWord, Map<String, Integer> wordsRankedByOccurenceTwoOrMore) {
        List<String> headlinesToRemove = new ArrayList<>();
        loop: for(String headline : dataTotalForWord.get("correctedHeadlines")) {
            for (Map.Entry<String, Integer> entry : wordsRankedByOccurenceTwoOrMore.entrySet()) {
                if(headline.contains(entry.getKey())) {
                    for (Map.Entry<String, Integer> entry2 : wordsRankedByOccurenceTwoOrMore.entrySet()) {
                        if(!entry.getKey().equals(entry2.getKey()) && headline.contains(entry2.getKey())) {
                            continue loop;
                        }
                    }
                }
            }
            headlinesToRemove.add(headline);
        }

        List<String> rawHeadlinesToRemove = new ArrayList<>();
        List<String> hrefsToRemove = new ArrayList<>();

        for(String headlineToRemove : headlinesToRemove) {
            for(int i = 0; i < dataTotalForWord.get("correctedHeadlines").size(); i++) {
                if(dataTotalForWord.get("correctedHeadlines").get(i).equals(headlineToRemove)) {
                    rawHeadlinesToRemove.add(dataTotalForWord.get("rawHeadlines").get(i));
                    hrefsToRemove.add(dataTotalForWord.get("hrefs").get(i));
                }
            }
        }

        dataTotalForWord.get("correctedHeadlines").removeAll(headlinesToRemove);
        dataTotalForWord.get("rawHeadlines").removeAll(rawHeadlinesToRemove);
        dataTotalForWord.get("hrefs").removeAll(hrefsToRemove);

        return dataTotalForWord;
    }

    public Map<String, Integer> getWordsRankedByOccurrence(List<String> correctedHeadlinesForWord, String word,
                                                           int amountRestriction) {
        List<Set<String>> wordSetsPerHeadline = new ArrayList<>();

        for(int i = 0; i < correctedHeadlinesForWord.size(); i++) {
            wordSetsPerHeadline.add(new HashSet<>());
            wordSetsPerHeadline.get(i).addAll(Arrays.asList(correctedHeadlinesForWord.get(i).split(" ")));
        }

        List<String> allWordsCombined = new ArrayList<>();

        for(Set<String> set : wordSetsPerHeadline) {
            allWordsCombined.addAll(set);
        }

        allWordsCombined = removeBlackListWords(allWordsCombined);
        allWordsCombined = removeTheKeyword(allWordsCombined, word);

        Map<String, Integer> wordsRankedByOccurenceAll = new HashMap<>();
        Map<String, Integer> wordsRankedByOccurenceTwoOrMore = new HashMap<>();

        for(String s : allWordsCombined) {
            if(wordsRankedByOccurenceAll.get(s) == null) {
                int frequency = Collections.frequency(allWordsCombined, s);
                wordsRankedByOccurenceAll.put(s, frequency);
            }
        }

        for (Map.Entry<String, Integer> entry : wordsRankedByOccurenceAll.entrySet()) {
            if(entry.getValue() > amountRestriction) {
                wordsRankedByOccurenceTwoOrMore.put(entry.getKey(), entry.getValue());
            }
        }
        return wordsRankedByOccurenceTwoOrMore;
    }

    private Map<String, List<String>> removeHeadlinesThatAreFromSameSite(Map<String, List<String>> dataTotalForWord) {
        List<String> hrefs = new ArrayList<>();
        hrefs.addAll(dataTotalForWord.get("hrefs"));

        List<String> correctedHeadlines = new ArrayList<>();
        correctedHeadlines.addAll(dataTotalForWord.get("correctedHeadlines"));

        List<String> rawHeadlines = new ArrayList<>();
        rawHeadlines.addAll(dataTotalForWord.get("rawHeadlines"));

        if(hrefs.size() == correctedHeadlines.size() && hrefs.size() == rawHeadlines.size()) {
            List<String> sites = new ArrayList<>();

            for(String href : hrefs) {
                if(href.contains("www.")) {
                    String site = href.split("\\.")[1];
                    sites.add(site);
                } else if(href.contains("feeds.reuters")) {
                    sites.add("reuters");
                } else {
                    String site = href.split("\\.")[0];
                    sites.add(site);
                }
            }

            Set<String> sitesAsSet = new HashSet<>();
            List<Integer> indicesToRemove = new ArrayList<>();

            for(int i = 0; i < sites.size(); i++) {
                if(!sitesAsSet.add(sites.get(i))) {
                    indicesToRemove.add(i);
                }
            }

            hrefs = removeIndicesFromStringList(hrefs, indicesToRemove);
            correctedHeadlines = removeIndicesFromStringList(correctedHeadlines, indicesToRemove);
            rawHeadlines = removeIndicesFromStringList(rawHeadlines, indicesToRemove);

            Map<String, List<String>> dataTotalForWordToReturn = new HashMap<>();

            dataTotalForWordToReturn.put("correctedHeadlines", correctedHeadlines);
            dataTotalForWordToReturn.put("rawHeadlines", rawHeadlines);
            dataTotalForWordToReturn.put("hrefs", hrefs);

            return dataTotalForWordToReturn;
        } else {
            return dataTotalForWord;
        }
    }

    private List<String> removeIndicesFromStringList(List<String> stringList, List<Integer> indices) {
        List<String> cleanedStringList = new ArrayList<>();

        for(int i = 0; i < stringList.size(); i++) {
            boolean notToBeAdded = false;

            for(Integer integer : indices) {
                if(i == integer) {
                    notToBeAdded = true;
                    break;
                }
            }

            if(!notToBeAdded) {
                cleanedStringList.add(stringList.get(i));
            }
        }
        return cleanedStringList;
    }

    public static List<String> removeBlackListWords(List<String> allWords) {
        List<String> blackListWords = new ArrayList<>();

        blackListWords.add("the");
        blackListWords.add("to");
        blackListWords.add("in");
        blackListWords.add("of");
        blackListWords.add("a");
        blackListWords.add("and");
        blackListWords.add("for");
        blackListWords.add("on");
        blackListWords.add("is");
        blackListWords.add("2017");
        blackListWords.add("by");
        blackListWords.add("at");
        blackListWords.add("us");
        blackListWords.add("as");
        blackListWords.add("from");
        blackListWords.add("after");
        blackListWords.add("are");
        blackListWords.add("it");
        blackListWords.add("that");
        blackListWords.add("this");
        blackListWords.add("be");
        blackListWords.add("you");
        blackListWords.add("an");
        blackListWords.add("his");
        blackListWords.add("will");
        blackListWords.add("has");
        blackListWords.add("was");
        blackListWords.add("have");
        blackListWords.add("your");
        blackListWords.add("how");
        blackListWords.add("who");
        blackListWords.add("not");
        blackListWords.add("but");
        blackListWords.add("its");
        blackListWords.add("what");
        blackListWords.add("he");
        blackListWords.add("their");
        blackListWords.add("man");
        blackListWords.add("her");
        blackListWords.add("get");
        blackListWords.add("no");
        blackListWords.add("our");
        blackListWords.add("new");
        blackListWords.add("more");
        blackListWords.add("with");
        blackListWords.add("news");
        blackListWords.add("ago");
        blackListWords.add("about");
        blackListWords.add("over");
        blackListWords.add("up");
        blackListWords.add("out");
        blackListWords.add("all");
        blackListWords.add("or");

        allWords.removeAll(blackListWords);

        return allWords;
    }

    private List<String> removeTheKeyword(List<String> allWords, String keyWord) {
        allWords.removeAll(Collections.singleton(keyWord));
        return allWords;
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
}
