package com.katrikken.gdpai.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class SessionController {

    // Allow the React dev server to create the session cookie.
    @CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true")
    @GetMapping("/session/start")
    public Map<String, String> startSession(HttpServletRequest request) {
        HttpSession session = request.getSession(true); // create session if missing
        return Map.of("sessionId", session.getId(), "status", "started");
    }
}
