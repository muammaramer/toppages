package org.jahia.modules;

import org.jahia.services.content.rules.AddedNodeFact;

/**
 * A Service to update the JCR Nodes when a node properties are updated as specified in rules.drl
 */
public class TopPagesService {
    private String name;

    public void updateTopPages(AddedNodeFact fact){
        TopPages topPages= new TopPages();
        topPages.updateTopPages(fact.getNode());
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
