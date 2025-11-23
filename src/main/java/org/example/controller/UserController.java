package org.example.controller;

import org.example.dto.CreateUserRequest;
import org.example.dto.UpdateUserRequest;
import org.example.dto.UserDto;
import org.example.service.UserService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "APIs for managing users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @Operation(
            summary = "Create a new user",
            description = "Creates a new user with name, email, and age. All fields are mandatory."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User created successfully",
                    content = @Content(schema = @Schema(implementation = UserDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = String.class))
            )
    })
    public ResponseEntity<EntityModel<UserDto>> createUser(
            @Parameter(
                    description = "User creation request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateUserRequest.class))
            )
            @Valid @RequestBody CreateUserRequest request) {

        UserDto userDto = userService.createUser(request);
        EntityModel<UserDto> resource = EntityModel.of(userDto);

        // Add HATEOAS links
        resource.add(linkTo(methodOn(UserController.class).getUserById(userDto.getId())).withSelfRel());
        resource.add(linkTo(methodOn(UserController.class).updateUser(userDto.getId(), null)).withRel("update"));
        resource.add(linkTo(methodOn(UserController.class).deleteUser(userDto.getId())).withRel("delete"));
        resource.add(linkTo(methodOn(UserController.class).getAllUsers()).withRel("all-users"));

        return new ResponseEntity<>(resource, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get user by ID",
            description = "Retrieves a user by their unique identifier including creation timestamp"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User found",
                    content = @Content(schema = @Schema(implementation = UserDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = String.class))
            )
    })
    public ResponseEntity<EntityModel<UserDto>> getUserById(
            @Parameter(
                    description = "User ID",
                    required = true,
                    example = "1"
            )
            @PathVariable Long id) {

        UserDto userDto = userService.getUserById(id);
        EntityModel<UserDto> resource = EntityModel.of(userDto);

        resource.add(linkTo(methodOn(UserController.class).getUserById(id)).withSelfRel());
        resource.add(linkTo(methodOn(UserController.class).updateUser(id, null)).withRel("update"));
        resource.add(linkTo(methodOn(UserController.class).deleteUser(id)).withRel("delete"));
        resource.add(linkTo(methodOn(UserController.class).getAllUsers()).withRel("all-users"));

        return ResponseEntity.ok(resource);
    }

    @GetMapping
    @Operation(
            summary = "Get all users",
            description = "Retrieves a list of all users with their details and creation timestamps"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved list of users"
    )
    public ResponseEntity<CollectionModel<EntityModel<UserDto>>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();

        List<EntityModel<UserDto>> userResources = users.stream()
                .map(user -> {
                    EntityModel<UserDto> resource = EntityModel.of(user);
                    resource.add(linkTo(methodOn(UserController.class).getUserById(user.getId())).withSelfRel());
                    resource.add(linkTo(methodOn(UserController.class).updateUser(user.getId(), null)).withRel("update"));
                    resource.add(linkTo(methodOn(UserController.class).deleteUser(user.getId())).withRel("delete"));
                    return resource;
                })
                .collect(Collectors.toList());

        CollectionModel<EntityModel<UserDto>> collection = CollectionModel.of(userResources);
        collection.add(linkTo(methodOn(UserController.class).getAllUsers()).withSelfRel());
        collection.add(linkTo(methodOn(UserController.class).createUser(null)).withRel("create-user"));

        return ResponseEntity.ok(collection);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update user",
            description = "Updates an existing user's information including name, email, and age"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User updated successfully",
                    content = @Content(schema = @Schema(implementation = UserDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = String.class))
            )
    })
    public ResponseEntity<EntityModel<UserDto>> updateUser(
            @Parameter(
                    description = "User ID",
                    required = true,
                    example = "1"
            )
            @PathVariable Long id,

            @Parameter(
                    description = "User update request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UpdateUserRequest.class))
            )
            @Valid @RequestBody UpdateUserRequest request) {

        UserDto userDto = userService.updateUser(id, request);
        EntityModel<UserDto> resource = EntityModel.of(userDto);

        resource.add(linkTo(methodOn(UserController.class).getUserById(id)).withSelfRel());
        resource.add(linkTo(methodOn(UserController.class).updateUser(id, null)).withRel("update"));
        resource.add(linkTo(methodOn(UserController.class).deleteUser(id)).withRel("delete"));
        resource.add(linkTo(methodOn(UserController.class).getAllUsers()).withRel("all-users"));

        return ResponseEntity.ok(resource);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete user",
            description = "Deletes a user by their ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "User deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = String.class))
            )
    })
    public ResponseEntity<Void> deleteUser(
            @Parameter(
                    description = "User ID",
                    required = true,
                    example = "1"
            )
            @PathVariable Long id) {

        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @Operation(hidden = true)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
