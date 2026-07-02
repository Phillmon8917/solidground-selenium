package io.github.phillmon.selenium.modulars.calendar;

import org.testng.annotations.Test;

import java.time.LocalDate;

import static org.testng.Assert.assertEquals;

public class DateObjectTest {
    @Test
    public void fromStringParsesDefaultPattern() {
        DateObject date = DateObject.fromString("January 15, 2026");
        assertEquals(date.getDay(), 15);
        assertEquals(date.getMonth(), "JANUARY");
        assertEquals(date.getMonthValue(), 1);
        assertEquals(date.getYear(), 2026);
    }

    @Test
    public void fromStringParsesCustomPattern() {
        DateObject date = DateObject.fromString("2026-07-02", "yyyy-MM-dd");
        assertEquals(date.getYear(), 2026);
        assertEquals(date.getMonthValue(), 7);
        assertEquals(date.getDay(), 2);
    }

    @Test(expectedExceptions = InvalidDateException.class)
    public void fromStringRejectsBlankText() {
        DateObject.fromString("   ");
    }

    @Test(expectedExceptions = InvalidDateException.class)
    public void fromStringRejectsUnparsableText() {
        DateObject.fromString("not a date");
    }

    @Test(expectedExceptions = InvalidDateException.class)
    public void fromStringRejectsInvalidPattern() {
        DateObject.fromString("2026-07-02", "not-a-real-pattern-????");
    }

    @Test(expectedExceptions = InvalidDateException.class)
    public void fromLocalDateRejectsYearBelowSupportedRange() {
        DateObject.fromLocalDate(LocalDate.of(1939, 12, 31));
    }

    @Test(expectedExceptions = InvalidDateException.class)
    public void fromLocalDateRejectsYearAboveSupportedRange() {
        DateObject.fromLocalDate(LocalDate.of(2081, 1, 1));
    }

    @Test
    public void fromLocalDateAcceptsBoundaryYears() {
        assertEquals(DateObject.fromLocalDate(LocalDate.of(1940, 1, 1)).getYear(), 1940);
        assertEquals(DateObject.fromLocalDate(LocalDate.of(2080, 12, 31)).getYear(), 2080);
    }

    @Test
    public void builderCombinesDayMonthYear() {
        DateObject date = DateObject.builder().withDay(4).withMonth("july").withYear(2026).build();
        assertEquals(date.getDay(), 4);
        assertEquals(date.getMonth(), "JULY");
        assertEquals(date.getYear(), 2026);
    }

    @Test(expectedExceptions = InvalidDateException.class)
    public void builderRejectsInvalidMonthName() {
        DateObject.builder().withDay(1).withMonth("Smarch").withYear(2026).build();
    }

    @Test(expectedExceptions = InvalidDateException.class)
    public void builderRejectsImpossibleCalendarDate() {
        DateObject.builder().withDay(30).withMonth("February").withYear(2026).build();
    }

    @Test(expectedExceptions = InvalidDateException.class)
    public void builderRequiresEveryField() {
        DateObject.builder().withDay(1).withYear(2026).build();
    }

    @Test
    public void equalsAndHashCodeAreBasedOnTheWrappedDate() {
        DateObject a = DateObject.fromString("January 15, 2026");
        DateObject b = DateObject.fromLocalDate(LocalDate.of(2026, 1, 15));
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void toStringUsesDefaultPattern() {
        DateObject date = DateObject.fromLocalDate(LocalDate.of(2026, 1, 15));
        assertEquals(date.toString(), "January 15, 2026");
    }
}
