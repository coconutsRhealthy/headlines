package com.lennart.model.headlinesBuzzDb;

import com.lennart.controller.Controller;
import com.lennart.model.headlinesBigDb.BigDbStorer;
import org.jsoup.nodes.Element;

import java.util.*;

/**
 * Created by LPO21630 on 19-6-2017.
 */
public class DataForAllBuzzWordsProvider {

    public Map<String, Map<String, List<String>>> getDataForAllBuzzWords(Map<String, Double> buzzWords, BigDbStorer bigDbStorer) throws Exception {
        Map<String, Map<String, List<String>>> dataForAllBuzzWords = new HashMap<>();

        for (Map.Entry<String, Double> entry : buzzWords.entrySet()) {
            Map<String, List<String>> dataForBuzzword = myNewOwnCompareLastNew(entry.getKey(), bigDbStorer);

            List<String> headLinesForWord = dataForBuzzword.get("correctedHeadlines");

            if(headLinesForWord.size() >= 3) {
                headLinesForWord = removeHeadlinesThatWerePresentInPreviousIteration(headLinesForWord);

                if(headLinesForWord.size() >= 3) {
                    dataForAllBuzzWords.put(entry.getKey(), dataForBuzzword);
                } else {
                    new StoreBuzzwords().storeBuzzwordsInDeclinedDb(entry.getKey() + "---" + headLinesForWord.get(0));
                }
            }
        }
        return dataForAllBuzzWords;
    }

    private List<String> removeHeadlinesThatWerePresentInPreviousIteration(List<String> headLinesForWord) throws Exception {
        List<String> headlinesThatShouldBeRemoved = new ArrayList<>();
        BigDbStorer bigDbStorer = new BigDbStorer();

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
        Map<String, Integer> wordsRankedByOccurenceTwoOrMore = getWordsRankedByOccurrence(correctedHeadlinesForWord, word);

        //dan verwijder je uit alle gegevens op basis van de woordanalyse de niet relevante entries
        dataTotalForWord = getRelevantEntries(dataTotalForWord, wordsRankedByOccurenceTwoOrMore);
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

    private Map<String, Integer> getWordsRankedByOccurrence(List<String> correctedHeadlinesForWord, String word) {
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
            if(entry.getValue() > 2) {
                wordsRankedByOccurenceTwoOrMore.put(entry.getKey(), entry.getValue());
            }
        }
        return wordsRankedByOccurenceTwoOrMore;
    }

    private List<String> removeBlackListWords(List<String> allWords) {
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
}
