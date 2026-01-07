    package com.carwash.auth_service.controller;

    import com.carwash.auth_service.domain.User;
    import com.carwash.auth_service.dto.AuthRequest;
    import com.carwash.auth_service.dto.AuthResponse;
    import com.carwash.auth_service.dto.UserResponse;
    import com.carwash.auth_service.security.FirebaseTokenVerifier;
    import com.carwash.auth_service.security.JwtUtil;
    import com.carwash.auth_service.service.UserService;
    import com.google.firebase.auth.FirebaseToken;
    import org.springframework.web.bind.annotation.*;

    @RestController
    @RequestMapping("/auth")
    public class AuthController {

        private final FirebaseTokenVerifier tokenVerifier;
        private final UserService userService;
        private final JwtUtil jwtUtil;

        public AuthController(FirebaseTokenVerifier tokenVerifier,
                              UserService userService, JwtUtil jwtUtil) {
            this.tokenVerifier = tokenVerifier;
            this.userService = userService;
            this.jwtUtil = jwtUtil;
        }

//        @PostMapping("/login")
//        public UserResponse login(@RequestBody AuthRequest request) {
//
//            FirebaseToken token = tokenVerifier.verify(request.getFirebaseToken());
//
//            String phone = (String) token.getClaims().get("phone_number");
//
//            System.out.println("Phone number from token" +phone );
//            System.out.println("Token"+ token.getClaims().toString());
//            if (phone == null) {
//                throw new RuntimeException("Phone number not found in Firebase token");
//            }
//
//            return userService.getUserByPhone(phone);
//        }

        @PostMapping("/login")
        public AuthResponse login(@RequestBody AuthRequest request) {

            // 1️⃣ Verify Firebase token
            FirebaseToken token = tokenVerifier.verify(request.getFirebaseToken());

            String phone = (String) token.getClaims().get("phone_number");

            System.out.println("Phone number" + phone );

            if (phone == null) {
                throw new RuntimeException("Phone number not found in Firebase token");
            }

            // 2️⃣ Login or auto-register user
            User user = userService.loginOrRegister(phone);



            // 3️⃣ Generate JWT
            String jwt = jwtUtil.generateToken(user.getId(), user.getPhoneNumber());

            // 4️⃣ Return JWT
            return new AuthResponse(jwt, user.getPhoneNumber());
        }


    }
