package com.example.vms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.vms.jwt.JwtAuthenticationFilter;
import com.example.vms.jwt.JwtTokenProvider;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	
	@Bean
	JwtTokenProvider jwtTokenProvider() {
		return new JwtTokenProvider();
	}
	
	@Bean
	JwtAuthenticationFilter authenticationFilter() {
		return new JwtAuthenticationFilter(jwtTokenProvider());
	}

	

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    	http.csrf().disable()
          
      .logout()
          .logoutUrl("/employee/logout")
          .logoutSuccessUrl("/employee/login")
          .invalidateHttpSession(true);

        
        http.authorizeRequests()
        .requestMatchers("/**", "/css/**", "/js/**", "/image/**").permitAll()
        .requestMatchers("/manager/**").permitAll()
        .requestMatchers("/employee/**").permitAll()
        .requestMatchers("/scheduler/**").permitAll()
        .requestMatchers("/certificate/**").permitAll()
        .requestMatchers("/vacation/**").permitAll()
        .anyRequest().authenticated();
        
        http.sessionManagement((session)->session.sessionCreationPolicy(
				SessionCreationPolicy.STATELESS)); // 세션방식의 인증을 사용하지 않겠다는 뜻
		
		http.addFilterBefore(authenticationFilter(), // 인증필터
				UsernamePasswordAuthenticationFilter.class); // 스프링시큐리티에서 제공해주는거
		// 패스워드와 이름으로 인증
		
		// CORS 설정 추가
        http.cors();
            

        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("*");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}