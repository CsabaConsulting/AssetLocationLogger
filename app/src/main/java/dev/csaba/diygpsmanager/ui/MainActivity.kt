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
import kotlinx.android.synthetic.main.activity_main.*
import dev.csaba.diygpsmanager.R
import dev.csaba.diygpsmanager.data.getSecondaryFirebaseConfiguration
import dev.csaba.diygpsmanager.ui.adapter.OnAssetClickListener
import dev.csaba.diygpsmanager.ui.adapter.AssetAdapter
import dev.csaba.diygpsmanager.viewmodel.MainViewModel


class MainActivity : AppCompatActivityWithActionBar(), OnAssetClickListener {

    companion object {
        private const val TAG = "FirestoreAssetRepo"
    }

    private lateinit var viewModel: MainViewModel
    private val assetAdapter = AssetAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recycler.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recycler.adapter = assetAdapter

        val projectConfiguration = this.getSecondaryFirebaseConfiguration()
        val options = FirebaseOptions.Builder()
            .setProjectId(projectConfiguration.projectId)
            .setApplicationId(projectConfiguration.applicationId)
            .setApiKey(projectConfiguration.apiKey)
            .build()

        // Initialize secondary FirebaseApp.
        Firebase.initialize(applicationContext, options, "secondary")
        val secondaryApp = Firebase.app("secondary")
        // Get FireStore for the other app.
        val secondaryDB = FirebaseFirestore.getInstance(secondaryApp).apply {
            firestoreSettings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build()
        }

        val auth = FirebaseAuth.getInstance(secondaryApp)
        if (auth.currentUser == null) {
            auth.signInAnonymously()
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "signInAnonymously:success")

                        viewModel = MainViewModel(secondaryDB)

                        viewModel.assetList.observe(this, Observer {
                            assetAdapter.setItems(it)
                        })

                        addAsset.setOnClickListener {
                            viewModel.addAsset(assetTitle.text.toString())
                            assetTitle.text?.clear()
                        }
                    } else {
                        Log.w(TAG, "signInAnonymously:failure", task.exception)
                        Toast.makeText(baseContext,
                            applicationContext.getString(R.string.authentication_failed),
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    override fun onDeleteClick(assetId: String) {
        viewModel.deleteAsset(assetId)
    }
}
