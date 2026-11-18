package app.kupe.ui.map.components

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.webkit.WebViewAssetLoader
import org.json.JSONObject
import java.util.Locale

const val DEFAULT_MAP_STYLE = "https://tiles.openfreemap.org/styles/liberty"

private const val TAG = "KupeMap"
private const val MAP_PAGE = "https://appassets.androidplatform.net/assets/kupe-map/index.html"

class KupeMapController internal constructor(private val webView: WebView) {
    fun flyTo(lat: Double, lng: Double, zoom: Double) {
        webView.post {
            webView.evaluateJavascript(
                "window.map && window.map.flyTo({center:[$lng,$lat],zoom:$zoom});", null
            )
        }
    }

    fun setLanguage(language: String) {
        webView.post {
            webView.evaluateJavascript(
                "window.applyLanguage && window.applyLanguage(${JSONObject.quote(language)});", null
            )
        }
    }
}

private class MapBridge(private val onReady: () -> Unit) {
    @JavascriptInterface
    fun onMapReady() = onReady()

    @JavascriptInterface
    fun log(msg: String) {
        Log.d(TAG, "js: $msg")
    }
}

private fun mapUrl(
    styleUrl: String,
    lat: Double,
    lng: Double,
    zoom: Double,
    language: String,
    debug: Boolean,
): String = Uri.parse(MAP_PAGE).buildUpon()
    .appendQueryParameter("style", styleUrl)
    .appendQueryParameter("lat", lat.toString())
    .appendQueryParameter("lng", lng.toString())
    .appendQueryParameter("zoom", zoom.toString())
    .appendQueryParameter("lang", language)
    .appendQueryParameter("debug", if (debug) "1" else "0")
    .build()
    .toString()

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun KupeMap(
    modifier: Modifier = Modifier,
    styleUrl: String = DEFAULT_MAP_STYLE,
    initialLat: Double = 40.4168,
    initialLng: Double = -3.7038,
    initialZoom: Double = 1.5,
    language: String = Locale.getDefault().language,
    debug: Boolean = true,
    onMapReady: (KupeMapController) -> Unit = {},
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val webView = remember { arrayOfNulls<WebView>(1) }

    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> webView[0]?.onResume()
                Lifecycle.Event.ON_PAUSE -> webView[0]?.onPause()
                else -> Unit
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
            webView[0]?.let {
                it.removeJavascriptInterface("Android")
                it.destroy()
            }
            webView[0] = null
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            val assetLoader = WebViewAssetLoader.Builder()
                .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(ctx))
                .build()

            WebView(ctx).also { wv ->
                webView[0] = wv
                wv.setBackgroundColor(0xFF05070F.toInt())
                wv.settings.javaScriptEnabled = true
                wv.settings.domStorageEnabled = true
                wv.webViewClient = object : WebViewClient() {
                    override fun shouldInterceptRequest(
                        view: WebView,
                        request: WebResourceRequest,
                    ): WebResourceResponse? = assetLoader.shouldInterceptRequest(request.url)
                }
                wv.webChromeClient = object : WebChromeClient() {
                    override fun onConsoleMessage(m: ConsoleMessage): Boolean {
                        if (!m.message().startsWith("performance warning")) {
                            Log.d(TAG, "console: ${m.message()}")
                        }
                        return true
                    }
                }
                wv.addJavascriptInterface(
                    MapBridge { wv.post { onMapReady(KupeMapController(wv)) } },
                    "Android"
                )
                wv.loadUrl(mapUrl(styleUrl, initialLat, initialLng, initialZoom, language, debug))
            }
        }
    )
}