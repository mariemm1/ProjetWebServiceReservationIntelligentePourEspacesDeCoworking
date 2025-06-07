package org.example.projetwebservice.GraphQL.Resolver;

import org.example.projetwebservice.Model.Workspace;
import org.example.projetwebservice.Repository.WorkspaceRepository;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class WorkspaceQueryResolver {

    private final WorkspaceRepository workspaceRepository;

    public WorkspaceQueryResolver(WorkspaceRepository workspaceRepository) {
        this.workspaceRepository = workspaceRepository;
    }

    @QueryMapping
    public List<Workspace> getWorkspaces() {
        return workspaceRepository.findAll();
    }

    @QueryMapping
    public Workspace getWorkspaceById(@Argument Long id) {
        return workspaceRepository.findById(id).orElse(null);
    }
}
