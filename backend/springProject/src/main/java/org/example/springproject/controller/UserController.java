package org.example.springproject.controller;

import org.example.springproject.entity.User;
import org.example.springproject.service.IUserService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private IUserService userService;

    @GetMapping("/list")
    public List<User> getAllUsers() {
        return userService.list();
    }

    @GetMapping("/getUserById")
    public User getUserById(@RequestParam Integer id) {
        return userService.getById(id);
    }

    @PostMapping("/addUser")
    public boolean addUser(@RequestBody User user) {
        return userService.save(user);
    }

    @PutMapping("/updateUser")
    public boolean updateUser(@RequestBody User user) {
        return userService.updateById(user);
    }

    @DeleteMapping("/deleteUser")
    public boolean deleteUser(@RequestParam Integer id) {
        return userService.removeById(id);
    }
}
