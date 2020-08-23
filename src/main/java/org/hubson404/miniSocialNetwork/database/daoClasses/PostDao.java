package org.hubson404.miniSocialNetwork.database.daoClasses;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hubson404.miniSocialNetwork.clients.TaggingManager;
import org.hubson404.miniSocialNetwork.database.HibernateUtil;
import org.hubson404.miniSocialNetwork.model.*;
import org.hubson404.miniSocialNetwork.model.utils.PostType;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class PostDao {
    public void saveOrUpdate(Post post) {
        SessionFactory sessionFactory = HibernateUtil.getOurSessionFactory();
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.saveOrUpdate(post);
//            new TaggingManager().manageTags(post);
//            session.saveOrUpdate(post);

            transaction.commit();
        } catch (HibernateException he) {
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }

    public Optional<Post> findById(Class<Post> classType, Long id) {
        SessionFactory sessionFactory = HibernateUtil.getOurSessionFactory();
        try (Session session = sessionFactory.openSession()) {
            // istnieje prawdopodobieństwo, że rekord nie zostanie odnaleziony
            return Optional.ofNullable(session.get(classType, id));
        } catch (HibernateException he) {
            he.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<List<Post>> findPostByTag(String tagName) {
        List<Post> list = new ArrayList<>();

        Optional<Tag> op = new TagDao().findByTagName(tagName);

        if (op.isPresent()) {
            Tag tag = op.get();
            List<Post> all = this.findAll();
            all.stream().filter(post -> post.getIncludedTags().contains(tag)).forEach(list::add);

            return Optional.ofNullable(list);

        } else {
            System.err.println("Given tag doesn't exist.");
            return Optional.empty();
        }
    }

    public void delete(Post post) {
        SessionFactory sessionFactory = HibernateUtil.getOurSessionFactory();
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            // instrukcja która służy do usuwania z bazy danych
            session.delete(post);

            transaction.commit();
        } catch (HibernateException he) {
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }

    public List<Post> findAll() {
        List<Post> list = new ArrayList<>();

        SessionFactory sessionFactory = HibernateUtil.getOurSessionFactory();
        try (Session session = sessionFactory.openSession()) {

            // narzędzie do tworzenia zapytań i kreowania klauzuli 'where'
            CriteriaBuilder cb = session.getCriteriaBuilder();

            // obiekt reprezentujący zapytanie
            CriteriaQuery<Post> criteriaQuery = cb.createQuery(Post.class);

            // obiekt reprezentujący tabelę bazodanową.
            // do jakiej tabeli kierujemy nasze zapytanie?
            Root<Post> rootTable = criteriaQuery.from(Post.class);

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

    public void likePost(ServiceUser loggedUser, Post post) {
        SessionFactory sessionFactory = HibernateUtil.getOurSessionFactory();
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            LikeBadge lb = new LikeBadge(loggedUser, post);
            session.saveOrUpdate(lb);

            loggedUser.getLikeBadges().add(lb); // todo: przetestuj sobie
            post.getLikeBadges().add(lb);

            session.saveOrUpdate(post);

            transaction.commit();
        } catch (HibernateException he) {
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }

    public void unLikePost(ServiceUser loggedUser, Post post) {
        SessionFactory sessionFactory = HibernateUtil.getOurSessionFactory();
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            // TODO: zapytanie find - szukanie po encjach
            Optional<LikeBadge> op = post.getLikeBadges()
                    .stream()
                    .filter(fi -> fi.getServiceUser().equals(loggedUser))
                    .findFirst();
            LikeBadge lbBeingRemoved = op.get();

            // jak znajdziesz likebadge
            // to ten kod zostaje
            post.getLikeBadges().remove(lbBeingRemoved);
            loggedUser.getLikeBadges().remove(lbBeingRemoved);

            session.saveOrUpdate(post);
            session.saveOrUpdate(loggedUser);

            transaction.commit();
        } catch (HibernateException he) {
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }

    public boolean isLiked(ServiceUser loggedUser, Post post) {
        boolean isPresent = post.getLikeBadges()
                .stream()
                .map(LikeBadge::getServiceUser)
                .anyMatch(u -> u.equals(loggedUser));
        return isPresent;
    }


    public Optional<Post> writePost(Scanner scanner, ServiceUser loggedUser, PostType postType) {
        SessionFactory sessionFactory = HibernateUtil.getOurSessionFactory();
        Transaction transaction = null;

        String postContent;
        System.out.println("Write your message: ");
        postContent = scanner.nextLine();
        Post post = new Post(postContent, postType, loggedUser);

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            System.out.println("Message posted:\n" + loggedUser.getAvatar() + "\n<"
                    + loggedUser.getAccountName() + "> : \"" + postContent + "\"");

            session.saveOrUpdate(post);

            transaction.commit();

        } catch (HibernateException he) {
            if (transaction != null) {
                transaction.rollback();
            }
        }

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            new TaggingManager().manageTags(post);
            session.saveOrUpdate(post);

            transaction.commit();
        } catch (HibernateException he) {
            if (transaction != null) {
                transaction.rollback();
            }
        }
        return Optional.ofNullable(post);
    }

    public void commentPost(Post commentPost, Post mainPost) {
        SessionFactory sessionFactory = HibernateUtil.getOurSessionFactory();
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            commentPost.setMainPost(mainPost);
            session.saveOrUpdate(commentPost);

            mainPost.getComments().add(commentPost);
            session.saveOrUpdate(mainPost);

            transaction.commit();
        } catch (HibernateException he) {
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }

    public void showPost(Post post) {

        ServiceUser op = post.getOriginalPoster();

        System.out.println(op.getAvatar() + " <" + op.getUserName() + "> :");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        System.out.println("{postID: " + post.getPostId()
                + "; postStatus: " + post.getPostType()
                + "; date: " + post.getCreateDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        if (post.getPostType() == PostType.COMMENT) {
            System.out.println("{Comment to  post(postID: " + post.getMainPost().getPostId() + ")}");
        }

        System.out.println("-----------------------------------");
        System.out.println("<p> " + post.getContent() + " </p>");
        System.out.println("-----------------------------------");

        System.out.println("{comments: (" + post.getComments().size()
                + ") ; likes: (" + post.getLikeBadges().size()
                + ") ; forwards: (" + post.getForwardBadges().size() + ")}");

        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }

    public void showAllComments(Post post) {
        post.getComments().forEach(this::showPost);
    }

}