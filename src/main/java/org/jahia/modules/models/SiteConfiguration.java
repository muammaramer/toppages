package org.jahia.modules.models;

import org.hibernate.validator.constraints.NotEmpty;
import org.jahia.utils.i18n.Messages;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageResolver;
import org.springframework.context.i18n.LocaleContextHolder;


import java.io.Serializable;
import java.util.Locale;


public class SiteConfiguration implements Serializable {

    @NotEmpty(message = "Please enter a Site Name")
    private String siteName;
    @NotEmpty(message = "Please enter the awstats URL")
    private String reportUrl;
    private String includeFilter;
    private String excludeFilter;
    private static final String BUNDLE = "resources.toppages";
    private boolean toBeUpdated = false;

    public boolean isToBeUpdated() {
        return toBeUpdated;
    }

    public void setToBeUpdated(boolean toBeUpdated) {
        this.toBeUpdated = toBeUpdated;
    }


    public SiteConfiguration() {
    }

    public SiteConfiguration(String siteName, String reportUrl, String includeFilter, String excludeFilter) {
        this.siteName = siteName;
        this.reportUrl = reportUrl;
        this.includeFilter = includeFilter;
        this.excludeFilter = excludeFilter;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getReportUrl() {
        return reportUrl;
    }

    public void setReportUrl(String reportUrl) {
        this.reportUrl = reportUrl;
    }

    public String getIncludeFilter() {
        return includeFilter;
    }

    public void setIncludeFilter(String includeFilter) {
        this.includeFilter = includeFilter;
    }

    public String getExcludeFilter() {
        return excludeFilter;
    }

    public void setExcludeFilter(String excludeFilter) {
        this.excludeFilter = excludeFilter;
    }

    public MessageResolver getMessage(String source, String bundleKey) {
        Locale locale = LocaleContextHolder.getLocale();
        return new MessageBuilder().error().source(source).defaultText(Messages.get(BUNDLE, bundleKey, locale)).build();
    }


}


