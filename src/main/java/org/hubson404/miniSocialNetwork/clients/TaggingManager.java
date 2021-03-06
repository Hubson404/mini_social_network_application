package org.hubson404.miniSocialNetwork.clients;

import org.hubson404.miniSocialNetwork.database.daoClasses.TagDao;
import org.hubson404.miniSocialNetwork.model.Post;
import org.hubson404.miniSocialNetwork.model.Tag;

import java.util.*;
import java.util.stream.Collectors;

public class TaggingManager {

    public void manageTags(Post post) {

        TagDao tD = new TagDao();

        Map<String, Long> tagIdNameMap = tD.findAll(Tag.class)
                .stream().collect(Collectors.toMap(tag -> tag.getTagName().toLowerCase(), Tag::getTagId));

        for (String tag : lookupTags(post)) {

            Tag tagInstance;

            if (tagIdNameMap.containsKey(tag)) {
                tagInstance = tD.findById(Tag.class, tagIdNameMap.get(tag)).get();
            } else {
                tagInstance = new Tag(tag);
                tD.saveOrUpdate(tagInstance);
            }
            tD.addTag(tagInstance, post);
        }
    }

    private Set<String> lookupTags(Post post) {

        Set<String> includedTagsSet = getWords(post)
                .stream()
                .filter(word -> word.startsWith("#"))
                .filter(tag -> tag.length() > 1)
                .map(tag -> tag.substring(1))
                .collect(Collectors.toSet());

        return includedTagsSet;
    }

    private List<String> getWords(Post post) {

        String postContent = post.getContent();

        String[] loremIpsumArray = postContent
                .replace(',', ' ')
                .replace('.', ' ')
                .split("[\\s]");

        for (String s : loremIpsumArray) {
            s = s.trim();
        }

        List<String> wordList = Arrays.stream(loremIpsumArray)
                .filter(s -> s.length() > 0)
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        return wordList;
    }

}
