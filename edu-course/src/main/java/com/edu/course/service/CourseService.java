package com.edu.course.service;

import com.edu.common.entity.Course;
import com.edu.common.entity.CourseSelection;
import com.edu.common.entity.Notification;
import com.edu.common.page.PageParam;
import com.edu.common.page.PageResult;
import com.edu.common.result.BusinessException;
import com.edu.common.service.BaseService;
import com.edu.course.mapper.CourseMapper;
import com.edu.course.mapper.CourseSelectionMapper;
import com.edu.course.mapper.NotificationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CourseService extends BaseService {

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private CourseSelectionMapper selectionMapper;

    @Autowired
    private NotificationMapper notificationMapper;

    public Course findById(Long id) {
        Course course = courseMapper.selectById(id);
        if (course == null) {
            log.warn("课程不存在: id={}", id);
            throw new BusinessException("课程不存在");
        }
        return course;
    }

    public List<Course> findList(Course course) {
        return courseMapper.selectList(course);
    }

    public PageResult<Course> page(PageParam param, Course course) {
        long total = courseMapper.countList(course);
        List<Course> list = courseMapper.selectPage(course, param.getOffset(), param.getPageSize());
        log.info("课程分页: total={}, page={}/{}", total, param.getPageNum(), param.getPageSize());
        return new PageResult<>(list, total, param.getPageNum(), param.getPageSize());
    }

    public List<Course> findListExcludingStudent(Course course, Long studentId) {
        return courseMapper.selectListExcludingStudent(course, studentId);
    }

    public PageResult<Course> pageExcludingStudent(PageParam param, Course course, Long studentId) {
        long total = courseMapper.countListExcludingStudent(course, studentId);
        List<Course> list = courseMapper.selectPageExcludingStudent(course, studentId, param.getOffset(), param.getPageSize());
        return new PageResult<>(list, total, param.getPageNum(), param.getPageSize());
    }

    public void add(Course course) {
        if (course.getStatus() == null || course.getStatus().isEmpty()) {
            course.setStatus("0");
        }
        if (courseMapper.insert(course) == 0) {
            log.warn("新增课程失败");
            throw new BusinessException("新增课程失败");
        }
        log.info("新增课程: id={}, name={}, status={}", course.getId(), course.getCourseName(), course.getStatus());
    }

    public List<Course> listPending() {
        return courseMapper.selectPending();
    }

    @Transactional
    public void approveCourse(Long id) {
        Course course = courseMapper.selectById(id);
        if (course == null) throw new BusinessException("课程不存在");
        if (!"0".equals(course.getStatus())) throw new BusinessException("该课程不是待审核状态");
        int n = courseMapper.approveById(id);
        if (n == 0) throw new BusinessException("审核通过失败");
        log.info("课程审核通过: id={}", id);
    }

    @Transactional
    public void rejectCourse(Long id) {
        int n = courseMapper.rejectById(id);
        if (n == 0) throw new BusinessException("审核拒绝失败，记录不存在或状态异常");
        log.info("课程审核拒绝: id={}", id);
    }

    public void update(Course course) {
        if (courseMapper.update(course) == 0) {
            log.warn("更新课程失败: id={}", course.getId());
            throw new BusinessException("更新课程失败");
        }
        log.info("更新课程: id={}", course.getId());
    }

    public void delete(Long id) {
        if (courseMapper.deleteById(id) == 0) {
            log.warn("删除课程失败: id={}", id);
            throw new BusinessException("删除课程失败");
        }
        log.info("删除课程: id={}", id);
    }

    @Transactional
    public void selectCourse(Long studentId, Long courseId) {
        CourseSelection active = selectionMapper.selectActiveByStudentAndCourse(studentId, courseId);
        if (active != null) {
            throw new BusinessException("已选过该课程");
        }
        CourseSelection pending = selectionMapper.selectPendingByStudentAndCourse(studentId, courseId);
        if (pending != null) {
            throw new BusinessException("已提交选课申请，等待审核");
        }
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new BusinessException("课程不存在");
        }
        if (course.getEnrolledCount() >= course.getMaxStudents()) {
            throw new BusinessException("选课人数已满");
        }
        int n = selectionMapper.reactivate(studentId, courseId, LocalDateTime.now());
        if (n == 0) {
            CourseSelection cs = new CourseSelection();
            cs.setStudentId(studentId);
            cs.setCourseId(courseId);
            cs.setStatus("0");
            cs.setSelectTime(LocalDateTime.now());
            selectionMapper.insert(cs);
        }
        log.info("选课申请: studentId={}, courseId={}", studentId, courseId);
    }

    @Transactional
    public void dropCourse(Long studentId, Long courseId) {
        CourseSelection active = selectionMapper.selectActiveByStudentAndCourse(studentId, courseId);
        if (active != null) {
            selectionMapper.updateStatus(studentId, courseId, "2", LocalDateTime.now());
            courseMapper.decrementEnrolled(courseId);
            log.info("退课: studentId={}, courseId={}", studentId, courseId);
            return;
        }
        CourseSelection pending = selectionMapper.selectPendingByStudentAndCourse(studentId, courseId);
        if (pending != null) {
            selectionMapper.rejectById(pending.getId());
            log.info("取消选课申请: studentId={}, courseId={}", studentId, courseId);
            return;
        }
        throw new BusinessException("未选过该课程");
    }

    public List<CourseSelection> listPendingSelections(Long teacherId) {
        return selectionMapper.selectPending(teacherId);
    }

    public List<Course> listMyCourses(Long studentId) {
        return selectionMapper.selectMyCourses(studentId);
    }

    public List<Course> listByTeacher(Long teacherId) {
        return courseMapper.selectByTeacher(teacherId);
    }

    @Transactional
    public void startCourse(Long id, Integer homeworkRatio, Integer examRatio, LocalDateTime examTime) {
        Course course = courseMapper.selectById(id);
        if (course == null) throw new BusinessException("课程不存在");
        if (!"1".equals(course.getStatus())) throw new BusinessException("只有即将开课的课程才能开课");
        int n = courseMapper.startCourse(id, homeworkRatio, examRatio, examTime);
        if (n == 0) throw new BusinessException("开课失败");
        // 发送开课通知
        Notification notif = new Notification();
        notif.setCourseId(id);
        notif.setTitle("课程已开课：" + course.getCourseName());
        notif.setContent("课程《" + course.getCourseName() + "》已正式开课！平时作业占比 " + homeworkRatio + "%，考试占比 " + examRatio + "%。");
        notificationMapper.insert(notif);
        if (examTime != null) {
            Notification examNotif = new Notification();
            examNotif.setCourseId(id);
            examNotif.setTitle("考试时间已定：" + course.getCourseName());
            examNotif.setContent("课程《" + course.getCourseName() + "》考试时间定为 " + examTime + "，请同学们做好准备。");
            notificationMapper.insert(examNotif);
        }
        log.info("开课成功: id={}, ratio={}/{}, examTime={}", id, homeworkRatio, examRatio, examTime);
    }

    @Transactional
    public void endCourse(Long id) {
        Course course = courseMapper.selectById(id);
        if (course == null) throw new BusinessException("课程不存在");
        if (!"2".equals(course.getStatus())) throw new BusinessException("只有授课中的课程才能结课");
        int n = courseMapper.endCourse(id);
        if (n == 0) throw new BusinessException("结课失败");
        Notification notif = new Notification();
        notif.setCourseId(id);
        notif.setTitle("课程已结课：" + course.getCourseName());
        notif.setContent("课程《" + course.getCourseName() + "》已结课，成绩将陆续公布。");
        notificationMapper.insert(notif);
        log.info("结课成功: id={}", id);
    }

    @Transactional
    public void setExamTime(Long id, LocalDateTime examTime) {
        Course course = courseMapper.selectById(id);
        if (course == null) throw new BusinessException("课程不存在");
        courseMapper.updateExamTime(id, examTime);
        Notification notif = new Notification();
        notif.setCourseId(id);
        String t = examTime != null ? examTime.toString() : "待定";
        notif.setTitle("考试时间更新：" + course.getCourseName());
        notif.setContent("课程《" + course.getCourseName() + "》考试时间已更新为 " + t + "。");
        notificationMapper.insert(notif);
        log.info("考试时间更新: id={}, examTime={}", id, examTime);
    }

    public List<Notification> getNotifications(Long studentId) {
        return notificationMapper.selectByStudent(studentId);
    }

    public List<Notification> getCourseNotifications(Long courseId) {
        return notificationMapper.selectByCourse(courseId);
    }

    @Transactional
    public void approveSelection(Long id) {
        CourseSelection cs = selectionMapper.selectById(id);
        if (cs == null) {
            throw new BusinessException("选课记录不存在");
        }
        if (!"0".equals(cs.getStatus())) {
            throw new BusinessException("该记录不是待审核状态");
        }
        Course course = courseMapper.selectById(cs.getCourseId());
        if (course.getEnrolledCount() >= course.getMaxStudents()) {
            throw new BusinessException("选课人数已满，无法通过");
        }
        selectionMapper.approveById(id);
        courseMapper.incrementEnrolled(cs.getCourseId());
        log.info("审批通过: selectionId={}, courseId={}", id, cs.getCourseId());
    }

    @Transactional
    public void rejectSelection(Long id) {
        int n = selectionMapper.rejectById(id);
        if (n == 0) {
            throw new BusinessException("审核不通过失败，记录不存在或状态异常");
        }
        log.info("审批拒绝: selectionId={}", id);
    }

    public List<CourseSelection> listSelectionsByCourse(Long courseId, Long teacherId) {
        return selectionMapper.selectByCourse(courseId, teacherId);
    }

}
