package org.hubson404.miniSocialNetwork.clients;

import org.hubson404.miniSocialNetwork.database.ServiceUserDao;
import org.hubson404.miniSocialNetwork.model.Post;
import org.hubson404.miniSocialNetwork.model.ServiceUser;

import java.util.List;

public class InteractionClient {

    public static void showUserPage(ServiceUser searchedUser, ServiceUser loggedUser) {

        ServiceUserDao sud = new ServiceUserDao();
        String avatar = searchedUser.getAvatar();
        String userName = searchedUser.getUserName();
        String accountName = searchedUser.getAccountName();
        int numberOfPosts = searchedUser.getPosts().size();
        int numberOfFollowers = searchedUser.getFollowers().size();
        int numberOfFollowed = searchedUser.getFollowedUsers().size();
        List<Post> latestPosts = sud.getLatestPosts(searchedUser);
        String relation = "[FOLLOW THIS USER]";
        if (loggedUser.equals(searchedUser)) {
            relation = "[YOUR ACCOUNT]";
        } else if (loggedUser.isFollowed(searchedUser)) {
            relation = "[FOLLOWING]";
        }

        String nameLine = "\n<" + userName + "> (accoutName: " + accountName + ") " + relation;
        String secondLine = "\nposts: (" + numberOfPosts + "); "
                + "followers: (" + numberOfFollowers + "); "
                + "following: (" + numberOfFollowed + ");";
        String latestPost;

        if (!latestPosts.isEmpty()) {
            latestPost = "\nlatest post: \n"
                    + "\"" + latestPosts.get(0).getContent() + "\"";
        } else {
            latestPost = "\n<" + userName + "> has not posted yet.";
        }
        for (int i = 0; i < secondLine.length(); i++) {
            System.out.print("~");
        }

        System.out.println(
                "\n[ " + avatar + " ]"
                        + nameLine
                        + secondLine);

        for (int i = 0; i < secondLine.length(); i++) {
            System.out.print("-");
        }

        System.out.println(latestPost);

        for (int i = 0; i < secondLine.length(); i++) {
            System.out.print("~");
        }
        System.out.println();
    }
}
