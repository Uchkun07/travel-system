# Spring Security 集成指南

## 概述

本项目已添加 `spring-boot-starter-security` 依赖,但配置为宽松模式以兼容现有的 JWT 认证系统。

## 当前配置

### SecurityConfig.java

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)  // 禁用 CSRF
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // 无状态会话
            )
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()  // 允许所有请求
            );
        return http.build();
    }
}
```

### 为什么这样配置?

1. **禁用 CSRF**: JWT 是无状态的,不需要 CSRF 保护
2. **无状态会话**: JWT 自带认证信息,不需要 session
3. **允许所有请求**: 使用自定义的 `JwtInterceptor` 进行权限控制

## 密码加密

### 当前实现 (PBKDF2)

项目当前使用手动实现的 PBKDF2WithHmacSHA256 算法:

```java
public class PasswordUtil {
    private static final int ITERATIONS = 100000;
    private static final int KEY_LENGTH = 256;

    public static String hashPassword(String password, String salt) {
        // PBKDF2WithHmacSHA256 实现
    }
}
```

### Spring Security 的 PasswordEncoder

配置中提供了 `BCryptPasswordEncoder` Bean:

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

## 迁移到 Spring Security 密码加密 (可选)

如果未来想使用 Spring Security 的密码加密,可以这样修改:

### 1. 修改 UserServiceImpl

```java
@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private PasswordEncoder passwordEncoder;  // 注入 Spring Security 的密码编码器

    @Override
    public void register(UserRegisterDTO registerDTO) {
        User user = new User();
        // ... 其他字段设置

        // 使用 BCrypt 加密密码
        String encodedPassword = passwordEncoder.encode(registerDTO.getPassword());
        user.setPassword(encodedPassword);
        user.setSalt(null);  // BCrypt 不需要单独的 salt

        userMapper.insert(user);
    }

    @Override
    public String login(UserLoginDTO loginDTO) {
        // 查询用户
        User user = queryUser(loginDTO.getUsername());

        // 验证密码
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        // ... 生成 JWT
    }
}
```

### 2. BCrypt vs PBKDF2 比较

| 特性     | PBKDF2             | BCrypt                              |
| -------- | ------------------ | ----------------------------------- |
| 算法类型 | 密钥派生函数       | 专门的密码散列函数                  |
| 成本因子 | 迭代次数 (100,000) | 工作因子 (默认 10)                  |
| Salt     | 需要手动生成和存储 | 自动生成并包含在输出中              |
| 输出格式 | 需要自己格式化     | `$2a$10$...` (包含算法、成本、salt) |
| 性能     | 快速               | 中等                                |
| 安全性   | ⭐⭐⭐⭐⭐         | ⭐⭐⭐⭐⭐                          |
| 易用性   | ⭐⭐⭐             | ⭐⭐⭐⭐⭐                          |

### 3. 推荐做法

**当前保持 PBKDF2 即可**,因为:

- ✅ PBKDF2 是 NIST 推荐的标准算法
- ✅ 已经实现并正常工作
- ✅ 100,000 次迭代足够安全
- ✅ 不需要迁移现有用户密码

**未来可以考虑 BCrypt**,如果:

- 想简化密码处理逻辑
- 不想手动管理 salt
- 希望使用 Spring Security 的标准方案

## 使用 Spring Security 的其他功能

### 1. 方法级安全注解

启用方法安全:

```java
@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    // ...
}
```

使用注解保护方法:

```java
@RestController
public class UserController {

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/users")
    public List<User> getAllUsers() {
        // 只有 ADMIN 角色可以访问
    }

    @PreAuthorize("#userId == authentication.principal.userId")
    @GetMapping("/users/{userId}")
    public User getUser(@PathVariable Long userId) {
        // 用户只能访问自己的信息
    }
}
```

### 2. 集成 JWT 过滤器

创建自定义的 JWT 认证过滤器:

```java
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) {
        String token = extractToken(request);

        if (token != null && jwtUtil.validateToken(token)) {
            // 创建 Authentication 对象
            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user, null, authorities);

            // 设置到 SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
```

在 SecurityConfig 中添加:

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .addFilterBefore(jwtAuthenticationFilter(),
                        UsernamePasswordAuthenticationFilter.class);
    return http.build();
}
```

## 总结

✅ **已完成**:

- 添加 `spring-boot-starter-security` 依赖
- 创建 `SecurityConfig` 配置类
- 提供 `PasswordEncoder` Bean
- 配置为与现有 JWT 系统兼容

📋 **当前状态**:

- Spring Security 已启用但不拦截请求
- 继续使用自定义的 `JwtInterceptor`
- 继续使用 PBKDF2 密码加密
- 为未来集成 Spring Security 功能做好准备

🔮 **未来选项**:

- 可以迁移到 Spring Security 的 JWT 过滤器
- 可以使用 BCrypt 密码编码器
- 可以使用方法级安全注解
- 可以使用 Spring Security 的其他高级功能

## 常见问题

### Q: 为什么添加 Spring Security 后还能正常访问?

A: 因为配置了 `.anyRequest().permitAll()`,所有请求都被允许。实际的权限控制由 `JwtInterceptor` 处理。

### Q: 需要修改现有代码吗?

A: 不需要。当前配置完全兼容现有的实现。

### Q: PBKDF2 安全吗?

A: 非常安全。PBKDF2 是 NIST 推荐的标准,100,000 次迭代足以抵御暴力破解攻击。

### Q: 什么时候需要用 Spring Security 的功能?

A: 当你需要:

- 方法级的权限控制 (`@PreAuthorize`)
- OAuth2/OpenID Connect 集成
- Remember-Me 功能
- 更复杂的角色权限管理

## 参考资源

- [Spring Security 官方文档](https://spring.io/projects/spring-security)
- [JWT 最佳实践](https://tools.ietf.org/html/rfc8725)
- [OWASP 密码存储指南](https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html)
