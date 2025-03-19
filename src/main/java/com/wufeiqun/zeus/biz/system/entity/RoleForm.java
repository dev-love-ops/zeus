package com.wufeiqun.zeus.biz.system.entity;

import com.wufeiqun.zeus.common.entity.BasePageQuery;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author wufeiqun
 * @date 2022-07-08
 */
public class RoleForm {

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class RoleSearchForm extends BasePageQuery {

    }

    @Data
    public static class CreateRoleForm{
        @NotBlank(message = "code不能为空")
        private String code;
        @NotBlank(message = "name不能为空")
        private String name;
        private String comment;
        @NotNull(message="menuIdList不能为null")
        private List<Long> menuIdList;
    }

    @Data
    public static class UpdateRoleMenuForm{
        @NotNull(message="id不能为空")
        private Long id;
        @NotBlank(message = "code不能为空")
        private String code;
        @NotBlank(message = "name不能为空")
        private String name;
        private String comment;
        private List<Long> menuIdList;
    }

    @Data
    public static class DeleteRoleForm {
        @NotNull(message = "角色id不能为空")
        private Long id;

    }

}
