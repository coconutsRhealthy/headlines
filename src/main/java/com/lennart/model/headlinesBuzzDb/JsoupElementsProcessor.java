package com.lennart.model.headlinesBuzzDb;

import com.lennart.model.headlinesBigDb.BigDbStorer;
import com.lennart.model.twitter.TweetMachine;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.*;

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
        headlinesPerWord = trimHeadlinesToMax150Characters(headlinesPerWord);
        headlinesPerWord = removeWrongContainsHeadlines(headlinesPerWord, " " + word + " ");
        return headlinesPerWord;
    }

    public List<String> getRawHeadlinesPerWord(List<Element> elementsPerWord, String word) {
        List<String> headlinesPerWord = getUncorrectedHeadlinesPerWord(elementsPerWord);
        headlinesPerWord = trimHeadlinesToMax150Characters(headlinesPerWord);
        headlinesPerWord = removeWrongContainsHeadlinesForRaw(headlinesPerWord, " " + word + " ");
        return headlinesPerWord;
    }

    public List<String> getHrefHeadlinesPerWord(List<Element> elementsPerWord, String word) {
        List<String> headlinesPerWord = getUncorrectedHeadlinesPerWord(elementsPerWord);
        headlinesPerWord = trimHeadlinesToMax150Characters(headlinesPerWord);
        headlinesPerWord = removeWrongContainsHeadlinesForHref(headlinesPerWord, " " + word + " ", elementsPerWord);
        return headlinesPerWord;
    }

    public List<String> replaceRawHeadlinesToH1ifPossible(Map<String, String> hrefAndRawHeadline) throws Exception {
        List<String> headlinesH1 = new ArrayList<>();

        for (Map.Entry<String, String> entry : hrefAndRawHeadline.entrySet()) {
            Document document = Jsoup.connect(entry.getKey()).get();
            Elements elements = document.select("h1");

            if(elements.size() == 1 && !elements.first().text().contains("Barchart, the leading provider")) {
                headlinesH1.add(elements.first().text());
            } else {
                headlinesH1.add(entry.getValue());
            }
        }
        return headlinesH1;
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

    private List<String> trimHeadlinesToMax150Characters(List<String> headlines) {
        List<String> trimmedHeadlines = new ArrayList<>();

        for(String headline : headlines) {
            if(headline.length() >= 151) {
                String trimmedHeadline = headline.substring(0, 151);
                //trimmedHeadline = removeLastHalfWordFromString(trimmedHeadline);
                trimmedHeadlines.add(trimmedHeadline);
            } else {
                trimmedHeadlines.add(headline);
            }
        }
        return trimmedHeadlines;
    }

    private String removeLastHalfWordFromString(String stringToChange) {
        if(stringToChange.length() == 78) {
            stringToChange = stringToChange.substring(0, stringToChange.lastIndexOf(" "));
            stringToChange = stringToChange + "...";
        }
        return stringToChange;
    }

    public List<String> getTextFromAllAElements(List<Document> allDocument) {
        List<String> aTexts = new ArrayList<>();

        for(Document document : allDocument) {
            if(document != null) {
                Elements elements = document.select("a");

                for(Element element : elements) {
                    aTexts.add(element.text());
                }
            }
        }
        return aTexts;
    }

    public List<String> getImageLinkForBuzzwordInList(List<String> hrefs, String buzzWord, List<String> headlines) throws Exception {
        List<String> asList = new ArrayList<>();

        String link = getImageLinkForBuzzword(hrefs, buzzWord, headlines);

        if(link != null) {
            asList.add(link);
        } else {
            asList = null;
        }
        return asList;
    }

    private String getImageLinkForBuzzword(List<String> hrefs, String buzzWord, List<String> headlines) throws Exception {
        String linkToReturn;

        List<String> imageLinksContainingBuzzword = getImageLinksContainingKeyWords(hrefs, buzzWord, headlines);
        Map<String, Integer> imageMapSortedBySize = getImgMapSortedBySize(imageLinksContainingBuzzword);

        if(imageMapSortedBySize.entrySet().iterator().hasNext()) {
            linkToReturn = imageMapSortedBySize.entrySet().iterator().next().getKey();
        } else {
            linkToReturn = null;
        }
        return linkToReturn;
    }

    private List<String> getImageLinksContainingKeyWords(List<String> hrefs, String buzzWord, List<String> headlines) throws Exception {
        List<String> imageLinksContainingBuzzword = new ArrayList<>();

        for(String href : hrefs) {
            Document document = Jsoup.connect(href).get();
            Elements elements = document.select("img[src]");

            for (Element element : elements) {
                String imageLink = element.attr("abs:src");

                if(imageLinkContainsKeyWords(imageLink, buzzWord, headlines)) {
                    imageLinksContainingBuzzword.add(element.attr("abs:src"));
                }
            }
        }
        return imageLinksContainingBuzzword;
    }

    private boolean imageLinkContainsKeyWords(String imageLink, String buzzWord, List<String> headlines) {
        boolean imageLinkContainsKeyWords = false;

        if(imageLink.contains(buzzWord)) {
            List<String> correctFormatHeadlines = new TweetMachine().convertHeadlinesToNonSpecialCharactersAndLowerCase(headlines);
            Map<String, Integer> wordsRankedByOccurenceTwoOrMore = new DataForAllBuzzWordsProvider().getWordsRankedByOccurrence(correctFormatHeadlines, buzzWord, 0);

            int counter = 0;

            for (Map.Entry<String, Integer> entry : wordsRankedByOccurenceTwoOrMore.entrySet()) {
                if(imageLink.contains(entry.getKey())) {
                    counter++;

                    if(counter >= 1) {
                        break;
                    }
                }
            }

            if(counter >= 1) {
                imageLinkContainsKeyWords = true;
            }
        }

        return imageLinkContainsKeyWords;
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

