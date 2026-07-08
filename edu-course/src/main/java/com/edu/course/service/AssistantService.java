package com.edu.course.service;

import com.edu.common.service.BaseService;
import com.edu.course.mapper.AssistantMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AssistantService extends BaseService {

    @Autowired
    private AssistantMapper assistantMapper;

    public Map<String, Object> ask(String question, Long userId, int role) {
        Map<String, Object> result = new HashMap<>();
        String q = question == null ? "" : question.trim();

        if (role == 1) {
            return handleStudentQuery(q, userId);
        } else if (role == 2) {
            return handleTeacherQuery(q, userId);
        } else {
            result.put("answer", "您好！我是智能课程助手，请问有什么可以帮助您的？");
            result.put("suggestions", Arrays.asList("查看课程总数", "查看学生总数", "查看教师总数"));
            return result;
        }
    }

    private Map<String, Object> handleStudentQuery(String q, Long userId) {
        Map<String, Object> result = new HashMap<>();
        List<String> suggestions = new ArrayList<>();

        if (q.isEmpty()) {
            result.put("answer", "你好！我是智能选课助手 🤖\n\n我可以帮你：\n1️⃣ 推荐适合你的课程\n2️⃣ 查询课程详情\n3️⃣ 查看已选课程\n4️⃣ 解答选课疑问\n\n你想了解什么？");
            suggestions.add("推荐课程");
            suggestions.add("查看已选课程");
            suggestions.add("有什么课程？");
            result.put("suggestions", suggestions);
            return result;
        }

        boolean handled = false;

        if (containsAny(q, "推荐", "建议", "选什么", "学什么")) {
            List<Map<String, Object>> recs = assistantMapper.recommendCourses(userId, 5);
            if (recs.isEmpty()) {
                result.put("answer", "目前没有更多可推荐的课程了，你已经选了很多课！可以等新课程发布后再来看看。");
            } else {
                StringBuilder sb = new StringBuilder("根据你的情况，为你推荐以下课程：\n\n");
                for (int i = 0; i < recs.size(); i++) {
                    Map<String, Object> c = recs.get(i);
                    sb.append(String.format("%d. 【%s】by %s (%d学分, 已选%d/%d人)\n",
                            i + 1, c.get("course_name"), c.get("teacherName"),
                            c.get("credit"), c.get("enrolled_count"), c.get("max_students")));
                    if (c.get("description") != null) {
                        String desc = c.get("description").toString();
                        sb.append("   ").append(desc.length() > 60 ? desc.substring(0, 60) + "..." : desc).append("\n");
                    }
                }
                sb.append("\n感兴趣的话可以点击课程查看详情并选课！");
                result.put("answer", sb.toString());
                result.put("suggestions", Arrays.asList(
                        "推荐更多课程",
                        "查看" + recs.get(0).get("course_name") + "详情",
                        "查看已选课程"
                ));
            }
            handled = true;
        }
        else if (containsAny(q, "已选", "我的课程", "我的选课")) {
            List<Map<String, Object>> selections = assistantMapper.mySelections(userId);
            if (selections.isEmpty()) {
                result.put("answer", "你还没有选课哦，快去课程广场看看吧！");
                suggestions.add("推荐课程");
                suggestions.add("查看课程列表");
            } else {
                StringBuilder sb = new StringBuilder("你的选课情况如下：\n\n");
                int active = 0;
                for (Map<String, Object> s : selections) {
                    String selStatus = s.get("selectionStatus").toString();
                    String label;
                    switch (selStatus) {
                        case "0": label = "⏳ 待审核"; break;
                        case "1": label = "✅ 已选课"; active++; break;
                        case "2": label = "❌ 已退选"; break;
                        case "4": label = "🚫 未通过"; break;
                        default: label = "未知";
                    }
                    sb.append(String.format("· 【%s】%s (%d学分)\n", s.get("course_name"), label, s.get("credit")));
                }
                sb.append(String.format("\n共 %d 门课程，其中 %d 门选课成功。", selections.size(), active));
                result.put("answer", sb.toString());
            }
            handled = true;
        }
        else if (containsAny(q, "详情", "怎么样", "是什么", "介绍")) {
            String keyword = q.replaceAll("详情|怎么样|是什么|介绍|查看|课程", "").trim();
            if (keyword.isEmpty()) {
                keyword = "Java";
            }
            List<Map<String, Object>> courses = assistantMapper.searchCourses(keyword);
            if (courses.isEmpty()) {
                result.put("answer", "没有找到包含\"" + keyword + "\"的课程，试试其他关键词吧。");
                suggestions.add("推荐课程");
                suggestions.add("查看所有课程");
            } else {
                StringBuilder sb = new StringBuilder("找到以下相关课程：\n\n");
                for (int i = 0; i < Math.min(3, courses.size()); i++) {
                    Map<String, Object> c = courses.get(i);
                    sb.append(String.format("【%s】by %s\n", c.get("course_name"), c.get("teacherName")));
                    sb.append(String.format("  学分：%d  |  人数：%d/%d  |  状态：%s\n",
                            c.get("credit"), c.get("enrolled_count"), c.get("max_students"),
                            formatCourseStatus(c.get("status").toString())));
                    if (c.get("description") != null) {
                        sb.append("  简介：").append(c.get("description")).append("\n");
                    }
                    sb.append("\n");
                }
                result.put("answer", sb.toString());
                result.put("suggestions", Arrays.asList("推荐课程", "查看已选课程", "选课建议"));
            }
            handled = true;
        }
        else if (containsAny(q, "老师", "教师", "谁教")) {
            String keyword = q.replaceAll("老师|教师|谁教|哪个老师", "").trim();
            List<Map<String, Object>> courses;
            if (!keyword.isEmpty()) {
                courses = assistantMapper.searchCourses(keyword);
            } else {
                courses = assistantMapper.listActiveCourses();
            }
            if (courses.isEmpty()) {
                result.put("answer", "没有找到相关信息。");
            } else {
                Set<String> seen = new HashSet<>();
                StringBuilder sb = new StringBuilder("以下是任课教师信息：\n\n");
                for (Map<String, Object> c : courses) {
                    String tn = c.get("teacherName").toString();
                    if (seen.add(tn)) {
                        sb.append(String.format("· %s  —  %s\n", tn, c.get("course_name")));
                    }
                }
                result.put("answer", sb.toString());
            }
            handled = true;
        }
        else if (containsAny(q, "学分")) {
            List<Map<String, Object>> selections = assistantMapper.mySelections(userId);
            if (selections.isEmpty()) {
                result.put("answer", "你还没有选课，暂无学分信息。快去课程广场选课吧！");
                suggestions.add("推荐课程");
                suggestions.add("查看课程列表");
            } else {
                int earnedCredit = 0, selectedCredit = 0, earnedCount = 0, selectedCount = 0;
                StringBuilder earnedSb = new StringBuilder();
                StringBuilder selectedSb = new StringBuilder();
                for (Map<String, Object> s : selections) {
                    String selStatus = s.get("selectionStatus").toString();
                    if (!"1".equals(selStatus) && !"0".equals(selStatus)) continue;
                    String courseStatus = s.get("courseStatus").toString();
                    int credit = ((Number) s.get("credit")).intValue();
                    String name = s.get("course_name").toString();
                    if ("2".equals(courseStatus) || "3".equals(courseStatus)) {
                        earnedCredit += credit;
                        earnedCount++;
                        earnedSb.append(String.format("· %s — %d学分\n", name, credit));
                    } else {
                        selectedCredit += credit;
                        selectedCount++;
                        selectedSb.append(String.format("· %s — %d学分\n", name, credit));
                    }
                }
                StringBuilder sb = new StringBuilder("你的学分统计：\n\n");
                sb.append("📖 已修学分（授课中/已结课）：").append(earnedCredit).append("学分\n");
                if (earnedCount > 0) sb.append(earnedSb);
                sb.append("\n📋 已选学分（即将开课）：").append(selectedCredit).append("学分\n");
                if (selectedCount > 0) sb.append(selectedSb);
                sb.append(String.format("\n共 %d 门课程，合计 %d 学分。", earnedCount + selectedCount, earnedCredit + selectedCredit));
                result.put("answer", sb.toString());
                result.put("suggestions", Arrays.asList("推荐课程", "查看已选课程"));
            }
            handled = true;
        }
        else if (containsAny(q, "进度", "学习进度", "学习情况", "学习状态", "课程进度")) {
            List<Map<String, Object>> progress = assistantMapper.learningProgress(userId);
            if (progress.isEmpty()) {
                result.put("answer", "你还没有选课或没有作业记录。快去选课开始学习吧！📚");
                suggestions.add("推荐课程");
                suggestions.add("查看已选课程");
            } else {
                StringBuilder sb = new StringBuilder("📊 你的学习进度报告\n\n");
                List<String> riskCourses = new ArrayList<>();
                for (Map<String, Object> p : progress) {
                    String name = p.get("course_name").toString();
                    int total = ((Number) p.get("totalAssignments")).intValue();
                    int submitted = ((Number) p.get("submittedCount")).intValue();
                    int graded = ((Number) p.get("gradedCount")).intValue();
                    int below60 = ((Number) p.get("below60Count")).intValue();
                    double avg = ((Number) p.get("avgScore")).doubleValue();

                    sb.append(String.format("【%s】\n", name));
                    sb.append(String.format("  作业进度：%d/%d 已提交", submitted, total));
                    if (total > 0) {
                        sb.append(String.format("（%d%%）", submitted * 100 / total));
                    }
                    sb.append("\n");
                    sb.append(String.format("  已批改：%d 份 | 平均分：%.1f\n", graded, avg));

                    boolean risk = false;
                    if (total > 0 && submitted < total) {
                        sb.append("  ⚠️ 有未提交的作业，请注意截止时间！\n");
                        risk = true;
                    }
                    if (below60 > 0) {
                        sb.append(String.format("  🔴 %d 次作业低于60分，需要加强复习！\n", below60));
                        risk = true;
                    }
                    if (avg > 0 && avg < 60 && graded >= total / 2) {
                        sb.append("  🚨 挂科风险高！建议立即寻求帮助，认真复习课程内容。\n");
                        risk = true;
                    }
                    if (risk) {
                        riskCourses.add(name);
                    }
                    sb.append("\n");
                }
                result.put("answer", sb.toString().trim());
                if (riskCourses.isEmpty()) {
                    result.put("suggestions", Arrays.asList("推荐课程", "查看已选课程", "学分信息"));
                } else {
                    result.put("suggestions", Arrays.asList("推荐课程", "查看已选课程", "如何提高成绩"));
                }
            }
            handled = true;
        }
        else if (containsAny(q, "提高", "如何学习", "怎么学", "挂科", "不及格", "成绩差")) {
            List<Map<String, Object>> progress = assistantMapper.learningProgress(userId);
            StringBuilder sb = new StringBuilder("💡 学习建议\n\n");
            sb.append("根据你的学习情况，给你以下建议：\n\n");
            sb.append("1️⃣ 按时完成作业 — 每次作业都是巩固知识的好机会\n");
            sb.append("2️⃣ 重视低分作业 — 仔细回顾错题，理解错误原因\n");
            sb.append("3️⃣ 多与老师交流 — 有问题及时在课程论坛提问\n");
            sb.append("4️⃣ 合理规划时间 — 为每门课分配固定学习时间\n");
            sb.append("5️⃣ 利用课程资源 — 查看课程资料和课件\n\n");
            boolean hasRisk = false;
            for (Map<String, Object> p : progress) {
                int total = ((Number) p.get("totalAssignments")).intValue();
                int submitted = ((Number) p.get("submittedCount")).intValue();
                int below60 = ((Number) p.get("below60Count")).intValue();
                double avg = ((Number) p.get("avgScore")).doubleValue();
                if (total > submitted || below60 > 0 || (avg > 0 && avg < 60)) {
                    hasRisk = true;
                    sb.append(String.format("⚠️ 【%s】需要重点关注！\n", p.get("course_name")));
                }
            }
            if (!hasRisk) {
                sb.append("目前你的学习状态不错，继续保持！🎉");
            } else {
                sb.append("\n加油！只要肯花时间，一定能提高成绩 💪");
            }
            result.put("answer", sb.toString());
            suggestions.add("学习进度");
            suggestions.add("查看已选课程");
            result.put("suggestions", suggestions);
            handled = true;
        }
        else if (containsAny(q, "hello", "hi", "你好", "在吗")) {
            result.put("answer", "你好呀！我是你的智能选课助手，有什么可以帮助你的吗？\n\n你可以问我：\n• 推荐课程\n• 查看已选课程\n• 课程详情\n• 学分信息\n• 学习进度");
            suggestions.add("推荐课程");
            suggestions.add("查看已选课程");
            suggestions.add("学习进度");
            result.put("suggestions", suggestions);
            handled = true;
        }

        if (!handled) {
            List<Map<String, Object>> courses = assistantMapper.searchCourses(q);
            if (!courses.isEmpty()) {
                StringBuilder sb = new StringBuilder("关于\"" + q + "\"，为你找到以下课程：\n\n");
                for (Map<String, Object> c : courses) {
                    sb.append(String.format("【%s】by %s\n", c.get("course_name"), c.get("teacherName")));
                    if (c.get("description") != null) {
                        sb.append("  ").append(c.get("description")).append("\n");
                    }
                }
                sb.append("\n你可以输入课程名查看更多详情，或者让我为你推荐课程。");
                result.put("answer", sb.toString());
                result.put("suggestions", Arrays.asList("推荐课程", "查看已选课程"));
            } else {
                result.put("answer", "抱歉，我没有完全理解你的问题。你可以试试以下问题：\n• 推荐课程\n• 查看已选课程\n• Java课程详情\n• 有哪些老师");
                suggestions.add("推荐课程");
                suggestions.add("查看已选课程");
                suggestions.add("学分信息");
                result.put("suggestions", suggestions);
            }
        }

        return result;
    }

    private Map<String, Object> handleTeacherQuery(String q, Long userId) {
        Map<String, Object> result = new HashMap<>();
        List<String> suggestions = new ArrayList<>();

        if (q.isEmpty()) {
            result.put("answer", "老师您好！我是智能教学助手 🤖\n\n我可以帮你：\n1️⃣ 课程统计概览\n2️⃣ 待批改作业\n3️⃣ 查询学生名单\n4️⃣ 待审批提醒\n\n请问你需要什么帮助？");
            suggestions.add("课程统计");
            suggestions.add("待批改作业");
            suggestions.add("待审批数量");
            result.put("suggestions", suggestions);
            return result;
        }

        boolean handled = false;

        if (containsAny(q, "统计", "概览", "情况")) {
            List<Map<String, Object>> stats = assistantMapper.teacherCourseStats(userId);
            long pending = assistantMapper.countPendingByTeacher(userId);
            int totalEnrolled = 0;
            StringBuilder sb = new StringBuilder("你的课程统计概览：\n\n");
            for (Map<String, Object> s : stats) {
                int ec = ((Number) s.get("enrolled_count")).intValue();
                int mc = ((Number) s.get("max_students")).intValue();
                int pc = ((Number) s.get("pendingCount")).intValue();
                totalEnrolled += ec;
                sb.append(String.format("· 【%s】已选 %d/%d 人", s.get("course_name"), ec, mc));
                if (pc > 0) {
                    sb.append(String.format("（%d 人待审批）", pc));
                }
                sb.append("\n");
            }
            sb.append(String.format("\n📊 共 %d 门课程，已选课学生 %d 人次。", stats.size(), totalEnrolled));
            if (pending > 0) {
                sb.append(String.format("\n⚠️ 你有 %d 条选课申请待审批。", pending));
            }
            result.put("answer", sb.toString());
            result.put("suggestions", Arrays.asList("待审批数量", "选课情况", "学生名单"));
            handled = true;
        }
        else if (containsAny(q, "待审批", "审核", "审批", "pending")) {
            long pending = assistantMapper.countPendingByTeacher(userId);
            if (pending == 0) {
                result.put("answer", "目前没有待审批的选课申请，一切正常！✅");
                suggestions.add("课程统计");
                suggestions.add("选课情况");
            } else {
                result.put("answer", String.format("你有 %d 条选课申请等待审批，快去看看谁选了你的课吧！", pending));
                result.put("suggestions", Arrays.asList("查看待审批", "课程统计", "学生名单"));
            }
            handled = true;
        }
        else if (containsAny(q, "学生名单", "选课学生", "哪些学生", "学生列表")) {
            List<Map<String, Object>> courses = assistantMapper.teacherCourses(userId);
            if (courses.isEmpty()) {
                result.put("answer", "你还没有创建课程。");
            } else {
                String matchedCourse = null;
                Long matchedId = null;
                for (Map<String, Object> c : courses) {
                    String cn = c.get("course_name").toString();
                    if (q.contains(cn)) {
                        matchedCourse = cn;
                        matchedId = (Long) c.get("id");
                        break;
                    }
                }
                if (matchedCourse != null) {
                    List<Map<String, Object>> students = assistantMapper.courseStudents(matchedId);
                    StringBuilder sb = new StringBuilder();
                    sb.append(String.format("【%s】学生名单\n\n", matchedCourse));
                    if (students.isEmpty()) {
                        sb.append("目前还没有学生选这门课。");
                    } else {
                        int active = 0, pending = 0;
                        for (Map<String, Object> s : students) {
                            String st = s.get("status").toString();
                            if ("1".equals(st)) active++;
                            else if ("0".equals(st)) pending++;
                        }
                        sb.append(String.format("共 %d 条记录（✅已选 %d 人，⏳待审 %d 人）\n\n", students.size(), active, pending));
                        for (Map<String, Object> s : students) {
                            String label;
                            switch (s.get("status").toString()) {
                                case "0": label = "⏳待审"; break;
                                case "1": label = "✅已选"; break;
                                case "2": label = "❌退选"; break;
                                case "4": label = "🚫未通过"; break;
                                default: label = "未知";
                            }
                            sb.append(String.format("· %s (%s)", s.get("real_name"), label));
                            if (s.get("score") != null) {
                                sb.append(String.format(" [成绩：%s]", s.get("score")));
                            }
                            sb.append("\n");
                        }
                    }
                    result.put("answer", sb.toString());
                    result.put("suggestions", Arrays.asList("课程统计", matchedCourse + "选课情况", "待审批数量"));
                } else {
                    StringBuilder sb = new StringBuilder("你的课程学生名单：\n\n");
                    for (Map<String, Object> c : courses) {
                        Long cid = (Long) c.get("id");
                        List<Map<String, Object>> students = assistantMapper.courseStudents(cid);
                        sb.append(String.format("【%s】%d 名学生\n", c.get("course_name"), students.size()));
                    }
                    sb.append("\n输入「课程名+学生名单」查看具体名单。");
                    result.put("answer", sb.toString());
                    result.put("suggestions", Arrays.asList("课程统计", "待审批数量", "选课建议"));
                }
            }
            handled = true;
        }
        else if (containsAny(q, "hello", "hi", "你好", "在吗")) {
            result.put("answer", "老师你好！我是教学助手，可以帮你查看课程统计、学生选课情况、待审批申请等。有什么需要帮忙的？");
            suggestions.add("课程统计");
            suggestions.add("待审批数量");
            result.put("suggestions", suggestions);
            handled = true;
        }
        else if (containsAny(q, "未批改", "待批改", "未批阅", "待批阅", "未改", "待改", "批改作业", "改作业")) {
            List<Map<String, Object>> subs = assistantMapper.ungradedSubmissions(userId);
            if (subs.isEmpty()) {
                result.put("answer", "太棒了！目前没有待批改的作业 🎉\n\n所有提交的作业都已完成批改。");
            } else {
                StringBuilder sb = new StringBuilder("你有 " + subs.size() + " 份作业待批改：\n\n");
                for (int i = 0; i < subs.size(); i++) {
                    Map<String, Object> s = subs.get(i);
                    sb.append(String.format("%d. 【%s】- %s\n", i + 1,
                            s.get("course_name"), s.get("assignmentTitle")));
                    sb.append(String.format("   学生：%s  |  提交时间：%s\n",
                            s.get("studentName"),
                            s.get("submit_time") != null ? s.get("submit_time").toString().substring(0, 16) : "未知"));
                }
                sb.append("\n快去批改吧！📝");
                result.put("answer", sb.toString());
                result.put("suggestions", Arrays.asList("课程统计", "待审批数量", "学生名单"));
            }
            handled = true;
        }
        else if (containsAny(q, "建议", "帮助")) {
            result.put("answer", "作为教师，你可以使用以下功能：\n\n" +
                    "📊 课程统计 — 查看各课程选课人数\n" +
                    "📝 待批改作业 — 查看未批改的作业提交\n" +
                    "📋 待审批 — 查看待处理的选课申请\n" +
                    "👨‍🎓 学生名单 — 查看课程选课学生\n" +
                    "📚 还可以直接输入课程名称查询详情");
            suggestions.add("课程统计");
            suggestions.add("待审批数量");
            suggestions.add("选课情况");
            handled = true;
        }

        if (!handled) {
            List<Map<String, Object>> courses = assistantMapper.teacherCourses(userId);
            for (Map<String, Object> c : courses) {
                if (c.get("course_name").toString().contains(q)) {
                    Long cid = (Long) c.get("id");
                    List<Map<String, Object>> students = assistantMapper.courseStudents(cid);
                    StringBuilder sb = new StringBuilder();
                    sb.append(String.format("【%s】选课情况\n\n", c.get("course_name")));
                    if (students.isEmpty()) {
                        sb.append("目前还没有学生选这门课。");
                    } else {
                        int pending = 0, active = 0;
                        for (Map<String, Object> s : students) {
                            String st = s.get("status").toString();
                            if ("0".equals(st)) pending++;
                            else if ("1".equals(st)) active++;
                        }
                        sb.append(String.format("总记录：%d 条\n", students.size()));
                        sb.append(String.format("✅ 已选课：%d 人\n", active));
                        sb.append(String.format("⏳ 待审批：%d 人\n", pending));
                        sb.append("\n学生列表：\n");
                        for (Map<String, Object> s : students) {
                            String label;
                            switch (s.get("status").toString()) {
                                case "0": label = "⏳待审"; break;
                                case "1": label = "✅已选"; break;
                                case "2": label = "❌退选"; break;
                                default: label = "未知";
                            }
                            sb.append(String.format("· %s (%s)", s.get("real_name"), label));
                            if (s.get("score") != null) {
                                sb.append(String.format(" [成绩：%s]", s.get("score")));
                            }
                            sb.append("\n");
                        }
                    }
                    result.put("answer", sb.toString());
                    result.put("suggestions", Arrays.asList("课程统计", "待审批数量", "返回"));
                    handled = true;
                    break;
                }
            }
        }

        if (!handled) {
            result.put("answer", "抱歉，我没有理解你的问题。你可以试试以下功能：\n• 课程统计 — 查看各课程数据\n• 待批改作业 — 查看未批改的提交\n• 待审批数量 — 查看待处理申请数\n• 学生名单 — 查看各课程学生");
            suggestions.add("课程统计");
            suggestions.add("待批改作业");
            suggestions.add("待审批数量");
            result.put("suggestions", suggestions);
        }

        return result;
    }

    private boolean containsAny(String text, String... keywords) {
        for (String kw : keywords) {
            if (text.contains(kw)) return true;
        }
        return false;
    }

    private String formatCourseStatus(String status) {
        switch (status) {
            case "1": return "即将开课";
            case "2": return "授课中";
            case "3": return "已结课";
            default: return "未知";
        }
    }
}
