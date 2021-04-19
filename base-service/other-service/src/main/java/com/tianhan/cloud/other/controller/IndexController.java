package com.tianhan.cloud.other.controller;

import com.tianhan.cloud.common.auth.UserDetailsImpl;
import com.tianhan.cloud.common.core.ResponseResult;
import com.tianhan.cloud.common.web.controller.BaseController;
import com.tianhan.cloud.usercenter.rpc.interfaces.IUsercenterRpc;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author NieAnTai
 * @Date 2021/4/15 3:55 下午
 * @Version 1.0
 * @Email nieat@foxmail.com
 * @Description
 **/
@RestController
@RequestMapping("/index")
public class IndexController extends BaseController {
    @DubboReference
    private IUsercenterRpc rpc;

    @GetMapping("/")
    public ResponseResult index() {
        UserDetailsImpl user = rpc.obtainUser("nieat");
        return doJsonOut(user);
    }
}
