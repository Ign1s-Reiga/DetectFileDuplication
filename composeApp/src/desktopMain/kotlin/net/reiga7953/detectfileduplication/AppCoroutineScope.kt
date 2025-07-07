package net.reiga7953.detectfileduplication

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

class AppCoroutineScope: CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Job() + Dispatchers.Default + CoroutineName("App Scope")
}
