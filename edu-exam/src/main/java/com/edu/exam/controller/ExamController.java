package com.edu.exam.controller;

import com.edu.common.annotation.RequireRole;
import com.edu.common.entity.Exam;
import com.edu.common.entity.ExamQuestion;
import com.edu.common.entity.ExamRecord;
import com.edu.common.page.PageParam;
import com.edu.common.page.PageResult;
import com.edu.common.result.Result;
import com.edu.exam.service.ExamService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("exam")
public class ExamController {

    @Autowired
    private ExamService examService;

    @GetMapping("/{id}")
    @RequireRole({1, 2, 3})
    public Result<Exam> getById(@PathVariable Long id) {
        return Result.ok(examService.findById(id));
    }

    @GetMapping("/list")
    @RequireRole({1, 2, 3})
    public Result<List<Exam>> list(Exam exam) {
        return Result.ok(examService.findList(exam));
    }

    @GetMapping("/page")
    @RequireRole({1, 2, 3})
    public Result<PageResult<Exam>> page(PageParam param, Exam exam) {
        return Result.ok(examService.page(param, exam));
    }

    @PostMapping
    @RequireRole(2)
    public Result<Exam> add(@Valid @RequestBody Exam exam) {
        examService.add(exam);
        return Result.ok(exam);
    }

    @PutMapping
    @RequireRole(2)
    public Result<Void> update(@Valid @RequestBody Exam exam) {
        examService.update(exam);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @RequireRole(2)
    public Result<Void> delete(@PathVariable Long id) {
        examService.delete(id);
        return Result.ok();
    }

    @PostMapping("/{id}/publish")
    @RequireRole(2)
    public Result<Void> publish(@PathVariable Long id) {
        examService.publish(id);
        return Result.ok();
    }

    @GetMapping("/{examId}/questions")
    @RequireRole({1, 2, 3})
    public Result<List<ExamQuestion>> getQuestions(@PathVariable Long examId) {
        return Result.ok(examService.getQuestions(examId));
    }

    @PostMapping("/{examId}/questions")
    @RequireRole(2)
    public Result<Void> saveQuestions(@PathVariable Long examId, @RequestBody List<ExamQuestion> questions) {
        examService.saveQuestions(examId, questions);
        return Result.ok();
    }

    @PostMapping("/{examId}/auto-generate")
    @RequireRole(2)
    public Result<List<ExamQuestion>> autoGenerate(@PathVariable Long examId,
                                                    @RequestParam int choiceCount,
                                                    @RequestParam int judgeCount) {
        return Result.ok(examService.autoGenerateQuestions(examId, choiceCount, judgeCount));
    }

    @PostMapping("/{examId}/submit")
    @RequireRole(1)
    public Result<ExamRecord> submit(@PathVariable Long examId, @RequestParam Long studentId,
                                      @RequestBody Map<Long, String> answers) {
        return Result.ok(examService.submitExam(examId, studentId, answers));
    }

    @GetMapping("/{examId}/record")
    @RequireRole({1, 2, 3})
    public Result<ExamRecord> getRecord(@PathVariable Long examId, @RequestParam Long studentId) {
        return Result.ok(examService.getRecord(examId, studentId));
    }

    @GetMapping("/{examId}/records")
    @RequireRole(2)
    public Result<List<ExamRecord>> getRecordsByExam(@PathVariable Long examId) {
        return Result.ok(examService.getRecordsByExam(examId));
    }

    @GetMapping("/my-records")
    @RequireRole(1)
    public Result<List<ExamRecord>> myRecords(@RequestParam Long studentId) {
        return Result.ok(examService.getRecordsByStudent(studentId));
    }
}
