package com.example.reservedassistance.utils;

import com.example.reservedassistance.dto.VisitorDto;
import com.example.reservedassistance.entity.Activity;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class QRCodeGenerateUtilsTest {

    @Test
    void testGenerateQRCode1() throws Exception {
        // Setup
        final VisitorDto visitor = new VisitorDto();
        visitor.setId(0);
        visitor.setRealName("realName");
        visitor.setIdentificationNum("identificationNum");
        visitor.setTelephone("telephone");

        final Activity activity = new Activity();
        activity.setId(0);
        activity.setActivityName("activityName");
        activity.setBeginTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
        activity.setEndTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
        activity.setAccessNum(0);

        // Run the test
        final String result = QRCodeGenerateUtils.generateQRCode(visitor, activity, "stadiumName");
        System.out.println(result);
        // Verify the results
    }

    @Test
    void testGenerateQRCode1_ThrowsIOException() {
        // Setup
        final VisitorDto visitor = new VisitorDto();
        visitor.setId(0);
        visitor.setRealName("realName");
        visitor.setIdentificationNum("identificationNum");
        visitor.setTelephone("telephone");

        final Activity activity = new Activity();
        activity.setId(0);
        activity.setActivityName("activityName");
        activity.setBeginTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
        activity.setEndTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
        activity.setAccessNum(0);

        // Run the test
        assertThatThrownBy(() -> QRCodeGenerateUtils.generateQRCode(visitor, activity, "stadiumName"))
                .isInstanceOf(IOException.class);
    }


}
