package com.atricore.iam.midpoint.connector.convenia;

import com.atricore.iam.convenia.api.model.CustomField;
import com.atricore.iam.convenia.api.model.Department;
import com.atricore.iam.convenia.api.model.Employee;
import com.atricore.iam.convenia.api.model.Job;

import org.identityconnectors.framework.common.objects.ConnectorObject;
import org.identityconnectors.framework.common.objects.ConnectorObjectBuilder;

import java.util.Optional;

public class ConveniaObjectBuilder {

    private ConveniaConfig config;

    public ConveniaObjectBuilder(ConveniaConfig cfg) {
        this.config = cfg;
    }

    public ConnectorObject createConnectorObject(Employee e) {
        ConnectorObjectBuilder builder = new ConnectorObjectBuilder();
        builder.setUid(e.getId());
        builder.setName(e.getEmail() != null ? e.getEmail() : e.getId());

        //builder.addAttribute(OperationalAttributes.ENABLE_DATE_NAME);
        builder.addAttribute(ConveniaSchema.ATTRIBUTE_STATUS, e.getStatus());
        builder.addAttribute(ConveniaSchema.ATTRIBUTE_FIRSTNAME, e.getName());
        builder.addAttribute(ConveniaSchema.ATTRIBUTE_LASTNAME, e.getLastName());
        builder.addAttribute(ConveniaSchema.ATTRIBUTE_EMAIL, e.getEmail());
        builder.addAttribute(ConveniaSchema.ATTRIBUTE_JOB_NAME, e.getJob().getName());
        builder.addAttribute(ConveniaSchema.ATTRIBUTE_DEPARTMENT_NAME, e.getDepartment().getName());
        builder.addAttribute(ConveniaSchema.ATTRIBUTE_ASSIGNED_DEPARTMENT, e.getDepartment().getId());
        builder.addAttribute(ConveniaSchema.ATTRIBUTE_ASSIGNED_JOB, e.getJob().getId());

        builder.addAttribute(ConveniaSchema.ATTRIBUTE_HIRINGDATE, e.getHiringDate());
        if (e.getDismissal() != null)
            builder.addAttribute(ConveniaSchema.ATTRIBUTE_DISMISSAL, e.getDismissal().getDate());


        // Add custom fields active in the configuration
        if (e.getCustomFields() != null) {
            for (CustomField cf : e.getCustomFields()) {
                Optional<com.atricore.iam.midpoint.connector.convenia.CustomField> ccf = config.getCustomFieldByOrigName(cf.getName());
                if (ccf.isEmpty())
                    continue;
                builder.addAttribute(ccf.get().getName(), cf.getValue());
            }
        }

        return builder.build();
    }

    protected boolean hasCustomField(String name) {

        return false;
    }

    public ConnectorObject createConnectorObject(Department e, String parent) {
        ConnectorObjectBuilder builder = new ConnectorObjectBuilder();
        builder.setUid(e.getId());
        builder.setName(e.getName());
        if (parent != null)
            builder.addAttribute(ConveniaSchema.ATTRIBUTE_PARENT, parent);

        return builder.build();
    }

    public ConnectorObject createConnectorObject(Job j) {
        ConnectorObjectBuilder builder = new ConnectorObjectBuilder();
        builder.setUid(j.getId());
        builder.setName(j.getName());
        builder.addAttribute(ConveniaSchema.ATTRIBUTE_DESCRIPTION, j.getDescription());
        return builder.build();
    }
}
