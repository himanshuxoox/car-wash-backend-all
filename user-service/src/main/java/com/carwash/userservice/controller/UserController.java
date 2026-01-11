package com.carwash.userservice.controller;

import com.carwash.userservice.domain.User;
import com.carwash.userservice.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public User me(@RequestHeader("X-User-Phone") String phone) {
        System.out.println(phone);
        return userService.getByPhone(phone);
    }
}
