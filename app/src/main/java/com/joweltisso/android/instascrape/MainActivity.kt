package com.joweltisso.android.instascrape

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
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
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private val STORAGE_PERMISSION_CODE = 1
    private val SCRAPE_LIST_REQUEST_CODE = 1
    private val SCRAPE_EMAIL_LIST_REQUEST_CODE = 2
    private lateinit var progressBar: ProgressBar
    private lateinit var logTextView: EditText
    private var scrapedCounter: Int = 0
    private var IOexceptionCounter: Int = 0
    private var modeselectorCounter: Int = 0
    private var threadSelectorCounter: Int = 0
    private lateinit var btnModeSelector: Button
    private lateinit var scrapedCount: TextView
    private lateinit var emailCountView: TextView
    private lateinit var emailViewObject: EditText
    private lateinit var btnThreadSelector: Button
    private lateinit var stringColorSpan: SpannableStringBuilder
    private lateinit var countDownTimer: CountDownTimer
    private var freeHeapSize: Long = 0
    private var logTextViewLength: Int = 0
    private var emailTextViewLength: Int = 0

    private var isCoroutineActive: Boolean = false

    private var mUrl: String = "https://www.instagram.com/explore/tags/"
    private var titleList = arrayOf("Fullname", "Username", "Follower", "Following", "Bio", "Url", "Business account", "Joint recently", "Business Category", "Verified account")
    private var titleEmailList = arrayOf("Fullname", "Username", "Email", "Follower", "Following", "Bio", "Url", "Business account", "Joint recently", "Business Category", "Verified account")
    private var scrapedList = arrayListOf<Array<String>>()
    private var scrapedEmailList = arrayListOf<Array<String>>()
    private var logtext: String = ""
    private var email: String = ""
    private var emailCount: Int = 0
    private var threadStartCounter: Int = 0

    private val headersMap = mapOf(
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

    private val CREATE_SCRAPED_FILE = 1
    private val CREATE_SCRAPED_LIST_FILE = 2
    private var locationPageOrTagPage = "TagPage"
    private var locationOrHashtag = "hashtag"
    private var edge_hashtag_location_to_media = "edge_hashtag_to_media"

    private lateinit var runtime: Runtime
    private lateinit var maxHeapView: TextView
    private lateinit var allowedHeapView: TextView
    private lateinit var freeHeapView: TextView
    private lateinit var coroutineCount: TextView
    private var coroutineCountVar: Int = 0
    private lateinit var job: Job
    lateinit var btnStart: ToggleButton
    lateinit var timerView: TextView
    private var savedInstanceBundle: Bundle? = null

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //To apply theme on start of the activity
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.DarkTheme)
        } else {
            setTheme(R.style.LightTheme)
        }
        setContentView(R.layout.activity_main)

        //To check for savedInstanceState and restore the state available when switching between dayNaight mode
        if (savedInstanceState != null) {

            savedInstanceBundle = savedInstanceState
            with(savedInstanceState) {
                scrapedCounter = getInt("scrapedCount")
                emailCount = getInt("emailCount")
                coroutineCountVar = getInt("coroutineCount")
                freeHeapSize = getLong("freeHeapSize")
                logTextViewLength = getInt("logTextViewLength")
                emailTextViewLength = getInt("emailTextViewLength")
                threadStartCounter = getInt("threadStartCounter")
                email = getString("email")!!
                logtext = getString("stringColorSpan")!!

            }
        } else {

        }

        stringColorSpan = SpannableStringBuilder()
        logTextView = findViewById(R.id.logTextView)
        progressBar = findViewById(R.id.progress_horizontalX)

        //Clickable View Initiation
        val btnSave: Button = findViewById(R.id.btnSaveTask)
        val btnSaveEmail: Button = findViewById(R.id.btnSaveEmailTask)
        btnModeSelector = findViewById(R.id.btnModeSelector)
        btnStart = findViewById(R.id.btn_start)
        btnThreadSelector = findViewById(R.id.btnThreadSelector)
        val btnClearLog: TextView = findViewById(R.id.btnClearLog)
        val btnMenu: TextView = findViewById(R.id.btnMenu)
        val btnDayNightMode: LinearLayout = findViewById(R.id.btn_day_night_mode)

        //Hashtag View Initiation
        val tagViewObject: EditText = findViewById(R.id.hashtag1)
        val tagViewObject2: EditText = findViewById(R.id.hashtag2)
        val tagViewObject3: EditText = findViewById(R.id.hashtag3)
        val tagViewObject4: EditText = findViewById(R.id.hashtag4)
        val hashViewObject2: TextView = findViewById(R.id.hash2)
        val hashViewObject3: TextView = findViewById(R.id.hash3)
        val hashViewObject4: TextView = findViewById(R.id.hash4)

        scrapedCount = findViewById(R.id.scrapedCount)
        emailCountView = findViewById(R.id.emailCount)
        emailViewObject = findViewById(R.id.emailTextView)

        //Heap View Initiation
        maxHeapView = findViewById(R.id.maxHeapMemory)
        allowedHeapView = findViewById(R.id.allowedHeapMemory)
        freeHeapView = findViewById(R.id.freeHeapMemory)
        coroutineCount = findViewById(R.id.coroutineCounts)

        scrapedCount.text = scrapedCounter.toString()
        emailCountView.text = emailCount.toString()
        timerView = findViewById(R.id.timer)


        //To save the state of the theme
        val themeModePrefs: SharedPreferences = getSharedPreferences("themeModePrefs", Context.MODE_PRIVATE)
        val sharedPrefsEdit: SharedPreferences.Editor = themeModePrefs.edit()
        val isNightModeOn: Boolean = themeModePrefs.getBoolean("NightMode", false)

        if (isNightModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }

        scrapedList.add(titleList)
        scrapedEmailList.add(titleEmailList)

        //To display maximum heap memory
        runtime = Runtime.getRuntime()
        val maxHeapSizeInMB = runtime.maxMemory() / 1048576L
        maxHeapView.text = maxHeapSizeInMB.toString() + " MB"

        //To display allowed heap memory
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryClass = activityManager.memoryClass
        allowedHeapView.text = memoryClass.toString() + " MB"

        //To display to free heap memory
        freeHeapView.text = checkFreeHeapSize().toString() + " MB"

        //Day & Night mode Click event
        btnDayNightMode.setOnClickListener {
            if (isNightModeOn) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                sharedPrefsEdit.putBoolean("NightMode", false) //To save theme state
                sharedPrefsEdit.apply()
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                sharedPrefsEdit.putBoolean("NightMode", true) //To save theme state
                sharedPrefsEdit.apply()
            }
            //finish()
            // overridePendingTransition(0, 0) //To remove default transition animation between activities
            //startActivity(Intent(this, this.javaClass))
            this.recreate()
        }

        //Touch event for Thread selector button
        btnThreadSelector.setOnTouchListener { _, event ->
            when (event.action) {

                MotionEvent.ACTION_DOWN -> {
                    //To change color when button is pressed
                    btnThreadSelector.setBackgroundResource(R.drawable.btn_background)
                }
                MotionEvent.ACTION_UP -> {
                    //To change color when button is released
                    btnThreadSelector.setBackgroundColor(resources.getColor(android.R.color.transparent))
                    //To check if the start button is still on and the process is still running
                    if (btnStart.isChecked) {
                        Toast.makeText(this, "Process still running!", Toast.LENGTH_SHORT).show()
                    } else {
                        //To hide and unhide the hashtag views according to the thread selected
                        threadSelectorCounter++
                        if (threadSelectorCounter > 3) {
                            threadSelectorCounter = 0
                        }
                        when (threadSelectorCounter) {
                            0 -> {
                                btnThreadSelector.text = "Thread: 1"
                                tagViewObject2.visibility = View.GONE
                                tagViewObject3.visibility = View.GONE
                                tagViewObject4.visibility = View.GONE
                                hashViewObject2.visibility = View.GONE
                                hashViewObject3.visibility = View.GONE
                                hashViewObject4.visibility = View.GONE
                            }
                            1 -> {
                                btnThreadSelector.text = "Thread: 2"
                                tagViewObject2.visibility = View.VISIBLE
                                tagViewObject3.visibility = View.GONE
                                tagViewObject4.visibility = View.GONE
                                hashViewObject2.visibility = View.VISIBLE
                                hashViewObject3.visibility = View.GONE
                                hashViewObject4.visibility = View.GONE
                            }
                            2 -> {
                                btnThreadSelector.text = "Thread: 3"
                                tagViewObject3.visibility = View.VISIBLE
                                tagViewObject4.visibility = View.GONE
                                hashViewObject3.visibility = View.VISIBLE
                                hashViewObject4.visibility = View.GONE
                            }
                            3 -> {
                                btnThreadSelector.text = "Thread: 4"
                                tagViewObject4.visibility = View.VISIBLE
                                hashViewObject4.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }
            true
        }

        //Touch event for Mode selector button
        btnModeSelector.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    //To change color when button is pressed
                    btnModeSelector.setBackgroundResource(R.drawable.btn_background2)
                }
                MotionEvent.ACTION_UP -> {
                    btnModeSelector.setBackgroundColor(resources.getColor(android.R.color.transparent))
                    if (btnStart.isChecked) { // To check if the start button is on
                        Toast.makeText(this, "Process still running!", Toast.LENGTH_SHORT).show()
                    } else {
                        //To change the value of modeselectorCounter and change the text in button accordingly

                        modeselectorCounter++
                        if (modeselectorCounter > 2) {
                            modeselectorCounter = 0
                        }
                        when (modeselectorCounter) {
                            0 -> btnModeSelector.text = "Lite"
                            1 -> btnModeSelector.text = "Medium"
                            2 -> btnModeSelector.text = "Heavy"
                            else -> btnModeSelector.text = "Lite"
                        }
                    }
                }
            }
            true
        }

        //checked event for Start button
        btnStart.setOnCheckedChangeListener { _, isChecked ->
            //Check if network is connected then perform action
            if (checkNetworkConnection(this)) {
                if (isChecked) {
                    btnStart.setBackgroundResource(R.drawable.btn_background2)
                    val tag: String
                    val tag2: String
                    val tag3: String
                    val tag4: String

                    //Check if the hashtags fields are not empty before performing an operation
                    if (tagViewObject.length() != 0 && tagViewObject2.length() != 0 && tagViewObject3.length() != 0 && tagViewObject4.length() != 0) {
                        tag = tagViewObject.text.toString()
                        tag2 = tagViewObject2.text.toString()
                        tag3 = tagViewObject3.text.toString()
                        tag4 = tagViewObject4.text.toString()

                        countDownTimer() //5 min timer for limiting scraping at one interval

                        progressBar.visibility = View.VISIBLE

                        getlocationSourcecode(mUrl + tag) //Perform the scraping operation with the provided hashtags

                        // To perform multiple scraping operations according to the threads selected
                        if (threadSelectorCounter == 1 || threadSelectorCounter == 2 || threadSelectorCounter == 3) {
                            getlocationSourcecode(mUrl + tag2)
                        }
                        if (threadSelectorCounter == 2 || threadSelectorCounter == 3) {
                            getlocationSourcecode(mUrl + tag3)
                        }
                        if (threadSelectorCounter == 3) {
                            getlocationSourcecode(mUrl + tag4)
                        }
                    } else {
                        Toast.makeText(this, "Input Hashtag", Toast.LENGTH_SHORT).show()
                        btnStart.isChecked = false
                    }
                } else {
                    //To cancel all the operations when start button is turned off
                    btnStart.setBackgroundColor(resources.getColor(android.R.color.transparent))
                    job.cancel() //To cancel the coroutines
                    isCoroutineActive = job.isActive // assigning boolean according to the job coroutine status
                    progressBar.visibility = View.INVISIBLE
                    if (countDownTimer != null) {
                        countDownTimer.cancel() // To cancel the count down time whenever the start button is turned off
                        timerView.text = "" //To reset the timer view field
                    }
                }
            } else {
                btnStart.isChecked = false
                Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show()
            }
        }

        //Touch event for Menu button
        btnMenu.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    btnMenu.setTextColor(resources.getColor(R.color.colorAccent))
                }
                MotionEvent.ACTION_UP -> {
                    btnMenu.setTextColor(resources.getColor(R.color.dark_green))
                    startActivity(Intent(this, MenuActivity::class.java))
                }
            }
            true
        }

        //Touch event for Save data button
        btnSave.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    btnSave.setBackgroundResource(R.drawable.btn_background2)
                }
                MotionEvent.ACTION_UP -> {
                    btnSave.setBackgroundColor(resources.getColor(android.R.color.transparent))
                    // To check if the storage permission has been granted or not
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        if (btnStart.isChecked) { // To check if the start button is on
                            Toast.makeText(this, "Process still running!", Toast.LENGTH_SHORT).show()
                        } else {
                            //To call the method for saving scraped data
                            createFile(CREATE_SCRAPED_FILE, "Instagram_scraped_data.xls")
                        }
                    } else {
                        //If storage permission not granted then ask for permission
                        Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show()
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
                    }
                }
            }
            true
        }

        //Touch event for Save email data button
        btnSaveEmail.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    btnSaveEmail.setBackgroundResource(R.drawable.btn_background)
                }
                MotionEvent.ACTION_UP -> {
                    btnSaveEmail.setBackgroundColor(resources.getColor(android.R.color.transparent))
                    // To check if the storage permission has been granted or not
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        if (btnStart.isChecked) {
                            Toast.makeText(this, "Process still running!", Toast.LENGTH_SHORT).show()
                        } else {
                            //To call the method for saving scraped data
                            createFile(CREATE_SCRAPED_LIST_FILE, "Instagram_scraped_email_data.xls")
                        }
                    } else {
                        //If storage permission not granted then ask for permission
                        Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show()
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
                    }
                }
            }
            true
        }

        //Touch event for Clear log button
        btnClearLog.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    btnClearLog.setTextColor(resources.getColor(R.color.colorAccent))
                }
                MotionEvent.ACTION_UP -> {
                    //To clear the log view and the spannableStringBuilder instance where the colored strings are stored
                    btnClearLog.setTextColor(resources.getColor(R.color.pink))
                    logtext = ""
                    stringColorSpan.clear()
                    stringColorSpan.clearSpans()
                    logTextView.setText("")
                    email = ""
                    emailViewObject.setText("")
                    threadStartCounter = 0
                    logTextViewLength = 0
                    scrapedCounter = 0
                    scrapedCount.text = "0"
                    emailCount = 0
                    emailCountView.text = "0"
                    coroutineCountVar = 0
                    coroutineCount.text = "0"
                }
            }
            true
        }

    }

    //Count Down timer method
    private fun countDownTimer() {
        //Creating an instance of CountDownTimer and storing in a countDownTimer variable
        //To start the countdown with 5mins
        countDownTimer = object : CountDownTimer(300000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timerView.text = "" + String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)))
            }

            override fun onFinish() {
                timerView.text = ""
                btnStart.isChecked = false
            }
        }.start()

    }

    //Method to check free heap memory avaible for the device
    private fun checkFreeHeapSize(): Long {
        val usedMemInMB = (runtime.totalMemory() - runtime.freeMemory()) / 1048576L
        val maxHeapSizeInMB = runtime.maxMemory() / 1048576L
        return maxHeapSizeInMB - usedMemInMB
    }

    //To show Notification/Toast after storage permission request
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            Toast.makeText(this, "Granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Denied", Toast.LENGTH_SHORT).show()
        }
    }

    //Method to create a file for xls format
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun createFile(requestCode: Int, fileName: String) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/vnd.ms-excel\",\"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            putExtra(Intent.EXTRA_TITLE, fileName)
            // Optionally, specify a URI for the directory that should be opened in
            // the system file picker before your app creates the document.
            //putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
        }
        startActivityForResult(intent, requestCode)
    }

    //To write data in the file after the xls file has been created according to the request code
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }
        if (requestCode == SCRAPE_LIST_REQUEST_CODE) { //To save the rough data in the file
            if (data != null) {
                val uri = data.data
                writeToFile(scrapedList, uri!!)
            }
        }
        if (requestCode == SCRAPE_EMAIL_LIST_REQUEST_CODE) { //To save the email contained data
            if (data != null) {
                val uri = data.data
                writeToFile(scrapedEmailList, uri!!)
            }
        }
    }

    private fun writeToFile(data: ArrayList<Array<String>>, uri: Uri) {

        //Creating a workbook
        val workbook: Workbook = HSSFWorkbook()
        var cell: Cell?

        //Creating a sheet
        val sheet: Sheet?
        sheet = workbook.createSheet("Instagram Data")

        //Creating a column and row
        try {
            for (a in data) {
                val row: Row = sheet.createRow(data.indexOf(a))

                for (i in a) {
                    cell = row.createCell(a.indexOf(i))
                    cell.setCellValue(i)
                }
            }
        } catch (e: ConcurrentModificationException) {
            e.printStackTrace()
        }
        //Setting the column width
        sheet.setColumnWidth(0, 10 * 200)  //Test this after a while
        sheet.setColumnWidth(1, 10 * 200)  //Test this after a while

        //To write the data in the workbook using outputStreamWriter
        try {
            val outputStreamWriter = contentResolver.openOutputStream(uri)
            workbook.write(outputStreamWriter)
            //outputStreamWriter!!.close()
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
        }
    }

    //Method to check the active network connection
    private fun checkNetworkConnection(context: Context): Boolean {
        if (context == null) return false

        val connectivityManager: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val network: Network? = connectivityManager.activeNetwork
                if (network != null) {
                    val networkCapabilities: NetworkCapabilities? = connectivityManager.getNetworkCapabilities(network)
                    if (networkCapabilities != null) {
                        return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    }
                }
            } else {
                val networkInfos = connectivityManager.allNetworkInfo
                for (tempNetworkInfo in networkInfos) {
                    if (tempNetworkInfo.isConnected) {
                        return true
                    }
                }
            }
        }
        return false
    }

    //Method to intiate the scraping process using the url passed
    private fun getlocationSourcecode(url: String) {

        //initiating the method of scraping in the lifecycleScope coroutine
        // and storing it in a job variable to be able to cancel the coroutine
        job = lifecycleScope.launch(Dispatchers.IO) {

            isCoroutineActive = isActive
            //To store the string in the spannableStringBuilder instance which is stringColorSpan
            if (threadStartCounter == 0) {
                logtext = "${Thread.currentThread().name} : Thread started = $isActive \n"
                stringColorSpan.append(logtext)
                threadStartCounter = 1

            }

            if (savedInstanceBundle == null) {    //To check for savedInstanceBundle to prevent overwriting logtext when switching between dayNight mode
                logtext = "${Thread.currentThread().name} : Thread started = $isActive \n"
                stringColorSpan.append(logtext)

            } else {

                savedInstanceBundle = null
            }

            try {

                //To get the source code of given url assotiated with the given hashtag
                val doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Linux; Android 7.0; SAMSUNG SM-G570Y Build/MMB29K)" +
                                " AppleWebKit/537.36 (KHTML, like Gecko) SamsungBrowser/4.0 Chrome/44.0.2403.133 Mobile Safari/537.36")
                        .headers(headersMap)
                        .ignoreHttpErrors(true) //To prevent the scraping from stopping and moving on to the next link if one link doesnot work
                        .get()     // Network Request

                if (doc != null) {
                    val scripts = doc.select("script[type=\"text/javascript\"]")
                    val s = scripts!!.eq(3).toString()
                    val start = s.indexOf("{")
                    val end = s.indexOf(";</script>")


                    if (s.contains(">window._sharedData = {\"config\"")) {

                        val trying = s.substring(start, end)

                        //Parsing json data
                        val rootObject = JSONObject(trying)
                        val entryObject = rootObject.getJSONObject("entry_data")
                        val pageArray = entryObject.getJSONArray(locationPageOrTagPage)
                        val pageObject = pageArray.getJSONObject(0)
                        val graphqlObject = pageObject.getJSONObject("graphql")
                        val locationObject = graphqlObject.getJSONObject(locationOrHashtag)

                        //This section is for recent post
                        val recentPostsObject = locationObject.getJSONObject(edge_hashtag_location_to_media)
                        val recentEdgeArray = recentPostsObject.getJSONArray("edges")
                        val recentPostCount = recentEdgeArray.length() - 1

                        //This loop is for recent post
                        loopThroughPosts(1, recentEdgeArray)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                //If there is any kind of IOException
                //Stop the scraping process after encountering 5 exceptions
                IOexceptionCounter++
                if (IOexceptionCounter == 5) {
                    IOexceptionCounter = 0
                    withContext(Dispatchers.Main) {
                        btnStart.isChecked = false
                        Toast.makeText(applicationContext, "Failed to fetch the data!", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()

            } catch (e: OutOfMemoryError) {
                e.printStackTrace()
                //To stop the scraping process if there is OutOfMemoryError
                job.cancel()
                isCoroutineActive = job.isActive
                progressBar.visibility = View.INVISIBLE
                btnStart.isChecked = false
                Toast.makeText(applicationContext, "Out of Memory, Process Stopped!", Toast.LENGTH_SHORT).show()
            } catch (e: StringIndexOutOfBoundsException) {
                e.printStackTrace()

            } catch (e: KotlinNullPointerException) {
                e.printStackTrace()
            }

            //Delay between each recursive calls
            delay(100)

            //To perform action in the Main Thread
            withContext(Dispatchers.Main) {
                freeHeapSize = checkFreeHeapSize()
                freeHeapView.text = freeHeapSize.toString() + " MB"
                coroutineCount.text = (++coroutineCountVar).toString()

            }
        }

        //To check if the start button is still on to perform the recursive operation
        if (btnStart.isChecked) {
            lifecycleScope.launch(Dispatchers.Default) {
                job.join()
                withContext(Dispatchers.Main) {
                    //Updating the freeHeapSize in every operation
                    freeHeapView.text = checkFreeHeapSize().toString() + " MB"

                }
                withContext(Dispatchers.IO) {
                    //Calling the scraping function
                    getlocationSourcecode(url)
                }
            }
        } else {
            //To update the logView if the Start button is turned off and the process is stopped
            runBlocking(Dispatchers.Main) {
                stringColorSpan.append("Thread stopped\n")
                val p = Pattern.compile("Thread stopped")
                val m = p.matcher(stringColorSpan)
                while (m.find()) {
                    stringColorSpan.setSpan(ForegroundColorSpan(resources.getColor(R.color.pink2)), m.start(), m.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                }
                colorString()
                logTextView.text = stringColorSpan
                logTextViewLength = logTextView.length()
                logTextView.setSelection(logTextViewLength)
            }

        }

    }

    //Also a part of scraping method
    private suspend fun loopThroughPosts(forloopCount: Int, jsonArray: JSONArray) {

        var docProfile: Document?
        lateinit var docUsername: Document

        try {
            //To scrape through all the number of post acquired at one search which is approx 24 profiles
            for (i in 0..forloopCount) {
                if (btnStart.isChecked) {

                    val post = jsonArray.getJSONObject(i)
                    val node = post.getJSONObject("node")
                    val shortcode = node.getString("shortcode")

                    //To get the profile url from the source code received using the shortcode of each post
                    val postUrl = "https://www.instagram.com/p/$shortcode"

                    docUsername = Jsoup.connect(postUrl)
                            .userAgent("Mozilla/5.0 (Linux; Android 7.0; SAMSUNG SM-G570Y Build/MMB29K) AppleWebKit/537.36 (KHTML, like Gecko) SamsungBrowser/4.0 Chrome/44.0.2403.133 Mobile Safari/537.36")
                            .headers(headersMap)
                            .ignoreHttpErrors(true)
                            .get()  // Network Request

                    var scriptsUsername: Elements?

                    if (docUsername != null) {

                        scriptsUsername = docUsername.select("script[type=\"text/javascript\"]")
                        val s2: String? = scriptsUsername!!.eq(3).toString()
                        val start2 = s2!!.indexOf("{")
                        val end2 = s2.indexOf(";</script>")

                        if (s2.contains("window._sharedData = {\"config\"")) {

                            val trying2: String? = s2.substring(start2, end2)

                            val rootNameObject = JSONObject(trying2)
                            val usernameEntryObject = rootNameObject.getJSONObject("entry_data")
                            val usernamePostPageArray = usernameEntryObject.getJSONArray("PostPage")
                            val usernameEntryRootObject = usernamePostPageArray.getJSONObject(0)
                            val usernameGraphqlObject = usernameEntryRootObject.getJSONObject("graphql")
                            val usernameShortCodeObject = usernameGraphqlObject.getJSONObject("shortcode_media")
                            val usernameOwnerObject = usernameShortCodeObject.getJSONObject("owner")
                            val username = usernameOwnerObject.getString("username")
                            val profileUrl = "https://www.instagram.com/$username"

                            //To get profile details like follower, postCount, following, bio etc and shortcodes for the posts

                            docProfile = Jsoup.connect(profileUrl)
                                    .userAgent("Mozilla/5.0 (Linux; Android 7.0; SAMSUNG SM-G570Y Build/MMB29K) AppleWebKit/537.36 (KHTML, like Gecko) SamsungBrowser/4.0 Chrome/44.0.2403.133 Mobile Safari/537.36")
                                    .headers(headersMap)
                                    .ignoreHttpErrors(true)
                                    .get()  // Network Request

                            if (docProfile != null) {

                                val scriptsProfile = docProfile.select("script[type=\"text/javascript\"]")

                                val s3: String? = scriptsProfile.eq(3).toString()
                                val start3 = s3!!.indexOf("{")
                                val end3 = s3.indexOf(";</script>")

                                if (s3.contains("window._sharedData = {\"config\"")) {
                                    val trying3: String? = s3.substring(start3, end3)
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

                                    //Storing an array of data scraped from one profile and storing it in a variable
                                    val accountData = arrayOf(fullName, userName, "$followerCount", "$followingCount",
                                            profileBio, profileWebsiteUrl, "$businessAccount",
                                            "$jointRecently", businessCategory, "$verifiedAccount")

                                    //The array is then stored in an arrayList for final output
                                    //This arrayList is required for writing file to xls format
                                    scrapedList.add(accountData)

                                    //To search for emails in the scraped data using Regex
                                    scrapedCounter++           //For counting number of emails found
                                    val emailHolderString = "$fullName $userName $profileBio $profileWebsiteUrl"
                                    val pattern = Pattern.compile("([a-zA-Z0-9._-]+@([a-zA-Z0-9_-]+\\.)+[a-zA-Z0-9_-]+)")
                                    val emailMatcher = pattern.matcher(emailHolderString)

                                    //To add data to the arrayList when email is found
                                    while (emailMatcher.find()) {
                                        email += emailMatcher.group() + " "
                                        emailCount++
                                        val accountEmailData = arrayOf(fullName, userName, emailMatcher.group(), "$followerCount", "$followingCount", profileBio, profileWebsiteUrl, "$businessAccount", "$jointRecently", businessCategory, "$verifiedAccount")
                                        scrapedEmailList.add(accountEmailData)
                                    }

                                    // To add color to the string and display it in the logView
                                    logtext = "Fullname : $fullName Username : $userName Follower : $followerCount Following : $followingCount Url : $profileWebsiteUrl Bio : $profileBio \n"

                                    stringColorSpan.append(logtext)
                                    colorString()
                                    //To perform extensive scraping operation acccording to the mode seclected Medium/Heavy
                                    if (modeselectorCounter != 0) {
                                        //To get the shortcode for the posts in the profile
                                        val postShortcodeDetailObject = userDetailObject.getJSONObject("edge_owner_to_timeline_media")
                                        val postCount = postShortcodeDetailObject.getInt("count")
                                        val postEdgeDetailArray = postShortcodeDetailObject.getJSONArray("edges")

                                        if (postEdgeDetailArray.length() != 0) {
                                            var loopCount: Int = if (postEdgeDetailArray.length() > 2) {
                                                2
                                            } else {
                                                postEdgeDetailArray.length()
                                            }

                                            for (i2 in 0 until loopCount) {
                                                val profilePostEdgeObject = postEdgeDetailArray.getJSONObject(i2)
                                                val profileNodeObject = profilePostEdgeObject.getJSONObject("node")
                                                val profilePostShortcode = profileNodeObject.getString("shortcode")

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
                                                    //
                                                    val sPP = scriptsProfilePost.eq(3).toString()
                                                    val startPP = sPP.indexOf("{")
                                                    val endPP = sPP.indexOf(";</script>")

                                                    if (sPP.contains("window._sharedData = {\"config\"")) {
                                                        val tryingPP = sPP.substring(startPP, endPP)
                                                        //
                                                        val rootProfilePostObject = JSONObject(tryingPP)
                                                        val ppEntryData = rootProfilePostObject.getJSONObject("entry_data")
                                                        val ppPostPageArray = ppEntryData.getJSONArray("PostPage")
                                                        val rootPostPageObject = ppPostPageArray.getJSONObject(0)
                                                        val ppgraphqlObject = rootPostPageObject.getJSONObject("graphql")
                                                        val ppshortcodeMediaobject = ppgraphqlObject.getJSONObject("shortcode_media")
                                                        var accessibilityCaption: String? = null //accessibility caption
                                                        if (ppshortcodeMediaobject.has("accessibility_caption")) {
                                                            accessibilityCaption = ppshortcodeMediaobject.getString("accessibility_caption")
                                                        }
                                                        val ppProfilePostCaptionObject = ppshortcodeMediaobject.getJSONObject("edge_media_to_caption")
                                                        val ppProfilePostCaptionEdgeArray = ppProfilePostCaptionObject.getJSONArray("edges")
                                                        var profilePostCaption: String? = null //post caption
                                                        if (!ppProfilePostCaptionEdgeArray.isNull(0)) {
                                                            val ppProfilePostCaptionEdgeRootObject = ppProfilePostCaptionEdgeArray.getJSONObject(0)
                                                            val ppProfilePostCaptionNodeObject = ppProfilePostCaptionEdgeRootObject.getJSONObject("node")
                                                            profilePostCaption = ppProfilePostCaptionNodeObject.getString("text")
                                                        }

                                                        if (modeselectorCounter == 2) {
                                                            val ppedgeMediaToParentCommentobject = ppshortcodeMediaobject.getJSONObject("edge_media_to_parent_comment")
                                                            val ppCommentEdgesArray = ppedgeMediaToParentCommentobject.getJSONArray("edges")

                                                            if (ppCommentEdgesArray.length() != 0) {
                                                                var loopCount2: Int = if (ppCommentEdgesArray.length() > 2) {
                                                                    2
                                                                } else {
                                                                    ppCommentEdgesArray.length()
                                                                }
                                                                for (ic in 0 until loopCount2) {
                                                                    val ppCommentObject = ppCommentEdgesArray.getJSONObject(ic)
                                                                    val ppCommentNodeObject = ppCommentObject.getJSONObject("node")
                                                                    val ppcomment = ppCommentNodeObject.getString("text") // PostComments

                                                                    logtext = "AccessibilityCaption : $accessibilityCaption Post caption : $profilePostCaption Post Comments $ppcomment\n\n\n"

                                                                    withContext(Dispatchers.Main) {
                                                                        stringColorSpan.append(logtext)
                                                                        logTextView.text = stringColorSpan

                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    //To update the ui with data
                                    withContext(Dispatchers.Main) {
                                        logTextView.text = stringColorSpan
                                        logTextViewLength = logTextView.length()
                                        logTextView.setSelection(logTextView.length())

                                        scrapedCount.text = scrapedCounter.toString()
                                        emailViewObject.setText(email)
                                        emailTextViewLength = emailViewObject.length()
                                        emailViewObject.setSelection(emailTextViewLength)
                                        emailCountView.text = emailCount.toString()
                                        freeHeapView.text = checkFreeHeapSize().toString() + " MB"

                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: JSONException) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                logtext = "**Unable to scrape data due to JSONException\n"
                stringColorSpan.append(logtext)
                logTextView.text = stringColorSpan
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()

        } catch (e: StringIndexOutOfBoundsException) {
            e.printStackTrace()

        } catch (e: HttpStatusException) {
            e.printStackTrace()

        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
            //To cancel the scraping operation if OutOfMemoryError occurs
            job.cancel()
            isCoroutineActive = job.isActive
            progressBar.visibility = View.INVISIBLE
            btnStart.isChecked = false
            Toast.makeText(this, "Out of Memory, Process Stopped!", Toast.LENGTH_SHORT).show()
        } catch (e: ArrayIndexOutOfBoundsException) {
            e.printStackTrace()
        }

    }

    private fun colorString() {
        val p1 = Pattern.compile("Fullname")
        val m1 = p1.matcher(stringColorSpan)
        val p2 = Pattern.compile("Username")
        val m2 = p2.matcher(stringColorSpan)
        val p3 = Pattern.compile("Follower")
        val m3 = p3.matcher(stringColorSpan)
        val p4 = Pattern.compile("Following")
        val m4 = p4.matcher(stringColorSpan)
        val p5 = Pattern.compile("Url")
        val m5 = p5.matcher(stringColorSpan)
        val p6 = Pattern.compile("Bio")
        val m6 = p6.matcher(stringColorSpan)
        val p7 = Pattern.compile("DefaultDispatcher-worker-")
        val m7 = p7.matcher(stringColorSpan)
        val p8 = Pattern.compile("true")
        val m8 = p8.matcher(stringColorSpan)
        while (m7.find()) {
            stringColorSpan.setSpan(ForegroundColorSpan(resources.getColor(R.color.blue)), m7.start(), m7.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        }
        while (m8.find()) {
            stringColorSpan.setSpan(ForegroundColorSpan(resources.getColor(R.color.green)), m8.start(), m8.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        }

        while (m1.find() && m2.find() && m3.find() && m4.find() && m5.find() && m6.find()) {
            stringColorSpan.setSpan(ForegroundColorSpan(resources.getColor(R.color.purple)), m1.start(), m1.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            stringColorSpan.setSpan(ForegroundColorSpan(resources.getColor(R.color.purple)), m2.start(), m2.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            stringColorSpan.setSpan(ForegroundColorSpan(resources.getColor(R.color.purple)), m3.start(), m3.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            stringColorSpan.setSpan(ForegroundColorSpan(resources.getColor(R.color.purple)), m4.start(), m4.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            stringColorSpan.setSpan(ForegroundColorSpan(resources.getColor(R.color.purple)), m5.start(), m5.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            stringColorSpan.setSpan(ForegroundColorSpan(resources.getColor(R.color.purple)), m6.start(), m6.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        }
    }

    //To restore the available data when switching between dayNight mode
    override fun onResume() {
        super.onResume()
        stringColorSpan.append(logtext)
        colorString()
        logTextView.text = stringColorSpan
        emailViewObject.setText(email)
        if (!btnStart.isChecked) {
            logTextView.setSelection(logTextViewLength)
            emailViewObject.setSelection(emailTextViewLength)
        }
        freeHeapView.text = freeHeapSize.toString() + " MB"
        coroutineCount.text = (coroutineCountVar).toString()
        scrapedCount.text = scrapedCounter.toString()
        emailCountView.text = emailCount.toString()


    }

    //To save state when switching between dayNight mode
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("scrapedCount", scrapedCounter) // Working
        outState.putInt("emailCount", emailCount) // Working
        outState.putInt("coroutineCount", coroutineCountVar) // Working
        outState.putInt("logTextViewLength", logTextViewLength) // Working
        outState.putInt("emailTextViewLength", emailTextViewLength)
        outState.putInt("threadStartCounter", threadStartCounter)// Working
        outState.putLong("freeHeapSize", freeHeapSize) // Working
        outState.putString("stringColorSpan", stringColorSpan.toString()) // Working
        outState.putString("email", email) // Working

        super.onSaveInstanceState(outState)
    }
}

