package com.edu.user;

import com.edu.common.entity.User;
import com.edu.common.result.BusinessException;
import com.edu.user.mapper.UserMapper;
import com.edu.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void login_shouldReturnTokenAndUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("zhangsan");
        user.setPassword("123456");
        user.setRole(1);
        when(userMapper.selectByUsername("zhangsan")).thenReturn(user);

        var result = userService.login("zhangsan", "123456");

        assertNotNull(result.get("token"));
        assertEquals("zhangsan", ((User) result.get("user")).getUsername());
    }

    @Test
    void login_shouldThrowWhenWrongPassword() {
        User user = new User();
        user.setUsername("zhangsan");
        user.setPassword("123456");
        when(userMapper.selectByUsername("zhangsan")).thenReturn(user);

        assertThrows(BusinessException.class, () -> userService.login("zhangsan", "wrong"));
    }

    @Test
    void login_shouldThrowWhenUserNotFound() {
        when(userMapper.selectByUsername("nobody")).thenReturn(null);
        assertThrows(BusinessException.class, () -> userService.login("nobody", "pwd"));
    }

    @Test
    void add_shouldCallInsert() {
        User user = new User();
        when(userMapper.insert(user)).thenReturn(1);
        assertDoesNotThrow(() -> userService.add(user));
    }

    @Test
    void add_shouldThrowWhenInsertFails() {
        when(userMapper.insert(any())).thenReturn(0);
        assertThrows(BusinessException.class, () -> userService.add(new User()));
    }

    @Test
    void delete_shouldCallDeleteById() {
        when(userMapper.deleteById(1L)).thenReturn(1);
        assertDoesNotThrow(() -> userService.delete(1L));
    }

    @Test
    void delete_shouldThrowWhenNotFound() {
        when(userMapper.deleteById(99L)).thenReturn(0);
        assertThrows(BusinessException.class, () -> userService.delete(99L));
    }

    @Test
    void findById_shouldReturnUser() {
        User user = new User();
        user.setId(1L);
        when(userMapper.selectById(1L)).thenReturn(user);
        assertEquals(1L, userService.findById(1L).getId());
    }

    @Test
    void findList_shouldReturnList() {
        when(userMapper.selectList(any())).thenReturn(List.of(new User(), new User()));
        assertEquals(2, userService.findList(new User()).size());
    }
}
