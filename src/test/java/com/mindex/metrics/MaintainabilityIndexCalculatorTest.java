package com.mindex.metrics;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MaintainabilityIndexCalculatorTest {
    @Test
    void testCalculateMI() {
        double mi = MaintainabilityIndexCalculator.calculate(20.0, 2, 10);
        assertTrue(mi > 0 && mi <= 100);
    }

    @Test
    void testInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> {
            MaintainabilityIndexCalculator.calculate(0, 2, 10);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            MaintainabilityIndexCalculator.calculate(20, -1, 10);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            MaintainabilityIndexCalculator.calculate(20, 2, 0);
        });
    }
} 