package com.example.android.iscrape

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.*
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import java.util.*

class MainActivity : AppCompatActivity() {

    private val STORAGE_PERMISSION_CODE = 1
    val FILE_EXPORT_REQUEST_CODE = 1
    val context: Context = this
    var txt: EditText? = null
    var mLoopCount: EditText? = null
    var shortcodeList: ArrayList<String>? = null
    var mainProfileUrl: ArrayList<String>? = null
    var profileShortcodeList: ArrayList<ProfileItems>? = null
    var count = 0
    var progressBar: ProgressBar? = null

    // var asyncTask: getDatax? = null
    var TAG = "MainActivity"

    //Web Activity class
    private var mContext: Context? = null
    private var mActivity: Activity? = null

    lateinit private var mWebView: WebView
    lateinit private var mScrollbtn: Button
    private var mbtnState: Button? = null
    var isCoroutineActive: Boolean? = null
    lateinit var docProfileData: String
    lateinit var locationSourceCode:Document

    private var mUrl = "https://www.instagram.com/explore/locations/216978098/mumbai-maharashtra/"
    private var mUrl2 = "https://www.instagram.com/explore/locations/204517928/chicago-illinois/"
    private var mUrl3 = "https://www.instagram.com/explore/locations/360180752/wuhan-china/"
    private var mUrl4 = "https://www.instagram.com/explore/locations/1268778/inorbit-mall/"
    private var mUrl5 = "https://www.instagram.com/explore/locations/747477016/grant-road/"
    var test = arrayOf("fullName", "userName", "followerCount", "followingCount", "profileBio", "profileWebsiteUrl", "businessAccount", "jointRecently", "businessCategory", "verifiedAccount")
    var testList = arrayListOf<Array<String>>()


    // private var mUrl = "https://www.instagram.com/explore/tags/business/"
    var dynamicUrl: String = "https://www.instagram.com/explore/locations/747477016/grant-road/"
    val headersMap = mapOf(
            "Accept" to "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
            "Accept-Encoding" to "gzip, deflate, br",
            "Accept-Language" to "en-IN,en-GB;q=0.9,en-US;q=0.8,en;q=0.7",
            "Referer" to "https://www.google.com",
            "Sec-Fetch-Dest" to "document",
            "Sec-Fetch-Mode" to "navigate",
            "Sec-Fetch-Site" to "none",
            "Upgrade-Insecure-Requests" to "1",
            "User-Agent" to "Mozilla/5.0 (Linux; Android 7.0; SAMSUNG SM-G570Y Build/MMB29K) AppleWebKit/537.36 (KHTML, like Gecko) SamsungBrowser/4.0 Chrome/44.0.2403.133 Mobile Safari/537.36"
    )

    val CREATE_FILE = 1

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn: Button = findViewById(R.id.btnClick)
        val btnSave: Button = findViewById(R.id.btnSaveTask)
        val btnStop: Button = findViewById(R.id.btnStopTask)
        var mgetLink: Button = findViewById(R.id.btn_getLink)
        var mCreateSheet: ToggleButton = findViewById(R.id.btn_createsheet)
        var addressBar: EditText = findViewById(R.id.addressbar)
        var edt: EditText = findViewById(R.id.input)


        var mWebView1:WebView = findViewById(R.id.web_view1)
        var mWebView2:WebView = findViewById(R.id.web_view2)
        var mWebView3:WebView = findViewById(R.id.web_view3)
        var obj1 = dynamicUrlObject("")
        var obj2 = dynamicUrlObject("")
        var obj3 = dynamicUrlObject("")
        renderWebPage(mUrl4,mWebView1)
        renderWebPage(mUrl2,mWebView2)
        renderWebPage(mUrl3,mWebView3)

        txt = findViewById(R.id.textview)
        // val btn = findViewById(R.id.btnClick) as Button
        mLoopCount = findViewById(R.id.loopCount)
        shortcodeList = ArrayList()
        mainProfileUrl = ArrayList()
        profileShortcodeList = ArrayList()
        progressBar = findViewById(R.id.progress_horizontalX)
        testList.add(test)
        lateinit var job: Job

