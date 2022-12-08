package com.mgbt.socialapp_backend.model.service;

import com.mgbt.socialapp_backend.model.entity.Post;
import com.mgbt.socialapp_backend.model.entity.UserApp;
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

    //region PAGINATION
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

    //region Friends feed
    @Transactional(readOnly = true)
    public List<Post> findFriendsFeedByUserId(Long idUser, Integer from) {
        return repository.findFriendsFeedByUser(idUser, from);
    }

    @Transactional(readOnly = true)
    public Long findLastIdPostFromFriendsFeedByUserId(Long idUser) {
        return repository.findLastIdPostFromFriendsFeedByUser(idUser);
    }

    @Transactional(readOnly = true)
    public List<Post> findOldFriendsFeedByUserId(Long idUser, Integer from) {
        return repository.findOldFriendsFeedByUser(idUser, from);
    }

    @Transactional(readOnly = true)
    public Long findLastIdPostFromOldFriendsFeedByUserId(Long idUser) {
        return repository.findLastIdPostFromOldFriendsFeedByUser(idUser);
    }
    //endregion

    //region Following feed
    @Transactional(readOnly = true)
    public List<Post> findFollowingFeedByUserId(Long idUser, Integer from) {
        return repository.findFollowingFeedByUser(idUser, from);
    }

    @Transactional(readOnly = true)
    public Long findLastIdPostFromFollowingFeedByUserId(Long idUser) {
        return repository.findLastIdPostFromFollowingFeedByUser(idUser);
    }

    @Transactional(readOnly = true)
    public List<Post> findOldFollowingFeedByUserId(Long idUser, Integer from) {
        return repository.findOldFollowingFeedByUser(idUser, from);
    }

    @Transactional(readOnly = true)
    public Long findLastIdPostFromOldFollowingFeedByUserId(Long idUser) {
        return repository.findLastIdPostFromOldFollowingFeedByUser(idUser);
    }
    //endregion

    //region Trend feed
    @Transactional(readOnly = true)
    public List<Post> findTrendFeed(Integer from) {
        return repository.findTrendFeed(from);
    }

    @Transactional(readOnly = true)
    public Long findLastIdPostFromTrendFeed(Long idUser) {
        return repository.findLastIdPostFromTrendFeed(idUser);
    }
    //endregion

    //region By user
    @Transactional(readOnly = true)
    public List<Post> findPostsByUserId(Long idUser, Integer from) {
        return repository.findPostsByUser(idUser, from);
    }

    @Transactional(readOnly = true)
    public Long findLastIdPostFromUserPosts(Long idUser) {
        return repository.findLastIdPostFromPostsByUser(idUser);
    }

    @Transactional(readOnly = true)
    public List<Post> findLikedPostsByUserId(Long idUser, Integer from) {
        return repository.findLikedPostsByUser(idUser, from);
    }

    @Transactional(readOnly = true)
    public Long findLastIdPostFromLikedPostsUser(Long idUser) {
        return repository.findLastIdPostFromLikedPostsByUser(idUser);
    }
    //endregion

    public boolean getIsLastPage(Long lastId, List<Post> postsPage) {
        //When a user enters their feed or profile page and none of these have posts,
        //the id of the last post is null, so a check must be made so that the system
        //does not throw an exception.
        if (lastId == null) {
            return true;
        }
        return postsPage.get(postsPage.size()-1).getIdPost().equals(lastId);
    }
    //endregion

    @Transactional(readOnly = true)
    public Post findTheMostPopularPostsFromToday() {
        return repository.findTheMostPopularPostFromToday();
    }

    @Transactional(readOnly = true)
    public Post findByUserAndIsPinned(UserApp user) {
        return repository.findByUserAndIsPinned(user, true);
    }
}