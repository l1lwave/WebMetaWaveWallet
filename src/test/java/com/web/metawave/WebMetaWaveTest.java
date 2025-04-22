package com.web.metawave;

import com.web.metawave.test.*;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        BalanceServiceTest.class,
        UserServiceTest.class,
        TransactionServiceTest.class,
        NotificationServiceTest.class,
        TokenServiceTest.class,
        NetworkServiceTest.class
})
public class WebMetaWaveTest {
}
