package com.atricore.iam.midpoint.connector.convenia;

import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.objects.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ConveniaSchema {

    private static final Log LOG = Log.getLog(ConveniaSchema.class);

    private static Set<String> accountAttributeNames;
    private static Set<String> jobAttributeNames;
    private static Set<String> departmentAttributeNames;

    // Connector schema instance, built on-demand.
    private static Schema schema;

    //--- Schema Object Classes ----------------------------------------------------------------------------------------
    // system user object
    public static final String OBJECT_EMPLOYEE = "employee";

    //JOB system role object
    public static final String OBJECT_JOB = "__JOB__";

    //DEPARTMENT system group object
    public static final String OBJECT_DEPARTMENT = "__DEPARTMENT__";

    public static final String ATTRIBUTE_ID = "id"; // Backend id

    public static final String ATTRIBUTE_STATUS = "status";

    public static final String ATTRIBUTE_FIRSTNAME = "first_name";
    public static final String ATTRIBUTE_DESCRIPTION = "description";
    public static final String ATTRIBUTE_LASTNAME = "last_name";
    public static final String ATTRIBUTE_MOTHERNAME = "mother_name";
    public static final String ATTRIBUTE_FATHERNAME = "father_name";
    public static final String ATTRIBUTE_EMAIL = "email";
    public static final String ATTRIBUTE_JOB_NAME = "job_name";
    public static final String ATTRIBUTE_DEPARTMENT_NAME = "department_name";
    public static final String ATTRIBUTE_PARENT = "parent";
    public static final String ATTRIBUTE_ALT_EMAIL = "alternative_email";
    public static final String ATTRIBUTE_HIRINGDATE = "hiring_date";
    public static final String ATTRIBUTE_DISMISSAL = "dismissal_date";
    public static final String ATTRIBUTE_BIRTHDATE = "birth_date";

    public static final String ATTRIBUTE_ASSIGNED_DEPARTMENT = "department"; // List of all directly assigned groups
    public static final String ATTRIBUTE_ASSIGNED_JOB = "job"; // List of all directly assigned groups

    public static Schema getSchema(ConveniaConfig configuration) {
        createSchema(configuration);
        return schema;
    }

    public static Set<String> getAccountAttributeNames(ConveniaConfig configuration) {
        createSchema(configuration);
        return accountAttributeNames;
    }

    public static Set<String> getJobAttributeNames(ConveniaConfig configuration) {
        createSchema(configuration);
        return jobAttributeNames;
    }

    public static Set<String> getDepartmentAttributeNames(ConveniaConfig configuration) {
        createSchema(configuration);
        return departmentAttributeNames;
    }

    private static void createSchema(ConveniaConfig configuration) {

        if (schema != null) {
            return;
        }

        LOG.info("Creating CONVENIA connector schema ... ");

        // Build convenia schema
        SchemaBuilder schemaBuilder = new SchemaBuilder(ConveniaConnector.class);

        // -----------------------------------------------
        // Build JOB schema
        // -----------------------------------------------
        ObjectClassInfoBuilder jobClassBuilder = new ObjectClassInfoBuilder();
        jobClassBuilder.setType(ConveniaObjectClass.JOB_NAME);
        jobClassBuilder.addAttributeInfo(createSimpleAttribute(ATTRIBUTE_DESCRIPTION));

        ObjectClassInfo job = jobClassBuilder.build();
        schemaBuilder.defineObjectClass(ConveniaObjectClass.JOB_NAME, job.getAttributeInfo());
        jobAttributeNames = createAttributeNames(job);

        // -----------------------------------------------
        // Build DEPARTMENT schema
        // -----------------------------------------------
        ObjectClassInfoBuilder departmentClassBuilder = new ObjectClassInfoBuilder();
        departmentClassBuilder.setType(ConveniaObjectClass.DEPARTMENT_NAME);
        departmentClassBuilder.addAttributeInfo(createSimpleAttribute(ATTRIBUTE_PARENT));

        ObjectClassInfo department = departmentClassBuilder.build();
        schemaBuilder.defineObjectClass(ConveniaObjectClass.DEPARTMENT_NAME, department.getAttributeInfo());
        departmentAttributeNames = createAttributeNames(department);

        // -----------------------------------------------
        // Build EMPLOYEE schema
        // -----------------------------------------------
        ObjectClassInfoBuilder accountClassBuilder = new ObjectClassInfoBuilder();
        accountClassBuilder.setType(ObjectClass.ACCOUNT_NAME);

        accountClassBuilder.addAttributeInfo(createSimpleAttribute(ATTRIBUTE_STATUS));

        accountClassBuilder.addAttributeInfo(createSimpleAttribute(ATTRIBUTE_FIRSTNAME));
        accountClassBuilder.addAttributeInfo(createSimpleAttribute(ATTRIBUTE_LASTNAME));
        accountClassBuilder.addAttributeInfo(createSimpleAttribute(ATTRIBUTE_MOTHERNAME));
        accountClassBuilder.addAttributeInfo(createSimpleAttribute(ATTRIBUTE_FATHERNAME));
        accountClassBuilder.addAttributeInfo(createSimpleAttribute(ATTRIBUTE_EMAIL));
        accountClassBuilder.addAttributeInfo(createSimpleAttribute(ATTRIBUTE_JOB_NAME));
        accountClassBuilder.addAttributeInfo(createSimpleAttribute(ATTRIBUTE_DEPARTMENT_NAME));
        accountClassBuilder.addAttributeInfo(createSimpleAttribute(ATTRIBUTE_ALT_EMAIL));
        accountClassBuilder.addAttributeInfo(createSimpleAttribute(ATTRIBUTE_HIRINGDATE));
        accountClassBuilder.addAttributeInfo(createSimpleAttribute(ATTRIBUTE_DISMISSAL));
        accountClassBuilder.addAttributeInfo(createSimpleAttribute(ATTRIBUTE_BIRTHDATE));

        configuration.getCustomFieldConfigs().stream()
                .map(cf -> {
                    AttributeInfoBuilder customFields = new AttributeInfoBuilder(cf.getName());

                    customFields.setType(String.class);
                    customFields.setUpdateable(false);
                    customFields.setReadable(true);
                    customFields.setMultiValued(false);

                    return customFields.build();
                }).forEach(accountClassBuilder::addAttributeInfo);

        AttributeInfoBuilder assignedDepartment = new AttributeInfoBuilder(ATTRIBUTE_ASSIGNED_DEPARTMENT);
        assignedDepartment.setType(String.class);
        assignedDepartment.setUpdateable(true);
        assignedDepartment.setMultiValued(false);
        accountClassBuilder.addAttributeInfo(assignedDepartment.build());

        AttributeInfoBuilder assignedJob = new AttributeInfoBuilder(ATTRIBUTE_ASSIGNED_JOB);
        assignedJob.setType(String.class);
        assignedJob.setUpdateable(true);
        assignedJob.setMultiValued(false);
        accountClassBuilder.addAttributeInfo(assignedJob.build());

        ObjectClassInfo account = accountClassBuilder.build();
        schemaBuilder.defineObjectClass(ObjectClass.ACCOUNT_NAME, account.getAttributeInfo());
        accountAttributeNames = createAttributeNames(account);

        // BUILD SCHEMA
        schema = schemaBuilder.build();

        LOG.info("Created CONVENIA connector schema ... " + schema);
    }

    private static Set<String> createAttributeNames(ObjectClassInfo oci) {
        Set<String> result = new HashSet<String>();
        Iterator<AttributeInfo> iterator = oci.getAttributeInfo().iterator();

        while (iterator.hasNext()) {
            AttributeInfo a = iterator.next();
            result.add(a.getName());
        }
        return result;
    }

    private static AttributeInfo createSimpleAttribute(String name) {
        AttributeInfoBuilder aib = new AttributeInfoBuilder();
        aib.setName(name);
        aib.setType(String.class);
        aib.setUpdateable(false);
        aib.setCreateable(false);

        return aib.build();
    }
}