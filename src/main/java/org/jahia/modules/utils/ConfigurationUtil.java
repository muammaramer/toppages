package org.jahia.modules.utils;

import org.jahia.modules.models.SiteConfiguration;
import org.jahia.services.content.JCRCallback;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.JCRTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import java.util.ArrayList;
import java.util.List;

public class ConfigurationUtil {
    static Logger logger = LoggerFactory.getLogger(ConfigurationUtil.class);
    private String key;

    public void setKey(String key) {
        this.key = key;
    }
    public String getKey() {
        return key;
    }


    @Autowired
    private JCRTemplate jcrTemplate;


    public void setJcrTemplate(JCRTemplate jcrTemplate) {
        this.jcrTemplate = jcrTemplate;
    }

    public List<String> getSitesConfigList() {

        ArrayList<String> result = null;
        try {

            result = (ArrayList<String>) jcrTemplate.doExecuteWithSystemSession(
                    new JCRCallback() {
                        @Override
                        public List<String> doInJCR(JCRSessionWrapper session) throws RepositoryException {
                            JCRNodeWrapper sitesNode = null;
                            ArrayList<String> configList = new ArrayList<>();
                            //Getting filter Sites nodes
                            try {
                                sitesNode = session.getNode("/settings/top-pages/");
                                NodeIterator iterator = sitesNode.getNodes();
                                while (iterator.hasNext()) {
                                    JCRNodeWrapper configNode = (JCRNodeWrapper) iterator.nextNode();

                                    configList.add(configNode.getName());
                                }
                            } catch (PathNotFoundException e) {
                                logger.debug("TopPages: Configuration Node does not exist in JCR", e);
                            }

                            return configList;
                        }
                    }
            );
        } catch (RepositoryException e) {
            logger.error("TopPages: Unable to get Configuration", e);

        }

        return result;
    }


    public SiteConfiguration getSiteConfig(final String siteName) {

        SiteConfiguration result = null;
        try {

            result = (SiteConfiguration) jcrTemplate.doExecuteWithSystemSession(
                    new JCRCallback() {
                        @Override
                        public SiteConfiguration doInJCR(JCRSessionWrapper session) throws RepositoryException {
                            JCRNodeWrapper siteNode = null;

                            //Getting filter Sites nodes
                            try {
                                siteNode = session.getNode("/settings/top-pages/" + siteName);
                                if (siteNode != null) {
                                    return new SiteConfiguration(siteNode.getName(), siteNode.getProperty("awStatsUrl").getString(), siteNode.getProperty("includeFilter").getString(), siteNode.getPropertyAsString("excludeFilter"));
                                }
                            } catch (PathNotFoundException e) {
                                logger.debug("TopPages: Configuration Node does not exist in JCR", e);

                            }
                            return null;
                        }
                    }
            );
        } catch (RepositoryException e) {
            logger.error("TopPages: Unable to get Configuration", e);

        }

        return result;
    }


}
