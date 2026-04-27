package com.code42.jenkins.pipelinekt.dsl.jobproperty

import com.code42.jenkins.pipelinekt.core.Parameter
import com.code42.jenkins.pipelinekt.core.step.Step
import com.code42.jenkins.pipelinekt.dsl.DslContext
import com.code42.jenkins.pipelinekt.internal.step.scripted.PropertiesParameters

fun DslContext<Step>.propertiesParameters(parameters: List<Parameter>) {
    if (parameters.isNotEmpty()) {
        add(PropertiesParameters(parameters))
    }
}

fun DslContext<Step>.propertiesParameters(block: DslContext<Parameter>.() -> Unit) {
    val list = DslContext.into(block)
    propertiesParameters(list)
}
