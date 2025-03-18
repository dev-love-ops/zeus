package com.wufeiqun.zeus.common.utils;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.sql.Types;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 1. 生成代码的时候, 每一个类型都有enableFileOverride方法, 默认是false, 所以如果生成的文件存在, 那么不会覆盖.
 */
public class CodeGenerator {
    public static void main(String[] args) {
        String url = "jdbc:mysql://127.0.0.1:3306/zeus?useUnicode=true&zeroDateTimeBehavior=convertToNull&allowMultiQueries=true&allowPublicKeyRetrieval=true&useSSL=false&tinyInt1isBit=true";
        String username = "root";
        String password = "root";

        String entityPath = "/Users/rocky/self/github/zeus/src/main/java";
        String xmlPath = "/Users/rocky/self/github/zeus/src/main/resources/mapper";

        // 生成所有表的代码
        List<String> tableList = Arrays.asList("user", "role", "user_role_relation", "department", "user_favorite_application",
        "server", "role_menu_relation", "operation_record", "menu", "environment", "cicd_restart_record", "cicd_deploy_record",
                "cicd_build_record", "application_resource_relation", "application_deploy_config", "application");

//        List<String> tableList = List.of("user");

        FastAutoGenerator.create(url, username, password)
                .globalConfig(builder -> {
                    builder.author("wufeiqun") // 设置作者
                            .outputDir(entityPath); // 指定输出目录
                })
                .dataSourceConfig(builder ->
                        builder.typeConvertHandler((globalConfig, typeRegistry, metaInfo) -> {
                            int typeCode = metaInfo.getJdbcType().TYPE_CODE;
                            if (typeCode == Types.SMALLINT) {
                                // 自定义类型转换
                                return DbColumnType.INTEGER;
                            }
                            return typeRegistry.getColumnType(metaInfo);
                        })
                )
                .packageConfig(builder ->
                        builder.parent("com.wufeiqun") // 设置父包名
                                .moduleName("zeus") // 设置父包模块名
                                .entity("dao") // entity存放的包的名字
                                .mapper("dao") // mapper存放的包的名字
                                .pathInfo(Collections.singletonMap(OutputFile.xml, xmlPath)) // 设置mapperXml生成路径
                )
                .strategyConfig(builder ->
                        builder.addInclude(tableList) // 设置需要生成的表名
                        // 实体类配置
                        .entityBuilder()
                            .disableSerialVersionUID() // 实体类不序列化
                            .enableLombok() // 使用lombok代替getter/setter
                            .enableFileOverride() // 覆盖已生成文件, 不然字段变化不会更新
                        // mapper配置
                        .mapperBuilder()
                        .enableBaseResultMap()
                        .enableBaseColumnList()
                        // controller配置
                        .controllerBuilder()
                        .disable() // 自己创建controller



                )
                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
    }
}

