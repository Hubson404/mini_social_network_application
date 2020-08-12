package org.hubson404.miniSocialNetwork;

import org.hubson404.miniSocialNetwork.clients.AppSession;

import java.util.Scanner;

public class Application {

    public static void main(String[] args) {

        AppSession appSession = new AppSession();

        appSession.openSession(new Scanner(System.in));

    }
}
