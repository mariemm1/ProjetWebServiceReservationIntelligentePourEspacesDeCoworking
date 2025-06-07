package org.example.projetwebservice.GraphQL.Resolver;

import org.example.projetwebservice.GraphQL.Input.WorkspaceInput;
import org.example.projetwebservice.Model.Workspace;
import org.example.projetwebservice.Repository.WorkspaceRepository;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.stereotype.Controller;

@Controller
public class WorkspaceMutationResolver {

    private final WorkspaceRepository workspaceRepository;

    public WorkspaceMutationResolver(WorkspaceRepository workspaceRepository) {
        this.workspaceRepository = workspaceRepository;
    }

    @MutationMapping
    public Workspace createWorkspace(@Argument WorkspaceInput input) {
        Workspace workspace = new Workspace();
        workspace.setName(input.getName());
        workspace.setType(input.getType());
        return workspaceRepository.save(workspace);
    }

    @MutationMapping
    public Workspace updateWorkspace(@Argument Long id, @Argument WorkspaceInput input) {
        return workspaceRepository.findById(id).map(workspace -> {
            workspace.setName(input.getName());
            workspace.setType(input.getType());
            return workspaceRepository.save(workspace);
        }).orElseThrow(() -> new RuntimeException("Workspace with id " + id + " not found"));
    }

    @MutationMapping
    public Boolean deleteWorkspace(@Argument Long id) {
        if (workspaceRepository.existsById(id)) {
            workspaceRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
