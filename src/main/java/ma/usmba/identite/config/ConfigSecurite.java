package ma.usmba.identite.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class ConfigSecurite {

    @Bean
    public SecurityFilterChain chaineFiltres(HttpSecurity http,
                                            OAuth2UserService<OidcUserRequest, OidcUser> serviceOidc) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/accueil", "/connexion", "/acces-refuse",
                                "/styles/**", "/images/**", "/erreur").permitAll()
                        .requestMatchers("/administration/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/connexion")
                        .userInfoEndpoint(info -> info.oidcUserService(serviceOidc))
                        .defaultSuccessUrl("/espace", true)
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/deconnexion", "GET"))
                        .logoutSuccessUrl("/?deconnexion")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("PORTAIL_SESSION")
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/acces-refuse")
                );
        return http.build();
    }

    /**
     * Le fournisseur d'identite porte les roles dans un claim "realm_access.roles".
     * On les transforme en autorites Spring afin d'utiliser hasRole("ADMIN").
     */
    @Bean
    public OAuth2UserService<OidcUserRequest, OidcUser> serviceOidc() {
        OidcUserService delegate = new OidcUserService();
        return request -> {
            OidcUser utilisateur = delegate.loadUser(request);
            return new UtilisateurOidcRoles(utilisateur);
        };
    }
}
