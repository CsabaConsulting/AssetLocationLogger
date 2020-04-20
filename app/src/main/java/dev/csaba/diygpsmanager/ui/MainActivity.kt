package dev.csaba.diygpsmanager.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import com.google.firebase.ktx.initialize
import dev.csaba.diygpsmanager.ApplicationSingleton
import kotlinx.android.synthetic.main.activity_main.addAsset
import kotlinx.android.synthetic.main.activity_main.assetTitle
import kotlinx.android.synthetic.main.activity_main.recycler
import dev.csaba.diygpsmanager.R
import dev.csaba.diygpsmanager.data.getSecondaryFirebaseConfiguration
import dev.csaba.diygpsmanager.ui.adapter.OnAssetInputListener
import dev.csaba.diygpsmanager.ui.adapter.AssetAdapter
import dev.csaba.diygpsmanager.viewmodel.MainViewModel


class MainActivity : AppCompatActivityWithActionBar(), OnAssetInputListener {

    companion object {
        private const val TAG = "MainActivity"
        private const val SECONDARY_NAME = "secondary"
    }

    private lateinit var viewModel: MainViewModel
    private val assetAdapter = AssetAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recycler.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recycler.adapter = assetAdapter

        val appSingleton = application as ApplicationSingleton
        // Get or initialize secondary FirebaseApp.
        if (appSingleton.firebaseApp == null) {
            val projectConfiguration = this.getSecondaryFirebaseConfiguration()
            val options = FirebaseOptions.Builder()
                .setProjectId(projectConfiguration.projectId)
                .setApplicationId(projectConfiguration.applicationId)
                .setApiKey(projectConfiguration.apiKey)
                .build()

            Firebase.initialize(applicationContext, options, SECONDARY_NAME)
            appSingleton.firebaseApp = Firebase.app(SECONDARY_NAME)
        }

        // Get FireStore for the secondary app.
        if (appSingleton.firestore == null) {
            appSingleton.firestore = FirebaseFirestore.getInstance(appSingleton.firebaseApp!!).apply {
                firestoreSettings = FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(true)
                    .build()
            }
        }

        // Authenticate
        val auth = FirebaseAuth.getInstance(appSingleton.firebaseApp!!)
        if (auth.currentUser == null || auth.currentUser!!.uid.isBlank()) {
            auth.signInAnonymously()
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "signInAnonymously:success")
                        populateViewModel(appSingleton.firestore!!)
                    } else {
                        Log.w(TAG, "signInAnonymously:failure", task.exception)
                        Toast.makeText(baseContext,
                            applicationContext.getString(R.string.authentication_failed),
                            Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            populateViewModel(appSingleton.firestore!!)
        }
    }

    private fun populateViewModel(firestore: FirebaseFirestore) {
        viewModel = MainViewModel(firestore)

        viewModel.assetList.observe(this, Observer {
            assetAdapter.setItems(it)
        })

        addAsset.setOnClickListener {
            viewModel.addAsset(assetTitle.text.toString())
            assetTitle.text?.clear()
        }
    }

    override fun onTrackClick(assetId: String) {
        // TODO: navigate to Track Activity
    }

    override fun onLockUnlockClick(assetId: String) {
        viewModel.lockUnlockAsset(assetId)
    }

    override fun onDeleteClick(assetId: String) {
        viewModel.deleteAsset(assetId)
    }

    override fun onLockRadiusChange(assetId: String, lockRadius: Int) {
        viewModel.setAssetLockRadius(assetId, lockRadius)
    }

    override fun onPeriodIntervalChange(assetId: String, periodIntervalProgress: Int) {
        viewModel.setAssetPeriodInterval(assetId, periodIntervalProgress)
    }
}
