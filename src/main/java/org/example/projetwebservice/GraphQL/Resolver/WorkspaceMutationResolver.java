package org.example.projetwebservice.GraphQL.Resolver;

import graphql.kickstart.tools.GraphQLMutationResolver;
import org.example.projetwebservice.GraphQL.Input.WorkspaceInput;
import org.example.projetwebservice.Model.Workspace;
import org.example.projetwebservice.Repository.WorkspaceRepository;
import org.springframework.stereotype.Component;

@Component
public class WorkspaceMutationResolver implements GraphQLMutationResolver {
    private final WorkspaceRepository workspaceRepository;

    public WorkspaceMutationResolver(WorkspaceRepository workspaceRepository) {
        this.workspaceRepository = workspaceRepository;
    }

    public Workspace createWorkspace(WorkspaceInput input) {
        Workspace workspace = new Workspace();
        workspace.setName(input.getName());
        workspace.setType(input.getType());
        return workspaceRepository.save(workspace);
    }

    public Boolean deleteWorkspace(Long id) {
        if (workspaceRepository.existsById(id)) {
            workspaceRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
