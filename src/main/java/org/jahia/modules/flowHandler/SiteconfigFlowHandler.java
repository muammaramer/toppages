package org.jahia.modules.flowHandler;

import org.apache.commons.lang.StringUtils;
import org.jahia.modules.models.SiteConfiguration;
import org.jahia.modules.models.TopPagesConfigModel;
import org.jahia.modules.models.TopPagesNode;
import org.jahia.services.content.*;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.message.MessageResolver;
import org.springframework.webflow.execution.RequestContext;

import javax.jcr.ItemExistsException;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class SiteconfigFlowHandler implements Serializable {

    private static final Logger logger = getLogger(SiteconfigFlowHandler.class);

    public void setJcrTemplate(JCRTemplate jcrTemplate) {
        this.jcrTemplate = jcrTemplate;
    }

    @Autowired
    private transient JCRTemplate jcrTemplate;

    TopPagesConfigModel model;

    public TopPagesConfigModel init() {
        if (logger.isDebugEnabled()) {
            logger.debug("Getting the configurations list");
        }

        try {
            this.model = jcrTemplate.doExecuteWithSystemSession(
                    new JCRCallback<TopPagesConfigModel>() {
                        @Override
                        public TopPagesConfigModel doInJCR(JCRSessionWrapper session) throws RepositoryException {
                            JCRNodeWrapper sitesNode;
                            TopPagesConfigModel sitesModel = new TopPagesConfigModel();
                            sitesModel.setSelectedSiteName("");
                            //Getting filter Sites nodes
                            try {
                                sitesNode = session.getNode("/settings/top-pages/");
                            } catch (PathNotFoundException e) {//Folders has to be created
                                if (session.nodeExists("/settings")) {
                                    sitesNode = session.getNode("/settings").addNode("top-pages", "jnt:globalSettings");
                                } else {
                                    sitesNode = session.getNode("/").addNode("settings", "jnt:globalSettings").addNode("top-pages", "jnt:globalSettings");
                                }
                            }

                            for (JCRNodeWrapper site : sitesNode.getNodes()) {
                                sitesModel.addSiteConfig(new SiteConfiguration(site.getName(), site.getProperty("awStatsUrl").getString(), site.getProperty("includeFilter").getString(), site.getPropertyAsString("excludeFilter")));

                            }
                            session.save();

                            if (logger.isDebugEnabled()) {
                                logger.debug("End of Retreiving top pages sites configuratoins");
                            }

                            return sitesModel;
                        }
                    }
            );

        } catch (RepositoryException e) {
            logger.error("Top pages: Unable to find an existing sites configuration", e);
            return new TopPagesConfigModel();
        }
        return this.model;
    }


    public boolean saveSiteConfiguration(final SiteConfiguration site, MessageContext messageContext) {
        if (logger.isDebugEnabled()) {
            logger.debug("Saving new Site configuration: {}", site);
        }

        boolean created = true;
        final String siteName = site.getSiteName();
        final String url = site.getReportUrl();
        final String includeFilter = site.getIncludeFilter();
        final String excludeFilter = site.getExcludeFilter();
        final MessageResolver itemAlreadyExistsMessage = site.getMessage("saveError", "toppages.form.error.alreadyExist");
        try {

            MessageResolver creationResult = jcrTemplate.doExecuteWithSystemSession(
                    new JCRCallback<MessageResolver>() {
                        @Override
                        public MessageResolver doInJCR(JCRSessionWrapper session) throws RepositoryException {
                            JCRNodeWrapper topPagesConfigNode = null;
                            JCRNodeWrapper topPagesSite = null;
                            boolean jcrOk = true;
                            try {

                                topPagesConfigNode = session.getNode("/settings/top-pages/");

                            } catch (PathNotFoundException e) {
                                //path does not exist, need to create it, check if parent nodes exists too for first time creation
                                if (session.nodeExists("/settings/")) {
                                    topPagesConfigNode = session.getNode("/settings/").addNode("top-pages", "jnt:globalSettings");

                                } else {
                                    topPagesConfigNode = session.getNode("/").addNode("settings", "jnt:globalSettings").addNode("top-pages", "jnt:globalSettings").addNode(siteName, "jtopmix:siteConfig");
                                }
                            }
                            try {
                                if (topPagesConfigNode != null) {
                                    topPagesSite = topPagesConfigNode.addNode(siteName, "jtopmix:siteConfig");

                                    if (topPagesSite != null) {
                                        topPagesSite.setProperty("awStatsUrl", url);
                                        topPagesSite.setProperty("includeFilter", includeFilter);
                                        topPagesSite.setProperty("excludeFilter", excludeFilter);

                                    }
                                }
                            } catch (ItemExistsException e) {
                                jcrOk = false;
                                logger.warn("A site with the same name already exists");
                            }
                            session.save();
                            return jcrOk ? null : itemAlreadyExistsMessage;

                        }
                    }
            );

            if (creationResult != null) {//Name already exists
                messageContext.addMessage(creationResult);
                created = false;
            }

        } catch (RepositoryException e) {//Any other node creation Issue
            if (e.getCause().toString().contains("IllegalNameException")) {
                messageContext.addMessage(this.model.getMessage("saveError", "toppages.form.error.IllegaleName"));
                logger.error("Failed to create top Pages site configuration node, Illegal Name found", e);

            } else {
                messageContext.addMessage(this.model.getMessage("saveError", "toppages.form.error.saveError"));
                logger.error("Failed to create top Pages site configuration node", e);

            }
        }

        return created;

    }

    public SiteConfiguration setSelectedConfiguration(TopPagesConfigModel model) {
        if (logger.isDebugEnabled()) {
            logger.debug(" Setting the selected site: {}", model.getSelectedSiteName());
        }
        final String selectedSiteName = model.getSelectedSiteName();
        try {
            return jcrTemplate.doExecuteWithSystemSession(
                    new JCRCallback<SiteConfiguration>() {
                        @Override
                        public SiteConfiguration doInJCR(JCRSessionWrapper session) throws RepositoryException {
                            JCRNodeWrapper siteNode = null;
                            //Getting filter Sites nodes
                            try {
                                siteNode = session.getNode("/settings/top-pages/" + selectedSiteName);
                            } catch (PathNotFoundException e) {
                                logger.debug("Unable to get the selected configuration!", e);
                                return null;
                            }

                            SiteConfiguration config = new SiteConfiguration(siteNode.getName(), siteNode.getProperty("awStatsUrl").getString(), siteNode.getProperty("includeFilter").getString(), siteNode.getPropertyAsString("excludeFilter"));
                            config.setToBeUpdated(true);

                            if (logger.isDebugEnabled()) {
                                logger.debug("End of Retreiving top pages sites configurations");
                            }
                            return config;
                        }
                    }
            );
        } catch (RepositoryException e) {
            logger.error("Failed to set the selected site", e);

        }
        return null;

    }

    public boolean deleteSiteConfiguration() {
        if (!model.getConfirmDelete().equals("delete") || StringUtils.isEmpty(model.getSelectedSiteName())) {
            return false;
        }

        final String selectedSiteName = model.getSelectedSiteName();

        try {
            jcrTemplate.doExecuteWithSystemSession(
                    new JCRCallback() {
                        @Override
                        public Boolean doInJCR(JCRSessionWrapper session) throws RepositoryException {
                            JCRNodeWrapper siteNode = null;
                            //Getting filter Sites nodes
                            try {
                                siteNode = session.getNode("/settings/top-pages/" + selectedSiteName);
                                siteNode.remove();
                                session.save();
                            } catch (PathNotFoundException e) {
                                logger.debug("Error while deleting the site:" + selectedSiteName + ", site not found", e);
                                return false;
                            }
                            return true;
                        }
                    }
            );
        } catch (RepositoryException e) {
            logger.error("Top-pages - Failed to delete site configuration", e);

        }

        return true;
    }


    public boolean updateSiteConfiguration(final SiteConfiguration siteConfig, MessageContext messageContext) {
        if (logger.isDebugEnabled()) {
            logger.debug("Saving new Site configuration: {}",siteConfig.toString());
        }

        boolean updated = true;
        final String siteToUpdate = this.model.getSelectedSiteName();

        final String siteName = siteConfig.getSiteName();
        final String url = siteConfig.getReportUrl();
        final String includeFilter = siteConfig.getIncludeFilter();
        final String excludeFilter = siteConfig.getExcludeFilter();

        try {

            MessageResolver updateResult = jcrTemplate.doExecuteWithSystemSession(
                    new JCRCallback<MessageResolver>() {
                        @Override
                        public MessageResolver doInJCR(JCRSessionWrapper session) throws RepositoryException {
                            JCRNodeWrapper topPagesSite = null;
                            topPagesSite = session.getNode("/settings/top-pages/" + siteToUpdate);
                            try {
                                if (!siteToUpdate.equals(siteName)) { // Updated Name
                                    topPagesSite.rename(siteName);
                                }
                                topPagesSite.setProperty("awStatsUrl", url);
                                topPagesSite.setProperty("includeFilter", includeFilter);
                                topPagesSite.setProperty("excludeFilter", excludeFilter);

                                session.save();
                            } catch (ItemExistsException e) {
                                logger.warn("Unable to update site configuration, a site configuration with the same name already exists", e);
                                return siteConfig.getMessage("configUpdate", "toppages.form.error.alreadyExist");
                            }
                            return null;
                        }
                    }

            );

            if (updateResult != null) {
                updated = false;
                messageContext.addMessage(updateResult);
            }

        } catch (RepositoryException e) {//Any other node creation Issue
            if (e.getCause().toString().contains("IllegalNameException")) {
                messageContext.addMessage(siteConfig.getMessage("configUpdate", "toppages.form.error.IllegaleName"));
                logger.error("Failed to update top Pages site configuration node", e);

            } else {
                messageContext.addMessage(siteConfig.getMessage("configUpdate", "toppages.form.error.saveError"));
                logger.error("Failed to updated top Pages site configuration node", e);

            }
        }

        return updated;

    }

    public SiteConfiguration newSiteConfiguration() {
        return new SiteConfiguration();
    }

    public void getAllNodes(RequestContext context) {

        JCRSessionWrapper jcrSessionWrapper = null;
        final String query = "select * from [jtopmix:topPages]";
        List<TopPagesNode> allNodes = new ArrayList<>();
        try {
            //Get live
            jcrSessionWrapper = JCRSessionFactory.getInstance().getCurrentUserSession("default");
            NodeIterator iterator = jcrSessionWrapper.getWorkspace().getQueryManager().createQuery(query, Query.JCR_SQL2).execute().getNodes();
            if (!iterator.hasNext()) {
                logger.info("No top pages nodes available to update");
                return;
            }
            while (iterator.hasNext()) {
                JCRNodeWrapper node = (JCRNodeWrapper) iterator.nextNode();
                String jahiaSite = node.getPropertyAsString("jahiaSite");
                String parentPage = getParentPage(node);
                String defaultLang = node.getResolveSite().getDefaultLanguage();
                TopPagesNode s = new TopPagesNode(node.getName(), jahiaSite, node.getPath(), node.getLastPublishedAsDate(), defaultLang, parentPage);
                allNodes.add(s);
            }
            //Sort by siteName
            Comparator<TopPagesNode> compareBySite = Comparator.comparing(TopPagesNode::getJahiaSite);
            Collections.sort(allNodes, compareBySite);
            context.getFlowScope().put("allNodes", allNodes);


        } catch (RepositoryException e) {
            logger.info("An exception occured while updating the top pages", e);
        }

    }

    private String getParentPage(JCRNodeWrapper node) throws RepositoryException {
        if (node.getParent().getPrimaryNodeTypeName().equals("jnt:page")) {
            return node.getParent().getPath();
        }
        return getParentPage(node.getParent());
    }
}
