package com.atricore.iam.midpoint.connector.convenia;

import com.atricore.iam.convenia.api.ApiClient;
import com.atricore.iam.convenia.api.ApiClientImpl;
import com.atricore.iam.convenia.api.model.Department;
import com.atricore.iam.midpoint.connector.convenia.ops.DepartmentQuery;
import com.atricore.iam.midpoint.connector.convenia.ops.EmployeeQuery;
import com.atricore.iam.midpoint.connector.convenia.ops.JobQuery;
import com.evolveum.polygon.common.GuardedStringAccessor;
import org.identityconnectors.common.CollectionUtil;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.api.operations.GetApiOp;
import org.identityconnectors.framework.common.objects.*;


import org.identityconnectors.framework.common.objects.filter.Filter;
import org.identityconnectors.framework.common.objects.filter.FilterTranslator;
import org.identityconnectors.framework.spi.Configuration;
import org.identityconnectors.framework.spi.Connector;
import org.identityconnectors.framework.spi.ConnectorClass;
import org.identityconnectors.framework.spi.operations.SchemaOp;
import org.identityconnectors.framework.spi.operations.SearchOp;
import org.identityconnectors.framework.spi.operations.TestOp;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ConnectorClass(displayNameKey = "convenia.connector.display", configurationClass = ConveniaConfig.class)
public class ConveniaConnector implements Connector, SchemaOp, TestOp, SearchOp<Filter>, GetApiOp {

    private static final Log LOGGER = Log.getLog(ConveniaConnector.class);

    private ConveniaConfig configuration;
    private ApiClient client;

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public void init(Configuration configuration) {
        LOGGER.info("CONVENNIA CONNECTOR : Initialize");

        this.configuration = (ConveniaConfig) configuration;
        this.configuration.init();
        this.configuration.validate();

        GuardedStringAccessor a = new GuardedStringAccessor();
        this.configuration.getPrivateToken().access(a);

        if (this.configuration.getBaseURL() != null && !this.configuration.getBaseURL().isEmpty()) {
            this.client = new ApiClientImpl(a.getClearString(), this.configuration.getBaseURL());
        } else {
            this.client = new ApiClientImpl(a.getClearString());
        }

        // Parse custom Fields from config

    }

    @Override
    public void dispose() {
        this.client = null;
        this.configuration = null;

    }

    @Override
    public Schema schema() {
        return ConveniaSchema.getSchema(this.configuration);
    }

    @Override
    public FilterTranslator<Filter> createFilterTranslator(ObjectClass objectClass,
                                                           OperationOptions options) {
        LOGGER.info(">>> create filter translator");
        return new FilterTranslator<Filter>() {
            public List<Filter> translate(Filter filter) {
                if (filter != null) {
                    LOGGER.info(">>> translate: " + filter.getClass().getName());
                }
                return CollectionUtil.newList(filter);
            }
        };
    }

    @Override
    public void executeQuery(ObjectClass objectClass, Filter filter, ResultsHandler handler, OperationOptions operationOptions) {
        LOGGER.info(">>> executing query, objectClass: [" + objectClass.getObjectClassValue() + "], filter: [" + (filter != null ? filter.getClass().getName() : "null") + "], handler: [" + handler.getClass().getName() + "]");

        ConveniaObjectClass coc = new ConveniaObjectClass(objectClass);

        if (coc.isAccount()) {
            EmployeeQuery q = new EmployeeQuery(this.configuration, this.client);
            q.execute(coc, filter, handler, operationOptions);
        } else if (coc.isDepartment()) {
            DepartmentQuery q = new DepartmentQuery(this.configuration, this.client);
            q.execute(coc, filter, handler, operationOptions);
        } else if (coc.isJob()) {
            JobQuery q = new JobQuery(this.configuration, this.client);
            q.execute(coc, filter, handler, operationOptions);
        } else {
            // Error
            LOGGER.error("Unknown objectclass:" + objectClass.getObjectClassValue());
        }

    }

    @Override
    public ConnectorObject getObject(ObjectClass objectClass, Uid uid, OperationOptions operationOptions) {
        LOGGER.info(">>> getObject, objectClass: [" + objectClass.getObjectClassValue() + "] " + uid.getUidValue());

        ConveniaObjectClass coc = new ConveniaObjectClass(objectClass);

        if (coc.isAccount()) {
            EmployeeQuery q = new EmployeeQuery(this.configuration, this.client);
            return q.getObject(coc, uid.getUidValue());
        } else if (coc.isDepartment()) {
            DepartmentQuery q = new DepartmentQuery(this.configuration, this.client);
            return q.getObject(coc, uid.getUidValue());
        } else if (coc.isJob()) {
            JobQuery q = new JobQuery(this.configuration, this.client);
            return q.getObject(coc, uid.getUidValue());
        }
        // Error
        LOGGER.error("Unknown objectclass:" + objectClass.getObjectClassValue());
        return null;

    }

    @Override
    public void test() {
        try {
            // maybe there is a better endpoint for test?
            this.client.sendGetRequest("ethnicities");
        } catch (IOException e) {
            LOGGER.error("Could not send GET request (I/O exception)", e);
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            LOGGER.error("Could not send GET request (interrupted exception)", e);
            throw new RuntimeException(e);
        }
    }
}
