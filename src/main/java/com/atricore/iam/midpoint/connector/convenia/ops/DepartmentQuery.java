package com.atricore.iam.midpoint.connector.convenia.ops;

import com.atricore.iam.convenia.api.ApiClient;
import com.atricore.iam.convenia.api.ApiException;
import com.atricore.iam.convenia.api.model.Department;
import com.atricore.iam.convenia.api.model.Response;
import com.atricore.iam.midpoint.connector.convenia.ConveniaConfig;
import com.atricore.iam.midpoint.connector.convenia.ConveniaObjectBuilder;
import com.atricore.iam.midpoint.connector.convenia.ConveniaObjectClass;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;
import org.identityconnectors.framework.common.objects.filter.Filter;

import java.util.List;

public class DepartmentQuery extends ConveniaAbstractOp {
    public DepartmentQuery(ConveniaConfig configuration, ApiClient connection) {
        super(configuration, connection);
    }

    public void execute(ConveniaObjectClass objectClass, Filter filter, ResultsHandler handler, OperationOptions options) {

        try {
            Response<List<Department>> r = client.getDepartments();
            ConveniaObjectBuilder b = new ConveniaObjectBuilder(this.configuration);
            // Add root department

            ConnectorObject root = b.createConnectorObject(rootDepartment, null);
            if (filter == null || filter.accept(root)) {
                handler.handle(root);
            }

            for (Department d : r.getData()) {
                // TODO : We can handle the special case for EqualsFilter and attribute __UID__ with a search by ID
                ConnectorObject o = b.createConnectorObject(d, rootDepartment.getId());
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
          Response<List<Department>> r = client.getDepartments();
          ConveniaObjectBuilder b = new ConveniaObjectBuilder(this.configuration);

          if (uid.equals(rootDepartment.getId())) {
              return b.createConnectorObject(rootDepartment, null);
          }

          for (Department d : r.getData()) {
              if (d.getId().equals(uid)) {
                  return b.createConnectorObject(d, rootDepartment.getId());
              }
          }

        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

}

