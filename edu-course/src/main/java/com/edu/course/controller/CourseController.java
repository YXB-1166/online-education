package com.edu.course.controller;

import com.edu.common.annotation.RequireRole;
import com.edu.common.entity.Chapter;
import com.edu.common.entity.Course;
import com.edu.common.entity.CourseSelection;
import com.edu.common.entity.Notification;
import com.edu.common.page.PageParam;
import com.edu.common.page.PageResult;
import com.edu.common.result.Result;
import com.edu.course.service.ChapterService;
import com.edu.course.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("course")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private ChapterService chapterService;

    @GetMapping("/{id}")
    @RequireRole({1, 2, 3})
    public Result<Course> getById(@PathVariable Long id) {
        return Result.ok(courseService.findById(id));
    }

    @GetMapping("/list")
    @RequireRole({1, 2, 3})
    public Result<List<Course>> list(Course course, @RequestParam(required = false) Long excludeStudentId) {
        if (excludeStudentId != null) {
            return Result.ok(courseService.findListExcludingStudent(course, excludeStudentId));
        }
        return Result.ok(courseService.findList(course));
    }

    @GetMapping("/page")
    @RequireRole({1, 2, 3})
    public Result<PageResult<Course>> page(PageParam param, Course course, @RequestParam(required = false) Long excludeStudentId) {
        if (excludeStudentId != null) {
            return Result.ok(courseService.pageExcludingStudent(param, course, excludeStudentId));
        }
        return Result.ok(courseService.page(param, course));
    }

    @PostMapping
    @RequireRole({2, 3})
    public Result<Void> add(@Valid @RequestBody Course course) {
        courseService.add(course);
        return Result.ok();
    }

    @PutMapping
    @RequireRole({2, 3})
    public Result<Void> update(@Valid @RequestBody Course course) {
        courseService.update(course);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @RequireRole({2, 3})
    public Result<Void> delete(@PathVariable Long id) {
        courseService.delete(id);
        return Result.ok();
    }

    @PostMapping("/select")
    @RequireRole(1)
    public Result<Void> selectCourse(@RequestParam Long studentId, @RequestParam Long courseId) {
        courseService.selectCourse(studentId, courseId);
        return Result.ok();
    }

    @PostMapping("/drop")
    @RequireRole(1)
    public Result<Void> dropCourse(@RequestParam Long studentId, @RequestParam Long courseId) {
        courseService.dropCourse(studentId, courseId);
        return Result.ok();
    }

    @GetMapping("/selection/pending")
    @RequireRole(2)
    public Result<List<CourseSelection>> pendingSelections(@RequestParam(required = false) Long teacherId) {
        return Result.ok(courseService.listPendingSelections(teacherId));
    }

    @GetMapping("/my")
    @RequireRole(1)
    public Result<List<Course>> myCourses(@RequestParam Long studentId) {
        return Result.ok(courseService.listMyCourses(studentId));
    }

    @PutMapping("/selection/approve/{id}")
    @RequireRole(2)
    public Result<Void> approveSelection(@PathVariable Long id) {
        courseService.approveSelection(id);
        return Result.ok();
    }

    @PutMapping("/selection/reject/{id}")
    @RequireRole(2)
    public Result<Void> rejectSelection(@PathVariable Long id) {
        courseService.rejectSelection(id);
        return Result.ok();
    }

    @GetMapping("/{courseId}/chapters")
    @RequireRole({1, 2, 3})
    public Result<List<Chapter>> getChapters(@PathVariable Long courseId) {
        return Result.ok(chapterService.getChaptersWithPoints(courseId));
    }

    @PostMapping("/{courseId}/chapters/generate")
    @RequireRole({2, 3})
    public Result<List<Chapter>> generateChapters(@PathVariable Long courseId) {
        return Result.ok(chapterService.autoGenerate(courseId));
    }

    @GetMapping("/my-teaching")
    @RequireRole(2)
    public Result<List<Course>> myTeaching(@RequestParam Long teacherId) {
        return Result.ok(courseService.listByTeacher(teacherId));
    }

    @GetMapping("/pending")
    @RequireRole(3)
    public Result<List<Course>> pendingCourses() {
        return Result.ok(courseService.listPending());
    }

    @PutMapping("/approve/{id}")
    @RequireRole(3)
    public Result<Void> approveCourse(@PathVariable Long id) {
        courseService.approveCourse(id);
        return Result.ok();
    }

    @PutMapping("/reject/{id}")
    @RequireRole(3)
    public Result<Void> rejectCourse(@PathVariable Long id) {
        courseService.rejectCourse(id);
        return Result.ok();
    }

    @PutMapping("/{id}/start")
    @RequireRole(2)
    public Result<Void> startCourse(@PathVariable Long id,
                                    @RequestParam Integer homeworkRatio,
                                    @RequestParam Integer examRatio,
                                    @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime examTime) {
        courseService.startCourse(id, homeworkRatio, examRatio, examTime);
        return Result.ok();
    }

    @PutMapping("/{id}/end")
    @RequireRole(2)
    public Result<Void> endCourse(@PathVariable Long id) {
        courseService.endCourse(id);
        return Result.ok();
    }

    @PutMapping("/{id}/exam-time")
    @RequireRole(2)
    public Result<Void> setExamTime(@PathVariable Long id,
                                    @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime examTime) {
        courseService.setExamTime(id, examTime);
        return Result.ok();
    }

    @GetMapping("/notifications")
    @RequireRole(1)
    public Result<List<Notification>> notifications(@RequestParam Long studentId) {
        return Result.ok(courseService.getNotifications(studentId));
    }

    @GetMapping("/notifications/unread-count")
    @RequireRole(1)
    public Result<Integer> unreadCount(@RequestParam Long studentId) {
        return Result.ok(courseService.getUnreadCount(studentId));
    }

    @PutMapping("/notification/{notificationId}/read")
    @RequireRole(1)
    public Result<Void> markRead(@PathVariable Long notificationId, @RequestParam Long studentId) {
        courseService.markNotificationRead(notificationId, studentId);
        return Result.ok();
    }

    @GetMapping("/{courseId}/notifications")
    @RequireRole({1, 2, 3})
    public Result<List<Notification>> courseNotifications(@PathVariable Long courseId) {
        return Result.ok(courseService.getCourseNotifications(courseId));
    }

    @GetMapping("/selection/list")
    @RequireRole(2)
    public Result<List<CourseSelection>> listSelections(@RequestParam Long courseId, @RequestParam Long teacherId) {
        return Result.ok(courseService.listSelectionsByCourse(courseId, teacherId));
    }

}
