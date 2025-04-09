package com.wufeiqun.zeus.biz.cicd.entity;

import com.wufeiqun.zeus.common.entity.BasePageQuery;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


/**
 * @author wufeiqun
 * @date 2022-07-07
 * 发布请求参数结构体
 */
public class CicdDeployForm {
    /**
     * 运行发布的参数
     */
    @Data
    @ToString
    public static class RunDeployForm {
        /**
         * 构建记录ID
         */
        @NotNull(message = "buildRecordId不能为空")
        private Long buildRecordId;
        /**
         * 应用Code
         */
        @NotBlank(message = "appCode不能为空")
        private String appCode;
        /**
         * 构建环境
         */
        @NotBlank(message = "envCode不能为空")
        private String envCode;
        /**
         * 发布模式 @see zeus.cicd.enums.CicdMode
         */
        @NotNull(message = "deployMode不能为空")
        private Integer deployMode;
        /**
         * 是否容器发布
         */
        @NotNull(message = "dockerDeploy不能为空")
        private Boolean dockerDeploy;

        /**
         * 发布的目标机器
         */
        private String serverIp;
        private Boolean isRollback;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class DeployRecordQueryForm extends BasePageQuery {
        private Long id;
        private String name;
        private Integer status;
        private String appCode;
        private String envCode;
        private Integer deployMode;
        private Boolean dockerDeploy;
        private Long buildRecordId;
        private String createUser;
        private Boolean isRollback;
    }

    /**
     * 异步任务回调更新发布记录表单
     */
    @Data
    @ToString
    public static class CallBackUpdateDeployRecordForm {
        private Long deployRecordId;
        private Integer status;
    }
}
