package com.wooooooak.lastcapture.ui.screenshots.adater

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.wooooooak.lastcapture.R
import com.wooooooak.lastcapture.data.model.ScreenShot
import com.wooooooak.lastcapture.databinding.ItemThumbnailBinding
import com.wooooooak.lastcapture.ui.screenshots.DetailScreenShotActivity
import com.wooooooak.lastcapture.ui.screenshots.ImageViewerViewModel
import wooooooak.dev.kcsimplealertview.woakalertview.SimpleAlertView
import java.io.File

class ScreenShotAdapter(
    private val activity: Activity,
    private val imageViewerViewModel: ImageViewerViewModel
) : ListAdapter<ScreenShot, ScreenShotAdapter.ViewHolder>(ScreenShotDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemThumbnailBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val screenShot = getItem(position)
        holder.apply {
            val (onClickListener, onLongClickListener) = createOnClickListener(screenShot)
            bind(onClickListener, onLongClickListener, screenShot)
            itemView.tag = screenShot
        }
    }

    private fun createOnClickListener(screenShot: ScreenShot): Pair<View.OnClickListener, View.OnLongClickListener> {
        val onClickListener = View.OnClickListener {
            val intent = Intent(activity, DetailScreenShotActivity::class.java).apply {
                putExtra(DetailScreenShotActivity.SHARED_FILE_URI, screenShot.uri)
            }
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                activity, it.findViewById(R.id.thumbnail), "screenShot"
            )
            activity.startActivity(intent, options.toBundle())
        }

        val onLongClickListener = View.OnLongClickListener {
            // TODO 권한을 얻어 MediaStore로 삭제해야함
//            showDeleteAlertView(screenShot, it)
            true
        }

        return Pair(onClickListener, onLongClickListener)
    }

    private fun showDeleteAlertView(file: File, view: View) {
        SimpleAlertView(activity as AppCompatActivity) {
            title {
                text = activity.resources.getString(R.string.ask_delete_title_1)
                textSize = activity.resources.getDimension(R.dimen.alert_view_title)
            }
            message {
                text = activity.resources.getString(R.string.ask_delete_title_2)
                textSize = activity.resources.getDimension(R.dimen.alert_view_message)
            }
            button {
                text = activity.resources.getString(R.string.cancel)
            }
            button {
                text = activity.resources.getString(R.string.delete)
                textColor = ContextCompat.getColor(activity, R.color.colorAccent)
                onClick = {
                    if (file.exists()) {
                        val isDeletedSuccessfully = file.delete()
                        if (isDeletedSuccessfully) {
                            imageViewerViewModel.refreshItem()
                            activity.baseContext.sendBroadcast(
                                Intent(
                                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)
                                )
                            )
                            Snackbar.make(
                                view, activity.resources.getString(R.string.delete_success),
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Snackbar.make(
                            view, activity.resources.getString(R.string.not_exist_file), Snackbar
                                .LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }.show()
    }


    inner class ViewHolder(
        val binding: ItemThumbnailBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            onClickListener: View.OnClickListener,
            onLongClickListener: View.OnLongClickListener,
            item: ScreenShot
        ) {
            binding.apply {
                screenShot = item
                this.onClickListener = onClickListener
                this.onLongClickListener = onLongClickListener
                executePendingBindings()
            }
        }
    }
}

private class ScreenShotDiffCallback : DiffUtil.ItemCallback<ScreenShot>() {
    override fun areItemsTheSame(oldItem: ScreenShot, newItem: ScreenShot) = oldItem == newItem

    override fun areContentsTheSame(oldItem: ScreenShot, newItem: ScreenShot) = oldItem == newItem
}