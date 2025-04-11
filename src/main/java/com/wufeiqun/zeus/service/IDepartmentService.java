package com.wufeiqun.zeus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wufeiqun.zeus.dao.Department;

import java.util.Map;

/**
 * <p>
 * 部门 服务类
 * </p>
 *
 * @author wufeiqun
 * @since 2025-03-18
 */
public interface IDepartmentService extends IService<Department> {
    Map<String, Department> getDepartmentMap();
}
