package org.hubson404.miniSocialNetwork.clients;

import org.hubson404.miniSocialNetwork.database.EntityDao;
import org.hubson404.miniSocialNetwork.model.Post;
import org.hubson404.miniSocialNetwork.model.ServiceUser;

import java.util.Scanner;

public class AppSession {

    private ServiceUser loggedUser;
    private boolean isLoggedIn = false;
    private String greeting = "Welcome to the MiniSocialNetwork.";
    private String spacer = "=================================";
    private EntityDao<Post> postEntityDao = new EntityDao<>();
    private EntityDao<ServiceUser> userEntityDao = new EntityDao<>();


    public void openSession(Scanner scanner) {
        System.out.println(spacer + "\n" + greeting + "\n" + spacer);
        LoggingInClient loggingInClient = new LoggingInClient();
        System.out.println("Select command: \n1) LOG IN\n2) CREATE ACCOUNT");

        String command = scanner.nextLine();

        switch (command) {
            case "1":
                loggedUser = loggingInClient.logIn(scanner).get();
                isLoggedIn = true;
                break;
            case "2":
                userEntityDao.saveOrUpdate(SignInClient.createAccount(scanner));
                loggedUser = loggingInClient.logIn(scanner).get();
                isLoggedIn = true;
                break;
            default:
                System.out.println("Command unknown.");
                break;
        }

        do {
            System.out.println("Select command: \n1) CREATE POST\n2) LOG OUT");
            command = scanner.nextLine();

            switch (command) {
                case "1":
                    Post post = PostingClient.writePost(scanner, loggedUser);
                    postEntityDao.saveOrUpdate(post);
                    break;
                case "2":
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
