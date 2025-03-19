package com.wufeiqun.zeus.biz.system.entity;

import com.wufeiqun.zeus.common.entity.BasePageQuery;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @author wufeiqun
 * @date 2022-07-08
 */
public class UserForm {
    @Data
    public static class LoginForm {
        @NotNull(message = "不能为空")
        private String username;
        @NotNull(message = "不能为空")
        private String password;
    }

    @Data
    public static class ChangePasswordForm {
        @NotNull(message = "新密码不能为空")
        private String password;
    }

    @Data
    public static class UserSearchForm extends BasePageQuery {
        private String account;
        private String username;
    }

    @Data
    public static class UpdateUserForm{
        @NotNull(message="id不能为空")
        private Long id;
        @NotBlank(message = "account不能为空")
        private String account;
        private String name;
        private String departmentCode;
        private Boolean status;
        private List<String> roleList;
    }
}
