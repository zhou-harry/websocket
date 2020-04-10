package com.harry.auth;

import javax.security.auth.Subject;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

public class Authentication implements Principal {

    public static final Map<String, String> USERS_MAP = new HashMap<String, String>();

    static {
        USERS_MAP.put("admin", "admin");
        USERS_MAP.put("harry", "harry");
    }

    private String name;

    public Authentication(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean implies(Subject subject) {
        return false;
    }

}
