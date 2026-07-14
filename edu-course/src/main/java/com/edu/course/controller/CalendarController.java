package com.edu.course.controller;

import com.edu.common.result.Result;
import com.edu.course.mapper.CalendarMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("calendar")
public class CalendarController {

    @Autowired
    private CalendarMapper calendarMapper;

    @GetMapping("/events")
    public Result<List<Map<String, Object>>> events(@RequestParam Long userId, @RequestParam Integer role) {
        if (role == 2) {
            return Result.ok(calendarMapper.selectEventsByTeacher(userId));
        }
        return Result.ok(calendarMapper.selectEventsByStudent(userId));
    }
}
