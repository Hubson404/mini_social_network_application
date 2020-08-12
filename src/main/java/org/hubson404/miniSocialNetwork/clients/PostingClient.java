package org.hubson404.miniSocialNetwork.clients;

import org.hubson404.miniSocialNetwork.model.Post;
import org.hubson404.miniSocialNetwork.model.ServiceUser;

import java.util.Scanner;

public class PostingClient {

    public static Post writePost(Scanner scanner, ServiceUser user) {

        String command;

        System.out.println("Write your message: ");
        command = scanner.nextLine();

        Post post = new Post(command, user);

        System.out.println("Message posted:\n" + user.getAvatar() + "\n<"
                + user.getAccountName() + "> : \"" + command + "\"");

        return post;
    }

}
