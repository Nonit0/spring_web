package com.iesvdc.dam.demo2.configuracion;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConf {

    @Autowired
    DataSource dataSource;

    // le digo las tablas que contienen usuario/password/rol
    @Autowired
    public void configure(AuthenticationManagerBuilder amb) throws Exception {
        amb.jdbcAuthentication()
                .dataSource(dataSource)
                .usersByUsernameQuery("select username, password, enabled " +
                        "from usuario where username = ?")
                .authoritiesByUsernameQuery("select username, rol as 'authority' " +
                        "from usuario where username = ?");
    }

    // configuro algoritmo de hash
    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    
    @Bean
    public SecurityFilterChain filter(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests((request) -> request
                        .requestMatchers("/carrito/pago", "/pedidos/**")
                        .authenticated()
                        .requestMatchers("/", "/css/**", "/webjars/**", "/img/**", "/js/**",
                                "/register/**", "/ayuda/**", "/login", "/login/**",
                                "/codpos/**", "/denegado", "/error", "/inserta",
                                "/acerca", "/catalogo/**", "/carrito/**")
                        .permitAll()
                        .requestMatchers("/admin/**")
                        .hasAuthority("ADMIN")
                        .anyRequest()
                        .authenticated())
                .formLogin((formLogin) -> formLogin
                        .loginPage("/login")
                        .permitAll())
                .exceptionHandling((exception) -> exception.accessDeniedPage("/denegado"))
                .rememberMe(Customizer.withDefaults())
                .logout((logout) -> logout
                        .invalidateHttpSession(true)
                        .logoutSuccessUrl("/")
                        // .deleteCookies("JSESSIONID") // no es necesario, JSESSIONID se hace por
                        // defecto
                        .permitAll())
                .build();
    } 

}
