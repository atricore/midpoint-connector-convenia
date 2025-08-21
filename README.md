# Convenia Midpoint Connector

A ConnId connector for integrating [Convenia](https://convenia.com.br/) HR system with [Evolveum MidPoint](https://evolveum.com/midpoint/). This connector enables synchronization of employee data, organizational structure, and job information between Convenia and MidPoint.

## Features

The connector supports three main object types:

- **Employees** (`__ACCOUNT__`): Employee accounts with personal information, job assignments, and custom fields
- **Departments** (`__DEPARTMENT__`): Organizational units that can be mapped to MidPoint OrgUnits
- **Jobs** (`__JOB__`): Job positions that can be used as Roles in MidPoint

### Supported Operations

- **Schema Discovery**: Automatically discovers available attributes
- **Search/Query**: Retrieve employees, departments, and jobs with filtering support
- **Get Object**: Fetch individual objects by UID
- **Test Connection**: Validate connector configuration and API connectivity

## Configuration

### Required Parameters

| Parameter | Description | Required |
|-----------|-------------|----------|
| `privateToken` | Convenia API private token for authentication | Yes |
| `orgUUID` | Organization UUID identifier | Yes |
| `orgName` | Organization name | Yes |

### Optional Parameters

| Parameter | Description | Default |
|-----------|-------------|---------|
| `baseUrl` | Custom Convenia API base URL | Uses default Convenia API URL |
| `enrichEmployeeData` | Perform additional queries to fetch custom fields for employees | `false` |
| `customFields` | Comma-separated list of custom field mappings (format: `original_name:mapped_name`) | None |

### Custom Fields Configuration

Custom fields can be configured using the `customFields` parameter. The format is:
```
original_field_name:mapped_field_name,another_field:mapped_name
```

Example:
```
employee_id:employeeId,cost_center:costCenter
```

## Object Schemas

### Employee (Account) Attributes

| Attribute | Type | Description |
|-----------|------|-------------|
| `__UID__` | String | Unique identifier |
| `__NAME__` | String | Employee name |
| `id` | String | Convenia employee ID |
| `status` | String | Employee status |
| `first_name` | String | First name |
| `last_name` | String | Last name |
| `mother_name` | String | Mother's name |
| `father_name` | String | Father's name |
| `email` | String | Primary email address |
| `alternative_email` | String | Alternative email address |
| `job_name` | String | Job title |
| `department_name` | String | Department name |
| `hiring_date` | String | Date of hire |
| `dismissal_date` | String | Dismissal date (if applicable) |
| `birth_date` | String | Date of birth |
| `department` | String | Assigned department reference |
| `job` | String | Assigned job reference |

### Department Attributes

| Attribute | Type | Description |
|-----------|------|-------------|
| `__UID__` | String | Unique identifier |
| `__NAME__` | String | Department name |
| `parent` | String | Parent department reference |

### Job Attributes

| Attribute | Type | Description |
|-----------|------|-------------|
| `__UID__` | String | Unique identifier |
| `__NAME__` | String | Job title |
| `description` | String | Job description |

## Installation

1. Build the connector:
   ```bash
   mvn clean package
   ```

2. Copy the generated JAR file to your MidPoint `icf-connectors` directory:
   ```bash
   cp target/connector-convenia-*.jar $MIDPOINT_HOME/icf-connectors/
   ```

3. Restart MidPoint to load the connector.

## MidPoint Configuration Example

```xml
<resource>
    <name>Convenia HR System</name>
    <connectorRef type="ConnectorType">
        <filter>
            <q:equal>
                <q:path>connectorType</q:path>
                <q:value>com.atricore.iam.midpoint.connector.convenia.ConveniaConnector</q:value>
            </q:equal>
        </filter>
    </connectorRef>
    
    <connectorConfiguration>
        <icfc:configurationProperties>
            <icfcp:privateToken>
                <clearValue>your-convenia-api-token</clearValue>
            </icfcp:privateToken>
            <icfcp:orgUUID>your-org-uuid</icfcp:orgUUID>
            <icfcp:orgName>Your Organization Name</icfcp:orgName>
            <icfcp:enrichEmployeeData>true</icfcp:enrichEmployeeData>
            <icfcp:customFields>employee_id:employeeId,cost_center:costCenter</icfcp:customFields>
        </icfc:configurationProperties>
    </connectorConfiguration>
    
    <schemaHandling>
        <objectType>
            <kind>account</kind>
            <intent>default</intent>
            <objectClass>ri:AccountObjectClass</objectClass>
            <!-- Add your attribute mappings here -->
        </objectType>
    </schemaHandling>
</resource>
```

## Departments Configuration

The connector can create a root department that serves as a parent for all other departments. Configure the `orgUUID` and `orgName` parameters to enable this feature. These values will be used to add a virtual root department to the list of departments reported by Convenia.

## Development

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- Access to Convenia API

### Building

```bash
mvn clean compile
```

### Testing

```bash
mvn test
```

### Dependencies

- ConnId Framework 1.5.1.10
- Convenia API Client 1.2.3
- Apache Commons Lang3 3.14.0

## API Integration

The connector uses the Convenia REST API to retrieve data. It supports:

- Employee data retrieval with optional enrichment for custom fields
- Department hierarchy information
- Job/position data
- Connection testing via the ethnicities endpoint

## Troubleshooting

### Common Issues

1. **Authentication Errors**: Verify your `privateToken` is valid and has appropriate permissions
2. **Connection Timeouts**: Check network connectivity and `baseUrl` configuration
3. **Missing Custom Fields**: Ensure `enrichEmployeeData` is enabled and custom field names are correct

### Logging

Enable debug logging for the connector by adding this to your MidPoint logging configuration:

```xml
<logger name="com.atricore.iam.midpoint.connector.convenia" level="DEBUG"/>
```

## License

Licensed under the Apache License, Version 2.0. See the parent project for full license details.

## Support

For issues and questions:
- Check the MidPoint documentation
- Review Convenia API documentation
- Contact your system administrator