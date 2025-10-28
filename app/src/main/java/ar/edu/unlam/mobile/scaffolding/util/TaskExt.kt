package ar.edu.unlam.mobile.scaffolding.util

import com.google.android.gms.tasks.Task
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

suspend fun <T> Task<T>.awaitCatching(): T? = suspendCancellableCoroutine { cont ->
    addOnSuccessListener { cont.resume(it) }
    addOnFailureListener { cont.resume(null) }
}
