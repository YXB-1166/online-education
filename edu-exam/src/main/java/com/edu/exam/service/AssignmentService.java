package com.edu.exam.service;

import com.edu.common.entity.Assignment;
import com.edu.common.page.PageParam;
import com.edu.common.page.PageResult;
import com.edu.common.result.BusinessException;
import com.edu.common.service.BaseService;
import com.edu.exam.mapper.AssignmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssignmentService extends BaseService {

    @Autowired
    private AssignmentMapper assignmentMapper;

    public Assignment findById(Long id) {
        Assignment assignment = assignmentMapper.selectById(id);
        if (assignment == null) {
            log.warn("作业不存在: id={}", id);
            throw new BusinessException("作业不存在");
        }
        return assignment;
    }

    public List<Assignment> findList(Assignment assignment) {
        return assignmentMapper.selectList(assignment);
    }

    public PageResult<Assignment> page(PageParam param, Assignment assignment) {
        long total = assignmentMapper.countList(assignment);
        List<Assignment> list = assignmentMapper.selectPage(assignment, param.getOffset(), param.getPageSize());
        log.info("作业分页: total={}, page={}/{}", total, param.getPageNum(), param.getPageSize());
        return new PageResult<>(list, total, param.getPageNum(), param.getPageSize());
    }

    public void add(Assignment assignment) {
        if (assignmentMapper.insert(assignment) == 0) {
            log.warn("发布作业失败");
            throw new BusinessException("发布作业失败");
        }
        log.info("发布作业: id={}, title={}", assignment.getId(), assignment.getTitle());
    }

    public void update(Assignment assignment) {
        if (assignmentMapper.update(assignment) == 0) {
            log.warn("更新作业失败: id={}", assignment.getId());
            throw new BusinessException("更新作业失败");
        }
        log.info("更新作业: id={}", assignment.getId());
    }

    public void delete(Long id) {
        if (assignmentMapper.deleteById(id) == 0) {
            log.warn("删除作业失败: id={}", id);
            throw new BusinessException("删除作业失败");
        }
        log.info("删除作业: id={}", id);
    }

}
