package com.edu.course.service;

import com.edu.common.entity.ForumPost;
import com.edu.common.entity.ForumReply;
import com.edu.common.entity.ForumLike;
import com.edu.common.page.PageParam;
import com.edu.common.page.PageResult;
import com.edu.common.result.BusinessException;
import com.edu.common.service.BaseService;
import com.edu.course.mapper.ForumLikeMapper;
import com.edu.course.mapper.ForumPostMapper;
import com.edu.course.mapper.ForumReplyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ForumPostService extends BaseService {

    @Autowired
    private ForumPostMapper forumPostMapper;

    @Autowired
    private ForumReplyMapper forumReplyMapper;

    @Autowired
    private ForumLikeMapper forumLikeMapper;

    public ForumPost findById(Long id) {
        ForumPost post = forumPostMapper.selectById(id);
        if (post == null) {
            log.warn("帖子不存在: id={}", id);
            throw new BusinessException("帖子不存在");
        }
        return post;
    }

    public List<ForumPost> findList(ForumPost forumPost) {
        return forumPostMapper.selectList(forumPost);
    }

    public PageResult<ForumPost> page(PageParam param, ForumPost forumPost) {
        long total = forumPostMapper.countList(forumPost);
        List<ForumPost> list = forumPostMapper.selectPage(forumPost, param.getOffset(), param.getPageSize());
        log.info("帖子分页: total={}, page={}/{}", total, param.getPageNum(), param.getPageSize());
        return new PageResult<>(list, total, param.getPageNum(), param.getPageSize());
    }

    public void add(ForumPost forumPost) {
        if (forumPostMapper.insert(forumPost) == 0) {
            log.warn("发帖失败");
            throw new BusinessException("发帖失败");
        }
        log.info("发帖: id={}, title={}", forumPost.getId(), forumPost.getTitle());
    }

    public void update(ForumPost forumPost) {
        if (forumPostMapper.update(forumPost) == 0) {
            log.warn("更新帖子失败: id={}", forumPost.getId());
            throw new BusinessException("更新帖子失败");
        }
        log.info("更新帖子: id={}", forumPost.getId());
    }

    public void delete(Long id) {
        if (forumPostMapper.deleteById(id) == 0) {
            log.warn("删除帖子失败: id={}", id);
            throw new BusinessException("删除帖子失败");
        }
        log.info("删除帖子: id={}", id);
    }

    public List<ForumReply> findReplies(Long postId) {
        return forumReplyMapper.selectByPostId(postId);
    }

    public void addReply(ForumReply reply) {
        if (forumReplyMapper.insert(reply) == 0) {
            log.warn("回复失败: postId={}", reply.getPostId());
            throw new BusinessException("回复失败");
        }
        forumPostMapper.incrementReply(reply.getPostId());
        log.info("回复帖子: postId={}, replyId={}", reply.getPostId(), reply.getId());
    }

    public void deleteReply(Long id) {
        forumReplyMapper.deleteById(id);
        log.info("删除回复: id={}", id);
    }

    public boolean toggleLike(Long postId, Long userId) {
        if (forumLikeMapper.exists(postId, userId) > 0) {
            forumLikeMapper.deleteByPostIdAndUserId(postId, userId);
            forumPostMapper.decrementLike(postId);
            log.info("取消点赞: postId={}, userId={}", postId, userId);
            return false;
        } else {
            ForumLike like = new ForumLike();
            like.setPostId(postId);
            like.setUserId(userId);
            forumLikeMapper.insert(like);
            forumPostMapper.incrementLike(postId);
            log.info("点赞: postId={}, userId={}", postId, userId);
            return true;
        }
    }

    public boolean isLiked(Long postId, Long userId) {
        return forumLikeMapper.exists(postId, userId) > 0;
    }

    public ForumPost findByIdWithLike(Long id, Long userId) {
        ForumPost post = findById(id);
        post.setLiked(isLiked(id, userId));
        return post;
    }
}
