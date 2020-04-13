package dev.csaba.assetlocationlogger.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import dev.csaba.assetlocationlogger.data.Asset


class AssetDiffUtilCallback(
    private val oldList: List<Asset>,
    private val newList: List<Asset>
): DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) = oldList[oldItemPosition].id == newList[newItemPosition].id

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].title == newList[newItemPosition].title
    }
}
