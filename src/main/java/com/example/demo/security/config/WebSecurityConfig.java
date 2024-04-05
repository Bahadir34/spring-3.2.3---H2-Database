package com.example.demo.security.config;

import com.example.demo.security.jwt.AuthEntryPointJwt;
import com.example.demo.security.jwt.AuthTokenFilter;
import com.example.demo.security.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.headers(e->e.frameOptions(t->t.disable()))
                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-managing-panel/**").disable())
                .cors(e -> e.disable())
                .exceptionHandling(e-> e.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(e->e.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(e->e.requestMatchers(AUTH_WHITE_LIST).permitAll()
                        .anyRequest().permitAll());

        http.addFilterBefore(authenticationJwtTokenFilter() , UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter()
    {
        return new AuthTokenFilter();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider()
    {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());

        return authenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public WebMvcConfigurer corsConfigurer()
    {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) //cors ayarını yazmazsak uygulama canlıya çıktığı zaman problem yaşanabilir.
            {
                registry.addMapping("/**") // tüm url'leri kapsar.
                        .allowedOrigins("*").allowedHeaders("*").allowedMethods("*");
            }
        };
    }
    private static final String[] AUTH_WHITE_LIST = {
            "/",
            "swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "index.html",
            "/images/**",
            "/css/**",
            "/js/**",
            "/contact-messages/save",
            "/auth/login",
            "/login",
            "/auth/register",
            "/auth/forgot-password",
            "/adverts/**",
            "/categories/**",
            "/tour-requests/**",
            "/countries/**",
            "/advert-types/**",
            "/cities/**",
            "/districts/**",
            "/register",
            "/tour-requests/**",
            "/settings/db-reset",
            "/auth/**",
            "/actuator/**",
            "/auth/google/register",
    };
}
