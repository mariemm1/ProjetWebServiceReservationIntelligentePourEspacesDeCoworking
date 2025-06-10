package org.example.projetwebservice.DTO;

import org.example.projetwebservice.Model.Enum.WorkspaceType;

public class WorkspaceRequest {
    private String name;
    private WorkspaceType type;

    public WorkspaceRequest() {}

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
