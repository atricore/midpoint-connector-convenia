package com.atricore.iam.midpoint.connector.convenia.ops;

import com.atricore.iam.convenia.api.ApiClient;
import com.atricore.iam.convenia.api.ApiException;
import com.atricore.iam.convenia.api.model.Department;
import com.atricore.iam.convenia.api.model.Employee;
import com.atricore.iam.convenia.api.model.Response;
import com.atricore.iam.midpoint.connector.convenia.ConveniaConfig;
import com.atricore.iam.midpoint.connector.convenia.ConveniaObjectBuilder;
import com.atricore.iam.midpoint.connector.convenia.ConveniaObjectClass;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;
import org.identityconnectors.framework.common.objects.filter.Filter;

import java.util.List;

public class EmployeeQuery extends ConveniaAbstractOp {

    private static final Log LOG = Log.getLog(EmployeeQuery.class);

    public EmployeeQuery(ConveniaConfig configuration, ApiClient client) {
        super(configuration, client);
    }

    public void execute(ConveniaObjectClass objectClass, Filter filter, ResultsHandler handler,
                        OperationOptions options) {

        LOG.info("Execute employee query start : {0}", objectClass.toString());

        try {

            ConveniaObjectBuilder b = new ConveniaObjectBuilder(this.configuration);

            // If the filter is by __UID__ use direct api search
            if (filter != null && filter instanceof EqualsFilter) {

                EqualsFilter eqFilter = (EqualsFilter) filter;
                LOG.info("Execute employee query by attr:" + eqFilter.getAttribute().getName());
                if (eqFilter.getAttribute().getName().equals("__UID__")) {

                    List<Object> v = eqFilter.getAttribute().getValue();
                    LOG.info("Execute employee query by attr:" + eqFilter.getAttribute().getName() + "[" + v.get(0) + "]");

                    Response<Employee> r = client.getEmployee((String) v.get(0));
                    if (r.getData() == null) {
                        LOG.info("Execute employee query by attr, employee not found");
                        return;
                    }

                    // The current employee in r does not have a STATUS
                    // We can search with a match by email in the list of employees
                    // The result should be 0 for deleted/dismissed, 1 for existing employees.
                    Response<List<Employee>> rs = client.getEmployeesByEmail(r.getData().getEmail());
                    if (rs.getData() == null) {
                        LOG.info("Execute employee query by email, employee not found");
                        return;
                    }

                    if (rs.getData().size() != 1) {
                        LOG.info("Execute employee query by email, employee found #" + rs.getData().size());
                        return;
                    }

                    Employee enriched = r.getData();

                    // Enrich the employee
                    Employee e = rs.getData().get(0);
                    if (e.getStatus() != null) {
                        enriched.setStatus(e.getStatus());
                    }
                    if (e.getDismissal() != null) {
                        enriched.setDismissal(e.getDismissal());
                    }

                    LOG.info("Execute employee query by attr, found #" + enriched.getId() + "[" + enriched.getEmail() + "]");

                    ConnectorObject obj = b.createConnectorObject(enriched);
                    handler.handle(obj);

                    return;
                } else {
                    LOG.error("Ignoring filter type: " + filter.getClass());
                }

            }

            // Build the list of employees, if the connector is set to enrich
            // Retrieve the first page to get pagination details
            Response<List<Employee>> response = client.getEmployees();
            int page = 1;

            // Iterate over each page, at least we process once
            while (page <= response.getLastPage()) {
                try {
                    response.getData()
                            .stream()
                            .map(e -> {
                                if (configuration.isEnrichEmployeeData()) {
                                    try {
                                        // Retrieve detailed employee data for enrichment
                                        Employee enriched = client.getEmployee(e.getId()).getData();

                                        // Work-around: copy missing fields from the original employee
                                        if (e.getStatus() != null)
                                            enriched.setStatus(e.getStatus());

                                        if (e.getDismissal() != null)
                                            enriched.setDismissal(e.getDismissal());

                                        // TODO : Add holidays information
                                        return enriched;
                                    } catch (ApiException err) {
                                        LOG.error("Error invoking API (RT): " + err.getMessage(), err);
                                        throw new RuntimeException("API exception occurred", err);
                                    }
                                } else {
                                    return e;
                                }
                            })
                            .map(b::createConnectorObject)
                            .forEach(handler::handle);

                    // Read next page
                    page++;
                    response = client.getEmployeesByPage(page);

                } catch (ApiException err) {
                    LOG.error("Failed to fetch employees on page " + page + ": " + err.getMessage(), err);
                    // Optionally handle the exception (e.g., break loop, continue, or rethrow)
                }
            }


        } catch (ApiException e) {
            LOG.error("Error invoking API: " + e.getMessage(), e);
            throw new RuntimeException(e);
        }

        LOG.info("Execute query finished : {0}", objectClass.toString());
    }

    public ConnectorObject getObject(ConveniaObjectClass objectClass, String uid) {
        try {
            Response<List<Employee>> r = client.getEmployees();
            ConveniaObjectBuilder b = new ConveniaObjectBuilder(this.configuration);
            for (Employee d : r.getData()) {
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
