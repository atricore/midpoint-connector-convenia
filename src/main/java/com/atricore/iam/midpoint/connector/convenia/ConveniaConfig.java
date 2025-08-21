package com.atricore.iam.midpoint.connector.convenia;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.exceptions.ConfigurationException;
import org.identityconnectors.framework.spi.AbstractConfiguration;
import org.identityconnectors.framework.spi.ConfigurationProperty;
import org.identityconnectors.framework.spi.StatefulConfiguration;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ConveniaConfig extends AbstractConfiguration implements StatefulConfiguration {

    private static final Log LOGGER = Log.getLog(ConveniaConfig.class);

    private GuardedString privateToken;
    private String baseUrl;
    private String orgName;
    private String orgUuid;
    private String customFields;
    private Set<CustomField> customFieldConfig= new HashSet<CustomField>();

    // When set to true, the connector will perform a query for each employee to obtain additional information like custom fields
    private boolean enrichEmployeeData = false;

    public void init() {
        if (getCustomFields() != null) {
            customFieldConfig.clear();
            customFieldConfig.addAll(Arrays.stream(getCustomFields().split(",")).map(CustomField::new).toList());
        }
    }

    public Set<CustomField> getCustomFieldConfigs() {
        return customFieldConfig;
    }

    public Optional<CustomField> getCustomFieldByOrigName(String n) {
        for (CustomField cf : customFieldConfig) {
            if (cf.getOriginal().equals(n)) {
                return Optional.of(cf);
            }
        }
        return Optional.empty();
    }

    @ConfigurationProperty(order = 1, displayMessageKey = "privateToken.display", helpMessageKey = "privateToken.help", required = true, confidential = true)
    public GuardedString getPrivateToken() {
        return privateToken;
    }

    public void setPrivateToken(GuardedString privateToken) {
        this.privateToken = privateToken;
    }

    @ConfigurationProperty(order = 2, displayMessageKey = "orgUuid.display", helpMessageKey = "orgUuid.help", required = true, confidential = false)
    public String getOrgUUID() {
        return orgUuid;
    }
    public void setOrgUUID(String u) {
        orgUuid = u;
    }

    @ConfigurationProperty(order = 3, displayMessageKey = "orgName.display", helpMessageKey = "orgName.help", required = true, confidential = false)
    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    @ConfigurationProperty(order = 4, displayMessageKey = "baseUrl.display", helpMessageKey = "baseUrl.help", required = false, confidential = false)
    public String getBaseURL() {
        return baseUrl;
    }

    public void setBaseURL(String baseURL) {
        this.baseUrl = baseURL;
    }

    @ConfigurationProperty(order = 5, displayMessageKey = "enrichEmployeeData.display", helpMessageKey = "enrichEmployeeData.help", required = false, confidential = false)
    public boolean isEnrichEmployeeData() {
        return enrichEmployeeData;
    }

    public void setEnrichEmployeeData(boolean enrichEmployeeData) {
        this.enrichEmployeeData = enrichEmployeeData;
    }

    @ConfigurationProperty(order = 2, displayMessageKey = "customFields.display", helpMessageKey = "customFields.help", required = false, confidential = false)
    public String getCustomFields() {
        return customFields;
    }

    public void setCustomFields(String u) {
        customFields = u;
    }

    @Override
    public void validate() {
        if ("".equals(privateToken)) {
            throw new ConfigurationException("Private Token cannot be empty.");
        }

        customFieldConfig.forEach(n -> {/* validate legal chars ?!*/});
    }

    @Override
    public void release() {

    }


}
