package com.example.android.iscrape

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.android.iscrape.MainActivity
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.io.IOException
import java.util.*
import kotlin.system.measureTimeMillis

class MainActivity : AppCompatActivity() {

    var txt: EditText? = null
    var mLoopCount: EditText? = null
    var shortcodeList: ArrayList<String>? = null
    var mainProfileUrl: ArrayList<String>? = null
    var profileShortcodeList: ArrayList<ProfileItems>? = null
    var count = 0
    var progressBar: ProgressBar? = null
    // var asyncTask: getDatax? = null

    //Web Activity class
    private var mContext: Context? = null
    private var mActivity: Activity? = null

    lateinit private var mWebView: WebView
    lateinit private var mScrollbtn: Button
    private var mbtnState: Button? = null
    var isCancelledx: Boolean = false
    lateinit var docProfileData:String

    private var mUrl = "https://www.instagram.com/explore/locations/216978098/mumbai-maharashtra/"
    private var mUrl2 = "https://www.instagram.com/explore/locations/204517928/chicago-illinois/"
    private var mUrl3 = "https://www.instagram.com/explore/locations/213567260/mumbai-trending/"
    private var mUrl4 = "https://www.instagram.com/explore/locations/1268778/inorbit-mall/"
    private var mUrl5 = "https://www.instagram.com/explore/locations/747477016/grant-road/"
    // private var mUrl = "https://www.instagram.com/explore/tags/business/"
    var dynamicUrl:String = "https://www.instagram.com/explore/locations/747477016/grant-road/"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn: Button = findViewById(R.id.btnClick)
        val btnStop: Button = findViewById(R.id.btnStopTask)
        var mgetLink: Button = findViewById(R.id.btn_getLink)
        var mtoJsoup: Button = findViewById(R.id.btn_toJsoup)
        var addressBar: EditText = findViewById(R.id.addressbar)
        var edt: EditText = findViewById(R.id.input)

        txt = findViewById(R.id.textview)
        // val btn = findViewById(R.id.btnClick) as Button
        mLoopCount = findViewById(R.id.loopCount)
        shortcodeList = ArrayList()
        mainProfileUrl = ArrayList()
        profileShortcodeList = ArrayList()
        progressBar = findViewById(R.id.progress_horizontalX)



        btn.setOnClickListener(View.OnClickListener {

            val urls = arrayOf(mUrl)

            GlobalScope.launch(Dispatchers.IO) {

                for (url in urls) {
                    launch { getlocationSourcecode(mUrl) }

                }
                Log.i("MainActivity", " TEST : This Main coroutine is running in ${Thread.currentThread().name} thread ")
            }

        })


        // Log.i("MainActivity", "TEST : linkUrl = " + addressBar.text.toString())
        /* GlobalScope.launch(Dispatchers.Default) {

             for(url in urls){
                 val result = async { getlocationSourcecode(url) }
             }
             *//* async { getlocationSourcecode(mUrl) }
           async { getlocationSourcecode(mUrl2) }
           async { getlocationSourcecode(mUrl3) }
           async { getlocationSourcecode(mUrl4) }
           async { getlocationSourcecode(mUrl5) }*//*
            Log.i("MainActivity", " TEST : This Second Main coroutine is running in ${Thread.currentThread().name} thread ")
        }*/

        btnStop.setOnClickListener(View.OnClickListener {
            /* if (asyncTask!!.status == AsyncTask.Status.RUNNING) {
                 //asyncTask!!.cancel(true)
                 isCancelledx = true
                 Log.i("MainActivity", "TEST : asyncTask running ")
                 Log.i("MainActivity", "TEST : asyncTask running isCancelledx = $isCancelledx ")
             } else {
                 isCancelledx = false
                 Log.i("MainActivity", "TEST : asyncTask not running ")
             }*/
            Log.i("MainActivity","dynamicUrl = $dynamicUrl")
        })


        // Get the application context
        mContext = applicationContext
        // Get the activity
        mActivity = this


        mWebView = findViewById(R.id.web_view)
        //mWebView = new WebView(getApplicationContext());

        mScrollbtn = findViewById(R.id.btn_scroll)
        mbtnState = findViewById(R.id.webviewChangestate)


        mWebView.isVerticalScrollBarEnabled = true

        //Load website in webview
        renderWebPage(mUrl)

        mgetLink.setOnClickListener(View.OnClickListener { addressBar.setText(mWebView.url) })
        /* mtoJsoup.setOnClickListener(View.OnClickListener {
             val intent = Intent(this@MainActivity, MainActivity::class.java)
             intent.putExtra("link", mWebView!!.url)
             startActivity(intent)
         })*/

        mScrollbtn.setOnClickListener {
            if (edt.getText().toString() != null) {
                mWebView.scrollBy(0, edt.getText().toString().toInt())
            } else {
                Toast.makeText(mContext, "Scroll addressBar null", Toast.LENGTH_SHORT).show()
            }
            Log.i("WebActivity", " TEST webview scrolled ")
        }


