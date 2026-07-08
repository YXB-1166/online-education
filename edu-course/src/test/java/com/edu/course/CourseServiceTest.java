package com.edu.course;

import com.edu.common.entity.Course;
import com.edu.common.entity.CourseSelection;
import com.edu.common.result.BusinessException;
import com.edu.course.mapper.CourseMapper;
import com.edu.course.mapper.CourseSelectionMapper;
import com.edu.course.service.CourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseMapper courseMapper;

    @Mock
    private CourseSelectionMapper selectionMapper;

    @InjectMocks
    private CourseService courseService;

    private Course course;

    @BeforeEach
    void setUp() {
        course = new Course();
        course.setId(1L);
        course.setCourseName("测试课程");
        course.setTeacherId(2L);
        course.setMaxStudents(50);
        course.setEnrolledCount(10);
    }

    @Test
    void findById_shouldReturnCourse() {
        when(courseMapper.selectById(1L)).thenReturn(course);
        assertEquals("测试课程", courseService.findById(1L).getCourseName());
    }

    @Test
    void findById_shouldThrowWhenNotExists() {
        when(courseMapper.selectById(1L)).thenReturn(null);
        assertThrows(BusinessException.class, () -> courseService.findById(1L));
    }

    @Test
    void selectCourse_shouldCreateNewSelection() {
        when(selectionMapper.selectActiveByStudentAndCourse(1L, 1L)).thenReturn(null);
        when(selectionMapper.selectPendingByStudentAndCourse(1L, 1L)).thenReturn(null);
        when(courseMapper.selectById(1L)).thenReturn(course);
        when(selectionMapper.reactivate(anyLong(), anyLong(), any())).thenReturn(0);

        courseService.selectCourse(1L, 1L);

        verify(selectionMapper).insert(any(CourseSelection.class));
    }

    @Test
    void selectCourse_shouldThrowWhenAlreadyActive() {
        when(selectionMapper.selectActiveByStudentAndCourse(1L, 1L)).thenReturn(new CourseSelection());
        assertThrows(BusinessException.class, () -> courseService.selectCourse(1L, 1L));
    }

    @Test
    void selectCourse_shouldThrowWhenCourseFull() {
        course.setEnrolledCount(50);
        when(selectionMapper.selectActiveByStudentAndCourse(1L, 1L)).thenReturn(null);
        when(selectionMapper.selectPendingByStudentAndCourse(1L, 1L)).thenReturn(null);
        when(courseMapper.selectById(1L)).thenReturn(course);

        assertThrows(BusinessException.class, () -> courseService.selectCourse(1L, 1L));
    }

    @Test
    void approveSelection_shouldSucceed() {
        CourseSelection cs = new CourseSelection();
        cs.setId(1L);
        cs.setCourseId(1L);
        cs.setStatus("0");
        when(selectionMapper.selectById(1L)).thenReturn(cs);
        when(courseMapper.selectById(1L)).thenReturn(course);

        courseService.approveSelection(1L);

        verify(selectionMapper).approveById(1L);
        verify(courseMapper).incrementEnrolled(1L);
    }

    @Test
    void approveSelection_shouldThrowWhenFull() {
        course.setEnrolledCount(50);
        CourseSelection cs = new CourseSelection();
        cs.setId(1L);
        cs.setCourseId(1L);
        cs.setStatus("0");
        when(selectionMapper.selectById(1L)).thenReturn(cs);
        when(courseMapper.selectById(1L)).thenReturn(course);

        assertThrows(BusinessException.class, () -> courseService.approveSelection(1L));
        verify(selectionMapper, never()).approveById(any());
    }

    @Test
    void dropCourse_shouldDropActive() {
        CourseSelection active = new CourseSelection();
        active.setStatus("1");
        when(selectionMapper.selectActiveByStudentAndCourse(1L, 1L)).thenReturn(active);

        courseService.dropCourse(1L, 1L);

        verify(selectionMapper).updateStatus(1L, 1L, "2", LocalDateTime.now());
        verify(courseMapper).decrementEnrolled(1L);
    }

    @Test
    void dropCourse_shouldRejectPending() {
        when(selectionMapper.selectActiveByStudentAndCourse(1L, 1L)).thenReturn(null);
        CourseSelection pending = new CourseSelection();
        pending.setId(5L);
        pending.setStatus("0");
        when(selectionMapper.selectPendingByStudentAndCourse(1L, 1L)).thenReturn(pending);

        courseService.dropCourse(1L, 1L);

        verify(selectionMapper).rejectById(5L);
    }

    @Test
    void dropCourse_shouldThrowWhenNotSelected() {
        when(selectionMapper.selectActiveByStudentAndCourse(1L, 1L)).thenReturn(null);
        when(selectionMapper.selectPendingByStudentAndCourse(1L, 1L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> courseService.dropCourse(1L, 1L));
    }

    @Test
    void rejectSelection_shouldSucceed() {
        when(selectionMapper.rejectById(1L)).thenReturn(1);
        assertDoesNotThrow(() -> courseService.rejectSelection(1L));
    }

    @Test
    void rejectSelection_shouldThrowWhenNotFound() {
        when(selectionMapper.rejectById(1L)).thenReturn(0);
        assertThrows(BusinessException.class, () -> courseService.rejectSelection(1L));
    }
}
