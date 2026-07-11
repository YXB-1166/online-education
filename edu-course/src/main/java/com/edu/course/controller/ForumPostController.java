package com.edu.course.controller;

import com.edu.common.annotation.RequireRole;
import com.edu.common.entity.ForumPost;
import com.edu.common.entity.ForumReply;
import com.edu.common.page.PageParam;
import com.edu.common.page.PageResult;
import com.edu.common.result.Result;
import com.edu.course.service.ForumPostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("forum")
public class ForumPostController {

    @Autowired
    private ForumPostService forumPostService;

    @GetMapping("/{id}")
    @RequireRole({1, 2})
    public Result<ForumPost> getById(@PathVariable Long id,
                                     @RequestParam(required = false) Long userId) {
        if (userId != null) {
            return Result.ok(forumPostService.findByIdWithLike(id, userId));
        }
        return Result.ok(forumPostService.findById(id));
    }

    @GetMapping("/list")
    @RequireRole({1, 2})
    public Result<List<ForumPost>> list(ForumPost forumPost,
                                        @RequestParam(required = false) Long userId) {
        List<ForumPost> list = forumPostService.findList(forumPost);
        if (userId != null) {
            for (ForumPost p : list) {
                p.setLiked(forumPostService.isLiked(p.getId(), userId));
            }
        }
        return Result.ok(list);
    }

    @GetMapping("/page")
    @RequireRole({1, 2})
    public Result<PageResult<ForumPost>> page(PageParam param, ForumPost forumPost,
                                               @RequestParam(required = false) Long userId) {
        PageResult<ForumPost> result = forumPostService.page(param, forumPost);
        if (userId != null) {
            for (ForumPost p : result.getList()) {
                p.setLiked(forumPostService.isLiked(p.getId(), userId));
            }
        }
        return Result.ok(result);
    }

    @PostMapping
    @RequireRole({1, 2})
    public Result<Void> add(@Valid @RequestBody ForumPost forumPost) {
        forumPostService.add(forumPost);
        return Result.ok();
    }

    @PutMapping
    @RequireRole({2, 3})
    public Result<Void> update(@Valid @RequestBody ForumPost forumPost) {
        forumPostService.update(forumPost);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @RequireRole({2, 3})
    public Result<Void> delete(@PathVariable Long id) {
        forumPostService.delete(id);
        return Result.ok();
    }

    @GetMapping("/{id}/replies")
    @RequireRole({1, 2})
    public Result<List<ForumReply>> replies(@PathVariable Long id) {
        return Result.ok(forumPostService.findReplies(id));
    }

    @PostMapping("/reply")
    @RequireRole({1, 2})
    public Result<Void> addReply(@Valid @RequestBody ForumReply reply) {
        forumPostService.addReply(reply);
        return Result.ok();
    }

    @DeleteMapping("/reply/{id}")
    @RequireRole({2, 3})
    public Result<Void> deleteReply(@PathVariable Long id) {
        forumPostService.deleteReply(id);
        return Result.ok();
    }

    @PostMapping("/{id}/like")
    @RequireRole({1, 2})
    public Result<Boolean> toggleLike(@PathVariable Long id,
                                       @RequestParam Long userId) {
        boolean liked = forumPostService.toggleLike(id, userId);
        return Result.ok(liked);
    }
}
