package com.bzj.index.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    @Autowired
    private RedisTemplate<String, String> redisMail;

    // 统一设置 token 有效时间（分钟）
    private static final long TOKEN_EXPIRE_MINUTES = 30;
    // 城市在线状态 TTL（小时）
    private static final long CITY_ONLINE_EXPIRE_HOURS = 1;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = request.getHeader("Authorization");

        if (token != null && !token.trim().isEmpty()) {
            String tokenKey = "token:" + token;
            String userId = redisMail.opsForValue().get(tokenKey);

            if (userId != null) {
                // 设置登录状态到 SecurityContext
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userId, null, new ArrayList<>());
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // 对登录相关 key 做续期
                extendKeyTTLIfExistsAndValidType(tokenKey, TOKEN_EXPIRE_MINUTES, TimeUnit.MINUTES, DataType.STRING);
                extendKeyTTLIfExistsAndValidType("user:" + userId + ":token", TOKEN_EXPIRE_MINUTES, TimeUnit.MINUTES, DataType.STRING);
                extendKeyTTLIfExistsAndValidType("user:" + userId + ":loginInfo", TOKEN_EXPIRE_MINUTES, TimeUnit.MINUTES, DataType.HASH);

                // 获取城市信息并续期城市在线 key（city:online:{city}:{userId}）
                String loginInfoKey = "user:" + userId + ":loginInfo";
                String city = (String) redisMail.opsForHash().get(loginInfoKey, "city");
                if (city != null && !city.trim().isEmpty()) {
                    String cityOnlineKey = "city:online:" + city + ":" + userId;
                    extendKeyTTLIfExistsAndValidType(cityOnlineKey, CITY_ONLINE_EXPIRE_HOURS, TimeUnit.HOURS, DataType.STRING);
                }

            } else {
                // token 无效或已过期
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 安全地续期 Redis key：要求 key 存在且类型匹配
     */
    private void extendKeyTTLIfExistsAndValidType(String key, long timeout, TimeUnit unit, DataType expectedType) {
        if (Boolean.TRUE.equals(redisMail.hasKey(key))) {
            DataType actualType = redisMail.type(key);
            if (actualType == expectedType) {
                redisMail.expire(key, timeout, unit);
            } else {
                // 可选：记录日志或报警
                System.err.println("Redis key 类型不匹配，跳过续期：key=" + key + "，期望类型=" + expectedType + "，实际=" + actualType);
            }
        }
    }
}
