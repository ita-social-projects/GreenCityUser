package greencity.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import greencity.security.filters.AccessTokenAuthenticationFilter;
import greencity.security.jwt.JwtTool;
import greencity.security.providers.JwtAuthenticationProvider;
import greencity.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

import static greencity.constant.AppConstant.*;
import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

/**
 * Config for security.
 *
 * @author Nazar Stasyuk && Yurii Koval
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final JwtTool jwtTool;
    private final UserService userService;
    private static final String USER_LINK = "/user";

    /**
     * Constructor.
     */

    @Autowired
    public SecurityConfig(JwtTool jwtTool, UserService userService) {
        this.jwtTool = jwtTool;
        this.userService = userService;
    }

    /**
     * Bean {@link PasswordEncoder} that uses in coding password.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Method for configure security.
     *
     * @param http {@link HttpSecurity}
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors()
            .and()
            .csrf()
            .disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .addFilterBefore(
                new AccessTokenAuthenticationFilter(jwtTool, authenticationManager(), userService),
                UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling()
            .authenticationEntryPoint((req, resp, exc) -> resp.sendError(SC_UNAUTHORIZED, "Authorize first."))
            .accessDeniedHandler((req, resp, exc) -> resp.sendError(SC_FORBIDDEN, "You don't have authorities."))
            .and()
            .authorizeRequests()
            .antMatchers("/static/css/**",
                "/static/img/**")
            .permitAll()
            .antMatchers(HttpMethod.GET,
                "/ownSecurity/verifyEmail",
                "/ownSecurity/updateAccessToken",
                "/ownSecurity/restorePassword",
                "/googleSecurity",
                "/facebookSecurity/generateFacebookAuthorizeURL",
                "/facebookSecurity/facebook",
                "/user/emailNotifications",
                "/user/activatedUsersAmount",
                "/user/{userId}/habit/assign",
                "/token",
                "/socket/**",
                "/user/findAllByEmailNotification")
            .permitAll()
            .antMatchers(HttpMethod.POST,
                "/ownSecurity/signUp",
                "/ownSecurity/signIn",
                "/ownSecurity/updatePassword",
                "/email/addEcoNews",
                "/email/sendReport",
                "/email/changePlaceStatus",
                "/email/sendHabitNotification")
            .permitAll()
            .antMatchers(HttpMethod.GET,
                USER_LINK,
                "/user/shopping-list-items/habits/{habitId}/shopping-list",
                "/user/{userId}/custom-shopping-list-items/available",
                "/user/{userId}/sixUserFriends/",
                "/user/{userId}/profile/",
                "/user/isOnline/{userId}/",
                "/user/{userId}/profileStatistics/",
                "/user/userAndSixFriendsWithOnlineStatus",
                "/user/userAndAllFriendsWithOnlineStatus",
                "/user/{userId}/recommendedFriends/",
                "/user/{userId}/findAll/friends/",
                "/user/{userId}/friendRequests/",
                "/user/findByIdForAchievement",
                "/user/findNotDeactivatedByEmail",
                "/user/findByEmail",
                "/user/findIdByEmail",
                "/user/findById",
                "/user/findUuidByEmail",
                "/user/createUbsRecord")
            .hasAnyRole(USER, ADMIN, MODERATOR)
            .antMatchers(HttpMethod.POST,
                USER_LINK,
                "/user/shopping-list-items",
                "/user/{userId}/habit",
                "/user/{userId}/userFriend/{friendId}",
                "/user/{userId}/declineFriend/{friendId}",
                "/user/{userId}/acceptFriend/{friendId}")
            .hasAnyRole(USER, ADMIN, MODERATOR)
            .antMatchers(HttpMethod.PUT,
                "/ownSecurity/changePassword",
                "/user/profile",
                "/user/{id}/updateUserLastActivityTime/{date}",
                "/user/{userId}/language/{languageId}")
            .hasAnyRole(USER, ADMIN, MODERATOR)
            .antMatchers(HttpMethod.PATCH,
                "/user/shopping-list-items/{userShoppingListItemId}",
                "/user/profilePicture",
                "/user/deleteProfilePicture")
            .hasAnyRole(USER, ADMIN, MODERATOR)
            .antMatchers(HttpMethod.DELETE,
                "/user/shopping-list-items/user-shopping-list-items",
                "/user/shopping-list-items",
                "/user/{userId}/userFriend/{friendId}")
            .hasAnyRole(USER, ADMIN, MODERATOR)
            .antMatchers(HttpMethod.GET,
                "/user/all",
                "/user/roles",
                "/user/findById",
                "/user/findUserForManagement",
                "/user/searchBy",
                "/user/findAll",
                "/user/{id}/friends")
            .hasAnyRole(ADMIN, MODERATOR)
            .antMatchers(HttpMethod.POST,
                "/user/filter",
                "/ownSecurity/register")
            .hasRole(ADMIN)
            .antMatchers(HttpMethod.PUT,
                USER_LINK)
            .hasRole(ADMIN)
            .antMatchers(HttpMethod.PATCH,
                "/user/status",
                "/user/role",
                "/user/update/role")
            .hasRole(ADMIN)
            .antMatchers(HttpMethod.POST,
                "/management/login")
            .permitAll()
            .antMatchers(HttpMethod.GET,
                "/management/login")
            .permitAll()
            .antMatchers("/css/**",
                "/img/**")
            .permitAll()
            .anyRequest().hasAnyRole(ADMIN);
    }

    /**
     * Method for configure matchers that will be ignored in security.
     *
     * @param web {@link WebSecurity}
     */
    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/v2/api-docs/**");
        web.ignoring().antMatchers("/swagger.json");
        web.ignoring().antMatchers("/swagger-ui.html");
        web.ignoring().antMatchers("/swagger-resources/**");
        web.ignoring().antMatchers("/webjars/**");
    }

    /**
     * Method for configure type of authentication provider.
     *
     * @param auth {@link AuthenticationManagerBuilder}
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(new JwtAuthenticationProvider(jwtTool));
    }

    /**
     * Provides AuthenticationManager.
     *
     * @return {@link AuthenticationManager}
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    /**
     * Bean {@link CorsConfigurationSource} that uses for CORS setup.
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("*"));
        configuration.setAllowedMethods(
            Arrays.asList("GET", "POST", "OPTIONS", "DELETE", "PUT", "PATCH"));
        configuration.setAllowedHeaders(
            Arrays.asList(
                "X-Requested-With", "Origin", "Content-Type", "Accept", "Authorization"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Bean {@link GoogleIdTokenVerifier} that uses in verify googleIdToken.
     *
     * @param clientId {@link String} - google client id.
     */
    @Bean
    public GoogleIdTokenVerifier googleIdTokenVerifier(@Value("${google.clientId}") String clientId) {
        return new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance())
            .setAudience(Collections.singletonList(clientId))
            .build();
    }
}
