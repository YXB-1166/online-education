package com.edu.exam;

import com.edu.common.entity.Assignment;
import com.edu.common.result.BusinessException;
import com.edu.exam.mapper.AssignmentMapper;
import com.edu.exam.service.AssignmentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssignmentServiceTest {

    @Mock
    private AssignmentMapper assignmentMapper;

    @InjectMocks
    private AssignmentService assignmentService;

    @Test
    void add_shouldCallInsert() {
        Assignment a = new Assignment();
        when(assignmentMapper.insert(a)).thenReturn(1);
        assertDoesNotThrow(() -> assignmentService.add(a));
    }

    @Test
    void add_shouldThrowWhenInsertFails() {
        when(assignmentMapper.insert(any())).thenReturn(0);
        assertThrows(BusinessException.class, () -> assignmentService.add(new Assignment()));
    }

    @Test
    void delete_shouldCallDeleteById() {
        when(assignmentMapper.deleteById(1L)).thenReturn(1);
        assertDoesNotThrow(() -> assignmentService.delete(1L));
    }

    @Test
    void delete_shouldThrowWhenNotFound() {
        when(assignmentMapper.deleteById(99L)).thenReturn(0);
        assertThrows(BusinessException.class, () -> assignmentService.delete(99L));
    }

    @Test
    void findById_shouldThrowWhenNotExists() {
        when(assignmentMapper.selectById(1L)).thenReturn(null);
        assertThrows(BusinessException.class, () -> assignmentService.findById(1L));
    }

    @Test
    void findList_shouldReturnList() {
        when(assignmentMapper.selectList(any())).thenReturn(List.of(new Assignment()));
        assertEquals(1, assignmentService.findList(new Assignment()).size());
    }
}
