package com.example.msauserservice.security;


import com.example.msauserservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurity extends WebSecurityConfigurerAdapter {

    private final UserService userService;
    private final BCryptPasswordEncoder encoder;
    private final Environment env;

    @Override
    public void configure(org.springframework.security.config.annotation.web.builders.WebSecurity web) {
        web.ignoring().mvcMatchers("/h2-console/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        String property = env.getProperty("gateway.ip");

        http.authorizeRequests()
                .antMatchers("/actuator/**").permitAll()
                .antMatchers("/**").access("true")
                .and()
                .addFilter(getAuthenticationFilter());

        http.csrf().disable();
        http.headers().frameOptions().disable();
    }

    @Bean
    public AuthenticationFilter getAuthenticationFilter() throws Exception {
        //  authenticationFilter.setAuthenticationManager(authenticationManager());
        return new AuthenticationFilter(authenticationManager(), userService, env);
    }

    // select pwd from users where email = ?
    // db_pwd(encryped) == input_pwd(encrypted)
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(encoder);
    }
}