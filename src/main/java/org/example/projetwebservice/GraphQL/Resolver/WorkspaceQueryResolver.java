package org.example.projetwebservice.GraphQL.Resolver;


import graphql.kickstart.tools.GraphQLQueryResolver;
import org.example.projetwebservice.Model.Workspace;
import org.example.projetwebservice.Repository.WorkspaceRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WorkspaceQueryResolver implements GraphQLQueryResolver {

    private final WorkspaceRepository workspaceRepository;

    public WorkspaceQueryResolver(WorkspaceRepository workspaceRepository) {
        this.workspaceRepository = workspaceRepository;
    }

    public List<Workspace> getWorkspaces() {
        return workspaceRepository.findAll();
    }

    public Workspace getWorkspaceById(Long id) {
        return workspaceRepository.findById(id).orElse(null);
    }
}


