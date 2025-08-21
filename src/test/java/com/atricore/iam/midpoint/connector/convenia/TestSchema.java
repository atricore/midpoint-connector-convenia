package com.atricore.iam.midpoint.connector.convenia;


import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.objects.ObjectClassInfo;
import org.identityconnectors.framework.common.objects.Schema;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class TestSchema {

    private static ConveniaConfig baseCfg;


    private static final String FIELD_2 = "Fíeld2";
    private static final String FIELD_2_NORM = "field2";

    @BeforeAll
    public static void setup() throws Exception {
        baseCfg = new ConveniaConfig();

        baseCfg.setBaseURL("http://localhost:8006/api/v3");
        baseCfg.setOrgName("my-org");
        baseCfg.setOrgUUID("03a83cac-8365-447c-89b4-c7857b7431b0");
        baseCfg.setEnrichEmployeeData(true);

        GuardedString s = new GuardedString();
        baseCfg.setPrivateToken(s);

    }

    @Test
    public void testSchema() {
        baseCfg.setCustomFields("FiledA," + FIELD_2 + ",É Lider?");
        baseCfg.init();
        assert baseCfg.getCustomFieldConfigs().size() == 3 : "Invalid number of custom field names " + baseCfg.getCustomFieldConfigs().size();

        Optional<CustomField> cf = baseCfg.getCustomFieldByOrigName(FIELD_2);

        assert cf.isPresent() : "Custom field not found " + FIELD_2;
        assert cf.get().getName().equals(FIELD_2_NORM);

        Schema s = ConveniaSchema.getSchema(baseCfg);

        // We have 3 classes
        assert s.getObjectClassInfo().size() == 3;

        s.getObjectClassInfo().forEach(c -> {
            if (c.getType().equals("__ACCOUNT__")) {
                validateAccount(c);
            } else if (c.getType().equals("__JOB__")) {
                validateJob(c);
            } else if (c.getType().equals("__DEPARTMENT__")) {
                validateDepartment(c);
            } else {
                throw new RuntimeException("Invalid object class:  " + c.getType());
            }
        });

    }

    protected void validateAccount(ObjectClassInfo c) {
        assert c.getAttributeInfo().size() == 17 : "invalid number of attributes " + c.getAttributeInfo().size();

        AtomicBoolean found = new AtomicBoolean(false);

        // Test  custom field 2
        c.getAttributeInfo().forEach(a -> {
            if (a.getName().equals(FIELD_2_NORM)) {
                found.set(true);
            }

        });

        assert found.get() : "Field not found : " + FIELD_2_NORM;
    }

    protected void validateJob(ObjectClassInfo j) {

    }

    protected void validateDepartment(ObjectClassInfo c) {

    }
}
