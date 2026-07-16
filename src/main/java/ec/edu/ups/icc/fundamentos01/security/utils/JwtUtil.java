package ec.edu.ups.icc.fundamentos01.security.utils;

import ec.edu.ups.icc.fundamentos01.security.config.JwtProperties;
import ec.edu.ups.icc.fundamentos01.security.services.UserDetailsImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    // NUEVAS CONSTANTES: Para identificar si el token es access o refresh
    private static final String TOKEN_TYPE_CLAIM = "type";
    private static final String ACCESS_TOKEN_TYPE = "access";
    private static final String REFRESH_TOKEN_TYPE = "refresh";

    private final JwtProperties jwtProperties;
    private final SecretKey key;

    /**
     * Constructor: Inicializa JwtUtil con propiedades y clave secreta
     * (¡Conservamos tu constructor original intacto!)
     */
    public JwtUtil(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
    }

    // =========================================================
    // NUEVOS MÉTODOS DEL .MD PARA REEMPLAZAR LA GENERACIÓN
    // =========================================================

    /*
     * Genera un access token desde Authentication.
     * Este método se usa en login.
     */
    public String generateAccessToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        return buildToken(
                userPrincipal,
                jwtProperties.getExpiration(),
                ACCESS_TOKEN_TYPE
        );
    }

    /*
     * Genera un access token desde UserDetailsImpl.
     * Este método se usa en register y refresh.
     */
    public String generateAccessTokenFromUserDetails(UserDetailsImpl userDetails) {
        return buildToken(
                userDetails,
                jwtProperties.getExpiration(),
                ACCESS_TOKEN_TYPE
        );
    }

    /*
     * Genera un refresh token.
     * Este token dura más tiempo y solo debe usarse en POST /api/auth/refresh
     */
    public String generateRefreshToken(UserDetailsImpl userDetails) {
        return buildToken(
                userDetails,
                jwtProperties.getRefreshExpiration(),
                REFRESH_TOKEN_TYPE
        );
    }

    /*
     * Método centralizado para construir tokens JWT.
     * Reemplaza a tus constructores viejos para no repetir código.
     */
    private String buildToken(
            UserDetailsImpl userDetails,
            Long expirationMs,
            String tokenType
    ) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        String roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .id(java.util.UUID.randomUUID().toString()) // jti: evita tokens idénticos si se emiten en el mismo segundo
                .subject(String.valueOf(userDetails.getId()))
                .claim("email", userDetails.getEmail())
                .claim("name", userDetails.getName())
                .claim("roles", roles)
                .claim(TOKEN_TYPE_CLAIM, tokenType) // Aquí inyecta "access" o "refresh"
                .issuer(jwtProperties.getIssuer())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    /*
     * Mantiene compatibilidad con el nombre anterior.
     * Evita que se rompan otras partes de tu código.
     */
    public String generateToken(Authentication authentication) {
        return generateAccessToken(authentication);
    }

    /*
     * Mantiene compatibilidad con el nombre anterior.
     */
    public String generateTokenFromUserDetails(UserDetailsImpl userDetails) {
        return generateAccessTokenFromUserDetails(userDetails);
    }

    /*
     * Extrae el email desde cualquier token válido.
     */
    public String getEmailFromToken(String token) {
        Claims claims = getClaims(token);
        return claims.get("email", String.class);
    }

    /*
     * Extrae el id del usuario desde el subject.
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaims(token);
        return Long.parseLong(claims.getSubject());
    }

    /*
     * NUEVO: Extrae el tipo del token.
     * Valores esperados: "access" o "refresh"
     */
    public String getTokenType(String token) {
        Claims claims = getClaims(token);
        return claims.get(TOKEN_TYPE_CLAIM, String.class);
    }

    /*
     * Valida firma, formato y expiración del token.
     * (Conservamos todos tus System.out y logger de errores)
     */
    public boolean validateToken(String authToken) {
        try {
            getClaims(authToken);
            return true;

        } catch (SignatureException ex) {
            logger.error("Firma JWT inválida: {}", ex.getMessage());

        } catch (MalformedJwtException ex) {
            logger.error("Token JWT malformado: {}", ex.getMessage());

        } catch (ExpiredJwtException ex) {
            logger.error("Token JWT expirado: {}", ex.getMessage());

        } catch (UnsupportedJwtException ex) {
            logger.error("Token JWT no soportado: {}", ex.getMessage());

        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string está vacío: {}", ex.getMessage());
        }

        return false;
    }

    public boolean validateAccessToken(String token) {
        return validateToken(token) &&
                ACCESS_TOKEN_TYPE.equals(getTokenType(token));
    }

    public boolean validateRefreshToken(String token) {
        return validateToken(token) &&
                REFRESH_TOKEN_TYPE.equals(getTokenType(token));
    }


    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}