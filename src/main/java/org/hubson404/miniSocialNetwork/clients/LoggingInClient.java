package org.hubson404.miniSocialNetwork.clients;

import org.hubson404.miniSocialNetwork.database.EntityDao;
import org.hubson404.miniSocialNetwork.model.ServiceUser;

import java.util.*;
import java.util.stream.Collectors;

public class LoggingInClient {

    private EntityDao<ServiceUser> serviceUserEntityDao = new EntityDao<>();
    private Map<String, Long> loginIdMap = new HashMap<>();

    public Optional<ServiceUser> logIn(Scanner scanner) {

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

        Optional<ServiceUser> user = serviceUserEntityDao.findById(ServiceUser.class, userId);
        return user;
    }

    private boolean checkPassword(long userId, String password) {

        String correctPassword = serviceUserEntityDao.findById(ServiceUser.class, userId).get().getPassword();
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
}
