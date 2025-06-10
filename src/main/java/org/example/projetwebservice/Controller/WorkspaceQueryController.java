package org.example.projetwebservice.Controller;

import org.example.projetwebservice.JWT.SecurityUtil;
import org.example.projetwebservice.Model.Enum.Role;
import org.example.projetwebservice.Model.User;
import org.example.projetwebservice.Model.Workspace;
import org.example.projetwebservice.Repository.UserRepository;
import org.example.projetwebservice.Repository.WorkspaceRepository;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class WorkspaceQueryController {

    private final WorkspaceRepository workspaceRepository;
    private final UserRepository userRepository;

    public WorkspaceQueryController(WorkspaceRepository workspaceRepository, UserRepository userRepository) {
        this.workspaceRepository = workspaceRepository;
        this.userRepository = userRepository;
    }

    @QueryMapping
    public List<Workspace> getWorkspaces() {
        User current = getCurrentUser();

        if (current.getRole() == Role.ADMIN) {
            return workspaceRepository.findAll();
        } else {
            return workspaceRepository.findAll().stream()
                    .filter(workspace -> workspace.getReservations() == null || workspace.getReservations().isEmpty())
                    .collect(Collectors.toList());
        }
    }

    @QueryMapping
    public List<Workspace> getAvailableWorkspaces(@Argument String date,
                                                  @Argument int startHour,
                                                  @Argument int endHour) {
        LocalDate targetDate = LocalDate.parse(date);
        LocalTime start = LocalTime.of(startHour, 0);
        LocalTime end = LocalTime.of(endHour, 0);

        return workspaceRepository.findAll().stream()
                .filter(workspace -> workspace.getReservations().stream().noneMatch(res ->
                        res.getDate().equals(targetDate) &&
                                start.isBefore(res.getEndTime()) &&
                                end.isAfter(res.getStartTime())
                ))
                .collect(Collectors.toList());
    }

    @QueryMapping
    public Workspace getWorkspaceById(@Argument Long id) {
        User current = getCurrentUser();

        Workspace workspace = workspaceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Workspace not found"));

        if (current.getRole() == Role.ADMIN) {
            return workspace;
        } else {
            if (workspace.getReservations() == null || workspace.getReservations().isEmpty()) {
                return workspace;
            } else {
                throw new RuntimeException("Access Denied: Workspace not available");
            }
        }
    }

    private User getCurrentUser() {
        String email = SecurityUtil.getCurrentUsername();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non authentifié"));
    }
}