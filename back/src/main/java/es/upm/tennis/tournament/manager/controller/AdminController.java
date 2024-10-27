package es.upm.tennis.tournament.manager.controller;

import es.upm.tennis.tournament.manager.DTO.UserDisplayDTO;
import es.upm.tennis.tournament.manager.model.User;
import es.upm.tennis.tournament.manager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public ResponseEntity<PagedModel<EntityModel<UserDisplayDTO>>> getAllUsers(Pageable pageable, PagedResourcesAssembler<UserDisplayDTO> pagedResourcesAssembler) {
        Page<User> users = userService.getAllUsers(pageable);
        Page<UserDisplayDTO> usersDTO = users.map(user -> {
            UserDisplayDTO dto = new UserDisplayDTO();
            dto.setId(user.getId());
            dto.setName(user.getName());
            dto.setPhoneNumber(String.format("+ %s %s", user.getPhonePrefix(), user.getPhoneNumber()));
            dto.setCreatedAt(user.getCreatedAt().toString());
            dto.setUsername(user.getUsername());
            dto.setEmail(user.getEmail());
            dto.setEnabledAccount(user.isEnabled());
            dto.setRole(user.getRole().getType().name());
            return dto;
        });

        PagedModel<EntityModel<UserDisplayDTO>> pagedModel = pagedResourcesAssembler.toModel(usersDTO, EntityModel::of);
        return ResponseEntity.ok(pagedModel);
    }
}
