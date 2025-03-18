package com.wufeiqun.zeus.common.exception;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.wufeiqun.zeus.common.entity.CommonVo;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Set;

/**
 * @author wufeiqun
 * @date 2022-07-07
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 处理 json 请求体调用接口对象参数校验失败抛出的异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public CommonVo<Object> jsonParamsException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        List<String> errorList = CollectionUtil.newArrayList();

        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            String msg = String.format("%s%s；", fieldError.getField(), fieldError.getDefaultMessage());
            errorList.add(msg);
        }

        return CommonVo.error("参数校验失败: " + StringUtils.join(errorList, "|"));
    }

    /**
     * 处理单个参数校验失败抛出的异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public CommonVo<Object> paramsException(ConstraintViolationException e) {

        List<String> errorList = CollectionUtil.newArrayList();
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        for (ConstraintViolation<?> violation : violations) {
            StringBuilder message = new StringBuilder();
            Path path = violation.getPropertyPath();
            String[] pathArr = StrUtil.splitToArray(path.toString(), ".");
            String msg = message.append(pathArr[1]).append(violation.getMessage()).toString();
            errorList.add(msg);
        }

        return CommonVo.error("参数校验失败: " + StringUtils.join(errorList, "|"));
    }

    /**
     * 请求方法不被允许异常
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public CommonVo<Object> httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return CommonVo.error("请求方法不允许!");
    }

    /**
     * 业务自定义异常
     */
    @ExceptionHandler(ServiceException.class)
    public CommonVo<Object> serviceException(Exception e) {
        log.error("业务自定义异常: ", e);
        return CommonVo.error(e.getMessage());
    }

    /**
     * 未知异常
     */
    @ExceptionHandler(Exception.class)
    public CommonVo<Object> unknownException(Exception e) {
        log.error("系统异常: ", e);
        return CommonVo.error("系统异常!");
    }
}
