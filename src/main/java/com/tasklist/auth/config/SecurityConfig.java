package com.tasklist.auth.config;

import com.tasklist.auth.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity(debug = true) //для вывода подробностей в лог
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    // для получение пользователя из БД
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    public void setUserDetailsService(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    // для хеширования паролей используется алгоритм Bcrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // стандартный готовый authenticationManager из Spring контейнера (используется для проверки логина-пароля)
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    // указываем наш сервис userDetailsService для проверки пользователя в БД и кодировщик паролей
    // эти методы доступны в документации Spring Security
    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        // настройки AuthenticationManager для правильной проверки логин-пароль
        authenticationManagerBuilder.
                userDetailsService(userDetailsService). // использовать наш сервис для загрузки User из БД
                passwordEncoder(passwordEncoder()); // указываем кодировщик пароля (для корректной проверки пароля)
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        /* если используется другая клиентская технология (не SpringMVC, а например Angular, React и пр.),
            то выключаем встроенную Spring-защиту от CSRF атак,
            иначе запросы от клиента не будут обрабатываться, т.к. Spring Security будет пытаться в каждом входящем запроcе искать спец. токен для защиты от CSRF
        */

        /* способ хранения сессии на сервере
           always - сохраняется JSESSIONID в куки браузера
           if_required(по умолчанию) - JSESSIONID создастся только после авторизации
           never - сессия создается, если есть JSESSIONID
           statеless - сессия никода не сохраняется на сервере(куки не будет) */
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER);

        http.csrf().disable(); // отключаем на время разработки(для методов post put и др.
        // которые изменяют данные, будут без ошибок)
        http.httpBasic().disable(); //отключаем стандартную авторизацию Spring
        http.formLogin().disable(); //отключаем стандартную форму логирования
        http.requiresChannel().anyRequest().requiresSecure(); //Обязательное исп. HTTPS
    }
}
