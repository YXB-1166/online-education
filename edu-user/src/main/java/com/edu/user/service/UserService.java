package com.edu.user.service;

import com.edu.common.entity.User;
import com.edu.common.page.PageParam;
import com.edu.common.page.PageResult;
import com.edu.common.result.BusinessException;
import com.edu.common.service.BaseService;
import com.edu.common.util.JwtUtil;
import com.edu.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService extends BaseService {

    @Autowired
    private UserMapper userMapper;

    public User findById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            log.warn("用户不存在: id={}", id);
            throw new BusinessException("用户不存在");
        }
        return user;
    }

    public List<User> findList(User user) {
        return userMapper.selectList(user);
    }

    public PageResult<User> page(PageParam param, User user) {
        long total = userMapper.countList(user);
        List<User> list = userMapper.selectPage(user, param.getOffset(), param.getPageSize());
        log.info("用户分页: total={}, page={}/{}", total, param.getPageNum(), param.getPageSize());
        return new PageResult<>(list, total, param.getPageNum(), param.getPageSize());
    }

    public void add(User user) {
        if (userMapper.insert(user) == 0) {
            log.warn("新增用户失败: username={}", user.getUsername());
            throw new BusinessException("新增用户失败");
        }
        log.info("新增用户: id={}, username={}", user.getId(), user.getUsername());
    }

    public void updateProfile(User user) {
        if (userMapper.updateProfile(user) == 0) {
            log.warn("更新个人资料失败: id={}", user.getId());
            throw new BusinessException("更新个人资料失败");
        }
        log.info("更新个人资料: id={}", user.getId());
    }

    public void update(User user) {
        if (userMapper.update(user) == 0) {
            log.warn("更新用户失败: id={}", user.getId());
            throw new BusinessException("更新用户失败");
        }
        log.info("更新用户: id={}", user.getId());
    }

    public void delete(Long id) {
        if (userMapper.deleteById(id) == 0) {
            log.warn("删除用户失败: id={}", id);
            throw new BusinessException("删除用户失败");
        }
        log.info("删除用户: id={}", id);
    }

    public Map<String, Object> login(String username, String password) {
        User user = userMapper.selectByUsername(username);
        if (user == null || !user.getPassword().equals(password)) {
            log.warn("登录失败: username={}", username);
            throw new BusinessException("用户名或密码错误");
        }
        String token = JwtUtil.generate(user.getId(), user.getRole());
        log.info("登录成功: id={}, username={}", user.getId(), username);
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", user);
        return result;
    }
}
