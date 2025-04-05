package com.ybyw.epol.filters;

import com.ybyw.epol.services.UserDetailsServiceImpl;
import com.ybyw.epol.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    public JwtRequestFilter(UserDetailsServiceImpl userDetailsService, JwtUtil jwtUtil) {
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtil jwtUtil;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //Authorization = Bearer 2352345235sdfrsfgsdfsdf
        String authHeader = request.getHeader("Authorization");

//        logger.info(" Header :  {}", requestHeader);
        String username = null;
        String token = null;

        if (authHeader != null && authHeader.startsWith("Bearer")) {
            //looking good
            token = authHeader.substring(7);
            username = jwtUtil.extractUsername(token);

            System.out.println(token);
        }


        // authentication null and user name have check
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            //fetch user details from username
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            Boolean validateToken = jwtUtil.validateToken(token, userDetails);
            if (validateToken) {

                //set the authentication
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);


            } else {
                logger.info("Validation fails !!");
            }


        }

        filterChain.doFilter(request, response);
    }
}
