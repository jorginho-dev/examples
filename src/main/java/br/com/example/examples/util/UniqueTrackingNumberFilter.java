package br.com.example.examples.util;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Component
public class UniqueTrackingNumberFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(UniqueTrackingNumberFilter.class);
    public static final String X_SESSION_ID_KEY = "x-sessionId";
    public static final String SESSION_ID_KEY = "sessionId";
    public static final String X_TRACE_ID_KEY = "x-traceId";
    public static final String TRACE_ID_KEY = "traceId";
    public static final String EMPTY_TRACE_ID = "";
    public static final String X_CONSUMER_USERNAME = "x-consumer-username";
    public static final String AUTHORIZATION = "authorization";
    public static final String CONSUMER_USERNAME_KEY = "customer-name";
    public static final String BRANCH_ID = "branchId";
    @Value("${log.client-info.enable:true}")
    private boolean logClientInfoEnable;
    @Value("${log.branch-info.enable:true}")
    private boolean logBranchInfoEnable;

    public UniqueTrackingNumberFilter() {
    }

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String traceId = (String) Optional.ofNullable(request.getHeader("x-traceId")).orElseGet(UniqueTrackingNumberFilter::generateTraceIdToMDC);
        MDC.put("traceId", traceId);
        String sessionId = request.getHeader("x-sessionId");
        if (Objects.nonNull(sessionId)) {
            MDC.put("sessionId", sessionId);
        }

        String consumerUsername = request.getHeader("x-consumer-username");
        if (Objects.nonNull(consumerUsername)) {
            MDC.put("customer-name", consumerUsername);
        }

        try {
            this.logRequestInfo(request);
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove("traceId");
            MDC.remove("sessionId");
            MDC.remove("customer-name");
        }

    }

    public static String generateTraceIdToMDC() {
        return UUID.randomUUID().toString();
    }

    private void logRequestInfo(HttpServletRequest request) {
        String consumerUsername = request.getHeader("x-consumer-username");
        String branchId = request.getHeader("branchId");
        boolean canLogInfo = false;
        StringBuilder clientInfo = new StringBuilder("Client");
        if (this.logClientInfoEnable && Objects.nonNull(consumerUsername)) {
            clientInfo.append(String.format(" consumer=\"%s\"", consumerUsername));
            canLogInfo = true;
        }

        if (this.logBranchInfoEnable && Objects.nonNull(branchId)) {
            clientInfo.append(String.format(" branchId=\"%s\"", branchId));
            canLogInfo = true;
        }

        if (canLogInfo) {
            log.info(clientInfo.toString());
        }

    }
}
