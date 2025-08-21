package com.atricore.iam.midpoint.connector.convenia.ops;



import com.atricore.iam.convenia.api.ApiClient;
import com.atricore.iam.convenia.api.model.Department;
import com.atricore.iam.midpoint.connector.convenia.ConveniaConfig;
import com.atricore.iam.midpoint.connector.convenia.ConveniaSchema;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.objects.OperationalAttributes;

public class ConveniaAbstractOp {

    public static final String LOG_OPERATION_CREATE = "CREATE";
    public static final String LOG_OPERATION_DELETE = "DELETE";
    public static final String LOG_OPERATION_QUERY = "QUERY";
    public static final String LOG_OPERATION_UPDATE = "UPDATE";
    public static final String LOG_OPERATION_TEST = "TEST";

    protected ConveniaConfig configuration;
    protected ApiClient client;
    protected Department rootDepartment;

    private static final Log LOG = Log.getLog(ConveniaAbstractOp.class);

    public ConveniaAbstractOp(ConveniaConfig configuration, ApiClient connection) {
        this.configuration = configuration;
        this.client = connection;
        this.rootDepartment = new Department();
        rootDepartment.setName(this.configuration.getOrgName());
        rootDepartment.setId(this.configuration.getOrgUUID());
    }

    ConveniaConfig getConfiguration() {
        return this.configuration;
    }

    ApiClient getClient() {
        return this.client;
    }

    public void dispose() {
        LOG.info("Dispose start");
        this.configuration = null;
        this.client = null;
        LOG.info("Dispose finished");
    }

    boolean isJobAttribute(String name) {
        return (name.equals(ConveniaSchema.ATTRIBUTE_ASSIGNED_JOB));
    }

    boolean isDepartmentAttribute(String name) {
        return (name.equals(ConveniaSchema.ATTRIBUTE_ASSIGNED_DEPARTMENT));
    }

}
