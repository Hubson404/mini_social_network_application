package org.hubson404.miniSocialNetwork.clients;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hubson404.miniSocialNetwork.database.EntityDao;
import org.hubson404.miniSocialNetwork.database.ServiceUserDao;
import org.hubson404.miniSocialNetwork.model.Post;
import org.hubson404.miniSocialNetwork.model.ServiceUser;

import java.util.Optional;
import java.util.Scanner;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppSession {

    private ServiceUser loggedUser;
    private ServiceUser foundUser;
    private boolean isLoggedIn = false;

    private final EntityDao<Post> peDao = new EntityDao<>();
    private final ServiceUserDao suDao = new ServiceUserDao();

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
                    suDao.saveOrUpdate(SignInClient.createAccount(scanner));
                    break;
                default:
                    System.out.println("Command unknown.");
                    break;
            }
        } while (!isLoggedIn);

        do {
            System.out.println("Select command: \n1) CREATE POST\n2) FIND USER BY USER_NAME\n3) LOG OUT");
            command = scanner.nextLine();

            switch (command) {
                case "1":
                    Post post = PostingClient.writePost(scanner, loggedUser);
                    peDao.saveOrUpdate(post);
                    break;

                case "2":
                    System.out.println("Insert userName of searched user: ");
                    String userName;
                    Optional<ServiceUser> op;

                    userName = scanner.nextLine();
                        op = suDao.findByUserName(ServiceUser.class, userName);
                        op.ifPresent(this::setFoundUser);
                        System.out.println(foundUser);
                    break;

                case "3":
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

