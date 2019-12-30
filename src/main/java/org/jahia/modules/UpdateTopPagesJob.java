package org.jahia.modules;

import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionFactory;
import org.jahia.services.content.JCRSessionWrapper;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import java.util.Locale;

/**
 * A Quartz job to update the top pages
 */
public class UpdateTopPagesJob extends QuartzJobBean {

    Logger logger = LoggerFactory.getLogger(UpdateTopPagesJob.class);

    TopPages topPages;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        topPages = new TopPages();
        //update all top pages in live and default workspaces
        updateWorkspace("live");
        updateWorkspace("default");
    }

    private void updateWorkspace(String workspace) {
        logger.info("TopPages Update Job: updating workspace: {}", workspace);
        JCRSessionWrapper jcrSessionWrapper = null;
        final String query = "select * from [jtopmix:topPages]";
        try {
            jcrSessionWrapper = JCRSessionFactory.getInstance().getCurrentSystemSession(workspace, Locale.ENGLISH, null);
            NodeIterator iterator = jcrSessionWrapper.getWorkspace().getQueryManager().createQuery(query, Query.JCR_SQL2).execute().getNodes();
            if (!iterator.hasNext()) {
                logger.info("No top pages nodes available to update");
                return;
            }
            while (iterator.hasNext()) {
                JCRNodeWrapper node = (JCRNodeWrapper) iterator.nextNode();
                topPages.updateTopPages(node);
            }

            logger.info("TopPages Update Job: All top pages nodes have been updated in workspace: {}", workspace);

        } catch (RepositoryException e) {
            logger.info("An exception occured while updating the top pages", e);
        }
    }
}
