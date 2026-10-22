package com.example.interview_helper_backend.controller;


import com.example.interview_helper_backend.model.dto.UserLoginDTO;
import com.example.interview_helper_backend.model.dto.UserRegisterDTO;
import com.example.interview_helper_backend.model.vo.UserLoginVO;
import com.example.interview_helper_backend.model.vo.UserProfileVO;
import com.example.interview_helper_backend.result.Result;
import com.example.interview_helper_backend.service.UserService;
import com.example.interview_helper_backend.util.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody UserRegisterDTO dto) {
        userService.register(dto);
        return Result.success("注册成功");
    }
    @PostMapping("/login")
    public Result<UserLoginVO> login(@Valid @RequestBody UserLoginDTO dto, HttpServletRequest request) {
        UserLoginVO userLoginVO=new UserLoginVO();
        userLoginVO.setToken(userService.login(dto, request.getRemoteAddr()));
        return Result.success("登录成功",userLoginVO);
    }
    @GetMapping("/profile")
    public Result<UserProfileVO> profile() {
        return Result.success(userService.profile(UserContext.getUserId()));
    }
    @PostMapping("/logout")
    public Result<Void> logout() {
        userService.logout();
        return Result.success("已退出登录");
    }
    @DeleteMapping("/delete")
    public Result<Void> delete(){
        userService.deleteUser();
        return Result.success("注销账号成功");
    }
}
