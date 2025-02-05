package com.khalid.anwargroup.security;

import com.khalid.anwargroup.constant.RestErrorMessageDetail;
import com.khalid.anwargroup.constant.Constants;
import com.khalid.anwargroup.constant.RestResponseMessage;
import com.khalid.anwargroup.constant.RestResponseStatusCode;
import com.khalid.anwargroup.domain.AccountInfo;
import com.khalid.anwargroup.domain.RoleInfo;
import com.khalid.anwargroup.repository.AccountInfoRepository;
import com.khalid.anwargroup.repository.RoleInfoRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import static com.khalid.anwargroup.constant.RestApiResponse.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JwtTokenAuthorizationOncePerRequestFilter extends OncePerRequestFilter {

    @Value("${jwt.get.token.uri}")
    private String authenticationPath;
    @Value("${jwt.refresh.token.uri}")
    private String refreshPath;
    @Value("${jwt.logout.token.uri}")
    private String logoutPath;
    @Value("${jwt.http.request.header}")
    private String tokenHeader;

    @Value("${insecurity.path.publicPath}")
    private String publicPath;

    private final UserDetailsService jwtInMemoryUserDetailsService;

    private final JwtTokenUtil jwtTokenUtil;

    private final RoleInfoRepository roleInfoRepository;
    private final AccountInfoRepository accountInfoRepository;

    private boolean isInsecurity(String[] paths, String matchPath) {
        return Arrays.stream(paths).anyMatch(path -> matchPath.contains(path.replace("/**", "")));
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        log.debug("Authentication Request For '{}'", request.getRequestURL());
        final String requestTokenHeader = request.getHeader(this.tokenHeader);

        String username = null;
        String jwtToken = null;

        if (requestTokenHeader != null && requestTokenHeader.startsWith(Constants.Security.TOKEN_TYPE)) {

            jwtToken = requestTokenHeader.substring(Constants.Security.TOKEN_TYPE_LEN);
            AccountInfo accountInfo = accountInfoRepository.findByAccessKey(jwtToken).orElse(null);

            RoleInfo roleInfo = roleInfoRepository.findByAccessKey(jwtToken).orElse(null);

            RoleInfo roleInfoByUser = roleInfoRepository.findByUserAccessKey(jwtToken).orElse(null);

            if ((Objects.isNull(roleInfo) || Objects.isNull(accountInfo))){
                writeInvalidTokenErrorResponse(response, RestErrorMessageDetail.INVALID_TOKEN_ERROR_MESSAGE, RestResponseMessage.INVALID_TOKEN, RestResponseStatusCode.NO_ACCESS_STATUS);
                return;
            }

            try{
                username = jwtTokenUtil.getUsernameFromToken(jwtToken);

                // Check if the user's role does not match the expected role and the request is not for refresh or logout
                // If any of these conditions are true, return a "ROLE_MISMATCH" response with a "NO_ACCESS_STATUS" status code
                if((!Objects.isNull(roleInfo) &&
                        !roleInfo.getRoleName().equals(jwtTokenUtil.getAllClaimsFromToken(jwtToken).get("roles").toString())) &&
                        (!Objects.isNull(roleInfoByUser) && !roleInfoByUser.getRoleName().equals(jwtTokenUtil.getAllClaimsFromToken(jwtToken).get("roles").toString())) &&
                        !String.valueOf(request.getRequestURL()).contains(refreshPath) && !String.valueOf(request.getRequestURL()).contains(logoutPath)){
                    writeDynamicTokenResponse(response, RestResponseMessage.ROLE_MISMATCH, RestResponseStatusCode.NO_ACCESS_STATUS);
                    return;
                }
            } catch (IllegalArgumentException | MalformedJwtException | SignatureException e){
                log.error("JWT_TOKEN_UNABLE_TO_GET_USERNAME", e);
                writeInvalidTokenErrorResponse(response, RestErrorMessageDetail.INVALID_TOKEN_ERROR_MESSAGE, RestResponseMessage.INVALID_TOKEN, RestResponseStatusCode.NO_ACCESS_STATUS);
                return;
            } catch (ExpiredJwtException e) {
                log.warn("JWT_TOKEN_EXPIRED", e);
                writeInvalidTokenErrorResponse(response, RestErrorMessageDetail.EXPIRED_TOKEN_ERROR_MESSAGE, RestResponseMessage.EXPIRED_TOKEN, RestResponseStatusCode.NO_ACCESS_STATUS);
                return;
            }

            log.debug("JWT_TOKEN_USERNAME_VALUE '{}'", username);

        }else{
            if (!isInsecurity(
                    new String[]{authenticationPath, publicPath},
                    String.valueOf(request.getRequestURL()))) {
                log.warn("JWT_TOKEN_DOES_NOT_START_WITH_TOKEN_STRING");
                writeInvalidTokenErrorResponse(response, RestErrorMessageDetail.INVALID_TOKEN_ERROR_MESSAGE, RestResponseMessage.INVALID_TOKEN, RestResponseStatusCode.NO_ACCESS_STATUS);
                return;
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails;
            try {
                userDetails = this.jwtInMemoryUserDetailsService.loadUserByUsername(username);
            } catch (UsernameNotFoundException ex) {
                writeInvalidTokenErrorResponse(response, RestErrorMessageDetail.INVALID_USER_TOKEN_ERROR_MESSAGE, RestResponseMessage.INVALID_TOKEN, RestResponseStatusCode.NO_ACCESS_STATUS);
                return;
            }
            if (Boolean.TRUE.equals(jwtTokenUtil.validateToken(jwtToken, userDetails))) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
