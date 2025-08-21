# Convenia Connector

Convenia connector for midpoint, it supports three object types:

* Employees: accounts
* Departments: custom class, can be mapped to OrgUnits
* Job: custom class, can be used as Roles

## Departments

If configured, the connector will create a department that can be used as 
parent for all other departments.  Configure **OrgUUID** and **OrgName**.  
These values will be used to add a department to the list reported by convenia.