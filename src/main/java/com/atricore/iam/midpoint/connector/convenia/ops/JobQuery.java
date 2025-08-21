package com.atricore.iam.midpoint.connector.convenia.ops;

import com.atricore.iam.convenia.api.ApiClient;
import com.atricore.iam.convenia.api.ApiException;
import com.atricore.iam.convenia.api.model.Department;
import com.atricore.iam.convenia.api.model.Job;
import com.atricore.iam.convenia.api.model.Response;
import com.atricore.iam.midpoint.connector.convenia.ConveniaConfig;
import com.atricore.iam.midpoint.connector.convenia.ConveniaObjectBuilder;
import com.atricore.iam.midpoint.connector.convenia.ConveniaObjectClass;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.objects.ConnectorObject;
import org.identityconnectors.framework.common.objects.OperationOptions;
import org.identityconnectors.framework.common.objects.ResultsHandler;
import org.identityconnectors.framework.common.objects.filter.Filter;

import java.util.List;

public class JobQuery extends ConveniaAbstractOp {

    private static final Log LOG = Log.getLog(JobQuery.class);

    public JobQuery(ConveniaConfig configuration, ApiClient connection) {
        super(configuration, connection);
    }

    public void execute(ConveniaObjectClass objectClass, Filter filter, ResultsHandler handler, OperationOptions options) {

        try {
            Response<List<Job>> r = client.getJobs();
            ConveniaObjectBuilder b = new ConveniaObjectBuilder(this.configuration);
            for (Job d : r.getData()) {
                ConnectorObject o = b.createConnectorObject(d);
                if (filter == null || filter.accept(o)) {
                    handler.handle(o);
                }
            }

        } catch (ApiException e) {

            // TODO : Improve
            throw new RuntimeException(e);
        }
    }

    public ConnectorObject getObject(ConveniaObjectClass objectClass, String uid) {
        try {
            Response<List<Job>> r = client.getJobs();
            ConveniaObjectBuilder b = new ConveniaObjectBuilder(this.configuration);
            for (Job d : r.getData()) {
                if (d.getId().equals(uid)) {
                    return b.createConnectorObject(d);
                }
            }

        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
        return null;
    }


}
