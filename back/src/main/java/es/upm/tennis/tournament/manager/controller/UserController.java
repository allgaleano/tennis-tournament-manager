package es.upm.tennis.tournament.manager.controller;


import es.upm.tennis.tournament.manager.model.User;
import es.upm.tennis.tournament.manager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getUserInfo(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                "username", user.getUsername(),
                "email", user.getEmail()
        ));
    }

    @GetMapping("/admin/users")
    public ResponseEntity<PagedModel<EntityModel<User>>> getAllUsers(Pageable pageable, PagedResourcesAssembler<User> pagedResourcesAssembler) {
        Page<User> users = userService.getAllUsers(pageable);

        PagedModel<EntityModel<User>> pagedModel = pagedResourcesAssembler.toModel(users, EntityModel::of);
        return ResponseEntity.ok(pagedModel);
    }
}