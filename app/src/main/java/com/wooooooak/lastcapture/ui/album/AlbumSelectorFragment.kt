package com.wooooooak.lastcapture.ui.album


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.wooooooak.lastcapture.databinding.FragmentScreenShotAlbumBinding
import com.wooooooak.lastcapture.ui.album.adapter.AlbumListAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class AlbumSelectorFragment : Fragment() {

    private lateinit var binding: FragmentScreenShotAlbumBinding

    private val viewModel: AlbumSelectorViewModel by viewModel()

    private val adapter: AlbumListAdapter by lazy {
        AlbumListAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScreenShotAlbumBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBinding()
        subscribeViewModel()
    }

    private fun initBinding() {
        with(binding) {
            viewModel = viewModel
            albumListView.adapter = adapter
            albumListView.layoutManager = GridLayoutManager(requireContext(), 2)
            lifecycleOwner = this@AlbumSelectorFragment
            executePendingBindings()
        }
    }

    private fun subscribeViewModel() {
        viewModel.albumList.observe(this, Observer {
            adapter.submitList(it)
        })
    }

}
