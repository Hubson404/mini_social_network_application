package org.hubson404.miniSocialNetwork.clients;

import org.hubson404.miniSocialNetwork.model.Post;
import org.hubson404.miniSocialNetwork.model.PostType;
import org.hubson404.miniSocialNetwork.model.ServiceUser;

import java.sql.SQLOutput;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class PostingClient {

    public static Post writePost(Scanner scanner, ServiceUser user) {

        String postContent;

        System.out.println("Write your message: ");
        postContent = scanner.nextLine();

        Post post = new Post(postContent, PostType.ORIGINAL, user);

        System.out.println("Message posted:\n" + user.getAvatar() + "\n<"
                + user.getAccountName() + "> : \"" + postContent + "\"");

        return post;
    }

    public static Post writeComment(Scanner scanner, ServiceUser user) {

        String postContent;

        System.out.println("Write your message: ");
        postContent = scanner.nextLine();

        Post post = new Post(postContent, PostType.COMMENT, user);

        System.out.println("Message posted:\n" + user.getAvatar() + "\n<"
                + user.getAccountName() + "> : \"" + postContent + "\"");

        return post;
    }

    public static void showPost(Post post) {

//        ServiceUser op = post.getOriginalPoster();
//        System.out.println(op.getAvatar() + " <" + op.getUserName() + "> :");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        System.out.println("{postID: " + post.getPostId()
                + "; postStatus: " + post.getPostType()
                + "; date: " + post.getCreateDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
//        if (post.getPostType() == PostType.COMMENT) {
//            System.out.println("{Comment to  post(postID: " + post.getMainPost().getMainPost().getPostId() + ")}");
//        }

        System.out.println("-----------------------------------");
        System.out.println("<p> " + post.getContent() + " </p>");
        System.out.println("-----------------------------------");

        System.out.println("{comments: (" + post.getComments().size()
                + ") ; likes: (" + post.getLikeBadges().size()
                + ") ;forwards: (" + post.getForwardBadges().size() + ")}");

        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }

}
