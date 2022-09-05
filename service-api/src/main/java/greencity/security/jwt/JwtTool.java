package greencity.security.jwt;

import greencity.dto.user.UserVO;
import greencity.enums.Role;
import greencity.security.service.AuthorityService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultJwtParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static greencity.constant.AppConstant.ROLE;

/**
 * Class that provides methods for working with JWT.
 *
 * @author Nazar Stasyuk && Yurii Koval.
 * @version 2.0
 */
@Slf4j
@Component
public class JwtTool {
    private final Integer accessTokenValidTimeInMinutes;
    private final Integer refreshTokenValidTimeInMinutes;
    private final String accessTokenKey;
    private final AuthorityService authorityService;

    /**
     * Constructor.
     */
    @Autowired
    public JwtTool(@Value("${accessTokenValidTimeInMinutes}") Integer accessTokenValidTimeInMinutes,
        @Value("${refreshTokenValidTimeInMinutes}") Integer refreshTokenValidTimeInMinutes,
        @Value("${tokenKey}") String accessTokenKey, AuthorityService authorityService) {
        this.accessTokenValidTimeInMinutes = accessTokenValidTimeInMinutes;
        this.refreshTokenValidTimeInMinutes = refreshTokenValidTimeInMinutes;
        this.accessTokenKey = accessTokenKey;
        this.authorityService = authorityService;
    }

    /**
     * Method for creating access token.
     *
     * @param email this is email of user.
     * @param role  this is role of user.
     */
    public String createAccessToken(String email, Role role) {
        Claims claims = Jwts.claims().setSubject(email);
        claims.put(ROLE, Collections.singleton(role.name()));

        if (role.equals(Role.ROLE_UBS_EMPLOYEE)) {
            claims.put("employee_authorities", authorityService.getAllEmployeesAuthorities(email));
        }

        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.MINUTE, accessTokenValidTimeInMinutes);
        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(calendar.getTime())
            .signWith(SignatureAlgorithm.HS256, accessTokenKey)
            .compact();
    }

    /**
     * Method for creating access token.
     *
     * @param user - entity {@link UserVO}
     */
    public String createRefreshToken(UserVO user) {
        Claims claims = Jwts.claims().setSubject(user.getEmail());
        claims.put(ROLE, Collections.singleton(user.getRole().name()));
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.MINUTE, refreshTokenValidTimeInMinutes);
        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(calendar.getTime())
            .signWith(SignatureAlgorithm.HS256, user.getRefreshTokenKey())
            .compact();
    }

    /**
     * Gets email from token and throws an error if token is expired. WARNING: The
     * method DOESN'T CHECK whether the token's signature is valid.
     *
     * @param token - access token
     * @return - user's email
     * @throws io.jsonwebtoken.ExpiredJwtException - if token is expired.
     */
    public String getEmailOutOfAccessToken(String token) {
        String[] splitToken = token.split("\\.");
        String unsignedToken = splitToken[0] + "." + splitToken[1] + ".";
        DefaultJwtParser parser = new DefaultJwtParser();
        Jwt<?, ?> jwt = parser.parse(unsignedToken);
        return ((Claims) jwt.getBody()).getSubject();
    }

    /**
     * Method that check if token still valid.
     *
     * @param token this is token.
     * @return {@link Boolean}
     */
    public boolean isTokenValid(String token, String tokenKey) {
        boolean isValid = false;
        try {
            Jwts.parser().setSigningKey(tokenKey).parseClaimsJws(token);
            isValid = true;
        } catch (Exception e) {
            log.info("Given token is not valid: " + e.getMessage());
        }
        return isValid;
    }

    /**
     * Returns access token key.
     *
     * @return accessTokenKey
     */
    public String getAccessTokenKey() {
        return accessTokenKey;
    }

    /**
     * Method that get token from {@link HttpServletRequest}.
     *
     * @param servletRequest this is your request.
     * @return {@link String} of token or null.
     */
    public String getTokenFromHttpServletRequest(HttpServletRequest servletRequest) {
        return Optional
            .ofNullable(servletRequest.getHeader("Authorization"))
            .filter(authHeader -> authHeader.startsWith("Bearer "))
            .map(token -> token.substring(7))
            .orElse(null);
    }

    /**
     * Generates a random string that can be used as refresh token key.
     *
     * @return random generated token key
     */
    public String generateTokenKey() {
        return UUID.randomUUID().toString();
    }

    /**
     * Generates a random string that can be used as refresh token key with
     * token-expiration date.
     *
     * @return random generated token key with token-expiration date
     */
    public String generateTokenKeyWithCodedDate() {
        Date date = new Date();
        Long dateLong = date.getTime();
        dateLong += 86400000L;
        String input = dateLong + "." + UUID.randomUUID().toString();
        return Base64.getEncoder().encodeToString(input.getBytes());
    }
}