        btn.setOnClickListener {


            /*   val urls = arrayOf(mUrl,mUrl2,mUrl3,mUrl4)

               job = GlobalScope.launch(Dispatchers.IO) {

                   isCoroutineActive = isActive
                   for (url in urls) {
                       launch { getlocationSourcecode(url) }
                   }
                   Log.i("MainActivity", " TEST : This Main coroutine is running in ${Thread.currentThread().name} thread ")
               }

               Log.i("TEST", "TEST : job is Active = ${job.isActive}")
               btn.setText("Stop")*/
        }

        mCreateSheet.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Log.i(TAG, "TEST : button toggled on ")

                val urls = arrayOf(mUrl, mUrl2, mUrl3, mUrl4)
                /*var mWebView1 = WebView(applicationContext)
                var mWebView2 = WebView(applicationContext)
                var mWebView3 = WebView(applicationContext)*/
                /*renderWebPage(mUrl,mWebView1)
                renderWebPage(mUrl2,mWebView2)
                renderWebPage(mUrl3,mWebView3)*/



                job = GlobalScope.launch(Dispatchers.IO) {

                    isCoroutineActive = isActive
                    /*  for (url in urls) {

                      }*/
                    launch { getlocationSourcecode(mUrl4, mWebView1, obj1) }
                    launch { getlocationSourcecode(mUrl2, mWebView2, obj2) }
                    launch { getlocationSourcecode(mUrl3, mWebView3, obj3) }
                    Log.i("MainActivity", " TEST : This Main coroutine is running in ${Thread.currentThread().name} thread ")
                }

                Log.i("TEST", "TEST : job is Active = ${job.isActive}")

            } else {
                Log.i(TAG, "TEST : button toggled off ")

                Log.i("TEST", "TEST : job isActive = ${job.isActive}")
                job.cancel()
                isCoroutineActive = job.isActive
                Log.i("TEST", " TEST : job Cancelling job")
                Log.i("TEST", "TEST : job isCancelled = ${job.isCancelled}")
            }
        }


        // Log.i("MainActivity", "TEST : linkUrl = " + addressBar.text.toString())


        btnSave.setOnClickListener {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                createtxtFile()
            } else {
                Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show()
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
            }


            //Log.i("MainActivity", "dynamicUrl = $dynamicUrl")

        }

        btnStop.setOnClickListener {
            Log.i("TEST", "TEST : job isActive = ${job.isActive}")
            /*job.cancel()
            isCoroutineActive = job.isActive*/
            Log.i("TEST", " TEST : job Cancelling job")
            Log.i("TEST", "TEST : job isCancelled = ${job.isCancelled}")
        }


        // Get the application context
        mContext = applicationContext
        // Get the activity
        mActivity = this


        //mWebView = findViewById(R.id.web_view)
        //mWebView = WebView(getApplicationContext())

        mScrollbtn = findViewById(R.id.btn_scroll)
        mbtnState = findViewById(R.id.webviewChangestate)

        mbtnState!!.setOnClickListener{
            renderWebPage(mUrl,mWebView1)
        }


        //mWebView.isVerticalScrollBarEnabled = true

        //Load website in webview
//        renderWebPage(mUrl)


        mgetLink.setOnClickListener{
            addressBar.setText(mWebView1.url)

            GlobalScope.launch(Dispatchers.IO) {
                locationSourceCode = Jsoup.connect(addressBar.text.toString()).get()
            }

        }


        mScrollbtn.setOnClickListener {
            if (edt.getText().toString() != null) {
                mWebView1.scrollBy(0, edt.getText().toString().toInt())
            } else {
                Toast.makeText(mContext, "Scroll addressBar null", Toast.LENGTH_SHORT).show()
            }
            Log.i("WebActivity", " TEST webview scrolled ")
        }


        Log.i("MainActivity", " TEST : This Main thread is running in ${Thread.currentThread().name} thread ")


    }

    /* override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
         if (requestCode == STORAGE_PERMISSION_CODE){

         }
     }*/

