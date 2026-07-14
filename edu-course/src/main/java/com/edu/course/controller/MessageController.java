package com.edu.course.controller;

import com.edu.common.annotation.RequireRole;
import com.edu.common.entity.Message;
import com.edu.common.result.Result;
import com.edu.course.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping("/send")
    @RequireRole({1, 2})
    public Result<Message> send(@RequestBody Message message) {
        return Result.ok(messageService.send(message));
    }

    @GetMapping("/conversations")
    @RequireRole({1, 2})
    public Result<List<Message>> conversations(@RequestParam Long userId) {
        return Result.ok(messageService.listConversations(userId));
    }

    @GetMapping("/conversation")
    @RequireRole({1, 2})
    public Result<List<Message>> conversation(@RequestParam Long userId, @RequestParam Long otherId) {
        return Result.ok(messageService.getConversation(userId, otherId));
    }

    @GetMapping("/unread-count")
    @RequireRole({1, 2})
    public Result<Map<String, Long>> unreadCount(@RequestParam Long userId) {
        return Result.ok(Map.of("count", messageService.countUnread(userId)));
    }
}
