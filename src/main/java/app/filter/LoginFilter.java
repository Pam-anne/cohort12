package app.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter(urlPatterns = {"/*"})
public class LoginFilter implements Filter {
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
        HttpSession session = httpRequest.getSession(false);

        // String defaultUri = httpRequest.getContextPath() + "/";
        // String loginUri = httpRequest.getContextPath() + "/login";
        // String indexUri = httpRequest.getContextPath() + "/index.jsp";
        // String contactUsUri = "contact_us";
        // String welcomeUri = "welcome.jsp";
        // String formUri = "form.jsp";
        String pageRequestUri = httpRequest.getRequestURI();
        boolean loggedIn = session != null && session.getAttribute("SESSION_ID") != null;
            String[] publicUrls = {
                "/login",
                "/index.jsp",
                "/contact_us",
                "/welcome.jsp",
                "/form.jsp",
                "/greet.jsp",
                "/login.jsp",
                "/dashboard.jsp",
                "/home.jsp",
                "/about.jsp",
                "/profile.jsp"
        };

        // if(loggedIn || pageRequestUri.equalsIgnoreCase(loginUri)
        //         || pageRequestUri.equalsIgnoreCase(defaultUri)
        //         || pageRequestUri.equalsIgnoreCase(indexUri)
        //         || pageRequestUri.contains(contactUsUri) 
        //         || pageRequestUri.contains(welcomeUri)
        //     || pageRequestUri.contains(formUri)){
        //     filterChain.doFilter(servletRequest, servletResponse);

        // } else {
        //     if (session != null)
        //         session.invalidate();

        //     httpResponse.sendRedirect(loginUri);
        // }
        boolean isPublic = false;

        for (String url : publicUrls) {
            if (pageRequestUri.endsWith(url) ) {
                isPublic = true;
                break;
            }
        }

        if (loggedIn || isPublic) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            if (session != null) session.invalidate();
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
        }

    }
}
