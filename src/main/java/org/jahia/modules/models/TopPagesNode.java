package org.jahia.modules.models;

import java.io.Serializable;
import java.util.Date;

public class TopPagesNode implements Serializable {

    private String name;
    private String jahiaSite;
    private String path;
    private String workspace;
    private Date lastPublished;
    private String parentPage;
    private String defaultLanguage;


    public TopPagesNode(String name, String jahiaSite, String path, Date lastPublished, String defaultLanguage,String parentPage) {
        this.name = name;
        this.jahiaSite = jahiaSite;
        this.path = path;
        this.lastPublished = lastPublished;
        this.defaultLanguage = defaultLanguage;
        this.parentPage = parentPage;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getWorkspace() {
        return workspace;
    }

    public void setWorkspace(String workspace) {
        this.workspace = workspace;
    }

    public Date getLastPublished() {
        return lastPublished;
    }

    public void setLastPublished(Date lastPublished) {
        this.lastPublished = lastPublished;
    }

    public String getJahiaSite() {
        return jahiaSite;
    }

    public void setJahiaSite(String jahiaSite) {
        this.jahiaSite = jahiaSite;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    public String getParentPage() {
        return parentPage;
    }

    public void setParentPage(String parentPage) {
        this.parentPage = parentPage;
    }


}
