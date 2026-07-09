package com.edu.exam.controller;

import com.edu.common.annotation.RequireRole;
import com.edu.common.entity.Assignment;
import com.edu.common.page.PageParam;
import com.edu.common.page.PageResult;
import com.edu.common.result.Result;
import com.edu.exam.service.AssignmentService;
import com.edu.common.entity.ResubmitOpportunity;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

import java.util.List;

@RestController
@RequestMapping("assignment")
public class AssignmentController {

    @Autowired
    private AssignmentService assignmentService;

    @GetMapping("/{id}")
    @RequireRole({1, 2})
    public Result<Assignment> getById(@PathVariable Long id) {
        return Result.ok(assignmentService.findById(id));
    }

    @GetMapping("/list")
    @RequireRole({1, 2})
    public Result<List<Assignment>> list(Assignment assignment) {
        return Result.ok(assignmentService.findList(assignment));
    }

    @GetMapping("/page")
    @RequireRole({1, 2})
    public Result<PageResult<Assignment>> page(PageParam param, Assignment assignment) {
        return Result.ok(assignmentService.page(param, assignment));
    }

    @PostMapping
    @RequireRole(2)
    public Result<Void> add(@Valid @RequestBody Assignment assignment) {
        assignmentService.add(assignment);
        return Result.ok();
    }

    @PutMapping
    @RequireRole(2)
    public Result<Void> update(@Valid @RequestBody Assignment assignment) {
        assignmentService.update(assignment);
        return Result.ok();
    }

    @PostMapping("/grant-resubmit")
    @RequireRole(2)
    public Result<Void> grantResubmit(@RequestParam Long assignmentId, @RequestParam Long studentId,
                                       @RequestParam String deadline) {
        assignmentService.grantResubmit(assignmentId, studentId, LocalDateTime.parse(deadline.replace(" ", "T")));
        return Result.ok();
    }

    @GetMapping("/resubmit-opportunities")
    @RequireRole({1, 2})
    public Result<List<ResubmitOpportunity>> resubmitOpportunities(@RequestParam Long studentId) {
        return Result.ok(assignmentService.getResubmitOpportunities(studentId));
    }

    @DeleteMapping("/{id}")
    @RequireRole(2)
    public Result<Void> delete(@PathVariable Long id) {
        assignmentService.delete(id);
        return Result.ok();
    }

}
