package com.amazon.ata.advertising.service.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RequestContextTest {

    @Test
    void testEquals1() {
        RequestContext context1 = new RequestContext("1", "1");
        RequestContext context2 = new RequestContext("1", "1");
        assertEquals(context1, context2);
    }

    @Test
    void testEquals2() {
        RequestContext context1 = new RequestContext("0", "1");
        RequestContext context2 = new RequestContext("1", "1");
        assertNotEquals(context1, context2);
    }

    @Test
    void hashCodes1() {
        RequestContext context1 = new RequestContext("1", "1");
        RequestContext context2 = new RequestContext("1", "1");
        assertEquals(context1.hashCode(), context2.hashCode());
    }
    @Test
    void hashCodes2() {
        RequestContext context1 = new RequestContext("0", "1");
        RequestContext context2 = new RequestContext("1", "1");
        assertNotEquals(context1.hashCode(), context2.hashCode());
    }
}
