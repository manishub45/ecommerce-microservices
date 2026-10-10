package com.api_gateway.filter;



import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.*;

public class UserIdHeaderWrapper extends HttpServletRequestWrapper {

    public static final String USER_ID_HEADER = "X-User-Id";

    private final String userId;

    public UserIdHeaderWrapper(HttpServletRequest request, String userId) {
        super(request);
        this.userId = userId;
    }

    @Override
    public String getHeader(String name) {
        if (USER_ID_HEADER.equalsIgnoreCase(name)) {
            return userId;
        }
        return super.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        if (USER_ID_HEADER.equalsIgnoreCase(name)) {
            if (userId == null) {
                return Collections.emptyEnumeration();
            }
            return Collections.enumeration(List.of(userId));
        }
        return super.getHeaders(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        List<String> names = new ArrayList<>();

        Enumeration<String> original = super.getHeaderNames();
        while (original.hasMoreElements()) {
            String name = original.nextElement();
            if (!USER_ID_HEADER.equalsIgnoreCase(name)) {
                names.add(name);
            }
        }

        if (userId != null) {
            names.add(USER_ID_HEADER);
        }

        return Collections.enumeration(names);
    }
}
