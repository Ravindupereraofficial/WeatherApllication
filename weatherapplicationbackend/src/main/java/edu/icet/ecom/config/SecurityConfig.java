package edu.icet.ecom.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

    @Value("${auth0.audience}")
    private String audience;

    @Value("${auth0.domain}")
    private String domain;

    @Value("${auth0.dev-permissive:false}")
    private boolean devPermissive;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http = http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
                   .csrf(csrf -> csrf.disable());

        boolean oauthConfigured = false;

        if (devPermissive) {
            log.warn("auth0.dev-permissive=true - skipping OAuth2 configuration and permitting GET /api/weather/**. NOT FOR PRODUCTION.");
        } else {
            if (domain != null && !domain.isBlank() && !domain.contains("YOUR_AUTH0_DOMAIN")) {
                try {
                    JwtDecoder jwtDecoder = createJwtDecoder();

                    http.oauth2ResourceServer(oauth2 -> oauth2
                            .jwt(jwt -> jwt
                                    .decoder(jwtDecoder)
                                    .jwtAuthenticationConverter(jwtAuthenticationConverter())
                            )
                    );

                    oauthConfigured = true;
                    log.info("OAuth2 resource server configured using issuer: https://{}/", domain);
                } catch (Exception ex) {
                    log.warn("Could not configure OAuth2 resource server (will run in permissive local mode): {}", ex.toString());
                }
            } else {
                log.warn("Auth0 domain is not configured (auth0.domain='{}'). OAuth2 resource server will be disabled.", domain);
            }
        }

    if (devPermissive || !domainConfigured(domain) && !oauthConfigured) {
        log.warn("Permitting GET /api/weather/** for local development. Do not use in production.");
        http.authorizeHttpRequests(authz -> authz
            .requestMatchers(HttpMethod.GET, "/api/weather/**").permitAll()
            .anyRequest().permitAll()
        );
    } else {
            http.authorizeHttpRequests(authz -> authz
                    .requestMatchers(HttpMethod.GET, "/api/weather/**").authenticated()
                    .anyRequest().permitAll()
            );
        }

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private JwtDecoder createJwtDecoder() {
        String issuerUrl = String.format("https://%s/", domain);

        NimbusJwtDecoder jwtDecoder = JwtDecoders.fromIssuerLocation(issuerUrl);

        OAuth2TokenValidator<Jwt> audienceValidator = new AudienceValidator(audience);
        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuerUrl);
        OAuth2TokenValidator<Jwt> withAudience = new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator);

        jwtDecoder.setJwtValidator(withAudience);

        return jwtDecoder;
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthorityPrefix("SCOPE_");
        authoritiesConverter.setAuthoritiesClaimName("scope");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        return converter;
    }

    private boolean domainConfigured(String domain) {
        return domain != null && !domain.isBlank() && !domain.contains("YOUR_AUTH0_DOMAIN");
    }
}