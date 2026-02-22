package cn.snowdreamsblog.redCulture.core.start.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/", "/index.html", "/css/**", "/images/**", "/spots/**",
                        "/login.html", "/register.html"
                ).permitAll()
                .anyRequest().authenticated()
        )
                .formLogin(form -> form.loginPage("/login"));

        return http.build();
    }
}
