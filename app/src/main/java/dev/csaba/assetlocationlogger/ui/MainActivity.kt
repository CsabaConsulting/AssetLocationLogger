package dev.csaba.assetlocationlogger.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import dev.csaba.assetlocationlogger.R
import dev.csaba.assetlocationlogger.ui.adapter.OnAssetClickListener
import dev.csaba.assetlocationlogger.ui.adapter.AssetAdapter
import dev.csaba.assetlocationlogger.viewmodel.MainViewModel


class MainActivity : AppCompatActivity(), OnAssetClickListener {

    private lateinit var viewModel: MainViewModel
    private val assetAdapter = AssetAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recycler.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recycler.adapter = assetAdapter

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        viewModel.assetList.observe(this, Observer {
            assetAdapter.setItems(it)
        })

        addAsset.setOnClickListener {
            viewModel.addAsset(assetTitle.text.toString())
            assetTitle.text?.clear()
        }
    }

    override fun onDeleteClick(assetId: String) {
        viewModel.deleteAsset(assetId)
    }
}
