package com.edu.exam;

import com.edu.common.entity.Submission;
import com.edu.common.result.BusinessException;
import com.edu.exam.mapper.SubmissionMapper;
import com.edu.exam.service.SubmissionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubmissionServiceTest {

    @Mock
    private SubmissionMapper submissionMapper;

    @InjectMocks
    private SubmissionService submissionService;

    @Test
    void add_shouldSetSubmitTimeAndStatus() {
        Submission s = new Submission();
        when(submissionMapper.insert(s)).thenReturn(1);
        submissionService.add(s);
        assertNotNull(s.getSubmitTime());
        assertEquals(1, s.getStatus());
    }

    @Test
    void add_shouldThrowWhenInsertFails() {
        when(submissionMapper.insert(any())).thenReturn(0);
        assertThrows(BusinessException.class, () -> submissionService.add(new Submission()));
    }

    @Test
    void grade_shouldCallMapper() {
        when(submissionMapper.grade(anyLong(), anyInt(), anyString(), any())).thenReturn(1);
        assertDoesNotThrow(() -> submissionService.grade(1L, 85, "不错"));
    }

    @Test
    void grade_shouldThrowWhenFails() {
        when(submissionMapper.grade(anyLong(), any(), any(), any())).thenReturn(0);
        assertThrows(BusinessException.class, () -> submissionService.grade(1L, 85, "不错"));
    }

    @Test
    void findById_shouldThrowWhenNotExists() {
        when(submissionMapper.selectById(1L)).thenReturn(null);
        assertThrows(BusinessException.class, () -> submissionService.findById(1L));
    }

    @Test
    void delete_shouldThrowWhenNotFound() {
        when(submissionMapper.deleteById(99L)).thenReturn(0);
        assertThrows(BusinessException.class, () -> submissionService.delete(99L));
    }
}
