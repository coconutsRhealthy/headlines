package com.lennart.model.headlinesBuzzDb;

import com.lennart.controller.Controller;
import com.lennart.model.headlinesBigDb.BigDbStorer;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class JsoupElementsProcessor {

    public List<Element> getAllElementsPerWord(String word, BigDbStorer bigDbStorer) throws Exception {
        List<Document> allDocuments = bigDbStorer.getListOfAllDocuments();
        List<Element> elementsPerWord = new ArrayList<>();

        for(Document document : allDocuments) {
            if(document != null) {
                Elements elements = document.select("a:contains(" + word + ")");
                if(elements.size() != 0) {
                    elementsPerWord.add(elements.get(0));
                }
            }
        }
        return elementsPerWord;
    }

    public List<String> getHeadlinesPerWord(List<Element> elementsPerWord, String word) {
        List<String> headlinesPerWord = getUncorrectedHeadlinesPerWord(elementsPerWord);
        headlinesPerWord = trimHeadlinesToMax77Characters(headlinesPerWord);
        headlinesPerWord = removeWrongContainsHeadlines(headlinesPerWord, " " + word + " ");
        return headlinesPerWord;
    }

    public List<String> getRawHeadlinesPerWord(List<Element> elementsPerWord, String word) {
        List<String> headlinesPerWord = getUncorrectedHeadlinesPerWord(elementsPerWord);
        headlinesPerWord = trimHeadlinesToMax77Characters(headlinesPerWord);
        headlinesPerWord = removeWrongContainsHeadlinesForRaw(headlinesPerWord, " " + word + " ");
        return headlinesPerWord;
    }

    public List<String> getHrefHeadlinesPerWord(List<Element> elementsPerWord, String word) {
        List<String> headlinesPerWord = getUncorrectedHeadlinesPerWord(elementsPerWord);
        headlinesPerWord = trimHeadlinesToMax77Characters(headlinesPerWord);
        headlinesPerWord = removeWrongContainsHeadlinesForHref(headlinesPerWord, " " + word + " ", elementsPerWord);
        return headlinesPerWord;
    }

    private List<String> getUncorrectedHeadlinesPerWord(List<Element> elementsList) {
        List<String> headlines = new ArrayList<>();

        for(Element e : elementsList) {
            headlines.add(e.text());
        }

        return headlines;
    }

    private List<String> getInitialHrefsAll(List<Element> elementsList) {
        List<String> hrefs = new ArrayList<>();

        for(Element e : elementsList) {
            hrefs.add(e.attr("abs:href"));
        }
        return hrefs;
    }

    private List<String> removeWrongContainsHeadlinesForHref(List<String> headlines, String word, List<Element> elementsPerWord) {
        List<String> correctHeadlines = new ArrayList<>();
        List<String> correctHrefHeadlines = new ArrayList<>();

        List<String> allHrefs = getInitialHrefsAll(elementsPerWord);

        List<String> lowerCaseReplacedHeadlines = new ArrayList<>();

        for(String headline : headlines) {
            String replacedHeadline = headline.replaceAll("[^A-Za-z0-9 ]", "");
            replacedHeadline = replacedHeadline.toLowerCase();
            lowerCaseReplacedHeadlines.add(replacedHeadline);
        }

        for(int i = 0; i < lowerCaseReplacedHeadlines.size(); i++) {
            if(lowerCaseReplacedHeadlines.get(i).contains(word)) {
                correctHeadlines.add(lowerCaseReplacedHeadlines.get(i));
                correctHrefHeadlines.add(allHrefs.get(i));
            }
        }

        String wordRemoveTrailingSpace = word.replace(" ", "");
        wordRemoveTrailingSpace = " " + wordRemoveTrailingSpace;

        for(int i = 0; i < lowerCaseReplacedHeadlines.size(); i++) {
            String headline = lowerCaseReplacedHeadlines.get(i);
            int length = wordRemoveTrailingSpace.length();

            String lastPart = "";

            if(headline.length() > length) {
                lastPart = headline.substring(headline.length() - length, headline.length());
            }

            if(lastPart.equals(wordRemoveTrailingSpace)) {
                correctHeadlines.add(headline);
                correctHrefHeadlines.add(allHrefs.get(i));
            }
        }

        return correctHrefHeadlines;
    }

    private List<String> removeWrongContainsHeadlinesForRaw(List<String> headlines, String word) {
        List<String> correctHeadlines = new ArrayList<>();
        List<String> correctRawHeadlines = new ArrayList<>();

        List<String> originalHeadlinesCopy = new ArrayList<>();
        originalHeadlinesCopy.addAll(headlines);

        List<String> lowerCaseReplacedHeadlines = new ArrayList<>();

        for(String headline : headlines) {
            String replacedHeadline = headline.replaceAll("[^A-Za-z0-9 ]", "");
            replacedHeadline = replacedHeadline.toLowerCase();
            lowerCaseReplacedHeadlines.add(replacedHeadline);
        }

        for(int i = 0; i < lowerCaseReplacedHeadlines.size(); i++) {
            if(lowerCaseReplacedHeadlines.get(i).contains(word)) {
                correctHeadlines.add(lowerCaseReplacedHeadlines.get(i));
                correctRawHeadlines.add(originalHeadlinesCopy.get(i));
            }
        }

        String wordRemoveTrailingSpace = word.replace(" ", "");
        wordRemoveTrailingSpace = " " + wordRemoveTrailingSpace;

        for(int i = 0; i < lowerCaseReplacedHeadlines.size(); i++) {
            String headline = lowerCaseReplacedHeadlines.get(i);
            int length = wordRemoveTrailingSpace.length();

            String lastPart = "";

            if(headline.length() > length) {
                lastPart = headline.substring(headline.length() - length, headline.length());
            }

            if(lastPart.equals(wordRemoveTrailingSpace)) {
                correctHeadlines.add(headline);
                correctRawHeadlines.add(originalHeadlinesCopy.get(i));
            }
        }

        return correctRawHeadlines;
    }

    private List<String> removeWrongContainsHeadlines(List<String> headlines, String word) {
        List<String> correctHeadlines = new ArrayList<>();

        List<String> lowerCaseReplacedHeadlines = new ArrayList<>();

        for(String headline : headlines) {
            String replacedHeadline = headline.replaceAll("[^A-Za-z0-9 ]", "");
            replacedHeadline = replacedHeadline.toLowerCase();
            lowerCaseReplacedHeadlines.add(replacedHeadline);
        }

        for(String headline : lowerCaseReplacedHeadlines) {
            if(headline.contains(word)) {
                correctHeadlines.add(headline);
            }
        }

        String wordRemoveTrailingSpace = word.replace(" ", "");
        wordRemoveTrailingSpace = " " + wordRemoveTrailingSpace;
        for(String headline : lowerCaseReplacedHeadlines) {
            int length = wordRemoveTrailingSpace.length();

            String lastPart = "";

            if(headline.length() > length) {
                lastPart = headline.substring(headline.length() - length, headline.length());
            }

            if(lastPart.equals(wordRemoveTrailingSpace)) {
                correctHeadlines.add(headline);
            }
        }

        return correctHeadlines;
    }

    private List<String> trimHeadlinesToMax77Characters(List<String> headlines) {
        List<String> trimmedHeadlines = new ArrayList<>();

        for(String headline : headlines) {
            if(headline.length() >= 78) {
                String trimmedHeadline = headline.substring(0, 78);
                trimmedHeadlines.add(trimmedHeadline);
            } else {
                trimmedHeadlines.add(headline);
            }
        }
        return trimmedHeadlines;
    }

    public void getTextFromAllAElements(List<Document> allDocument) {
        List<Element> aElements = new ArrayList<>();
        List<String> aTexts = new ArrayList<>();

        for(Document document : allDocument) {
            if(document != null) {
                Elements elements = document.select("a");

                for(Element element : elements) {
                    aTexts.add(element.text());
                }
            }
        }

        String testA = "Rob Kardashian and Blac Chyna throw down (and dirty) on...";
        String testB = "Egyptian soldiers killed Sinai suicide attack";
        String testC = "Fighter jets escort plane back to Montreal due to 'unruly passenger' report";
        String testD = "Xi calls on BRICS members to promote common development";
        String testE = "Microsoft to lay off thousands in sales, marketing reshuffle";

        boolean a = false;
        boolean b = false;
        boolean c = false;
        boolean d = false;
        boolean e = false;

        for(String s : aTexts) {
            if(!a) {
                if(s.contains(testA)) {
                    a = true;
                }
            }
            if(!b) {
                if(s.contains(testB)) {
                    b = true;
                }
            }
            if(!c) {
                if(s.contains(testC)) {
                    c = true;
                }
            }
            if(!d) {
                if(s.contains(testD)) {
                    d = true;
                }
            }
            if(!e) {
                if(s.contains(testE)) {
                    e = true;
                }
            }
        }

        System.out.println(a);
        System.out.println(b);
        System.out.println(c);
        System.out.println(d);
        System.out.println(e);
    }
}

