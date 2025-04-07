package com.wufeiqun.zeus.biz.cicd.entity;

import com.wufeiqun.zeus.common.entity.BasePageQuery;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


/**
 * @author wufeiqun
 * @date 2022-07-07
 * 构建请求参数结构体
 */
public class CicdBuildForm {
    /**
     * 运行构建的参数
     */
    @Data
    @ToString
    public static class RunBuildForm {
        /**
         * 任务项ID, 任务模式会用到, 非必填
         */
        public Integer taskItemId;
        /**
         * 应用Code
         */
        @NotBlank(message = "appCode不能为空")
        public String appCode;
        /**
         * 构建环境
         */
        @NotBlank(message = "envCode不能为空")
        public String envCode;
        /**
         * 构建profile, 非必填, 不填就会根据环境去应用配置中获取
         */
        public String profile;
        /**
         * 发布模式 @see zeus.cicd.enums.CicdMode
         */
        public int deployMode;
        /**
         * 构建分支, 快速模式会用到
         */
        public String buildBranch;
        /**
         * 是否重新安装依赖, NPM项目会用到
         */
        private Boolean reInstallDependency;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class BuildRecordSearchForm extends BasePageQuery {
        private Long id;
        private String name;
        private Integer status;
        private String buildAppCode;
        private String buildEnvCode;
        private Integer deployMode;
        private String createUser;
    }

    @Data
    @ToString
    public static class UpdateBuildRecordStatusForm {
        /**
         * 构建记录
         */
        private Long buildRecordId;
        /**
         * 构建制品文件地址
         */
        private String pkgUrl;
        /**
         * 构建制品harbor镜像地址
         */
        private String imageUrl;
        private Integer status;
        private Integer duration;
    }

}
