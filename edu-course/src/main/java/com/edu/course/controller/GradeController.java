package com.edu.course.controller;

import com.edu.common.annotation.RequireRole;
import com.edu.common.result.Result;
import com.edu.course.mapper.GradeMapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.util.*;

@RestController
@RequestMapping("grade")
public class GradeController {

    @Autowired
    private GradeMapper gradeMapper;

    @GetMapping("/stats/{courseId}")
    @RequireRole(2)
    public Result<Map<String, Object>> stats(@PathVariable Long courseId) {
        List<Map<String, Object>> grades = gradeMapper.selectCourseGrades(courseId);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("total", grades.size());

        int[] dist = new int[5];
        int passCount = 0;
        int maxScore = 0;
        int minScore = 100;
        double sum = 0;
        int scoredCount = 0;

        for (Map<String, Object> g : grades) {
            Object scoreObj = g.get("finalScore");
            if (scoreObj == null) continue;
            int score = ((Number) scoreObj).intValue();
            sum += score;
            scoredCount++;
            if (score >= 60) passCount++;
            if (score > maxScore) maxScore = score;
            if (score < minScore) minScore = score;
            if (score >= 90) dist[0]++;
            else if (score >= 80) dist[1]++;
            else if (score >= 70) dist[2]++;
            else if (score >= 60) dist[3]++;
            else dist[4]++;
        }

        result.put("scoredCount", scoredCount);
        result.put("passCount", passCount);
        result.put("passRate", scoredCount > 0 ? Math.round(passCount * 100.0 / scoredCount) : 0);
        result.put("avgScore", scoredCount > 0 ? Math.round(sum / scoredCount * 10.0) / 10.0 : 0);
        result.put("maxScore", maxScore);
        result.put("minScore", minScore == 100 && scoredCount == 0 ? 0 : minScore);

        Map<String, Integer> distribution = new LinkedHashMap<>();
        distribution.put("优秀(90-100)", dist[0]);
        distribution.put("良好(80-89)", dist[1]);
        distribution.put("中等(70-79)", dist[2]);
        distribution.put("及格(60-69)", dist[3]);
        distribution.put("不及格(0-59)", dist[4]);
        result.put("distribution", distribution);

        result.put("students", grades);
        return Result.ok(result);
    }

    @GetMapping("/trend")
    @RequireRole(1)
    public Result<List<Map<String, Object>>> trend(@RequestParam Long courseId, @RequestParam Long studentId) {
        return Result.ok(gradeMapper.selectStudentGradeTrend(courseId, studentId));
    }

    @GetMapping("/final")
    @RequireRole(1)
    public Result<Integer> finalScore(@RequestParam Long courseId, @RequestParam Long studentId) {
        return Result.ok(gradeMapper.selectFinalScore(courseId, studentId));
    }

    @GetMapping("/export/{courseId}")
    @RequireRole(2)
    public ResponseEntity<byte[]> export(@PathVariable Long courseId, @RequestParam String courseName) throws Exception {
        List<Map<String, Object>> grades = gradeMapper.selectCourseGrades(courseId);

        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("成绩表");
        Row header = sheet.createRow(0);
        String[] cols = {"序号", "学生姓名", "总评成绩", "状态"};
        for (int i = 0; i < cols.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(cols[i]);
            cell.setCellStyle(boldStyle(wb));
        }

        int rowIdx = 1;
        for (Map<String, Object> g : grades) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(rowIdx - 1);
            row.createCell(1).setCellValue((String) g.get("studentName"));
            Object score = g.get("finalScore");
            row.createCell(2).setCellValue(score != null ? ((Number) score).doubleValue() : 0);
            String status = "1".equals(g.get("selectionStatus")) ? "在读" : "已结课";
            row.createCell(3).setCellValue(status);
        }

        for (int i = 0; i < cols.length; i++) sheet.autoSizeColumn(i);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        wb.write(out);
        wb.close();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", courseName + "_成绩.xlsx");
        return ResponseEntity.ok().headers(headers).body(out.toByteArray());
    }

    private CellStyle boldStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }
}
