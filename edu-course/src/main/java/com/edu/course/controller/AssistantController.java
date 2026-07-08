package com.edu.course.controller;

import com.edu.common.annotation.RequireRole;
import com.edu.common.result.Result;
import com.edu.course.mapper.AssistantMapper;
import com.edu.course.service.AssistantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("assistant")
public class AssistantController {

    @Autowired
    private AssistantService assistantService;

    @Autowired
    private AssistantMapper assistantMapper;

    @PostMapping("/ask")
    @RequireRole({1, 2, 3})
    public Result<Map<String, Object>> ask(@RequestBody Map<String, Object> body) {
        String question = (String) body.getOrDefault("question", "");
        Long userId = Long.valueOf(body.get("userId").toString());
        int role = Integer.parseInt(body.get("role").toString());
        Map<String, Object> answer = assistantService.ask(question, userId, role);
        return Result.ok(answer);
    }

    @GetMapping("/progress")
    @RequireRole(1)
    public Result<List<Map<String, Object>>> progress(@RequestParam Long studentId) {
        return Result.ok(assistantMapper.learningProgress(studentId));
    }
}
