package org.hubson404.miniSocialNetwork;


import org.hubson404.miniSocialNetwork.database.EntityDao;
import org.hubson404.miniSocialNetwork.database.HibernateUtil;
import org.hubson404.miniSocialNetwork.model.Post;
import org.hubson404.miniSocialNetwork.model.ServiceUser;

public class Main {
    public static void main(String[] args) {

        ServiceUser user1 = new ServiceUser("user1","user1",
                "UserAcc1","UserName1","SomeAvatar",false);
        ServiceUser user2 = new ServiceUser("user2","user2",
                "UserAcc2","UserName2","SomeAvatar",false);
        ServiceUser user3 = new ServiceUser("user3","user3",
                "UserAcc3","UserName3","SomeAvatar",false);

        EntityDao<ServiceUser> serviceUserEntityDao = new EntityDao<>();
        EntityDao<Post> postEntityDao = new EntityDao<>();

        serviceUserEntityDao.saveOrUpdate(user1);
        serviceUserEntityDao.saveOrUpdate(user2);
        serviceUserEntityDao.saveOrUpdate(user3);

        Post post1 = new Post("blah, blah", user1);
        Post post2 = new Post("Some message", user2);
        Post post3 = new Post("", user1);

        postEntityDao.saveOrUpdate(post1);
        postEntityDao.saveOrUpdate(post2);
        postEntityDao.saveOrUpdate(post3);

        serviceUserEntityDao.findAll(ServiceUser.class).forEach(System.out::println);

        System.out.println("+++++++++++++++++++++++++++++++++");

        postEntityDao.findAll(Post.class).forEach(System.out::println);

    }
}
