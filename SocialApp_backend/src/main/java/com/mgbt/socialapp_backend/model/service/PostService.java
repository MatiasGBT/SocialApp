package com.mgbt.socialapp_backend.model.service;

import com.mgbt.socialapp_backend.model.entity.Post;
import com.mgbt.socialapp_backend.model.repository.IPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class PostService implements IService<Post> {

    @Autowired
    IPostRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<Post> toList() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public Post save(Post entity) {
        return repository.save(entity);
    }

    @Override
    @Transactional
    public void delete(Post entity) {
        repository.delete(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Post findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public Integer countUserPosts(Long idUser) {
        return repository.countPostsByUser(idUser);
    }

    //<editor-fold desc="PAGINATION">
    /*
      This space is for the lists of paginated posts on the index page (the feed and the feed from
      previous days) and the profile page (the user's posts).
      Currently all lists are divided into a limit of 10 publications per page, this can be seen
      in the SQL queries in the repository.
      By not using the Hibernate and Spring pagination system (since the system requires another
      type of pagination), it is necessary to obtain which is the last page of the query by checking
      if the last record obtained from said query has the minimum possible ID of the result of the
      query without pagination (instead of COUNT like normal pagination, the query use MIN).
    */
    @Transactional(readOnly = true)
    public List<Post> findFeedByUserId(Long idUser, Integer from) {
        return repository.findFeedByUser(idUser, from);
    }

    @Transactional(readOnly = true)
    public Long findLastIdPostFromUserFeed(Long idUser) {
        return repository.findLastIdPostFromFeedByUser(idUser);
    }

    @Transactional(readOnly = true)
    public List<Post> findOldFeedByUserId(Long idUser, Integer from) {
        return repository.findOldFeedByUser(idUser, from);
    }

    @Transactional(readOnly = true)
    public Long findLastIdPostFromOldUserFeed(Long idUser) {
        return repository.findLastIdPostFromOldFeedByUser(idUser);
    }

    @Transactional(readOnly = true)
    public List<Post> findByUserId(Long idUser, Integer from) {
        return repository.findByUser(idUser, from);
    }

    @Transactional(readOnly = true)
    public Long findLastIdPostFromUserPosts(Long idUser) {
        return repository.findLastIdPostFromPostsByUser(idUser);
    }

    public boolean getIsLastPage(Long lastId, List<Post> postsPage) {
        /*
          When a user enters their feed or profile page and none of these have posts,
          the id of the last post is null, so a check must be made so that the system
          does not throw an exception.
        */
        if (lastId == null) {
            return true;
        }
        return postsPage.get(postsPage.size()-1).getIdPost().equals(lastId);
    }
    //</editor-fold>
}
