package com.returncode.spring.security.demo;

import com.returncode.spring.security.demo.entity.TokenAuthenticationDetails;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SaveContextOnUpdateOrErrorResponseWrapper;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.web.util.WebUtils;

import javax.servlet.AsyncContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义 Token for SecurityContextRepository
 */
@Component
public class HttpTokenSecurityContextRepository implements SecurityContextRepository {
    private static Logger logger = LogManager.getLogger();
    private final static String TOKEN_HEADER_NAME = "AUTH-TOKEN";
    private boolean disableUrlRewriting = false;
    private boolean isServlet3 = ClassUtils.hasMethod(ServletRequest.class, "startAsync");
    private AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();
    @Autowired
    HttpToken httpToken;

    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder httpRequestResponseHolder) {
        HttpServletRequest request = httpRequestResponseHolder.getRequest();
        HttpServletResponse response = httpRequestResponseHolder.getResponse();
        String token = request.getHeader(TOKEN_HEADER_NAME);
        SecurityContext context = readSecurityContextFromCache(token);
        if (context == null) {
            context = SecurityContextHolder.createEmptyContext();
        }
        SaveToCacheResponseWrapper wrappedResponse = new SaveToCacheResponseWrapper(response, request, context);
        httpRequestResponseHolder.setResponse(wrappedResponse);
        if (isServlet3) {
            httpRequestResponseHolder.setRequest(new HttpTokenSecurityContextRepository.Servlet3SaveToCacheRequestWrapper(request, wrappedResponse));
        }
        return context;
    }

    @Override
    public void saveContext(SecurityContext securityContext, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        SaveToCacheResponseWrapper responseWrapper = WebUtils.getNativeResponse(httpServletResponse, SaveToCacheResponseWrapper.class);
        if (responseWrapper == null) {
            throw new IllegalStateException("Cannot invoke saveContext on response " + httpServletResponse + ". You must use the HttpRequestResponseHolder.response after invoking loadContext");
        }
        // saveContext() might already be called by the response wrapper
        // if something in the chain called sendError() or sendRedirect(). This ensures we
        // only call it
        // once per request.
        if (!responseWrapper.isContextSaved()) {
            responseWrapper.saveContext(securityContext);
        }
    }

    @Override
    public boolean containsContext(HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader(TOKEN_HEADER_NAME);
        if (token == null) {
            return false;
        }
        return httpToken.hasToken(token);
    }

    private SecurityContext readSecurityContextFromCache(String token) {
        if (token == null) {
            return null;
        }
        Object contextFromCache = httpToken.getToken(token);
        if (contextFromCache == null) {
            return null;
        }
        if (!(contextFromCache instanceof SecurityContext)) {
            return null;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Obtained a valid SecurityContext from " + token + ": '" + contextFromCache + "'");
        }
        return (SecurityContext) contextFromCache;
    }

    private static class Servlet3SaveToCacheRequestWrapper extends HttpServletRequestWrapper {
        private final SaveContextOnUpdateOrErrorResponseWrapper response;

        public Servlet3SaveToCacheRequestWrapper(HttpServletRequest request, SaveContextOnUpdateOrErrorResponseWrapper response) {
            super(request);
            this.response = response;
        }

        @Override
        public AsyncContext startAsync() {
            response.disableSaveOnResponseCommitted();
            return super.startAsync();
        }

        @Override
        public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
            response.disableSaveOnResponseCommitted();
            return super.startAsync(servletRequest, servletResponse);
        }
    }

    final class SaveToCacheResponseWrapper extends SaveContextOnUpdateOrErrorResponseWrapper {
        private final HttpServletRequest request;
        private final SecurityContext contextBeforeExecution;
        private final Authentication authBeforeExecution;

        SaveToCacheResponseWrapper(HttpServletResponse response, HttpServletRequest request, SecurityContext context) {
            super(response, disableUrlRewriting);
            this.request = request;
            this.contextBeforeExecution = context;
            this.authBeforeExecution = context.getAuthentication();
        }

        @Override
        protected void saveContext(SecurityContext context) {
            final Authentication authentication = context.getAuthentication();
            String token = request.getHeader(TOKEN_HEADER_NAME);
            if (authentication == null || trustResolver.isAnonymous(authentication)) {
                if (token != null && authBeforeExecution != null) {
                    // SEC-1587 A non-anonymous context may still be in the session
                    // SEC-1735 remove if the contextBeforeExecution was not anonymous
                    httpToken.removeToken(token);
                }
                return;
            }
            if (token == null) {
                TokenAuthenticationDetails details = (TokenAuthenticationDetails) context.getAuthentication().getDetails();
                token = details.getTokenId();
            }
            if (token != null) {
                // We may have a new session, so check also whether the context attribute
                // is set SEC-1561
                if (contextChanged(context) || !httpToken.hasToken(token)) {
                    httpToken.setToken(token, context);
                    if (logger.isDebugEnabled()) {
                        logger.debug("SecurityContext '" + context + "' stored to authHeader: '" + token);
                    }
                }
            }
        }

        private boolean contextChanged(SecurityContext context) {
            return context != contextBeforeExecution || context.getAuthentication() != authBeforeExecution;
        }
    }
}
