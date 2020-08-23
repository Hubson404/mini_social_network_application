package org.hubson404.miniSocialNetwork.clients;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hubson404.miniSocialNetwork.database.daoClasses.*;
import org.hubson404.miniSocialNetwork.model.*;
import org.hubson404.miniSocialNetwork.model.LikeBadge;
import org.hubson404.miniSocialNetwork.model.Post;
import org.hubson404.miniSocialNetwork.model.ServiceUser;
import org.hubson404.miniSocialNetwork.model.utils.PostType;

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

    private final PostDao pD = new PostDao();
    private final EntityDao<FollowInstance> fiD = new EntityDao<>();
    private final EntityDao<LikeBadge> lbD = new EntityDao<>();
    private final ServiceUserDao suD = new ServiceUserDao();

    public void openSession(Scanner scanner) {
        String greeting = "Welcome to the MiniSocialNetwork.";
        String spacer = "=================================";

        System.out.println(spacer + "\n" + greeting + "\n" + spacer);
        LoggingInClient loggingInClient = new LoggingInClient();
        String command;

        do {
            System.out.println("Select command: \n1) LOG IN\n2) CREATE ACCOUNT");
            command = scanner.nextLine();
            switch (command) {
                case "1":
                    Optional<ServiceUser> op;
                    do {
                        op = loggingInClient.logIn(scanner);
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
        } while (!isLoggedIn);

        do {
            System.out.println("Select command: " +
                    "\n1) CREATE POST" +
                    "\n2) FIND USER BY USER_NAME" +
                    "\n3) FIND POST BY POST_ID" +
                    "\n4) FIND POSTS BY TAG_NAME" +
                    "\n5) LOG OUT");
            command = scanner.nextLine();

            switch (command) {
                case "1":
                    pD.writePost(scanner, loggedUser, PostType.ORIGINAL);
                    break;

                case "2":
                    System.out.println("Insert userName of searched user: ");
                    String userName;
                    Optional<ServiceUser> op;

                    userName = scanner.nextLine();
                    op = suD.findByUserName(ServiceUser.class, userName);
                    op.ifPresent(this::setFoundUser);

                    while (foundUser != null && op.isPresent()) {
                        InteractionClient.showUserPage(foundUser, loggedUser);
                        System.out.println("Select command: " +
                                "\n1) SHOW ALL POSTS" +
                                "\n2) FOLLOW / UNFOLLOW USER" +
                                "\n3) GO BACK");
                        command = scanner.nextLine();
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
                                setFoundUser(null);
                                break;
                            default:
                                System.out.println("Command unknown.");
                                break;
                        }
                    }
                    break;

                case "3":
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
                                "\n4) GO BACK");

                        command = scanner.nextLine();
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
                                setFoundPost(null);
                                break;
                            default:
                                System.out.println("Command unknown.");
                                break;
                        }
                    }
                    break;
                case "4":
                    System.out.println("Insert searched tagName: ");
                    String tagName = scanner.nextLine();
                    Optional<List<Post>> postByTag = pD.findPostByTag(tagName);
                    postByTag.ifPresent(posts -> posts.forEach(pD::showPost));
                    break;
                case "5":
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
}

