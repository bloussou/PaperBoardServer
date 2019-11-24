package com.paperboard.server;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UserTest {
    @Test
    public void testEquals() {
        final User user1 = new User("bob");
        final User user2 = new User("bob");
        final User user3 = new User("alice");
        assertTrue(user1.equals(user2));
        assertFalse(user1.equals(user3));
    }
}
