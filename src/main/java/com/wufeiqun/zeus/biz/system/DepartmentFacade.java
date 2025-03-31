package com.wufeiqun.zeus.biz.system;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import com.wufeiqun.zeus.dao.Department;
import com.wufeiqun.zeus.service.IDepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 吴飞群
 * @createTime 2022/05/17
 */
@Service
@RequiredArgsConstructor
public class DepartmentFacade {
    private final IDepartmentService departmentService;

    /**
     * 获取部门树
     */
    public List<Tree<String>> getDepartmentTree(){
        // 配置
        TreeNodeConfig treeNodeConfig = new TreeNodeConfig();

        List<Department> nodeList = departmentService.list();

        //转换器
        return TreeUtil.build(nodeList, "0", treeNodeConfig,
                (treeNode, tree) -> {
                    tree.setId(treeNode.getCode());
                    tree.setParentId(treeNode.getParentCode());
                    tree.setName(treeNode.getName());
                    // 扩展属性
                    tree.putExtra("extraField", "ignore");
                });

    }
}
