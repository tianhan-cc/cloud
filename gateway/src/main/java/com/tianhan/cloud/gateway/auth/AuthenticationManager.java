package com.tianhan.cloud.gateway.auth;

import com.tianhan.cloud.common.auth.UserDetail;
import com.tianhan.cloud.common.core.SystemConstant;
import com.tianhan.cloud.gateway.handle.CaptchaHandle;
import com.tianhan.cloud.usercenter.rpc.interfaces.IUsercenterRpc;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.annotation.Resource;

/**
 * @Author NieAnTai
 * @Date 2021/3/28 12:33 上午
 * @Version 1.0
 * @Email nieat@foxmail.com
 * @Description
 **/
@Component
public class AuthenticationManager implements ReactiveAuthenticationManager {
    private final BCryptPasswordEncoder BC = new BCryptPasswordEncoder();

    @Resource
    private CaptchaHandle captchaHandle;
    @DubboReference
    private IUsercenterRpc userRpc;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        UsernamePasswordAuthenticationToken tmp = (UsernamePasswordAuthenticationToken) authentication;
        UserLoginParam loginParam = (UserLoginParam) tmp.getDetails();
        // 校验参数
        loginParam.validate();
        if (!captchaHandle.validatorCaptcha(loginParam.getValidate(), loginParam.getCaptcha())) {
            throw new BadCredentialsException("验证码错误!");
        }
        return obtainUserDetail(loginParam.getUsername())
                .filter(u -> BC.matches(loginParam.getPassword(), u.getPassword()))
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BadCredentialsException(SystemConstant.LOGIN_ERROR_MSG))))
                .doOnNext(preAuthenticationChecks::check)
                .publishOn(Schedulers.boundedElastic())
                .map(u -> new UsernamePasswordAuthenticationToken(u, u.getPassword(), u.getAuthorities()));
    }

    public Mono<UserDetail> obtainUserDetail(String username) {
        return Mono.justOrEmpty(userRpc.obtainUser(username));
    }

    private final UserDetailsChecker preAuthenticationChecks = user -> {
        if (!user.isAccountNonLocked()) {
            throw new LockedException("该账户已被锁定，请联系管理");
        }

        if (!user.isEnabled()) {
            throw new DisabledException("该账户已被注销，请联系管理");
        }

        if (!user.isAccountNonExpired()) {
            throw new AccountExpiredException("该账户已过期，请联系管理");
        }
    };
}
