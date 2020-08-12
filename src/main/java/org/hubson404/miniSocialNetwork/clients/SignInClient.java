package org.hubson404.miniSocialNetwork.clients;

import org.hubson404.miniSocialNetwork.database.EntityDao;
import org.hubson404.miniSocialNetwork.model.ServiceUser;

import java.util.*;

public class SignInClient {

    public static ServiceUser createAccount(Scanner scanner) {

        String login;
        String password;
        String accountName;
        String avatar = "( ＾◡＾)";
        boolean isPrivate;

        login = checkLoginAvailability(scanner);

        System.out.println("Insert new password: ");
        password = scanner.nextLine();

        System.out.println("Insert account name: ");
        accountName = checkAccountNameAvailability(scanner);

        System.out.println("Set account private? (Y/N): ");
        isPrivate = isAccountPrivate(scanner);

        ServiceUser serviceUser = new ServiceUser(login, password,
                accountName, accountName, avatar, isPrivate);

        return serviceUser;
    }

    private static boolean isAccountPrivate(Scanner scanner) {

        String command;

        do {
            command = scanner.nextLine().toUpperCase();
        } while (!command.equals("Y")
                && !command.equals("N"));

        return command.equals("Y");
    }

    private static String checkAccountNameAvailability(Scanner scanner) {

        EntityDao<ServiceUser> serviceUserEntityDao = new EntityDao<>();
        Set<String> accountNames = new HashSet<>();
        serviceUserEntityDao.findAll(ServiceUser.class).
                forEach(serviceUser -> accountNames.add(serviceUser.getAccountName()));

        String accountName;

        do {
            System.out.println("Input new accountName: ");
            accountName = scanner.nextLine();
        } while (accountNames.contains(accountName) || accountName.equals(""));

        return accountName;

    }

    private static String checkLoginAvailability(Scanner scanner) {

        EntityDao<ServiceUser> serviceUserEntityDao = new EntityDao<>();
        Set<String> logins = new HashSet<>();
        serviceUserEntityDao.findAll(ServiceUser.class).
                forEach(serviceUser -> logins.add(serviceUser.getLogin()));

        String login;

        do {
            System.out.println("Input new login: ");
            login = scanner.nextLine();
        } while (logins.contains(login) || login.equals(""));

        return login;
    }
}
