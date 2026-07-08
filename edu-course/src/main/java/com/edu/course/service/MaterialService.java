package com.edu.course.service;

import com.edu.common.entity.Material;
import com.edu.common.entity.MaterialReadRecord;
import com.edu.common.result.BusinessException;
import com.edu.common.service.BaseService;
import com.edu.course.mapper.MaterialMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MaterialService extends BaseService {

    @Autowired
    private MaterialMapper materialMapper;

    public List<Material> listByCourse(Long courseId) {
        return materialMapper.selectByCourse(courseId);
    }

    public Material findById(Long id) {
        Material m = materialMapper.selectById(id);
        if (m == null) throw new BusinessException("资料不存在");
        return m;
    }

    public void add(Material material) {
        if (material.getRequiredSeconds() == null || material.getRequiredSeconds() < 1) {
            material.setRequiredSeconds(60);
        }
        materialMapper.insert(material);
        log.info("发布资料: id={}, title={}", material.getId(), material.getTitle());
    }

    public void delete(Long id) {
        materialMapper.deleteById(id);
        log.info("删除资料: id={}", id);
    }

    public MaterialReadRecord startReading(Long materialId, Long studentId) {
        int cnt = materialMapper.countRead(materialId, studentId);
        if (cnt == 0) {
            materialMapper.insertRead(materialId, studentId);
        }
        return materialMapper.selectReadRecord(materialId, studentId);
    }

    @Transactional
    public void completeReading(Long materialId, Long studentId) {
        MaterialReadRecord record = materialMapper.selectReadRecord(materialId, studentId);
        if (record == null) throw new BusinessException("未开始阅读");
        if (record.getCompleted() == 1) return;
        Material m = materialMapper.selectById(materialId);
        if (m == null) throw new BusinessException("资料不存在");
        materialMapper.markComplete(materialId, studentId);
        log.info("资料阅读完成: materialId={}, studentId={}", materialId, studentId);
    }

}
