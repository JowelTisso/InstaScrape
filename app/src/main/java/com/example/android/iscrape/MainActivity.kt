package com.example.android.iscrape

import android.Manifest
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.io.IOException
import java.io.OutputStreamWriter
import java.util.*
import java.util.regex.Pattern


class MainActivity : AppCompatActivity() {

    private val STORAGE_PERMISSION_CODE = 1
    val FILE_EXPORT_REQUEST_CODE = 1
    var progressBar: ProgressBar? = null
    lateinit var logTextView: EditText
    var counter: Int = 0
    var modeselectorCounter: Int = 0
    lateinit var btnModeSelector: Button
    lateinit var scrapedCount: TextView
    lateinit var emailCountView: TextView
    lateinit var emailViewObject: EditText
    lateinit var btnNext: Button

    // var asyncTask: getDatax? = null
    var TAG = "MainActivity"

    var isCoroutineActive: Boolean = false

    //lateinit var docProfileData: String
    lateinit var locationSourceCode: Document

    var mUrl: String = "https://www.instagram.com/explore/locations/216978098/mumbai-maharashtra/"
    private var mUrl1 = "https://www.instagram.com/explore/locations/216978098/mumbai-maharashtra/"
    private var mUrl2 = "https://www.instagram.com/explore/locations/204517928/chicago-illinois/"
    private var mUrl3 = "https://www.instagram.com/explore/locations/360180752/wuhan-china/"
    private var mUrl4 = "https://www.instagram.com/explore/tags/workout/"
    private var mUrl5 = "https://www.instagram.com/explore/locations/747477016/grant-road/"
    var test = arrayOf("fullName", "userName", "followerCount", "followingCount", "profileBio", "profileWebsiteUrl", "businessAccount", "jointRecently", "businessCategory", "verifiedAccount")
    var testList = arrayListOf<Array<String>>()
    var logtext: String = ""
    var email: String = ""
    var emailCount: Int = 0


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
    var locationPageOrTagPage: String = "LocationsPage"
    var locationOrHashtag: String = "location"
    var edge_hashtag_location_to_media: String = "edge_location_to_media"
    var edge_hashtag_location_to_top_posts: String = "edge_location_to_top_posts"
    lateinit var runtime: Runtime
    lateinit var maxHeapView: TextView
    lateinit var allowedHeapView: TextView
    lateinit var freeHeapView: TextView
    lateinit var coroutineCount: TextView
    var coroutineCountVar: Int = 0
    lateinit var job: Job
    lateinit var btnStart: ToggleButton

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val btnSave: Button = findViewById(R.id.btnSaveTask)
        logTextView = findViewById(R.id.logTextView)
        btnModeSelector = findViewById(R.id.btnModeSelector)
        btnNext = findViewById(R.id.btnNext)

        btnStart = findViewById(R.id.btn_start)
        val btnTagOrLocation: ToggleButton = findViewById(R.id.btn_tagOrlocation)

        // val btn = findViewById(R.id.btnClick) as Button

        progressBar = findViewById(R.id.progress_horizontalX)
        testList.add(test)
        val tagViewObject: EditText = findViewById(R.id.hashtag1)
        val tagViewObject2: EditText = findViewById(R.id.hashtag2)
        val tagViewObject3: EditText = findViewById(R.id.hashtag3)
        val tagViewObject4: EditText = findViewById(R.id.hashtag4)

        scrapedCount = findViewById(R.id.scrapedCount)
        emailCountView = findViewById(R.id.emailCount)
        emailViewObject = findViewById(R.id.emailTextView)

        //Heap View Initiation
        maxHeapView = findViewById(R.id.maxHeapMemory)
        allowedHeapView = findViewById(R.id.allowedHeapMemory)
        freeHeapView = findViewById(R.id.freeHeapMemory)
        coroutineCount = findViewById(R.id.coroutineCounts)

        //Heap max memory check
        runtime = Runtime.getRuntime()
        val maxHeapSizeInMB = runtime.maxMemory() / 1048576L
        maxHeapView.setText(maxHeapSizeInMB.toString() + " MB")


