package com.edu.user.controller;

import com.edu.common.annotation.RequireRole;
import com.edu.common.entity.User;
import com.edu.common.page.PageParam;
import com.edu.common.page.PageResult;
import com.edu.common.result.Result;
import com.edu.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService;

    @Value("${server.port}")
    private String port;
    private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(getClass());

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        log.info("当前服务端口号：{}", port);
        return Result.ok(userService.login(body.get("username"), body.get("password")));
    }

    @PostMapping("/register")
    public Result<Map<String, Object>> register(@RequestBody Map<String, String> body) {
        User user = new User();
        user.setUsername(body.get("username"));
        user.setPassword(body.get("password"));
        user.setRealName(body.get("realName"));
        return Result.ok(userService.register(user));
    }

    @GetMapping("/{id}")
    @RequireRole({1, 2, 3})
    public Result<User> getById(@PathVariable Long id) {
        return Result.ok(userService.findById(id));
    }

    @GetMapping("/list")
    @RequireRole({1, 2, 3})
    public Result<List<User>> list(User user) {
        return Result.ok(userService.findList(user));
    }

    @GetMapping("/page")
    @RequireRole({1, 2, 3})
    public Result<PageResult<User>> page(PageParam param, User user) {
        return Result.ok(userService.page(param, user));
    }

    @PostMapping
    @RequireRole(3)
    public Result<Void> add(@Valid @RequestBody User user) {
        userService.add(user);
        return Result.ok();
    }

    @PutMapping("/profile")
    @RequireRole({1, 2})
    public Result<Void> updateProfile(HttpServletRequest request, @RequestBody User user) {
        Long userId = (Long) request.getAttribute("userId");
        user.setId(userId);
        userService.updateProfile(user);
        return Result.ok();
    }

    @PutMapping
    @RequireRole(3)
    public Result<Void> update(@Valid @RequestBody User user) {
        userService.update(user);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @RequireRole(3)
    public Result<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return Result.ok();
    }
}
