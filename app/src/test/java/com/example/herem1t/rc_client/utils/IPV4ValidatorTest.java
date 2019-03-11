package com.example.herem1t.rc_client.utils;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IPV4ValidatorTest {

    @Test
    public void testIpv4Validation() {
        IPV4Validator validator = new IPV4Validator();

        assertTrue(validator.validate("192.168.1.1"));
        assertTrue(validator.validate("0.0.0.0"));
        assertTrue(validator.validate("255.255.255.255"));
        assertFalse(validator.validate("192.168.1.1111"));
        assertFalse(validator.validate("278.168.1.11"));
        assertFalse(validator.validate("278.168.1.11."));
        assertFalse(validator.validate("278.168.1.1.2"));
        assertFalse(validator.validate("192.168.1.1.2"));
        assertFalse(validator.validate("192.168.1."));
        assertFalse(validator.validate("278.168.1."));
        assertFalse(validator.validate("1.1.1"));
        assertFalse(validator.validate(""));
        assertFalse(validator.validate(null));

    }

}