        val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryClass = am.memoryClass
        allowedHeapView.setText(memoryClass.toString() + " MB")
        Log.i(TAG, "TEST : memoryClass = " + Integer.toString(memoryClass))

        freeHeapView.setText(checkHeapSize().toString() + " MB")

        btnModeSelector.setOnClickListener {
            modeselectorCounter++
            if (modeselectorCounter > 2) {
                modeselectorCounter = 0
            }
            when (modeselectorCounter) {
                0 -> btnModeSelector.setText("Lite")
                1 -> btnModeSelector.setText("Medium")
                2 -> btnModeSelector.setText("Heavy")
                else -> btnModeSelector.setText("Lite")
            }
        }

        btnTagOrLocation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                locationPageOrTagPage = "TagPage"
                locationOrHashtag = "hashtag"
                edge_hashtag_location_to_media = "edge_hashtag_to_media"
                edge_hashtag_location_to_top_posts = "edge_hashtag_to_top_posts"
                mUrl = "https://www.instagram.com/explore/tags/"
            } else {
                locationPageOrTagPage = "LocationsPage"
                locationOrHashtag = "location"
                edge_hashtag_location_to_media = "edge_location_to_media"
                edge_hashtag_location_to_top_posts = "edge_location_to_top_posts"
                mUrl = "https://www.instagram.com/explore/locations/216978098/mumbai-maharashtra/"
            }

            //textViewObject.setText("$locationPageOrTagPage \n $locationOrHashtag \n $edge_hashtag_location_to_media \n $edge_hashtag_location_to_top_posts ")
        }


        btnStart.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {

                var tag: String
                var tag2: String
                var tag3: String
                var tag4: String
                if (btnTagOrLocation.isChecked) {
                    if (tagViewObject.length() != 0 && tagViewObject2.length() != 0 && tagViewObject3.length() != 0 && tagViewObject4.length() != 0) {
                        tag = tagViewObject.text.toString()
                        tag2 = tagViewObject2.text.toString()
                        tag3 = tagViewObject3.text.toString()
                        tag4 = tagViewObject4.text.toString()

                        Log.i(TAG, "btnTagOrLocation : button toggled on ")

                        /*lifecycleScope.launch(Dispatchers.IO) {

                            isCoroutineActive = isActive

                            launch { getlocationSourcecode(mUrl + tag) }
                            *//*launch { getlocationSourcecode(mUrl + tag2) }
                            launch { getlocationSourcecode(mUrl + tag3) }
                            launch { getlocationSourcecode(mUrl + tag4) }*//*
                            Log.i("MainActivity", " TEST : This Main coroutine is running in ${Thread.currentThread().name} thread ")
                        }*/
                        getlocationSourcecode(mUrl + tag)
                        getlocationSourcecode(mUrl + tag2)
                        getlocationSourcecode(mUrl + tag3)

                    } else {
                        Toast.makeText(this, "Input Hashtag", Toast.LENGTH_SHORT).show()
                        btnStart.isChecked = false

                    }

                } else {

                    Log.i(TAG, "btnTagOrLocation : button toggled on ")

                    /*job = lifecycleScope.launch(Dispatchers.IO) {

                        isCoroutineActive = isActive

                        launch { getlocationSourcecode(mUrl) }
                        //launch { getlocationSourcecode(mUrl2, obj2) }
                        //  launch { getlocationSourcecode(mUrl3, obj3) }
                        Log.i("MainActivity", " TEST : This Main coroutine is running in ${Thread.currentThread().name} thread ")
                    }*/
                    getlocationSourcecode(mUrl)
                   /* lifecycleScope.launch {
                        job.join()
                        Log.i("TEST", "TEST : job is Active status after getlocation method  = ${job.isActive}")
                    }*/

                }


            } else {
                Log.i("Cancel Button", "TEST : button toggled off ")
                Log.i("Cancel Button", "TEST : job isActive = ${job.isActive}")

                job.cancel()
                isCoroutineActive = job.isActive
                progressBar!!.progress = 0

                Log.i("Cancel Button", " TEST : job Cancelling job")
                Log.i("Cancel Button", "TEST : job isActive = ${job.isActive}")

            }
        }


        // Log.i("MainActivity", "TEST : linkUrl = " + addressBar.text.toString())


        btnSave.setOnClickListener {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                createFile()
            } else {
                Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show()
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
            }

        }

        btnNext.setOnClickListener {
            Intent(this, SecondActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }


        //Log.i("MainActivity", " TEST : This Main thread is running in ${Thread.currentThread().name} thread ")


    }

    fun checkHeapSize(): Long {
        val usedMemInMB = (runtime.totalMemory() - runtime.freeMemory()) / 1048576L
        val maxHeapSizeInMB = runtime.maxMemory() / 1048576L
        return maxHeapSizeInMB - usedMemInMB
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            Toast.makeText(this, "Granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Denied", Toast.LENGTH_SHORT).show()
        }
    }


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

    /* @RequiresApi(Build.VERSION_CODES.KITKAT)
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
     }*/

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
                //Log.i("MainActivity", "TEST : data = ${data.getStringExtra(Intent.EXTRA_SUBJECT)}")
                //Log.i("MainActivity", "TEST : data = ${data.getStringExtra("body")}")
                //var d = data?.extras
                //var r = d?.get("body")
                //Log.i("MainActivity", "TEST : resultString = $r")
                writeToFile2(testList, uri!!)

            }
        }

        if (requestCode == 2) {
            var uri = data!!.data
            writeToFile(locationSourceCode.toString(), uri!!)

        }
    }

    private fun writeToFile(data: String, uri: Uri?) {
        try {
            //val outputStreamWriter = contentResolver.openOutputStream(uri)
            val outputStreamWriter = OutputStreamWriter(openFileOutput("PostUrlSource(No PostPage).txt", Context.MODE_PRIVATE))
            outputStreamWriter.write(data)
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

        try {
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
        } catch (e: ConcurrentModificationException) {
            e.printStackTrace()
            Log.i("MainActvity", "TEST : ConcurrentModificationException = $e")
        }


        sheet.setColumnWidth(0, 10 * 200)
        sheet.setColumnWidth(1, 10 * 200)


        try {
            val outputStreamWriter = contentResolver.openOutputStream(uri)
            wb.write(outputStreamWriter)
            //outputStreamWriter!!.close()
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {

            Log.e("Exception", "File write failed: $e")
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
        }
    }

//    var trying: String? = null
//    var doc: Document? = null
//    var scripts: Elements? = null
//    var s: String? = null

    fun getlocationSourcecode(url: String) {

        //Recycle the variable and remove/delete the previous allocated data

        ///var ThreadName = Thread.currentThread().name

        //Log.i("MainActivity + ${Thread.currentThread().name}", "TEST : getDatax  called ")
        //Log.i("MainActivity + ${Thread.currentThread().name}", " TEST : This asyncTask thread is running in ${Thread.currentThread().name} thread ")
        job = lifecycleScope.launch(Dispatchers.IO) {

            Log.i("lifecycleScope + ${Thread.currentThread().name}", "TEST : Job status at Start = ${job.isActive}")

            isCoroutineActive = isActive

            try {

                //To get the source code of location mumbai maharastra

                val doc = Jsoup.connect(url) //Recycle the variable and remove/delete the previous allocated data
                        .userAgent("Mozilla/5.0 (Linux; Android 7.0; SAMSUNG SM-G570Y Build/MMB29K) AppleWebKit/537.36 (KHTML, like Gecko) SamsungBrowser/4.0 Chrome/44.0.2403.133 Mobile Safari/537.36")
                        .headers(headersMap)
                        .ignoreHttpErrors(true)
                        .get()     // Network Request

                //Log.i("MainActivity + ${Thread.currentThread().name}"," response = ${doc.toString()}")

                if (doc != null) {
                    val scripts = doc.select("script[type=\"text/javascript\"]") //Recycle the variable and remove/delete the previous allocated data
                    val s = scripts!!.eq(3).toString() //Recycle the variable and remove/delete the previous allocated data
                    val start = s.indexOf("{")
                    val end = s.indexOf(";</script>")


                    if (s.contains(">window._sharedData = {\"config\"")) {

                        val trying = s.substring(start, end)

                        //To get the souce code for each of the post in top post
                        val rootObject = JSONObject(trying)
                        val entryObject = rootObject.getJSONObject("entry_data")
                        val pageArray = entryObject.getJSONArray(locationPageOrTagPage)
                        val pageObject = pageArray.getJSONObject(0)
                        val graphqlObject = pageObject.getJSONObject("graphql")
                        val locationObject = graphqlObject.getJSONObject(locationOrHashtag)

                        //This section is for top post
                        val top_postsObject = locationObject.getJSONObject(edge_hashtag_location_to_top_posts)
                        // int topPostCount = top_postsObject.getInt("count");
                        val topEdgeArray = top_postsObject.getJSONArray("edges")
                        val topPostCount = topEdgeArray.length()

                        //This loop is for top post
                        //loopThroughPosts(topPostCount-1,topEdgeArray, "TopPost");

                        //This section is for recent post
                        val recent_postsObject = locationObject.getJSONObject(edge_hashtag_location_to_media)
                        // int topPostCount = top_postsObject.getInt("count");
                        val recentEdgeArray = recent_postsObject.getJSONArray("edges")
                        val recentPostCount = recentEdgeArray.length() - 1


                        //This loop is for recent post
                        Log.i(TAG,"TEST : RecentPostCount for the loop = $recentPostCount")
                        loopThroughPosts(recentPostCount, recentEdgeArray, "RecentPost")

                    }

                }


                // trying = String.valueOf(doc);
                //Log.i("TAG", "TEST : trying = "+ trying);
            } catch (e: IOException) {
                e.printStackTrace()
                Log.i("lifecycleScope + ${Thread.currentThread().name}", "TEST : scripts = $e")
            } catch (e: JSONException) {
                e.printStackTrace()
                Log.i("lifecycleScope + ${Thread.currentThread().name}", "TEST : scripts = $e")
            } catch (e: OutOfMemoryError) {
                e.printStackTrace()
                Log.i("lifecycleScope + ${Thread.currentThread().name}", "TEST : OutOfMemoryError = $e")
                job.cancel()
                isCoroutineActive = job.isActive
                progressBar!!.progress = 0
                btnStart.isChecked = false
                Toast.makeText(applicationContext, "Out of Memory, Process Stopped!", Toast.LENGTH_SHORT).show()
            } catch (e: StringIndexOutOfBoundsException) {
                e.printStackTrace()
                Log.i("lifecycleScope  = ${Thread.currentThread().name}", "TEST : stringOutbound = $e")
            } catch (e: KotlinNullPointerException){
                e.printStackTrace()
            }
            Log.i("lifecycleScope + ${Thread.currentThread().name}", "TEST : work is finished before withContext")

            delay(50)

            withContext(Dispatchers.Main) {

                freeHeapView.setText(checkHeapSize().toString() + " MB")
                coroutineCount.setText((++coroutineCountVar).toString())
                //btnStart.isChecked = false
                Log.i("lifecycleScope:D.Main", "TEST : job isActive = ${job.isActive} ")
            }

            Log.i("lifecycleScope:D.IO", "TEST : work is finished after withContext")

        }

        if (btnStart.isChecked) {
            lifecycleScope.launch(Dispatchers.Default) {
                job.join()
                Log.i("${Thread.currentThread().name}", "TEST : job.isActive =  ${job.isActive}")

                withContext(Dispatchers.Main) {
                    //btnStart.isChecked = false
                    freeHeapView.setText(checkHeapSize().toString() + " MB")
                }
                withContext(Dispatchers.IO) {
                    Log.i("${Thread.currentThread().name}", "TEST : url passed = $url")
                    getlocationSourcecode(url)
                }
            }
        }

    }


    suspend fun loopThroughPosts(forloopCount: Int, jsonArray: JSONArray, postType: String) {


       /* for (i in 0..10) {
            delay(1000)
            Log.i(TAG, "TEST : Process Running")
        }*/

        var docProfile: Document?
        lateinit var docUsername:Document
        progressBar!!.max = (forloopCount * 2) - 1
        try {

            for (i in 0..forloopCount) {

                if (btnStart.isChecked) {
                    Log.i("${Thread.currentThread().name}", "TEST : job isCoroutineActive = $isCoroutineActive")
                    progressBar!!.progress = i * 2
                    // Log.i("$postType + Thread = ${Thread.currentThread().name}", "TEST : loopCount = $i")
                    // Log.i("$postType + Thread = ${Thread.currentThread().name}", "TEST : PostCount = $forloopCount")
                    //Log.i("MainActivity", "TEST : topPostCount22222 = " + topPostCount2);
                    val post = jsonArray.getJSONObject(i)
                    val node = post.getJSONObject("node")
                    val shortcode = node.getString("shortcode")
                    //shortcodeList!!.add(shortcode)

                    //Log.i("$postType + Thread = ${Thread.currentThread().name}", "TEST : shortcode = $shortcode")
                    // Log.i("$postType + Thread =  ${Thread.currentThread().name}", "TEST : size = " + shortcodeList!!.size)

                    //To get the profile url from the source code received using the shortcode of each post
                    val postUrl = "https://www.instagram.com/p/$shortcode"
                    Log.i("$postType + Thread = ${Thread.currentThread().name}", "TEST : postUrl = $postUrl")

                    docUsername = Jsoup.connect(postUrl)
                            .userAgent("Mozilla/5.0 (Linux; Android 7.0; SAMSUNG SM-G570Y Build/MMB29K) AppleWebKit/537.36 (KHTML, like Gecko) SamsungBrowser/4.0 Chrome/44.0.2403.133 Mobile Safari/537.36")
                            .headers(headersMap)
                            .ignoreHttpErrors(true)
                            .get()  // Network Request

                    var scriptsUsername: Elements?

                    if (docUsername != null) {

                        scriptsUsername = docUsername.select("script[type=\"text/javascript\"]")
                        var s2: String? = scriptsUsername!!.eq(3).toString()
                        val start2 = s2!!.indexOf("{")
                        val end2 = s2.indexOf(";</script>")

                        if (s2.contains("window._sharedData = {\"config\"")) {
                            //Log.i("$postType + Thread = ${Thread.currentThread().name}", "TEST :  start2 = $start2   end2 = $end2")
                            // Log.i("MainActivity", "TEST :  s2 = $s2")
                            var trying2: String? = s2.substring(start2, end2)
                            //Log.i("MainActivity", "TEST : trying2 = " + trying2)
                            val rootNameObject = JSONObject(trying2)
                            val usernameEntryObject = rootNameObject.getJSONObject("entry_data")
                            val usernamePostPageArray = usernameEntryObject.getJSONArray("PostPage")
                            val usernameEntryRootObject = usernamePostPageArray.getJSONObject(0)
                            val usernameGraphqlObject = usernameEntryRootObject.getJSONObject("graphql")
                            val usernameShortCodeObject = usernameGraphqlObject.getJSONObject("shortcode_media")
                            val usernameOwnerObject = usernameShortCodeObject.getJSONObject("owner")
                            val username = usernameOwnerObject.getString("username")
                            val profileUrl = "https://www.instagram.com/$username"
                           // mainProfileUrl!!.add(profileUrl)
                            Log.i("$postType + Thread = ${Thread.currentThread().name}", "TEST : profileUrl = $profileUrl")


                            //To get profile details like follower, postCount, following, bio etc and shortcodes for the posts


                            docProfile = Jsoup.connect(profileUrl)
                                    .userAgent("Mozilla/5.0 (Linux; Android 7.0; SAMSUNG SM-G570Y Build/MMB29K) AppleWebKit/537.36 (KHTML, like Gecko) SamsungBrowser/4.0 Chrome/44.0.2403.133 Mobile Safari/537.36")
                                    .headers(headersMap)
                                    .ignoreHttpErrors(true)
                                    .get()  // Network Request

                            if (docProfile != null) {

                                var scriptsProfile = docProfile.select("script[type=\"text/javascript\"]")

                                //Log.i("$postType + Thread = ${Thread.currentThread().name}", "TEST : scriptsProfile size = " + scriptsProfile.size)

                                var s3: String? = scriptsProfile.eq(3).toString()
                                val start3 = s3!!.indexOf("{")
                                val end3 = s3.indexOf(";</script>")

                                if (s3.contains("window._sharedData = {\"config\"")) {
                                    var trying3: String? = s3.substring(start3, end3)
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
                                    var accountData = arrayOf(fullName, userName, "$followerCount", "$followingCount", profileBio, profileWebsiteUrl, "$businessAccount", "$jointRecently", businessCategory, "$verifiedAccount")


                                    //test+=accountData
                                    //This array is required for writing file to xls format
                                    testList.add(accountData)

                                    counter++
                                    var emailHolderString = fullName + " " + userName + " " + profileBio + " " + profileWebsiteUrl
                                    var pattern = Pattern.compile("([a-zA-Z0-9._-]+@([a-zA-Z0-9_-]+\\.)+[a-zA-Z0-9_-]+)")
                                    var emailMatcher = pattern.matcher(emailHolderString)

                                    while (emailMatcher.find()) {
                                        email += emailMatcher.group() + " "
                                        emailCount++
                                        Log.i(TAG, "TEST : email ${emailMatcher.group()}")
                                    }

                                    logtext += "** Full Name : $fullName Username : $userName follower : $followerCount following : $followingCount " + "\n"


                                   /* docUsername = null
                                    scriptsUsername = null
                                    s2 = null
                                    trying2 = null
                                    scriptsProfile = null
                                    s3 = null
                                    docProfile = null
                                    trying3 = null*/


                                    if (modeselectorCounter != 0) {
                                        //To get the shortcode for the posts in the profile
                                        val postShortcodeDetailObject = userDetailObject.getJSONObject("edge_owner_to_timeline_media")
                                        val postCount = postShortcodeDetailObject.getInt("count")
                                        val postEdgeDetailArray = postShortcodeDetailObject.getJSONArray("edges")
                                        // Log.i("MainActivity", "TEST : postEdgeDetailArray = $followerCount   $followingCount    $fullName    $userName   $postCount   $profileBio   $profileWebsiteUrl   $businessAccount   $jointRecently   $businessCategory   $verifiedAccount")
                                        // Log.i("MainActivity", "TEST : postEdgeDetailArray = " + postEdgeDetailArray.length())

                                        if (postEdgeDetailArray.length() != 0) {
                                            var loopCount: Int
                                            if (postEdgeDetailArray.length() > 2) {
                                                loopCount = 2
                                            } else {
                                                loopCount = postEdgeDetailArray.length()
                                            }


                                            for (i2 in 0 until loopCount) {
                                                val profilePostEdgeObject = postEdgeDetailArray.getJSONObject(i2)
                                                val profileNodeObject = profilePostEdgeObject.getJSONObject("node")
                                                val profilePostShortcode = profileNodeObject.getString("shortcode")

                                                //val profilePostShortcodeList = ArrayList<String>()
                                                //profilePostShortcodeList.add(profilePostShortcode)

                                                // val profilePostAccessibility_caption = profileNodeObject.getString("accessibility_caption") // accessibility caption
                                                //val profilePostAccessibility_captionList = ArrayList<String>()
                                                //profilePostAccessibility_captionList.add(profilePostAccessibility_caption)
                                                //val items = ProfileItems(profilePostShortcodeList, profilePostAccessibility_captionList)
                                                // profileShortcodeList!!.add(items)
                                                //Log.i("MainActivity", "TEST : profileShortcodeList = " + profileShortcodeList.size());

                                                //To get the post caption and two comments
                                                val profilePostUrl = "https://www.instagram.com/p/$profilePostShortcode"

                                                var docProfilePost: Document?

                                                docProfilePost = Jsoup.connect(profilePostUrl)
                                                        .header("Cookie", "ZvcurrentVolume=100; zvAuth=1; zvLang=0; ZvcurrentVolume=100; notice=11")
                                                        .headers(headersMap)
                                                        .userAgent("Mozilla/5.0 (Linux; Android 7.0; SAMSUNG SM-G570Y Build/MMB29K) AppleWebKit/537.36 (KHTML, like Gecko) SamsungBrowser/4.0 Chrome/44.0.2403.133 Mobile Safari/537.36")
                                                        .ignoreHttpErrors(true)
                                                        .get()


                                                if (docProfilePost != null) {
                                                    val scriptsProfilePost = docProfilePost.select("script[type=\"text/javascript\"]")
                                                    //Log.i("MainActivity", "TEST : profilePostUrl = $profilePostUrl")
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
                                                        var accessibilityCaption: String? = null //accessibility caption
                                                        if (ppshortcode_mediaObject.has("accessibility_caption")) {
                                                            accessibilityCaption = ppshortcode_mediaObject.getString("accessibility_caption")
                                                        }
                                                        val ppProfilePostCaptionObject = ppshortcode_mediaObject.getJSONObject("edge_media_to_caption")
                                                        val ppProfilePostCaptionEdgeArray = ppProfilePostCaptionObject.getJSONArray("edges")
                                                        var ProfilePostCaption: String? = null //post caption
                                                        if (!ppProfilePostCaptionEdgeArray.isNull(0)) {
                                                            val ppProfilePostCaptionEdgeRootObject = ppProfilePostCaptionEdgeArray.getJSONObject(0)
                                                            val ppProfilePostCaptionNodeObject = ppProfilePostCaptionEdgeRootObject.getJSONObject("node")
                                                            ProfilePostCaption = ppProfilePostCaptionNodeObject.getString("text")
                                                        }
                                                        //Log.i("MainActivity", "TEST : ProfilePostCaption = $ProfilePostCaption  ******accessibilityCaption =  $accessibilityCaption")
                                                        if (modeselectorCounter == 2) {
                                                            val ppEdge_media_to_parent_commentObject = ppshortcode_mediaObject.getJSONObject("edge_media_to_parent_comment")
                                                            val ppCommentEdgesArray = ppEdge_media_to_parent_commentObject.getJSONArray("edges")
                                                            //Log.i("MainActivity", "TEST : ppCommentEdgesArray length = " + ppCommentEdgesArray.length())
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
                                                                    val ppcomment = ppCommentNodeObject.getString("text") // PostComments
                                                                    Log.i("MainActivity", "TEST : ppcomment = $ppcomment")


                                                                    logtext += "**AccessibilityCaption : $accessibilityCaption Post caption : $ProfilePostCaption Post Comments " + "\n\n\n"

                                                                    withContext(Dispatchers.Main) {

                                                                        logTextView.setText(logtext)

                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }


                                    withContext(Dispatchers.Main) {

                                        logTextView.setText(logtext)
                                        logTextView.setSelection(logTextView.length())
                                        scrapedCount.setText(counter.toString())
                                        emailViewObject.setText(email)
                                        emailViewObject.setSelection(emailViewObject.length())
                                        emailCountView.setText(emailCount.toString())
                                        freeHeapView.setText(checkHeapSize().toString() + " MB")

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

            //writeToFile(docUsername.toString(), null)
        } catch (e: NullPointerException) {
            e.printStackTrace()
            Log.i("$postType + Thread = ${Thread.currentThread().name}", "TEST : scripts = $e")
        } catch (e: StringIndexOutOfBoundsException) {
            e.printStackTrace()
            Log.i("$postType + Thread = ${Thread.currentThread().name}", "TEST : stringOutbound = $e")
        } catch (e: HttpStatusException) {
            e.printStackTrace()
            Log.i("$postType + Thread = ${Thread.currentThread().name}", "TEST : HttpStatusException = $e")
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
            Log.i("TAG + ${Thread.currentThread().name}", "TEST : OutOfMemoryError = $e")
            job.cancel()
            isCoroutineActive = job.isActive
            progressBar!!.progress = 0
            btnStart.isChecked = false
            Toast.makeText(this, "Out of Memory, Process Stopped!", Toast.LENGTH_SHORT).show()
        }

    }


}

