package com.lennart.model.headlinesBuzzDb;

import com.lennart.model.headlinesFE.BuzzWord;

import java.util.*;

/**
 * Created by LennartMac on 27/09/2017.
 */
public class RelatedImageIdentifier {

    public List<BuzzWord> removeDoubleImagesFromBuzzWordList(List<BuzzWord> initialList) {
        List<BuzzWord> cleanedList = new ArrayList<>();

        Map<BuzzWord, String> buzzWordLinkMap = new LinkedHashMap<>();

        for(BuzzWord buzzWordNotYetInMap : initialList) {
            if(!buzzWordLinkMap.values().contains(buzzWordNotYetInMap.getImageLink())) {
                buzzWordLinkMap.put(buzzWordNotYetInMap, buzzWordNotYetInMap.getImageLink());
            } else {
                BuzzWord buzzWordAlreadyInMap = getKeyByValue(buzzWordLinkMap, buzzWordNotYetInMap.getImageLink());

                List<String> newHeadlines = buzzWordNotYetInMap.getHeadlines();
                List<String> newLinks = buzzWordNotYetInMap.getLinks();
                List<String> newSites = buzzWordNotYetInMap.getSites();
                List<String> linksAlreadyInMap = buzzWordAlreadyInMap.getLinks();




                if(buzzWordNotYetInMap.getEntry() > buzzWordAlreadyInMap.getEntry()) {

                    for(int i = 0; i < newLinks.size(); i++) {
                        if(!linksAlreadyInMap.contains(newLinks.get(i))) {
                            buzzWordAlreadyInMap.getLinks().add(0, newLinks.get(i));
                            buzzWordAlreadyInMap.getHeadlines().add(0, newHeadlines.get(i));
                            buzzWordAlreadyInMap.getSites().add(0, newSites.get(i));
                        }
                    }

//                    buzzWordAlreadyInMap.getHeadlines().addAll(0, buzzWordNotYetInMap.getHeadlines());
//                    buzzWordAlreadyInMap.getLinks().addAll(0, buzzWordNotYetInMap.getLinks());
//
//                    Set<String> headlinesAsSet = new LinkedHashSet<>();
//                    Set<String> linksAsSet = new LinkedHashSet<>();
//
//                    headlinesAsSet.addAll(buzzWordAlreadyInMap.getHeadlines());
//                    linksAsSet.addAll(buzzWordAlreadyInMap.getLinks());
//
//                    buzzWordAlreadyInMap.setHeadlines(new ArrayList<>());
//                    buzzWordAlreadyInMap.setLinks(new ArrayList<>());
//
//                    buzzWordAlreadyInMap.getHeadlines().addAll(headlinesAsSet);
//                    buzzWordAlreadyInMap.getLinks().addAll(linksAsSet);
                } else {

                    for(int i = 0; i < newLinks.size(); i++) {
                        if(!linksAlreadyInMap.contains(newLinks.get(i))) {
                            buzzWordAlreadyInMap.getLinks().add(newLinks.get(i));
                            buzzWordAlreadyInMap.getHeadlines().add(newHeadlines.get(i));
                            buzzWordAlreadyInMap.getSites().add(newSites.get(i));
                        }
                    }


//                    buzzWordAlreadyInMap.getHeadlines().addAll(buzzWordNotYetInMap.getHeadlines());
//                    buzzWordAlreadyInMap.getLinks().addAll(buzzWordNotYetInMap.getLinks());
//
//                    Set<String> headlinesAsSet = new LinkedHashSet<>();
//                    Set<String> linksAsSet = new LinkedHashSet<>();
//
//                    headlinesAsSet.addAll(buzzWordAlreadyInMap.getHeadlines());
//                    linksAsSet.addAll(buzzWordAlreadyInMap.getLinks());
//
//                    buzzWordAlreadyInMap.setHeadlines(new ArrayList<>());
//                    buzzWordAlreadyInMap.setLinks(new ArrayList<>());
//
//                    buzzWordAlreadyInMap.getHeadlines().addAll(headlinesAsSet);
//                    buzzWordAlreadyInMap.getLinks().addAll(linksAsSet);
                }
                buzzWordLinkMap.put(buzzWordAlreadyInMap, buzzWordAlreadyInMap.getImageLink());
            }
        }

        for (Map.Entry<BuzzWord, String> entry : buzzWordLinkMap.entrySet()) {
            cleanedList.add(entry.getKey());
        }
        return cleanedList;
    }

    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
}
