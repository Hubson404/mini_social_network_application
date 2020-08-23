package org.hubson404.miniSocialNetwork.database.daoClasses;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hubson404.miniSocialNetwork.database.HibernateUtil;
import org.hubson404.miniSocialNetwork.model.FollowInstance;
import org.hubson404.miniSocialNetwork.model.Post;
import org.hubson404.miniSocialNetwork.model.ServiceUser;
import org.hubson404.miniSocialNetwork.model.utils.UserNameSearchable;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ServiceUserDao {
    public void saveOrUpdate(ServiceUser serviceUser) {
        SessionFactory sessionFactory = HibernateUtil.getOurSessionFactory();
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.saveOrUpdate(serviceUser);

            transaction.commit();
        } catch (HibernateException he) {
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }

    public Optional<ServiceUser> findById(Class<ServiceUser> classType, Long id) {
        SessionFactory sessionFactory = HibernateUtil.getOurSessionFactory();
        try (Session session = sessionFactory.openSession()) {
            // istnieje prawdopodobieństwo, że rekord nie zostanie odnaleziony
            return Optional.ofNullable(session.get(classType, id));
        } catch (HibernateException he) {
            he.printStackTrace();
        }
        return Optional.empty();
    }

    public <T extends UserNameSearchable> Optional<T> findByUserName(Class<T> classType, String userName) {

        SessionFactory sessionFactory = HibernateUtil.getOurSessionFactory();
        try (Session session = sessionFactory.openSession()) {

            // narzędzie do tworzenia zapytań i kreowania klauzuli 'where'
            CriteriaBuilder cb = session.getCriteriaBuilder();

            // obiekt reprezentujący zapytanie
            CriteriaQuery<T> criteriaQuery = cb.createQuery(classType);

            // obiekt reprezentujący tabelę bazodanową.
            // do jakiej tabeli kierujemy nasze zapytanie?
            Root<T> rootTable = criteriaQuery.from(classType);

            // wykonanie zapytania
            criteriaQuery.select(rootTable)
                    .where(
                            // *userName*
                            // czy wartość kolumny 'userName' jest równa wartości zmiennej userName
                            cb.equal(rootTable.get("userName"), userName.toLowerCase())
                    );
            // istnieje prawdopodobieństwo, że rekord nie zostanie odnaleziony
            return Optional.ofNullable(session.createQuery(criteriaQuery).getSingleResult());
        } catch (HibernateException he) {
            he.printStackTrace();
        } catch (NoResultException e) {
            System.out.println("User <" + userName + "> not found.");
            return Optional.empty();
        }
        return Optional.empty();
    }

    public List<Post> getLatestPosts(ServiceUser serviceUser) {

        List<Post> list = new ArrayList<>();
        SessionFactory sessionFactory = HibernateUtil.getOurSessionFactory();

        try (Session session = sessionFactory.openSession()) {

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Post> cq = cb.createQuery(Post.class);
            Root<Post> root = cq.from(Post.class);
            cq.select(root)
                    .where(cb.equal(root.get("originalPoster"), serviceUser.getUserId()))
                    .orderBy(cb.desc(root.get("createDate")));

            list.addAll(session.createQuery(cq).list());
            session.close();
        } catch (HibernateException he) {
            he.printStackTrace();
        }
        return list;
    }

    public void delete(ServiceUser serviceUser) {
        SessionFactory sessionFactory = HibernateUtil.getOurSessionFactory();
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            // instrukcja która służy do usuwania z bazy danych
            session.delete(serviceUser);

            transaction.commit();
        } catch (HibernateException he) {
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }

    public List<ServiceUser> findAll(Class<ServiceUser> classType) {
        List<ServiceUser> list = new ArrayList<>();

        SessionFactory sessionFactory = HibernateUtil.getOurSessionFactory();
        try (Session session = sessionFactory.openSession()) {

            // narzędzie do tworzenia zapytań i kreowania klauzuli 'where'
            CriteriaBuilder cb = session.getCriteriaBuilder();

            // obiekt reprezentujący zapytanie
            CriteriaQuery<ServiceUser> criteriaQuery = cb.createQuery(classType);

            // obiekt reprezentujący tabelę bazodanową.
            // do jakiej tabeli kierujemy nasze zapytanie?
            Root<ServiceUser> rootTable = criteriaQuery.from(classType);

            // wykonanie zapytania
            criteriaQuery.select(rootTable);

            // specification
            list.addAll(session.createQuery(criteriaQuery).list());

            // poznanie uniwersalnego rozwiązania które działa z każdą bazą danych
            // używanie klas których będziecie używać na JPA (Spring)

        } catch (HibernateException he) {
            he.printStackTrace();
        }
        return list;
    }


    public void followUser(ServiceUser loggedUser, ServiceUser followedUser) {
        SessionFactory sessionFactory = HibernateUtil.getOurSessionFactory();
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            FollowInstance fi = new FollowInstance(followedUser, loggedUser);
            session.saveOrUpdate(fi);
            loggedUser.getFollowedUsers().add(fi);
            followedUser.getFollowers().add(fi);

            session.saveOrUpdate(loggedUser);
            session.saveOrUpdate(followedUser);

            transaction.commit();
        } catch (HibernateException he) {
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }

    public void unFollowUser(ServiceUser loggedUser, ServiceUser followedUser) {
        SessionFactory sessionFactory = HibernateUtil.getOurSessionFactory();
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            Optional<FollowInstance> op = loggedUser.getFollowedUsers()
                    .stream()
                    .filter(fi -> fi.getFollowedUser().equals(followedUser))
                    .findFirst();

            FollowInstance fiBeingRemoved = op.get();
            loggedUser.getFollowedUsers().remove(fiBeingRemoved);
            followedUser.getFollowers().remove(fiBeingRemoved);

            session.saveOrUpdate(loggedUser);
            session.saveOrUpdate(followedUser);

            transaction.commit();
        } catch (HibernateException he) {
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }

    public boolean isFollowedByUser(ServiceUser loggedUser, ServiceUser followedUser) {

        boolean isFollowed = followedUser.getFollowers()
                .stream()
                .map(FollowInstance::getMainUser)
                .anyMatch(user -> user.equals(loggedUser));
        return isFollowed;
    }

    public void showFollowedUsers(ServiceUser loggedUser) {

        Set<FollowInstance> followedUsers = loggedUser.getFollowedUsers();

        System.out.println("  -> Users followed by <" + loggedUser.getUserName() + ">: ");
        if (followedUsers.isEmpty()) {
            System.out.println("  -> <" + loggedUser.getUserName() + "> has not followed anyone yet.");
        }
        for (FollowInstance followedUser : followedUsers) {
            ServiceUser followedUserEntity = followedUser.getFollowedUser();
            System.out.println("{userName: " + followedUserEntity.getUserName()
                    + "; user ID: " + followedUserEntity.getUserId()
                    + "; following since: " + followedUser.getCreateDate().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"))
                    + "}");
        }
        System.out.println("  -> ***");
    }

    public void showFollowers(ServiceUser loggedUser) {
        Set<FollowInstance> followers = loggedUser.getFollowers();

        System.out.println("  -> Users following <" + loggedUser.getUserName() + ">: ");
        if (followers.isEmpty()) {
            System.out.println("  -> Nobody has followed <" + loggedUser.getUserName() + "> yet.");
        }
        for (FollowInstance follower : followers) {
            ServiceUser followerEntity = follower.getMainUser();
            System.out.println("{userName: " + followerEntity.getUserName()
                    + "; user ID: " + followerEntity.getUserId()
                    + "; following since: " + follower.getCreateDate().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"))
                    + "}");
        }
        System.out.println("  -> ***");
    }

    public void showUserPage(ServiceUser searchedUser, ServiceUser loggedUser) {

        ServiceUserDao sud = new ServiceUserDao();
        String avatar = searchedUser.getAvatar();
        String userName = searchedUser.getUserName();
        String accountName = searchedUser.getAccountName();
        int numberOfPosts = searchedUser.getPosts().size();
        int numberOfFollowers = searchedUser.getFollowers().size();
        int numberOfFollowed = searchedUser.getFollowedUsers().size();
        List<Post> latestPosts = sud.getLatestPosts(searchedUser);
        String relation = "[FOLLOW THIS USER]";
        if (loggedUser.equals(searchedUser)) {
            relation = "[YOUR ACCOUNT]";
        } else if (sud.isFollowedByUser(loggedUser, searchedUser)) {
            relation = "[FOLLOWING]";
        }

        String nameLine = "\n<" + userName + "> (accoutName: " + accountName + ") " + relation;
        String secondLine = "\nposts: (" + numberOfPosts + "); "
                + "followers: (" + numberOfFollowers + "); "
                + "following: (" + numberOfFollowed + ");";
        String latestPost;

        if (!latestPosts.isEmpty()) {
            latestPost = "\nlatest post: \n"
                    + "\"" + latestPosts.get(0).getContent() + "\"";
        } else {
            latestPost = "\n<" + userName + "> has not posted yet.";
        }
        for (int i = 0; i < secondLine.length(); i++) {
            System.out.print("~");
        }

        System.out.println(
                "\n[ " + avatar + " ]"
                        + nameLine
                        + secondLine);

        for (int i = 0; i < secondLine.length(); i++) {
            System.out.print("-");
        }

        System.out.println(latestPost);

        for (int i = 0; i < secondLine.length(); i++) {
            System.out.print("~");
        }
        System.out.println();
    }

}