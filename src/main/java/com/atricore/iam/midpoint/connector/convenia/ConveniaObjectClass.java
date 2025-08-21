package com.atricore.iam.midpoint.connector.convenia;

import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.objects.Name;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.ObjectClassUtil;
import org.identityconnectors.framework.common.objects.Uid;

public class ConveniaObjectClass {

    public static final String JOB_NAME = ObjectClassUtil.createSpecialName("JOB");
    public static final String DEPARTMENT_NAME = ObjectClassUtil.createSpecialName("DEPARTMENT");

    private ObjectClass objectClass;
    private String base;

    private static final Log LOG = Log.getLog(ConveniaObjectClass.class);

    public ConveniaObjectClass(ObjectClass objectClass) {

        if (objectClass.is(ObjectClass.ACCOUNT_NAME)) {
            base = ConveniaSchema.OBJECT_EMPLOYEE;
        } else if (objectClass.is(JOB_NAME)) {
            base = ConveniaSchema.OBJECT_JOB;
        } else if (objectClass.is(DEPARTMENT_NAME)) {
            base = ConveniaSchema.OBJECT_DEPARTMENT;
        } else {
            throw new ConnectorException("Unknown type ObjectClass " + objectClass.toString());
        }
        this.objectClass = objectClass;
    }

    public ObjectClass getObjectClass() {
        return this.objectClass;
    }

    public String getBase() {
        return base;
    }

    public boolean isAccount() {
        return objectClass.is(ObjectClass.ACCOUNT_NAME);
    }

    public boolean isJob() {
        return objectClass.is(JOB_NAME);
    }

    public boolean isDepartment() {
        return objectClass.is(DEPARTMENT_NAME);
    }

    public String assignAttribute(String attrName) {
        if (Uid.NAME.equals(attrName)) {
            return ConveniaSchema.ATTRIBUTE_ID;
        } else if ((isJob()) || (isDepartment())) {
            if (Name.NAME.equals(attrName)) {
                return ConveniaSchema.ATTRIBUTE_ID;
            } else {
                return attrName;
            }
        } else if ((isAccount())) {
            if (Name.NAME.equals(attrName)) {
                return ConveniaSchema.ATTRIBUTE_ID;
            } else {
                return attrName;
            }
        } else {
            return attrName;
        }
    }

    @Override
    public String toString() {
        return base + ":" + this.objectClass.toString();
    }
}
