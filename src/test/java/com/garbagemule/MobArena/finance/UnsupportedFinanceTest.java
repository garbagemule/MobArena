package com.garbagemule.MobArena.finance;

import org.junit.Before;
import org.junit.Test;

import java.util.logging.Logger;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class UnsupportedFinanceTest {

    private Logger log;
    private UnsupportedFinance subject;

    @Before
    public void setup() {
        log = mock(Logger.class);

        subject = new UnsupportedFinance(log);
    }

    @Test
    public void logsErrorOnGetBalance() {
        subject.getBalance(null);

        verify(log).severe(anyString());
    }

    @Test
    public void returnsNegativeOneOnGetBalance() {
        double result = subject.getBalance(null);

        assertThat(result, equalTo(-1.0));
    }

    @Test
    public void logsErrorOnDeposit() {
        subject.deposit(null, 1337);

        verify(log).severe(anyString());
    }

    @Test
    public void returnsFalseOnDeposit() {
        boolean result = subject.deposit(null, 1337);

        assertThat(result, equalTo(false));
    }

    @Test
    public void logsErrorOnWithdraw() {
        subject.withdraw(null, 1337);

        verify(log).severe(anyString());
    }

    @Test
    public void returnsFalseOnWithdraw() {
        boolean result = subject.withdraw(null, 1337);

        assertThat(result, equalTo(false));
    }

    @Test
    public void logsErrorOnFormat() {
        subject.format(1337);

        verify(log).severe(anyString());
    }

    @Test
    public void returnsErrorStringOnFormat() {
        String result = subject.format(1337);

        assertThat(result, equalTo("ERROR"));
    }

}
