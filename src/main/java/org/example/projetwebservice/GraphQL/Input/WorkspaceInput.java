package org.example.projetwebservice.GraphQL.Input;

import org.example.projetwebservice.Model.Enum.WorkspaceType;

public class WorkspaceInput {
    private String name;
    private WorkspaceType type;

    public WorkspaceInput() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public WorkspaceType getType() {
        return type;
    }

    public void setType(WorkspaceType type) {
        this.type = type;
    }
}
