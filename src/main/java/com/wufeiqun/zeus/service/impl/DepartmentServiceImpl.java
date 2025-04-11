package com.wufeiqun.zeus.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wufeiqun.zeus.dao.Department;
import com.wufeiqun.zeus.dao.DepartmentMapper;
import com.wufeiqun.zeus.service.IDepartmentService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 部门 服务实现类
 * </p>
 *
 * @author wufeiqun
 * @since 2025-03-18
 */
@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements IDepartmentService {

    @Override
    public Map<String, Department> getDepartmentMap() {
        return this.list().stream().collect(Collectors.toMap(Department::getCode, x -> x));
    }
}
