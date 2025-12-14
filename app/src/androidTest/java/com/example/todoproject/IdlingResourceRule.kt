package com.example.todoproject

import android.util.Log
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class IdlingResourceRule(private val idlingResource: IdlingResource) : TestWatcher() {

    private val ruleId = "IdlingResourceRule_v1"

    override fun starting(description: Description) {
        Log.d("IdlingResourceRule", "Registering idling resource for test: ${description.methodName}. Rule ID: $ruleId")
        IdlingRegistry.getInstance().register(idlingResource)
        super.starting(description)
    }

    override fun finished(description: Description) {
        Log.d("IdlingResourceRule", "Unregistering idling resource for test: ${description.methodName}.")
        IdlingRegistry.getInstance().unregister(idlingResource)
        super.finished(description)
    }
}
