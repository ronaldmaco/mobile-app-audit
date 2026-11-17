class HelpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val webView = WebView(this)
        setContentView(webView)
        webView.settings.javaScriptEnabled = true

        // Help pages can be opened from a deep link: myapp://help?page=<url>
        val url = intent.data?.getQueryParameter("page") ?: "https://help.example.com"
        if (url.startsWith("https://help.example.com")) {
            webView.addJavascriptInterface(AccountBridge(this), "Account")
            webView.loadUrl(url)
        }
    }
}

class AccountBridge(private val context: Context) {
    @JavascriptInterface
    fun authToken(): String = Session.current(context).accessToken
}
