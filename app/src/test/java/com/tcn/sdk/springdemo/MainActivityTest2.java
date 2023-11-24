package com.tcn.sdk.springdemo;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

import android.os.Build;
import android.widget.TextView;

import com.tcn.sdk.springdemo.presentation.MainActivity;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@Config(
        manifest=Config.NONE,
        constants = BuildConfig.class,
        sdk = Build.VERSION_CODES.N_MR1
)
@RunWith(RobolectricTestRunner.class)
public class MainActivityTest2 {
    private MainActivity activity;

    // @Before => JUnit 4 annotation that specifies this method should run before each test is run
    // Useful to do setup for objects that are needed in the test
    @Before
    public void setup() {
        // Convenience method to run MainActivity through the Activity Lifecycle methods:
        // onCreate(...) => onStart() => onPostCreate(...) => onResume()
        activity = Robolectric.setupActivity(MainActivity.class);
    }
    @Test
    public void shouldNotBeNull2() {
        Assert.assertNotNull(activity);
    }

    // @Test => JUnit 4 annotation specifying this is a test to be run
    // The test simply checks that our TextView exists and has the text "Hello world!"
    @Test
    public void validateTextViewContent() {
        TextView tvHelloWorld = (TextView) activity.findViewById(R.id.mtitle);
        assertNotNull("TextView could not be found", tvHelloWorld);
        assertEquals("TextView contains incorrect text", "Jorge", tvHelloWorld.getText().toString());
    }
}