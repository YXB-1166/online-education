package com.edu.exam.controller;

import com.edu.common.annotation.RequireRole;
import com.edu.common.entity.Submission;
import com.edu.common.page.PageParam;
import com.edu.common.page.PageResult;
import com.edu.common.result.Result;
import com.edu.exam.service.SubmissionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("submission")
public class SubmissionController {

    @Autowired
    private SubmissionService submissionService;

    @GetMapping("/{id}")
    @RequireRole({1, 2})
    public Result<Submission> getById(@PathVariable Long id) {
        return Result.ok(submissionService.findById(id));
    }

    @GetMapping("/list")
    @RequireRole({1, 2})
    public Result<List<Submission>> list(Submission submission) {
        return Result.ok(submissionService.findList(submission));
    }

    @GetMapping("/page")
    @RequireRole({1, 2})
    public Result<PageResult<Submission>> page(PageParam param, Submission submission) {
        return Result.ok(submissionService.page(param, submission));
    }

    @PostMapping
    @RequireRole(1)
    public Result<Void> add(@Valid @RequestBody Submission submission) {
        submissionService.add(submission);
        return Result.ok();
    }

    @PutMapping
    @RequireRole(2)
    public Result<Void> update(@Valid @RequestBody Submission submission) {
        submissionService.update(submission);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @RequireRole(2)
    public Result<Void> delete(@PathVariable Long id) {
        submissionService.delete(id);
        return Result.ok();
    }

    @PutMapping("/grade")
    @RequireRole(2)
    public Result<Void> grade(@RequestParam Long submissionId, @RequestParam Integer score,
                              @RequestParam String comment) {
        submissionService.grade(submissionId, score, comment);
        return Result.ok();
    }

    @GetMapping("/auto-comment")
    @RequireRole(2)
    public Result<String> autoComment(@RequestParam Long submissionId, @RequestParam Integer score) {
        String comment = submissionService.generateAutoComment(submissionId, score);
        return Result.ok(comment);
    }

}
