package org.hubson404.miniSocialNetwork.clients;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hubson404.miniSocialNetwork.database.daoClasses.*;
import org.hubson404.miniSocialNetwork.model.*;
import org.hubson404.miniSocialNetwork.model.utils.*;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppSession {

    private ServiceUser loggedUser;
    private ServiceUser foundUser;
    private Post foundPost;

    private boolean isLoggedIn = false;
    private static final Scanner scanner = new Scanner(System.in);

    private final PostDao pD = new PostDao();
    private final EntityDao<FollowInstance> fiD = new EntityDao<>();
    private final EntityDao<LikeBadge> lbD = new EntityDao<>();
    private final ServiceUserDao suD = new ServiceUserDao();

    public void openSession(Scanner scanner) {
        String greeting = "Welcome to the MiniSocialNetwork.";
        String spacer = "=================================";
        System.out.println(spacer + "\n" + greeting + "\n" + spacer);
        String command;

        do {
            loggingIn();
        } while (!isLoggedIn);

        do {
            System.out.println("Select command: " +
                    "\n1) CREATE POST" +
                    "\n2) FIND USER BY USER_NAME" +
                    "\n3) FIND POST BY POST_ID" +
                    "\n4) FIND POSTS BY TAG_NAME" +
                    "\n5) ACCOUNT SETTINGS" +
                    "\n6) LOG OUT");
            command = scanner.nextLine();

            switch (command) {
                case "1":
                    pD.writePost(scanner, loggedUser, PostType.ORIGINAL);
                    break;

                case "2":
                    userInteraction();
                    break;

                case "3":
                    postInteraction();
                    break;

                case "4":
                    System.out.println("Insert searched tagName: ");
                    String tagName = scanner.nextLine();
                    Optional<List<Post>> postByTag = pD.findPostByTag(tagName);
                    postByTag.ifPresent(posts -> posts.forEach(pD::showPost));
                    break;

                case "5":
                    setFoundUser(loggedUser);

                    do {
                        accountSettings();
                    }
                    while (foundUser != null);
                    break;

                case "6":
                    System.out.println("Logging out. See you soon!");
                    loggedUser = null;
                    isLoggedIn = false;
                    break;

                default:
                    System.out.println("Command unknown.");
                    break;
            }
        } while (isLoggedIn);
    }

    private void loggingIn() {
        System.out.println("Select command: \n1) LOG IN\n2) CREATE ACCOUNT");
        String command = scanner.nextLine();

        switch (command) {
            case "1":
                Optional<ServiceUser> op;
                do {
                    op = suD.logIn(scanner);
                    op.ifPresent(this::setLoggedUser);
                } while (op.isEmpty());

                System.out.println("Welcome <" + loggedUser.getUserName() + ">");
                setLoggedIn(true);
                break;

            case "2":
                suD.saveOrUpdate(SignInClient.createAccount(scanner));
                break;
            default:
                System.out.println("Command unknown.");
                break;
        }
    }

    private void userInteraction() {

        System.out.println("Insert userName of searched user: ");
        String userName;
        Optional<ServiceUser> op;

        userName = scanner.nextLine();
        op = suD.findByUserName(ServiceUser.class, userName);
        op.ifPresent(this::setFoundUser);

        while (foundUser != null && op.isPresent()) {
            suD.showUserPage(foundUser, loggedUser);
            System.out.println("Select command: " +
                    "\n1) SHOW ALL POSTS" +
                    "\n2) FOLLOW / UNFOLLOW USER" +
                    "\n3) SHOW FOLLOWERS" +
                    "\n4) SHOW FOLLOWED USERS" +
                    "\n5) GO BACK");
            String command = scanner.nextLine();
            switch (command) {
                case "1":
                    suD.getLatestPosts(foundUser).forEach(pD::showPost);
                    break;
                case "2":
                    if (loggedUser.equals(foundUser)) {
                        System.err.println("You can't follow yourself.");

                    } else if (suD.isFollowedByUser(loggedUser, foundUser)) {
                        suD.unFollowUser(loggedUser, foundUser);
                        System.out.println("<" + foundUser.getUserName()
                                + "> unfollowed <" + foundUser.getUserName() + ">");
                    } else {
                        suD.followUser(loggedUser, foundUser);
                        System.out.println("<" + foundUser.getUserName() + "> is now followed");
                    }
                    break;
                case "3":
                    suD.showFollowers(foundUser);
                    break;
                case "4":
                    suD.showFollowedUsers(foundUser);
                    break;
                case "5":
                    setFoundUser(null);
                    break;

                default:
                    System.out.println("Command unknown.");
                    break;
            }
        }
    }

    private void postInteraction() {
        System.out.println("Insert postID of searched post: ");
        long postId;
        Optional<Post> opPost;

        postId = Long.parseLong(scanner.nextLine());

        opPost = pD.findById(Post.class, postId);
        opPost.ifPresent(this::setFoundPost);

        while (opPost.isPresent() && foundPost != null) {
            pD.showPost(foundPost);
            System.out.println("Select command: " +
                    "\n1) COMMENT POST" +
                    "\n2) LIKE / UN-LIKE POST" +
                    "\n3) FORWARD POST" +
                    "\n4) SHOW ALL COMMENTS" +
                    "\n5) GO BACK");

            String command = scanner.nextLine();
            switch (command) {
                case "1":
                    Optional<Post> optionalComment = pD.writePost(scanner, loggedUser, PostType.COMMENT);
                    if (optionalComment.isPresent()) {
                        Post comment = optionalComment.get();
                        pD.commentPost(comment, foundPost);
                    }
                    break;
                case "2":
                    if (pD.isLiked(loggedUser, foundPost)) {
                        pD.unLikePost(loggedUser, foundPost);
                        System.out.println("<Post(ID: " + foundPost.getPostId()
                                + ")> has beed unliked by <" + loggedUser.getUserName() + ">");
                    } else {
                        pD.likePost(loggedUser, foundPost);
                        System.out.println("<Post(ID:" + foundPost.getPostId()
                                + ")> is now liked by <" + loggedUser.getUserName() + ">");
                    }
                    break;
                case "3":
                    //todo: implement forward post
                    System.out.println("Forwarding post");
                    break;
                case "4":
                    pD.showAllComments(foundPost);
                    System.out.println();
                    System.out.println("############ END OF COMMENTS ############");
                    System.out.println();
                    break;
                case "5":
                    setFoundPost(null);
                    break;
                default:
                    System.out.println("Command unknown.");
                    break;
            }
        }
    }

    private void accountSettings() {

        System.out.println("Select command: " +
                "\n1) CHANGE PASSWORD" +
                "\n2) CHANGE USER_NAME" +
                "\n3) CHANGE AVATAR" +
                "\n4) SET ACCOUNT TO NON-PRIVATE / PRIVATE" +
                "\n5) GO BACK");

        String command = scanner.nextLine();
        switch (command) {
            case "1":
                System.err.println("Insert OLD password:");
                String oldPassword = scanner.nextLine();

                if (foundUser.getPassword().equals(oldPassword)) {
                    boolean passwordMatch;
                    do {
                        System.err.println("Insert NEW password:");
                        String newPassword1 = scanner.nextLine();
                        System.err.println("Repeat NEW password:");
                        String newPassword2 = scanner.nextLine();
                        passwordMatch = (newPassword1.equals(newPassword2)
                                && !newPassword1.equals(oldPassword));
                        if (passwordMatch) {
                            foundUser.setPassword(newPassword1);
                            System.err.println("Password successfully changed.");
                            suD.saveOrUpdate(foundUser);
                        } else if (newPassword1.equals(oldPassword)) {
                            System.err.println("New password can't be the same as old password");
                        } else {
                            System.err.println("Passwords don't match");
                        }
                    } while (!passwordMatch);

                } else {
                    System.err.println("Wrong password.");
                }

                break;
            case "2":
                System.err.println("Insert NEW userName:");
                String newUserName = scanner.nextLine();

                while (newUserName.length() < 5 || newUserName.length() > 20) {

                    if (newUserName.length() < 5) {
                        System.out.println("Given userName is too short (min.5 - max.20 characters).");
                    } else {
                        System.out.println("Given userName is too long (min.5 - max.20 characters).");
                    }
                    System.out.println(" Select different userName: ");
                    newUserName = scanner.nextLine().toLowerCase();
                }
                foundUser.setUserName(newUserName);
                suD.saveOrUpdate(foundUser);
                System.err.println("userName successfully changeg to: <" + foundUser.getUserName() + ">");
                break;
            case "3":
                System.err.println("Select NEW avatar:");
                String avatar = scanner.nextLine();

                while (avatar.length() < 5 || avatar.length() > 20) {

                    if (avatar.length() < 5) {
                        System.out.println("Given avatar is too small (min.5 - max.20 characters).");
                    } else {
                        System.out.println("Given avatar is too big (min.5 - max.20 characters).");
                    }
                    System.out.println(" Select different avatar: ");
                    avatar = scanner.nextLine().toLowerCase();
                }
                foundUser.setAvatar(avatar);
                suD.saveOrUpdate(foundUser);
                System.err.println("Avatar successfully changeg to: <" + foundUser.getAvatar() + ">");

                break;
            case "4":
                if (!foundUser.isPrivateAccount()) {
                    foundUser.setPrivateAccount(true);
                    System.err.println("Account set to PRIVATE");
                } else {
                    foundUser.setPrivateAccount(false);
                    System.err.println("Account set to NON-PRIVATE");
                }
                suD.saveOrUpdate(foundUser);
                break;
            case "5":
                setFoundUser(null);
                break;
            default:
                System.out.println("Command unknown.");
                break;
        }
    }
}





