package com.abenezer.streambridge;

import com.abenezer.streambridge.util.Range;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StreamServiceRangeTest {
    @Test
    void testParseNormalRange() {
        Range r = Range.parse("bytes=0-99", 1000);
        assertNotNull(r);
        assertEquals(0, r.getStart());
        assertEquals(99, r.getEnd());
    }
    @Test
    void testParseSuffixRange() {
        Range r = Range.parse("bytes=-100", 1000);
        assertNotNull(r);
        assertEquals(900, r.getStart());
        assertEquals(999, r.getEnd());
    }
    @Test
    void testParseOpenRange() {
        Range r = Range.parse("bytes=100-", 1000);
        assertNotNull(r);
        assertEquals(100, r.getStart());
        assertEquals(999, r.getEnd());
    }
    @Test
    void testParseInvalid() {
        assertNull(Range.parse("bytes=abc-def", 1000));
        assertNull(Range.parse("bytes=100-50", 1000));
        assertNull(Range.parse("bytes=1000-1001", 1000));
        assertNull(Range.parse("invalid", 1000));
        assertNull(Range.parse("", 1000));
    }
}
