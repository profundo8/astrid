package com.todoroo.astrid.repeats;

import java.text.ParseException;
import java.util.Date;

import com.google.ical.values.Frequency;
import com.google.ical.values.RRule;
import com.todoroo.andlib.test.TodorooTestCase;
import com.todoroo.andlib.utility.DateUtilities;
import com.todoroo.astrid.data.Task;

public class AdvancedRepeatTests extends TodorooTestCase {


    public static void assertDatesEqual(long date, long other) {
        assertEquals("Expected: " + new Date(date) + ", Actual: " + new Date(other),
                date, other);
    }

    public void testDueDateInPast() throws ParseException {
        RRule rrule = new RRule();
        rrule.setInterval(1);
        rrule.setFreq(Frequency.DAILY);

        Task task = new Task();

        // repeat once => due date should become tomorrow
        long past = task.createDueDate(Task.URGENCY_SPECIFIC_DAY, new Date(110, 7, 1).getTime());
        task.setValue(Task.DUE_DATE, past);
        long today = task.createDueDate(Task.URGENCY_SPECIFIC_DAY, DateUtilities.now());
        long nextDueDate = RepeatTaskCompleteListener.computeNextDueDate(task, rrule.toIcal());
        assertDatesEqual(today, nextDueDate);

        // test specific day & time
        long pastWithTime = task.createDueDate(Task.URGENCY_SPECIFIC_DAY_TIME, new Date(110, 7, 1, 10, 4).getTime());
        task.setValue(Task.DUE_DATE, pastWithTime);
        Date date = new Date(DateUtilities.now() / 1000L * 1000L);
        date.setHours(10);
        date.setMinutes(4);
        date.setSeconds(0);
        long todayWithTime = task.createDueDate(Task.URGENCY_SPECIFIC_DAY_TIME, date.getTime()) / 1000L * 1000L;
        if(todayWithTime < DateUtilities.now())
            todayWithTime += DateUtilities.ONE_DAY;
        nextDueDate = RepeatTaskCompleteListener.computeNextDueDate(task, rrule.toIcal());
        assertDatesEqual(todayWithTime, nextDueDate);
    }

    public void testDueDateInPastRepeatMultiple() throws ParseException {
        RRule rrule = new RRule();
        rrule.setInterval(1);
        rrule.setFreq(Frequency.DAILY);
        Task task = new Task();

        // repeat once => due date should become tomorrow
        long past = task.createDueDate(Task.URGENCY_SPECIFIC_DAY_TIME, new Date(110, 7, 1, 0, 0, 0).getTime());
        task.setValue(Task.DUE_DATE, past);
        long nextDueDate = RepeatTaskCompleteListener.computeNextDueDate(task, rrule.toIcal());
        assertTrue(nextDueDate > DateUtilities.now());
        task.setValue(Task.DUE_DATE, nextDueDate);
        long evenMoreNextDueDate = RepeatTaskCompleteListener.computeNextDueDate(task, rrule.toIcal());
        assertNotSame(nextDueDate, evenMoreNextDueDate);
    }


}