/*
    fun writeFile() {
        val dataText = "Testing to save text version 3 successfull"

        val path = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            File(Environment.getExternalStorageDirectory(), "InstaScrape")
        } else {

        }
        var isPresent = true
        if (!path.exists()) {
            isPresent = path.mkdir()
        }
        if (isPresent) {
            Log.i("MainActivity", "TEST : isPresent = $isPresent")
            val file = File(path.absolutePath, "/dataText2.txt")
            val stream = FileOutputStream(file)
            stream.write(dataText.toByteArray())
            stream.close()
        } else {
            Log.i("MainActivity", "TEST : Path is not present")
            Log.i("MainActivity", "TEST : isPresent = $isPresent")
        }
        Log.i("MainActivity", "TEST : path = $path")
    }*/


    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun createFile() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/vnd.ms-excel\",\"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            putExtra(Intent.EXTRA_TITLE, "Instagram_data.xls")
            // Optionally, specify a URI for the directory that should be opened in
            // the system file picker before your app creates the document.
            //putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
        }

        startActivityForResult(intent, CREATE_FILE)
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun createtxtFile() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "txt/plain"
            putExtra(Intent.EXTRA_TITLE, "Instagram_profile_data.txt")
            // Optionally, specify a URI for the directory that should be opened in
            // the system file picker before your app creates the document.
            //putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
        }

        startActivityForResult(intent, 2)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        Log.i("MainActivity", "TEST : onActivityResult() called ")
        Log.i("MainActivity", "TEST : Activity.RESULT_OK() = ${Activity.RESULT_OK}")
        Log.i("MainActivity", "TEST : resultCode() = ${resultCode}")
        if (resultCode != Activity.RESULT_OK) {
            Log.i("MainActivity", "TEST : resultCode() = ${Activity.RESULT_OK}")
            return
        }
        if (requestCode == FILE_EXPORT_REQUEST_CODE) {
            Log.i("MainActivity", "TEST : requestCode() called ")
            if (data != null) {
                Log.i("MainActivity", "TEST : data = ${data.data.toString()}")
                val uri = data.data
                Log.i("MainActivity", "TEST : data = ${data.getStringExtra(Intent.EXTRA_SUBJECT)}")
                Log.i("MainActivity", "TEST : data = ${data.getStringExtra("body")}")
                var d = data?.extras
                var r = d?.get("body")
                Log.i("MainActivity", "TEST : resultString = $r")
                writeToFile2(testList, uri!!)
            }
        } /*else {
            Log.i("MainActivity", "TEST : requestCode() != FILE_EXPORT_REQUEST_CODE")
            Log.i("MainActivity", "TEST : requestCode() = $requestCode")
            Log.i("MainActivity", "TEST : FILE_EXPORT_REQUEST_CODE() = $FILE_EXPORT_REQUEST_CODE")
        }*/

        if (requestCode == 2){
            var uri = data!!.data
            writeToFile(locationSourceCode.toString(),uri!!)

        }
    }

    private fun writeToFile(data: String, uri:Uri) {
        try {
            val outputStreamWriter = contentResolver.openOutputStream(uri)
            outputStreamWriter!!.write(data.toByteArray())
            outputStreamWriter.close()
        } catch (e: IOException) {
            Log.e("Exception", "File write failed: $e")
        }
    }

    private fun writeToFile2(data: ArrayList<Array<String>>, uri: Uri) {

        val wb: Workbook = HSSFWorkbook()
        var cell: Cell?

        //Now we are creating sheet

        //Now we are creating sheet
        var sheet: Sheet? = null
        sheet = wb.createSheet("Instagram Data")
        //Now column and row
        //Now column and row

        for (a in data) {
            val row: Row = sheet.createRow(data.indexOf(a))

            for (i in a) {
                cell = row.createCell(a.indexOf(i))
                cell.setCellValue(i)
//                cell.setCellStyle(cellStyle)
                Log.i("MainActvity", "TEST : index = ${a.indexOf(i)}")
                Log.i("MainActvity", "TEST : tag size = ${a.size}")
                Log.i("MainActvity", "TEST : tag = $i")
            }
        }


        sheet.setColumnWidth(0, 10 * 200)
        sheet.setColumnWidth(1, 10 * 200)


        try {
            val outputStreamWriter = contentResolver.openOutputStream(uri)
//            outputStreamWriter!!.write(data.toByteArray())
//            outputStreamWriter.close()

            wb.write(outputStreamWriter)

        } catch (e: IOException) {

            Log.e("Exception", "File write failed: $e")
        }
    }


    protected fun renderWebPage(urlToRender: String?, webView: WebView) {

        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) { // Do something on page loading started
                mUrl = url
            }

            override fun onPageFinished(view: WebView, url: String) { // Do something when page loading finished
                mUrl = url
            }
        }
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {}
        }
        // Enable the javascript
        webView.settings.javaScriptEnabled = true
        // Render the web page
        webView.loadUrl(urlToRender)

    }


    suspend fun getlocationSourcecode(url: String, webView: WebView, urlobj:dynamicUrlObject ) {

        lateinit var trying: String

        var ThreadName = Thread.currentThread().name

        Log.i("MainActivity + ${Thread.currentThread().name}", "TEST : getDatax  called ")
        Log.i("MainActivity + ${Thread.currentThread().name}", " TEST : This asyncTask thread is running in ${Thread.currentThread().name} thread ")

        try {

            //To get the source code of location mumbai maharastra

            val response = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Linux; Android 7.0; SAMSUNG SM-G570Y Build/MMB29K) AppleWebKit/537.36 (KHTML, like Gecko) SamsungBrowser/4.0 Chrome/44.0.2403.133 Mobile Safari/537.36")
                    .headers(headersMap)
                    .ignoreHttpErrors(true)
                    .execute()          // Network Request
            // Log.i("MainActivity + ${Thread.currentThread().name}"," response = ${response.statusCode()}")
            if (response.statusCode() != 404) {

                val doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Linux; Android 7.0; SAMSUNG SM-G570Y Build/MMB29K) AppleWebKit/537.36 (KHTML, like Gecko) SamsungBrowser/4.0 Chrome/44.0.2403.133 Mobile Safari/537.36")
                        .headers(headersMap)
                        .ignoreHttpErrors(true)
                        .get()      // Network Request

                //Log.i("MainActivity + ${Thread.currentThread().name}"," response = ${doc.toString()}")

                val scripts = doc.select("script[type=\"text/javascript\"]")
                val s = scripts.eq(3).toString()
                val start = s.indexOf("{")
                val end = s.indexOf(";</script>")




                if (s.contains(">window._sharedData = {\"config\"")) {

                    trying = s.substring(start, end)

                    //To get the souce code for each of the post in top post
                    val rootObject = JSONObject(trying)
                    val entryObject = rootObject.getJSONObject("entry_data")
                    val pageArray = entryObject.getJSONArray("LocationsPage")
                    val pageObject = pageArray.getJSONObject(0)
                    val graphqlObject = pageObject.getJSONObject("graphql")
                    val locationObject = graphqlObject.getJSONObject("location")

                    //This section is for top post
                    val top_postsObject = locationObject.getJSONObject("edge_location_to_top_posts")
                    // int topPostCount = top_postsObject.getInt("count");
                    val topEdgeArray = top_postsObject.getJSONArray("edges")
                    val topPostCount = topEdgeArray.length()

                    //This loop is for top post
                    //loopThroughPosts(topPostCount-1,topEdgeArray, "TopPost");

                    //This section is for recent post
                    val recent_postsObject = locationObject.getJSONObject("edge_location_to_media")
                    // int topPostCount = top_postsObject.getInt("count");
                    val recentEdgeArray = recent_postsObject.getJSONArray("edges")
                    val recentPostCount = recentEdgeArray.length() - 1


                    //This loop is for recent post
                    docProfileData = loopThroughPosts(recentPostCount, recentEdgeArray, "RecentPost").toString()

                }
            }

            // trying = String.valueOf(doc);
            //Log.i("TAG", "TEST : trying = "+ trying);
        } catch (e: IOException) {
            e.printStackTrace()
            Log.i("TAG + ${Thread.currentThread().name}", "TEST : scripts = $e")
        } catch (e: JSONException) {
            e.printStackTrace()
            Log.i("TAG + ${Thread.currentThread().name}", "TEST : scripts = $e")
        }
        Log.i("TAG + ${Thread.currentThread().name}", "TEST : The work is finished" )

        withContext(Dispatchers.Main) {
            Log.i(TAG, "TEST : $ThreadName : url passed = $url")

            //dynamicUrl = webView.url

            Log.i(TAG,"TEST : $ThreadName : webViewUrl = ${webView.url}")
            Log.i(TAG,"TEST : $ThreadName : urlobj = ${urlobj.url}")

        }

        withContext(Dispatchers.IO) {
            if (isActive) {
                Log.i("TEST", "TEST ${Thread.currentThread().name} : job isActive = $isActive")
                launch { getlocationSourcecode(url, webView,  urlobj) }
            }

        }

    }


    fun loopThroughPosts(forloopCount: Int, jsonArray: JSONArray, postType: String): Document? {

        var docProfile: Document? = null

        progressBar!!.max = (forloopCount * 2) - 1
        try {
            for (i in 0..forloopCount) {

                if (isCoroutineActive!!) {
                    Log.i("TEST", "TEST : job isCoroutineActive = $isCoroutineActive")
                    progressBar!!.progress = i * 2
                    Log.i("$postType + Thread = ${Thread.currentThread().name}", "TEST : loopCount = $i")
                    Log.i("$postType + Thread = ${Thread.currentThread().name}", "TEST : PostCount = $forloopCount")
                    //Log.i("MainActivity", "TEST : topPostCount22222 = " + topPostCount2);
                    val post = jsonArray.getJSONObject(i)
                    val node = post.getJSONObject("node")
                    val shortcode = node.getString("shortcode")
                    shortcodeList!!.add(shortcode)

                    Log.i("$postType + Thread = ${Thread.currentThread().name}", "TEST : shortcode = $shortcode")
                    Log.i("$postType + Thread =  ${Thread.currentThread().name}", "TEST : size = " + shortcodeList!!.size)

                    //To get the profile url from the source code received using the shortcode of each post
                    val postUrl = "https://www.instagram.com/p/$shortcode"

                    val response = Jsoup.connect(postUrl)
                            .userAgent("Mozilla/5.0 (Linux; Android 7.0; SAMSUNG SM-G570Y Build/MMB29K) AppleWebKit/537.36 (KHTML, like Gecko) SamsungBrowser/4.0 Chrome/44.0.2403.133 Mobile Safari/537.36")
                            .headers(headersMap)
                            .ignoreHttpErrors(true)
                            .execute() // Network Request

                    var docUsername: Document?

                    if (response.statusCode() != 404) {
                        docUsername = Jsoup.connect(postUrl)
                                .userAgent("Mozilla/5.0 (Linux; Android 7.0; SAMSUNG SM-G570Y Build/MMB29K) AppleWebKit/537.36 (KHTML, like Gecko) SamsungBrowser/4.0 Chrome/44.0.2403.133 Mobile Safari/537.36")
                                .headers(headersMap)
                                .ignoreHttpErrors(true)
                                .get()  // Network Request

                        var scriptsUsername: Elements?

                        if (docUsername != null) {

                            scriptsUsername = docUsername.select("script[type=\"text/javascript\"]")
                            val s2 = scriptsUsername!!.eq(3).toString()
                            val start2 = s2.indexOf("{")
                            val end2 = s2.indexOf(";</script>")

                            if (s2.contains("window._sharedData = {\"config\"")) {
                                Log.i("$postType + Thread = ${Thread.currentThread().name}", "TEST :  start2 = $start2   end2 = $end2")
                                // Log.i("MainActivity", "TEST :  s2 = $s2")
                                val trying2 = s2.substring(start2, end2)
                                //Log.i("MainActivity", "TEST : trying2 = " + trying2);
                                val rootNameObject = JSONObject(trying2)
                                val usernameEntryObject = rootNameObject.getJSONObject("entry_data")
                                val usernamePostPageArray = usernameEntryObject.getJSONArray("PostPage")
                                val usernameEntryRootObject = usernamePostPageArray.getJSONObject(0)
                                val usernameGraphqlObject = usernameEntryRootObject.getJSONObject("graphql")
                                val usernameShortCodeObject = usernameGraphqlObject.getJSONObject("shortcode_media")
                                val usernameOwnerObject = usernameShortCodeObject.getJSONObject("owner")
                                val username = usernameOwnerObject.getString("username")
                                val profileUrl = "https://www.instagram.com/$username"
                                mainProfileUrl!!.add(profileUrl)
                                Log.i("$postType + Thread = ${Thread.currentThread().name}", "TEST : profileUrl = $profileUrl")


                                //To get profile details like follower, postCount, following, bio etc and shortcodes for the posts

                                val responseForProfileUrl = Jsoup.connect(profileUrl)
                                        .userAgent("Mozilla/5.0 (Linux; Android 7.0; SAMSUNG SM-G570Y Build/MMB29K) AppleWebKit/537.36 (KHTML, like Gecko) SamsungBrowser/4.0 Chrome/44.0.2403.133 Mobile Safari/537.36")
                                        .headers(headersMap)
                                        .ignoreHttpErrors(true)
                                        .execute()
                                if (responseForProfileUrl.statusCode() != 404) {

                                    docProfile = Jsoup.connect(profileUrl)
                                            .userAgent("Mozilla/5.0 (Linux; Android 7.0; SAMSUNG SM-G570Y Build/MMB29K) AppleWebKit/537.36 (KHTML, like Gecko) SamsungBrowser/4.0 Chrome/44.0.2403.133 Mobile Safari/537.36")
                                            .headers(headersMap)
                                            .ignoreHttpErrors(true)
                                            .get()  // Network Request
                                    val scriptsProfile = docProfile.select("script[type=\"text/javascript\"]")

                                    Log.i("$postType + Thread = ${Thread.currentThread().name}", "TEST : scriptsProfile size = " + scriptsProfile.size)

                                    val s3 = scriptsProfile.eq(3).toString()
                                    val start3 = s3.indexOf("{")
                                    val end3 = s3.indexOf(";</script>")

                                    if (s3.contains("window._sharedData = {\"config\"")) {
                                        val trying3 = s3.substring(start3, end3)
                                        //Log.i("ProfilePage", "s3 = $s3")
                                        // Log.i("MainActivity", "TEST : trying3 = $trying3")
                                        val rootDetailObject = JSONObject(trying3)
                                        val entryDetailObject = rootDetailObject.getJSONObject("entry_data")
                                        val pageDetailArray = entryDetailObject.getJSONArray("ProfilePage")
                                        val pageDetailObject = pageDetailArray.getJSONObject(0)
                                        val graphqlDetailObject = pageDetailObject.getJSONObject("graphql")
                                        val userDetailObject = graphqlDetailObject.getJSONObject("user")
                                        val profileBio = userDetailObject.getString("biography")  // Bio String
                                        val profileWebsiteUrl = userDetailObject.getString("external_url")  // Website slot String
                                        val followerDetailObject = userDetailObject.getJSONObject("edge_followed_by")
                                        val followerCount = followerDetailObject.getInt("count")  // Follower string
                                        val followingDetailObject = userDetailObject.getJSONObject("edge_follow")
                                        val followingCount = followingDetailObject.getInt("count") // Following String
                                        val fullName = userDetailObject.getString("full_name")  // Full Name String
                                        val userName = userDetailObject.getString("username") // UserName String
                                        val businessAccount = userDetailObject.getBoolean("is_business_account") // Value
                                        val jointRecently = userDetailObject.getBoolean("is_joined_recently") // Value
                                        val businessCategory = userDetailObject.getString("business_category_name") // Value
                                        val verifiedAccount = userDetailObject.getBoolean("is_verified") // Value

                                        Log.i("$postType + Thread = ${Thread.currentThread().name}", "TEST : postEdgeDetailArray = $followerCount   $followingCount    $fullName    $userName   $profileBio   $profileWebsiteUrl   $businessAccount   $jointRecently   $businessCategory   $verifiedAccount")

                                        //var accountData = "$fullName\t$userName\t$followerCount\t$followingCount\t$profileBio\t$profileWebsiteUrl\t$businessAccount\t$jointRecently\t$businessCategory\t$verifiedAccount\n"
                                        var accountData = arrayOf("$fullName", "$userName", "$followerCount", "$followingCount", "$profileBio", "$profileWebsiteUrl", "$businessAccount", "$jointRecently", "$businessCategory", "$verifiedAccount")


                                        //test+=accountData

                                        testList.add(accountData)


                                        //To get the shortcode for the posts in the profile
                                        /* val postShortcodeDetailObject = userDetailObject.getJSONObject("edge_owner_to_timeline_media")
                                         val postCount = postShortcodeDetailObject.getInt("count")
                                         val postEdgeDetailArray = postShortcodeDetailObject.getJSONArray("edges")
                                         Log.i("MainActivity", "TEST : postEdgeDetailArray = $followerCount   $followingCount    $fullName    $userName   $postCount   $profileBio   $profileWebsiteUrl   $businessAccount   $jointRecently   $businessCategory   $verifiedAccount")
                                         Log.i("MainActivity", "TEST : postEdgeDetailArray = " + postEdgeDetailArray.length())

                                         *//*  if (postEdgeDetailArray.length() != 0) {
                                      var loopCount: Int
                                      loopCount = if (postEdgeDetailArray.length() > 2) {
                                          2
                                      } else {
                                          ppCommentEdgesArray.length()
                                      }*//*


                                for (i2 in 0 until postEdgeDetailArray.length()) {
                                    val profilePostEdgeObject = postEdgeDetailArray.getJSONObject(i2)
                                    val profileNodeObject = profilePostEdgeObject.getJSONObject("node")
                                    val profilePostShortcode = profileNodeObject.getString("shortcode")

                                    val profilePostShortcodeList = ArrayList<String>()
                                    profilePostShortcodeList.add(profilePostShortcode)

                                    val profilePostAccessibility_caption = profileNodeObject.getString("accessibility_caption")
                                    val profilePostAccessibility_captionList = ArrayList<String>()
                                    profilePostAccessibility_captionList.add(profilePostAccessibility_caption)
                                    val items = ProfileItems(profilePostShortcodeList, profilePostAccessibility_captionList)
                                    profileShortcodeList!!.add(items)
                                    //Log.i("MainActivity", "TEST : profileShortcodeList = " + profileShortcodeList.size());

                                    //To get the post caption and two comments
                                    val profilePostUrl = "https://www.instagram.com/p/$profilePostShortcode"
                                    val response = Jsoup.connect(profilePostUrl).execute()
                                    var docProfilePost: Document? = null
                                    if (response.statusCode() != 404) {
                                        docProfilePost = Jsoup.connect(profilePostUrl)
                                                .header("Cookie", "ZvcurrentVolume=100; zvAuth=1; zvLang=0; ZvcurrentVolume=100; notice=11")
                                                .headers(headersMap)
                                                .userAgent("Mozilla/5.0 (Linux; Android 7.0; SAMSUNG SM-G570Y Build/MMB29K) AppleWebKit/537.36 (KHTML, like Gecko) SamsungBrowser/4.0 Chrome/44.0.2403.133 Mobile Safari/537.36")
                                                .get()
                                    }

                                    val scriptsProfilePost = docProfilePost!!.select("script[type=\"text/javascript\"]")
                                    Log.i("MainActivity", "TEST : profilePostUrl = $profilePostUrl")
                                    val sPP = scriptsProfilePost.eq(3).toString()
                                    val startPP = sPP.indexOf("{")
                                    val endPP = sPP.indexOf(";</script>")

                                    if (sPP.contains("window._sharedData = {\"config\"")) {
                                        val tryingPP = sPP.substring(startPP, endPP)
                                        //Log.i("MainActivity", "TEST : tryingPP = " + tryingPP);
                                        val rootProfilePostObject = JSONObject(tryingPP)
                                        val ppEntryData = rootProfilePostObject.getJSONObject("entry_data")
                                        val ppPostPageArray = ppEntryData.getJSONArray("PostPage")
                                        val rootPostPageObject = ppPostPageArray.getJSONObject(0)
                                        val ppgraphqlObject = rootPostPageObject.getJSONObject("graphql")
                                        val ppshortcode_mediaObject = ppgraphqlObject.getJSONObject("shortcode_media")
                                        var accessibilityCaption: String? = null
                                        if (ppshortcode_mediaObject.has("accessibility_caption")) {
                                            accessibilityCaption = ppshortcode_mediaObject.getString("accessibility_caption")
                                        }
                                        val ppProfilePostCaptionObject = ppshortcode_mediaObject.getJSONObject("edge_media_to_caption")
                                        val ppProfilePostCaptionEdgeArray = ppProfilePostCaptionObject.getJSONArray("edges")
                                        var ProfilePostCaption: String? = null
                                        if (!ppProfilePostCaptionEdgeArray.isNull(0)) {
                                            val ppProfilePostCaptionEdgeRootObject = ppProfilePostCaptionEdgeArray.getJSONObject(0)
                                            val ppProfilePostCaptionNodeObject = ppProfilePostCaptionEdgeRootObject.getJSONObject("node")
                                            ProfilePostCaption = ppProfilePostCaptionNodeObject.getString("text")
                                        }
                                        Log.i("MainActivity", "TEST : ProfilePostCaption = $ProfilePostCaption    $accessibilityCaption")
                                        *//* val ppEdge_media_to_parent_commentObject = ppshortcode_mediaObject.getJSONObject("edge_media_to_parent_comment")
                                         val ppCommentEdgesArray = ppEdge_media_to_parent_commentObject.getJSONArray("edges")
                                         Log.i("MainActivity", "TEST : ppCommentEdgesArray length = " + ppCommentEdgesArray.length())
                                         if (ppCommentEdgesArray.length() != 0) {
                                             var loopCount: Int
                                             loopCount = if (ppCommentEdgesArray.length() > 2) {
                                                 2
                                             } else {
                                                 ppCommentEdgesArray.length()
                                             }
                                             for (ic in 0 until loopCount) {
                                                 val ppCommentObject = ppCommentEdgesArray.getJSONObject(ic)
                                                 val ppCommentNodeObject = ppCommentObject.getJSONObject("node")
                                                 val ppcomment = ppCommentNodeObject.getString("text")
                                                 Log.i("MainActivity", "TEST : ppcomment = $ppcomment")
                                             }
                                         }*//*
                                    }
                                }*/
                                    }

                                }
                            }
                        }
                    }
                }


            }
        } catch (e: IOException) {
            e.printStackTrace()
            Log.i("$postType + Thread = ${Thread.currentThread().name}", "TEST : scripts = $e")
        } catch (e: JSONException) {
            e.printStackTrace()
            Log.i("$postType + Thread = ${Thread.currentThread().name}", "TEST : scripts = $e")
        } catch (e: NullPointerException) {
            e.printStackTrace()
            Log.i("$postType + Thread = ${Thread.currentThread().name}", "TEST : scripts = $e")
        } catch (e: StringIndexOutOfBoundsException) {
            e.printStackTrace()
            Log.i("$postType + Thread = ${Thread.currentThread().name}", "TEST : stringOutbound = $e")
        } catch (e: HttpStatusException) {
            e.printStackTrace()
            Log.i("$postType + Thread = ${Thread.currentThread().name}", "TEST : HttpStatusException = $e")
        }

        return docProfile
    }

    data class dynamicUrlObject(var url: String)

}