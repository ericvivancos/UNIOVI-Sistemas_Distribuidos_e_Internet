package com.example.sdi2324entrega192;


import com.example.sdi2324entrega192.interceptors.CustomSuccessHandler;
import com.example.sdi2324entrega192.interceptors.LoginFailure;
import com.example.sdi2324entrega192.interceptors.LogoutHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {



    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception{
        return super.authenticationManagerBean();
    }
    @Bean
    public CustomSuccessHandler loginSuccessHandler(){
        return new CustomSuccessHandler();
    }
    @Bean
    public LoginFailure loginFailure(){
        return new LoginFailure();
    }
    @Bean
    public LogoutHandler logoutHandler(){
        return new LogoutHandler();
    }
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SpringSecurityDialect securityDialect(){
        return new SpringSecurityDialect();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http
                .csrf().disable()
                .addFilterBefore(new PostEditFilter(), UsernamePasswordAuthenticationFilter.class)

                .authorizeRequests()
                .antMatchers("/css/**", "/images/**", "/script/**", "/", "/signup","/login", "/login/**", "/").permitAll()
                .antMatchers("/friendRequest/**").authenticated()  // Ruta de las solicitudes de amistad
                .antMatchers("/signup/**").not().authenticated()
                .antMatchers("/user/list/delete/**").hasAuthority("ROLE_ADMIN")
                .antMatchers("/user/list").authenticated()
                .antMatchers("/post/**").authenticated()
                .antMatchers("/logout").authenticated()
                //.antMatchers("/home").authenticated()
                .antMatchers("/logs/**").hasAuthority("ROLE_ADMIN")
                .antMatchers("/user/edit/**").hasRole("ADMIN")
                //solamente puede modificar el admin
                .antMatchers("/post/edit/**").authenticated()//no pones admin MIRAR PURBEA44
                .antMatchers("/post/listAll/**").hasRole("ADMIN")

                .antMatchers("/").hasAnyAuthority()
                //lo del login si lo pones ns xq no funciona se cambio en el controller
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .successHandler(loginSuccessHandler())
                .failureHandler(loginFailure())
                .permitAll()
                .and()
                .logout()
                .logoutSuccessHandler(logoutHandler())
                .permitAll()
                .and()
                .exceptionHandling()
                .accessDeniedPage("/error");
    }

}
