package org.jahia.modules;

import org.jahia.modules.utils.ConfigurationUtil;
import org.jahia.services.content.nodetypes.ExtendedPropertyDefinition;
import org.jahia.services.content.nodetypes.initializers.ChoiceListValue;
import org.jahia.services.content.nodetypes.initializers.ModuleChoiceListInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ChoiceListInitializer implements ModuleChoiceListInitializer {
    private String key;

    @Autowired
    private ConfigurationUtil configurationUtil;

    public void setConfigurationUtil(ConfigurationUtil configurationUtil) {
        this.configurationUtil = configurationUtil;
    }

    @Override
    public List<ChoiceListValue> getChoiceListValues(
            ExtendedPropertyDefinition epd, String param,
            List<ChoiceListValue> values, Locale locale,
            Map<String, Object> context) {
        List<ChoiceListValue> choiceListValues = new ArrayList<>();
        ArrayList<String> sitesList = (ArrayList) configurationUtil.getSitesConfigList();
        for (String s : sitesList) {
            choiceListValues.add(new ChoiceListValue(s, s));
        }

        return choiceListValues;
    }

    @Override
    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String getKey() {
        return key;
    }

}
