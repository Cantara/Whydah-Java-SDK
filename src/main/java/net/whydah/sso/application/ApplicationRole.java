package net.whydah.sso.application;

import java.io.Serializable;

/**
 * @author <a href="mailto:erik-dev@fjas.no">Erik Drolshammer</a> 2015-01-23
 */
public class ApplicationRole implements Serializable {
    private static final long serialVersionUID = -8050935915438584578L;
    private String id;
    private String name;

    private ApplicationRole() {
    }

    public ApplicationRole(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ApplicationRole role = (ApplicationRole) o;

        if (!id.equals(role.id)) return false;
        if (!name.equals(role.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }
}
