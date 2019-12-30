package org.jahia.modules.models;

import org.jahia.utils.i18n.Messages;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageResolver;
import org.springframework.context.i18n.LocaleContextHolder;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class TopPagesConfigModel implements Serializable {
    private static final String BUNDLE_NAME = "resources.topPages";

    private List<SiteConfiguration> siteConfigList = new ArrayList<>();

    private String selectedSiteName="";
   private  String confirmDelete = "";

    public String getConfirmDelete() {
        return confirmDelete;
    }

    public void setConfirmDelete(String confirmDelete) {
        this.confirmDelete = confirmDelete;
    }

    public String getSelectedSiteName() {
        return this.selectedSiteName;
    }

    public void setSelectedSiteName(String selectedSiteName) {
        this.selectedSiteName = selectedSiteName;
    }


    public List<SiteConfiguration> getSiteConfigList() {
        return siteConfigList;
    }

    public void setSiteConfigList(List<SiteConfiguration> siteConfigList) {
        this.siteConfigList = siteConfigList;
    }

    public void addSiteConfig(SiteConfiguration ss) {
        this.siteConfigList.add(ss);
    }

    public MessageResolver getMessage(String source, String key) {
        Locale locale = LocaleContextHolder.getLocale();
        return new MessageBuilder().error().source(source).defaultText(Messages.get(BUNDLE_NAME, key, locale)).build();

    }

}
