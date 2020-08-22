package org.hubson404.miniSocialNetwork.clients;

import org.hubson404.miniSocialNetwork.daoClasses.EntityDao;
import org.hubson404.miniSocialNetwork.model.ServiceUser;

import java.util.*;

public class SignInClient {

    private static EntityDao<ServiceUser> serviceUserEntityDao = new EntityDao<>();

    public static ServiceUser createAccount(Scanner scanner) {

        String login = checkLoginAvailability(scanner);
        String password = setPassword(scanner);
        String accountName = checkAccountNameAvailability(scanner);
        boolean isPrivate = isAccountPrivate(scanner);
        String avatar = "( ＾◡＾)";

        ServiceUser serviceUser = new ServiceUser(login, password,
                accountName, accountName, avatar, isPrivate);

        return serviceUser;
    }

    private static boolean isAccountPrivate(Scanner scanner) {

        String command;
        do {
            System.out.println("Set account private? (Y/N): ");
            command = scanner.nextLine().toUpperCase();
        } while (!command.equals("Y")
                && !command.equals("N"));
        return command.equals("Y");
    }

    private static String checkAccountNameAvailability(Scanner scanner) {

        Set<String> accountNames = new HashSet<>();
        serviceUserEntityDao.findAll(ServiceUser.class).
                forEach(serviceUser -> accountNames.add(serviceUser.getAccountName()));

        System.out.println("Select your new account_name: ");
        String accountName = scanner.nextLine().toLowerCase();

        while (accountName.length() < 5 || accountName.length() > 20 || accountNames.contains(accountName)) {

            if (accountName.length() < 5) {
                System.out.println("Given accountName is too short (min.5 - max.20 characters).");
            } else if (accountName.length() > 20) {
                System.out.println("Given accountName is too long (min.5 - max.20 characters).");
            } else if (accountNames.contains(accountName)) {
                System.out.println("Given accountName is already taken.");
            }
            System.out.println(" Select different accountName: ");
            accountName = scanner.nextLine().toLowerCase();
        }
        return accountName;
    }

    private static String checkLoginAvailability(Scanner scanner) {

        Set<String> logins = new HashSet<>();
        serviceUserEntityDao.findAll(ServiceUser.class).
                forEach(serviceUser -> logins.add(serviceUser.getLogin()));

        System.out.println("Select your new login: ");
        String login = scanner.nextLine().toLowerCase();

        while (login.length() < 5 || login.length() > 20 || logins.contains(login)) {

            if (login.length() < 5) {
                System.out.println("Given login is too short (min.5 - max.20 characters).");
            } else if (login.length() > 20) {
                System.out.println("Given login is too long (min.5 - max.20 characters).");
            } else if (logins.contains(login)) {
                System.out.println("Given login is already taken.");
            }
            System.out.println(" Select different login: ");
            login = scanner.nextLine().toLowerCase();
        }
        return login;
    }

    private static String setPassword(Scanner scanner) {

        System.out.println("Select your new password: ");
        String password = scanner.nextLine();

        while (password.length() < 5 || password.length() > 20) {

            if (password.length() < 5) {
                System.out.println("Given password is too short (min.5 - max.20 characters).");
            } else if (password.length() > 20) {
                System.out.println("Given password is too long (min.5 - max.20 characters).");
            }
            System.out.println(" Select different password: ");
            password = scanner.nextLine().toLowerCase();
        }
        return password;
    }
}
