package com.edu.course.controller;

import com.edu.common.annotation.RequireRole;
import com.edu.common.entity.Material;
import com.edu.common.entity.MaterialReadRecord;
import com.edu.common.result.Result;
import com.edu.course.service.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("course/material")
public class MaterialController {

    @Autowired
    private MaterialService materialService;

    @GetMapping("/list/{courseId}")
    @RequireRole({1, 2, 3})
    public Result<List<Material>> list(@PathVariable Long courseId) {
        return Result.ok(materialService.listByCourse(courseId));
    }

    @GetMapping("/{id}")
    @RequireRole({1, 2, 3})
    public Result<Material> getById(@PathVariable Long id) {
        return Result.ok(materialService.findById(id));
    }

    @PostMapping
    @RequireRole(2)
    public Result<Void> add(@RequestBody Material material) {
        materialService.add(material);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @RequireRole(2)
    public Result<Void> delete(@PathVariable Long id) {
        materialService.delete(id);
        return Result.ok();
    }

    @PostMapping("/start-read")
    @RequireRole(1)
    public Result<MaterialReadRecord> startRead(@RequestParam Long materialId, @RequestParam Long studentId) {
        return Result.ok(materialService.startReading(materialId, studentId));
    }

    @PostMapping("/complete-read")
    @RequireRole(1)
    public Result<Void> completeRead(@RequestParam Long materialId, @RequestParam Long studentId) {
        materialService.completeReading(materialId, studentId);
        return Result.ok();
    }

}
