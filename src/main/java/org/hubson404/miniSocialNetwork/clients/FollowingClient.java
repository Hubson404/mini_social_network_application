package org.hubson404.miniSocialNetwork.clients;

import org.hubson404.miniSocialNetwork.database.EntityDao;
import org.hubson404.miniSocialNetwork.model.ServiceUser;

public class FollowingClient {


    public static void followUser(ServiceUser mainUser, ServiceUser followedUser, EntityDao<ServiceUser> serviceUserEntityDao) {

        mainUser.followUser(followedUser);
        serviceUserEntityDao.saveOrUpdate(mainUser);

        System.out.println("<"+mainUser.getAccountName() + "> is now following: <"
                + followedUser.getUserName()+">");
    }

}