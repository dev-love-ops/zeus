package com.wufeiqun.zeus.biz.cmdb.entity;

import com.wufeiqun.zeus.common.entity.BasePageQuery;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import java.util.List;

/**
 * @author wufeiqun
 * @date 2022-07-07
 * 应用相关的请求参数结构体
 */
public class ApplicationForm {

    @Data
    public static class ApplicationExistForm {
        @NotBlank(message = "不能为空")
        @Length(max=64, message="长度不能超过64位")
        @Pattern(regexp = "^[\\w\\-]+$", message = "只允许输入: 数字/大小写字母/中划线")
        private String code;
    }

    @Data
    public static class ApplicationDeleteForm {
        @NotNull(message = "不能为Null")
        private Long id;
    }

    @Data
    public static class ApplicationBase {
        @NotBlank(message = "不能为空")
        @Length(max=64, message="长度不能超过64位")
        @Pattern(regexp = "^[\\w_\\-]+$", message = "只允许输入: 数字/字母/下划线/中划线")
        private String code;
        @NotBlank(message = "不能为空")
        private String name;
        private String owner;
        private String department;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class ApplicationAddForm extends ApplicationBase{
        private String comment;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class ApplicationUpdateForm extends ApplicationBase{
        private Long id;
        private String comment;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class ApplicationQueryForm extends BasePageQuery {
        private String code;
        private String name;
        private Boolean isMyFavorite;
    }

    @Data
    @ToString
    @EqualsAndHashCode(callSuper = true)
    public static class ApplicationResourceQueryForm extends BasePageQuery {
        @NotBlank(message = "不能为空")
        private String appCode;
        @NotBlank(message = "不能为空")
        private String env;
        private String resourceType;
    }

    @Data
    public static class ApplicationResourceCreateOrDeleteForm{
        @NotBlank(message = "不能为空")
        private String appCode;
        @NotBlank(message = "不能为空")
        private String env;
        @NotBlank(message = "不能为空")
        private String resourceType;
        @NotEmpty(message="不能为空")
        private List<String> instanceIdList;
    }



}
