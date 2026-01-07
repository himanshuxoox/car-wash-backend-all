package com.carwash.auth_service.controller;

import  com.carwash.auth_service.dto.CreateUserRequest;
import com.carwash.auth_service.dto.UserResponse;
import com.carwash.auth_service.service.UserService;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {

       this.userService = userService;
}

@PostMapping
public UserResponse createUser(@RequestBody CreateUserRequest request) {
    return userService.createUser(request);
}

@GetMapping("/{phone}")
public UserResponse getUser(@PathVariable String phone) {
    return userService.getUserByPhone(phone);
}


}
