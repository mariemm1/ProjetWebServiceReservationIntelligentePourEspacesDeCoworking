package org.example.projetwebservice.Controller;

import org.example.projetwebservice.DTO.WorkspaceRequest;
import org.example.projetwebservice.JWT.SecurityUtil;
import org.example.projetwebservice.Model.Enum.Role;
import org.example.projetwebservice.Model.User;
import org.example.projetwebservice.Model.Workspace;
import org.example.projetwebservice.Repository.UserRepository;
import org.example.projetwebservice.Repository.WorkspaceRepository;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.stereotype.Controller;

@Controller
public class WorkspaceMutationController {

    private final UserRepository userRepository;
    private final WorkspaceRepository workspaceRepository;

    public WorkspaceMutationController(UserRepository userRepository,
                                       WorkspaceRepository workspaceRepository) {
        this.userRepository = userRepository;
        this.workspaceRepository = workspaceRepository;
    }

    @MutationMapping
    public Workspace createWorkspace(@Argument WorkspaceRequest input) {
        User current = getCurrentUser();
        if (current.getRole() != Role.ADMIN) {
            throw new RuntimeException("Access Denied: Only ADMIN can create workspace");
        }

        Workspace workspace = new Workspace();
        workspace.setName(input.getName());
        workspace.setType(input.getType());
        return workspaceRepository.save(workspace);
    }

    @MutationMapping
    public Workspace updateWorkspace(@Argument Long id, @Argument WorkspaceRequest input) {
        User current = getCurrentUser();
        if (current.getRole() != Role.ADMIN) {
            throw new RuntimeException("Access Denied: Only ADMIN can update workspace");
        }

        return workspaceRepository.findById(id)
                .map(workspace -> {
                    workspace.setName(input.getName());
                    workspace.setType(input.getType());
                    return workspaceRepository.save(workspace);
                })
                .orElseThrow(() -> new RuntimeException("Workspace not found"));
    }

    @MutationMapping
    public Boolean deleteWorkspace(@Argument Long id) {
        User current = getCurrentUser();
        if (current.getRole() != Role.ADMIN) {
            throw new RuntimeException("Access Denied: Only ADMIN can delete workspace");
        }

        if (workspaceRepository.existsById(id)) {
            workspaceRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private User getCurrentUser() {
        String email = SecurityUtil.getCurrentUsername();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non authentifié"));
    }
}