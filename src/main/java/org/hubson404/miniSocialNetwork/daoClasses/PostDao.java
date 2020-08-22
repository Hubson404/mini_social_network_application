package org.hubson404.miniSocialNetwork.daoClasses;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hubson404.miniSocialNetwork.database.HibernateUtil;
import org.hubson404.miniSocialNetwork.model.*;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PostDao {
    public void saveOrUpdate(Post post) {
        SessionFactory sessionFactory = HibernateUtil.getOurSessionFactory();
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.saveOrUpdate(post);

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

//    public <T extends PostNameSearchable> Optional<T> findByUserName(Class<T> classType, String postName) {
//
//        SessionFactory sessionFactory = HibernateUtil.getOurSessionFactory();
//        try (Session session = sessionFactory.openSession()) {
//
//            CriteriaBuilder cb = session.getCriteriaBuilder();
//
//            CriteriaQuery<T> criteriaQuery = cb.createQuery(classType);
//
//            Root<T> rootTable = criteriaQuery.from(classType);
//
//            criteriaQuery.select(rootTable)
//                    .where(
//                            cb.equal(rootTable.get("postName"), postName.toLowerCase())
//                    );
//            return Optional.ofNullable(session.createQuery(criteriaQuery).getSingleResult());
//        } catch (HibernateException he) {
//            he.printStackTrace();
//        } catch (NoResultException e) {
//            System.out.println("User <" + postName + "> not found.");
//            return Optional.empty();
//        }
//        return Optional.empty();
//    }

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

    public List<Post> findAll(Class<Post> classType) {
        List<Post> list = new ArrayList<>();

        SessionFactory sessionFactory = HibernateUtil.getOurSessionFactory();
        try (Session session = sessionFactory.openSession()) {

            // narzędzie do tworzenia zapytań i kreowania klauzuli 'where'
            CriteriaBuilder cb = session.getCriteriaBuilder();

            // obiekt reprezentujący zapytanie
            CriteriaQuery<Post> criteriaQuery = cb.createQuery(classType);

            // obiekt reprezentujący tabelę bazodanową.
            // do jakiej tabeli kierujemy nasze zapytanie?
            Root<Post> rootTable = criteriaQuery.from(classType);

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
            post.getLikeBadges().add(lb);
            loggedUser.getLikeBadges().add(lb);

            session.saveOrUpdate(loggedUser);
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

            Optional<LikeBadge> op = post.getLikeBadges()
                    .stream()
                    .filter(fi -> fi.getServiceUser().equals(loggedUser))
                    .findFirst();
            LikeBadge lbBeingRemoved = op.get();
            post.getLikeBadges().remove(lbBeingRemoved);
            loggedUser.getLikeBadges().remove(lbBeingRemoved);

            session.saveOrUpdate(loggedUser);
            session.saveOrUpdate(post);

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

    public void commentPost(Post commentPost, Post mainPost) {
        SessionFactory sessionFactory = HibernateUtil.getOurSessionFactory();
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            CommentInstance ci = new CommentInstance(commentPost, mainPost);
            session.saveOrUpdate(ci);

            commentPost.setMainPost(ci);
            mainPost.getComments().add(ci);

            session.saveOrUpdate(mainPost);
            session.saveOrUpdate(commentPost);

            transaction.commit();
        } catch (HibernateException he) {
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }
}