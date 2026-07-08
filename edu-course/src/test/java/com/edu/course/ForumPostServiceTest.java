package com.edu.course;

import com.edu.common.entity.ForumPost;
import com.edu.common.entity.ForumReply;
import com.edu.common.result.BusinessException;
import com.edu.course.mapper.ForumPostMapper;
import com.edu.course.mapper.ForumReplyMapper;
import com.edu.course.service.ForumPostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ForumPostServiceTest {

    @Mock
    private ForumPostMapper forumPostMapper;

    @Mock
    private ForumReplyMapper forumReplyMapper;

    @InjectMocks
    private ForumPostService forumPostService;

    @Test
    void addReply_shouldInsertAndIncrementCount() {
        ForumReply reply = new ForumReply();
        reply.setPostId(1L);
        when(forumReplyMapper.insert(reply)).thenReturn(1);

        forumPostService.addReply(reply);

        verify(forumPostMapper).incrementReply(1L);
    }

    @Test
    void addReply_shouldThrowWhenInsertFails() {
        when(forumReplyMapper.insert(any())).thenReturn(0);
        assertThrows(BusinessException.class, () -> forumPostService.addReply(new ForumReply()));
        verify(forumPostMapper, never()).incrementReply(any());
    }

    @Test
    void add_shouldCallInsert() {
        ForumPost post = new ForumPost();
        when(forumPostMapper.insert(post)).thenReturn(1);
        assertDoesNotThrow(() -> forumPostService.add(post));
    }

    @Test
    void add_shouldThrowWhenInsertFails() {
        when(forumPostMapper.insert(any())).thenReturn(0);
        assertThrows(BusinessException.class, () -> forumPostService.add(new ForumPost()));
    }

    @Test
    void delete_shouldCallDeleteById() {
        when(forumPostMapper.deleteById(1L)).thenReturn(1);
        assertDoesNotThrow(() -> forumPostService.delete(1L));
    }

    @Test
    void delete_shouldThrowWhenNotFound() {
        when(forumPostMapper.deleteById(99L)).thenReturn(0);
        assertThrows(BusinessException.class, () -> forumPostService.delete(99L));
    }

    @Test
    void findById_shouldThrowWhenNotExists() {
        when(forumPostMapper.selectById(1L)).thenReturn(null);
        assertThrows(BusinessException.class, () -> forumPostService.findById(1L));
    }

    @Test
    void findReplies_shouldDelegate() {
        forumPostService.findReplies(1L);
        verify(forumReplyMapper).selectByPostId(1L);
    }
}