        Log.i("MainActivity", " TEST : This Main thread is running in ${Thread.currentThread().name} thread ")

    }


    protected fun renderWebPage(urlToRender: String?) {

        mWebView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) { // Do something on page loading started
                mUrl = url
            }

            override fun onPageFinished(view: WebView, url: String) { // Do something when page loading finished
                mUrl = url
            }
        }
        mWebView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {}
        }
        // Enable the javascript
        mWebView.settings.javaScriptEnabled = true
        // Render the web page
        mWebView.loadUrl(urlToRender)

    }

    /*  inner class getDatax : AsyncTask<String?, Void?, Void?>() {

          lateinit var trying: String


          override fun doInBackground(vararg urls: String?): Void? {

              Log.i("MainActivity", "TEST : getDatax  called ")
              Log.i("MainActivity", " TEST : This asyncTask thread is running in ${Thread.currentThread().name} thread ")

              try {

                  //To get the source code of location mumbai maharastra

                  val doc = Jsoup.connect(urls[0]).get()
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
                      // loopThroughPosts(topPostCount,topEdgeArray);

                      //This section is for recent post
                      val recent_postsObject = locationObject.getJSONObject("edge_location_to_media")
                      // int topPostCount = top_postsObject.getInt("count");
                      val recentEdgeArray = recent_postsObject.getJSONArray("edges")
                      val recentPostCount = recentEdgeArray.length()


                      //This loop is for recent post
                      loopThroughPosts(recentPostCount, recentEdgeArray)

                  }

                  // trying = String.valueOf(doc);
                  //Log.i("TAG", "TEST : trying = "+ trying);
              } catch (e: IOException) {
                  e.printStackTrace()
                  Log.i("TAG", "TEST : scripts = $e")
              } catch (e: JSONException) {
                  e.printStackTrace()
                  Log.i("TAG", "TEST : scripts = $e")
              }
              return null
          }

          override fun onPostExecute(aVoid: Void?) {
              super.onPostExecute(aVoid)

              mWebView.scrollBy(0, 1000)
              asyncTask = getDatax()
              asyncTask!!.execute(mWebView.url)

          }



      }*/


    suspend fun getlocationSourcecode(url: String) {

        lateinit var trying: String

        Log.i("MainActivity + ${Thread.currentThread().name}", "TEST : getDatax  called ")
        Log.i("MainActivity + ${Thread.currentThread().name}", " TEST : This asyncTask thread is running in ${Thread.currentThread().name} thread ")

        try {

            //To get the source code of location mumbai maharastra

            val response = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Linux; Android 7.0; SAMSUNG SM-G570Y Build/MMB29K) AppleWebKit/537.36 (KHTML, like Gecko) SamsungBrowser/4.0 Chrome/44.0.2403.133 Mobile Safari/537.36")
                    .ignoreHttpErrors(true)
                    .execute()          // Network Request
           // Log.i("MainActivity + ${Thread.currentThread().name}"," response = ${response.statusCode()}")
            if (response.statusCode() != 404) {

                val doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Linux; Android 7.0; SAMSUNG SM-G570Y Build/MMB29K) AppleWebKit/537.36 (KHTML, like Gecko) SamsungBrowser/4.0 Chrome/44.0.2403.133 Mobile Safari/537.36")
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
                    val recentPostCount = recentEdgeArray.length()-1


                    //This loop is for recent post
                    docProfileData = loopThroughPosts(1, recentEdgeArray, "RecentPost").toString()

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


        withContext(Dispatchers.Main) {
            mWebView.scrollBy(0, 1000)

            //txt?.setText(docProfileData)
            dynamicUrl = mWebView.url
        }


        withContext(Dispatchers.IO){
            launch { getlocationSourcecode(dynamicUrl) }
        }

    }


    fun loopThroughPosts(forloopCount: Int, jsonArray: JSONArray, postType:String):Document {

        lateinit var docProfile:Document

        progressBar!!.max = (forloopCount * 2) - 1
        try {
            for (i in 0..forloopCount) {
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
                        .ignoreHttpErrors(true)
                        .execute() // Network Request

                var docUsername: Document?

                if (response.statusCode() != 404) {
                    docUsername = Jsoup.connect(postUrl)
                            .userAgent("Mozilla/5.0 (Linux; Android 7.0; SAMSUNG SM-G570Y Build/MMB29K) AppleWebKit/537.36 (KHTML, like Gecko) SamsungBrowser/4.0 Chrome/44.0.2403.133 Mobile Safari/537.36")
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
                                    .ignoreHttpErrors(true)
                                    .execute()
                            if (responseForProfileUrl.statusCode() != 404) {

                                docProfile = Jsoup.connect(profileUrl)
                                        .userAgent("Mozilla/5.0 (Linux; Android 7.0; SAMSUNG SM-G570Y Build/MMB29K) AppleWebKit/537.36 (KHTML, like Gecko) SamsungBrowser/4.0 Chrome/44.0.2403.133 Mobile Safari/537.36")
                                        .ignoreHttpErrors(true)
                                        .get()  // Network Request
                                val scriptsProfile = docProfile.select("script[type=\"text/javascript\"]")

                                Log.i("$postType + Thread = ${Thread.currentThread().name}", "TEST : scriptsProfile size = " + scriptsProfile.size)

                                val s3 = scriptsProfile.eq(3).toString()
                                val start3 = s3.indexOf("{")
                                val end3 = s3.indexOf(";</script>")

                                if (s3.contains("window._sharedData = {\"config\"")) {
                                    val trying3 = s3.substring(start3, end3)
                                    Log.i("ProfilePage", "s3 = $s3")
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
        } catch (e: HttpStatusException){
            e.printStackTrace()
            Log.i("$postType + Thread = ${Thread.currentThread().name}", "TEST : HttpStatusException = $e")
        }

       return docProfile
    }
}