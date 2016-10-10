package com.seniordesign.wolfpack.quizinator;

import com.seniordesign.wolfpack.quizinator.Filters.NumberFilter;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by leonardj on 10/9/2016.
 */

public class FilterUnitTest {

    @Test
    public void defaultFilterRange() {
        NumberFilter filter = new NumberFilter();

        assertFalse(filter.isInRange(-1));
        assertTrue(filter.isInRange(0));
        assertTrue(filter.isInRange(60));
        assertFalse(filter.isInRange(61));
    }

    @Test
    public void customFilterRange() {
        NumberFilter filter = new NumberFilter(2, 10);

        assertFalse(filter.isInRange(-1));
        assertFalse(filter.isInRange(1));
        assertTrue(filter.isInRange(2));
        assertTrue(filter.isInRange(10));
        assertFalse(filter.isInRange(11));
    }
}
