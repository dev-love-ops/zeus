package com.wufeiqun.zeus.biz.cmdb.entity;

import com.wufeiqun.zeus.common.entity.BasePageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author wufeiqun
 * @date 2022-07-07
 * 服务器相关的请求参数结构体
 */
public class ServerForm {

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class ServerQueryForm extends BasePageQuery {
        private String privateIp;
        private String publicIp;
        private String instanceName;
    }

    @Data
    public static class ServerUpdateForm {
        private Long id;
        private String comment;
    }


}
