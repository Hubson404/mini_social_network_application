package org.hubson404.miniSocialNetwork.clients;

import org.hubson404.miniSocialNetwork.database.EntityDao;
import org.hubson404.miniSocialNetwork.model.Post;
import org.hubson404.miniSocialNetwork.model.ServiceUser;

import java.util.*;
import java.util.stream.Collectors;

public class LoggingInClient {

    private ServiceUser user;
    private Scanner scanner = new Scanner(System.in);
    private EntityDao<ServiceUser> serviceUserEntityDao = new EntityDao<>();
    private EntityDao<Post> postEntityDao = new EntityDao<>();
    private Map<String, Long> loginIdMap = new HashMap<>();
    private boolean isLoggedIn = false;


    public boolean writePost() {

        String command;

        if (!isLoggedIn) {

            System.out.println("You must be logged in to write a post." +
                    "\nDo you want to log in now? (Y/N): ");
            do {
                command = scanner.nextLine().toUpperCase();

            } while (!command.equals("Y")
                    && !command.equals("N"));

            if (command.equals("Y")) {
                logIn();

            } else {
                System.out.println("Ok, see you soon.");
                return false;
            }
        }

        System.out.println("Write your message: ");
        command = scanner.nextLine();

        Post post = new Post(command, user);

        postEntityDao.saveOrUpdate(post);

        System.out.println("Message posted:\n" + user.getAvatar() + "\n<"
                + user.getAccountName() + "> : \"" + command + "\"");

        return true;
    }


    public void logIn() {

        loginIdMap = serviceUserEntityDao.findAll(ServiceUser.class)
                .stream().collect(Collectors.toMap(ServiceUser::getLogin, ServiceUser::getUserId));

        String login;
        String password;

        do {
            System.out.println("Input your login: ");
            login = scanner.nextLine();
        } while (!checkLogin(login));

        long userId = loginIdMap.get(login);

        do {
            System.out.println("Input your password: ");
            password = scanner.nextLine();
        } while (!checkPassword(userId, password));

        user = serviceUserEntityDao.findById(ServiceUser.class, userId).get();
        isLoggedIn = true;
    }

    private boolean checkPassword(long userId, String password) {

        String correctPassword = serviceUserEntityDao.findById(ServiceUser.class, (long) userId).get().getPassword();
        boolean isCorrect = correctPassword.equals(password);

        if (!isCorrect) {
            System.out.println("Wrong password.");
        }
        return isCorrect;
    }

    private boolean checkLogin(String login) {

        boolean isCorrect = loginIdMap.containsKey(login);
        if (!isCorrect) {
            System.out.println("Login doesn't exist.");
        }
        return isCorrect;
    }

    public void logOut() {
        user = null;
        isLoggedIn = false;
    }
}
