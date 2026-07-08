package com.edu.course.service;

import com.edu.common.entity.Chapter;
import com.edu.common.entity.Course;
import com.edu.common.entity.KnowledgePoint;
import com.edu.course.mapper.ChapterMapper;
import com.edu.course.mapper.CourseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ChapterService {

    @Autowired
    private ChapterMapper chapterMapper;

    @Autowired
    private CourseMapper courseMapper;

    public List<Chapter> getChaptersWithPoints(Long courseId) {
        List<Chapter> chapters = chapterMapper.selectByCourse(courseId);
        for (Chapter ch : chapters) {
            ch.setKnowledgePoints(chapterMapper.selectKnowledgePoints(ch.getId()));
        }
        return chapters;
    }

    public List<Chapter> autoGenerate(Long courseId) {
        chapterMapper.deleteKnowledgePointsByCourse(courseId);
        chapterMapper.deleteByCourse(courseId);

        Course course = courseMapper.selectById(courseId);
        if (course == null) return new ArrayList<>();

        String desc = course.getDescription();
        String name = course.getCourseName();

        List<String[]> chapterDefs = generateChapterDefs(name, desc);

        List<Chapter> chapters = new ArrayList<>();
        for (int i = 0; i < chapterDefs.size(); i++) {
            String[] def = chapterDefs.get(i);
            Chapter ch = new Chapter();
            ch.setCourseId(courseId);
            ch.setTitle(def[0]);
            ch.setSummary(def.length > 1 ? def[1] : "");
            ch.setSortOrder(i + 1);
            chapterMapper.insert(ch);

            List<String> kpList = generateKnowledgePoints(name, def[0], i);
            List<KnowledgePoint> kps = new ArrayList<>();
            for (int j = 0; j < kpList.size(); j++) {
                KnowledgePoint kp = new KnowledgePoint();
                kp.setChapterId(ch.getId());
                kp.setContent(kpList.get(j));
                kp.setImportance(j < 2 ? "high" : "medium");
                kp.setSortOrder(j + 1);
                kps.add(kp);
            }
            if (!kps.isEmpty()) {
                chapterMapper.batchInsertKnowledgePoints(kps);
            }
            ch.setKnowledgePoints(kps);
            chapters.add(ch);
        }
        return chapters;
    }

    private List<String[]> generateChapterDefs(String name, String desc) {
        if (desc != null && desc.length() > 4) {
            String[] parts = desc.split("[，,、；;]");
            if (parts.length >= 3) {
                List<String[]> defs = new ArrayList<>();
                for (int i = 0; i < Math.min(parts.length, 6); i++) {
                    String t = parts[i].trim();
                    if (t.length() > 20) t = t.substring(0, 20);
                    defs.add(new String[]{t, "本章涵盖" + t + "相关知识点"});
                }
                return defs;
            }
        }

        return defaultChapters(name);
    }

    private List<String[]> defaultChapters(String name) {
        List<String[]> list = new ArrayList<>();
        if (name.contains("Java") || name.contains("java")) {
            list.addAll(Arrays.asList(
                new String[]{"Java语言基础", "数据类型、运算符、流程控制等基础语法"},
                new String[]{"面向对象编程", "类、对象、封装、继承、多态"},
                new String[]{"异常与常用类库", "异常处理机制与常用工具类"},
                new String[]{"输入输出与集合", "IO流与集合框架"}
            ));
        } else if (name.contains("数据") || name.contains("算法")) {
            list.addAll(Arrays.asList(
                new String[]{"线性结构", "顺序表、链表、栈与队列"},
                new String[]{"树与二叉树", "树结构定义、遍历与哈夫曼树"},
                new String[]{"图", "图的存储、遍历与最短路径"},
                new String[]{"查找与排序", "二分查找、哈希表与常见排序算法"}
            ));
        } else if (name.contains("数据库") || name.contains("SQL")) {
            list.addAll(Arrays.asList(
                new String[]{"关系数据库基础", "关系模型、完整性约束"},
                new String[]{"SQL语言", "DDL、DML、DQL与多表查询"},
                new String[]{"规范化设计", "范式理论与数据库设计"},
                new String[]{"事务与并发控制", "事务特性、封锁协议"}
            ));
        } else {
            list.addAll(Arrays.asList(
                new String[]{name + "导论", "课程概述与基础知识"},
                new String[]{name + "核心概念", "核心理论与方法"},
                new String[]{name + "应用实践", "实践应用与案例分析"},
                new String[]{name + "进阶专题", "进阶知识与综合训练"}
            ));
        }
        return list;
    }

    private List<String> generateKnowledgePoints(String courseName, String chapterTitle, int chapterIndex) {
        List<String> kps = new ArrayList<>();
        kps.add(chapterTitle + "核心概念");
        kps.add(chapterTitle + "基本原理");
        if (chapterIndex % 2 == 0) {
            kps.add(chapterTitle + "典型应用场景");
            kps.add(chapterTitle + "常见问题分析");
        } else {
            kps.add(chapterTitle + "实践操作方法");
            kps.add(chapterTitle + "实例演示");
        }
        kps.add(chapterTitle + "综合练习");
        return kps;
    }

}
