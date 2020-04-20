package dev.csaba.diygpsmanager.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.recycler_item.view.*
import dev.csaba.diygpsmanager.R
import dev.csaba.diygpsmanager.data.Asset
import java.text.SimpleDateFormat
import java.util.Locale


class AssetAdapter(private val assetClickListener: OnAssetClickListener?) : RecyclerView.Adapter<AssetViewHolder>() {

    private val assetList = emptyList<Asset>().toMutableList()
    private val createdFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item, parent, false)
        view.trackAsset.setOnClickListener { button -> assetClickListener?.run { this.onTrackClick(button.tag as String) } }
        view.lockUnlockAsset.setOnClickListener { button -> assetClickListener?.run { this.onLockUnlockClick(button.tag as String) } }
        view.deleteAsset.setOnClickListener { button -> assetClickListener?.run { this.onDeleteClick(button.tag as String) } }
        return AssetViewHolder(view)
    }

    override fun getItemCount() = assetList.size

    override fun onBindViewHolder(holder: AssetViewHolder, position: Int) {
        val asset = assetList[position]
        with(holder.containerView) {
            assetTitle.text = asset.title
            assetCreated.text = createdFormat.format(asset.created)
            assetUpdated.text = createdFormat.format(asset.updated)
            assetLockLat.text = asset.lockLat.toString()
            assetLockLon.text = asset.lockLon.toString()
//            assetLockRadius.val = asset.lockRadius
//            assetPeriodInterval.val = asset.periodInterval

            trackAsset.tag = assetList[position].id
            lockUnlockAsset.tag = assetList[position].id
            deleteAsset.tag = assetList[position].id
        }
    }

    fun setItems(newAssetList: List<Asset>) {
        val diffResult  = DiffUtil.calculateDiff(AssetDiffUtilCallback(assetList, newAssetList))

        assetList.clear()
        assetList.addAll(newAssetList)

        diffResult.dispatchUpdatesTo(this)
    }
}

class AssetViewHolder(override val containerView: View): RecyclerView.ViewHolder(containerView), LayoutContainer

interface OnAssetClickListener {
    fun onTrackClick(assetId: String)
    fun onLockUnlockClick(assetId: String)
    fun onDeleteClick(assetId: String)
}
