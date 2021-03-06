package dev.cappee.treble.device

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.nativead.NativeAdView
import dev.cappee.treble.R
import dev.cappee.treble.main.recycler.RecyclerViewAdapter
import dev.cappee.treble.databinding.FragmentMainBinding
import dev.cappee.treble.databinding.LayoutNativeAdBinding
import dev.cappee.treble.main.MainViewModel
import dev.cappee.treble.main.recycler.ItemDecoration
import dev.cappee.treble.model.Data
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random

class DeviceFragment : Fragment() {

    private lateinit var ad: NativeAdView
    private lateinit var data: MutableList<Any>

    private val viewModel: MainViewModel by activityViewModels()

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //Init RecyclerView
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(ItemDecoration(resources.getDimensionPixelSize(R.dimen.recycler_items_margin)))
        }

        //Observe LiveData and update adapter
        viewModel.liveDataDevice.observe(viewLifecycleOwner, {
            if (this::ad.isInitialized) {
                data = mutableListOf(
                    Data(
                        R.string.general,
                        arrayOf(R.string.identifier, R.string.battery),
                        arrayOf(it.identifier, it.battery),
                        null),
                    ad,
                    Data(
                        R.string.chipset,
                        arrayOf(R.string.processor, R.string.graphic_card, R.string.architecture),
                        arrayOf(it.cpu, it.gpu, it.arch),
                        null),
                    Data(
                        R.string.memory,
                        arrayOf(R.string.ram, R.string.intenal_memory, R.string.external_memory),
                        arrayOf(it.ram, it.internalMemory, it.externalMemory),
                        null),
                    Data(
                        R.string.display,
                        arrayOf(R.string.dimensions, R.string.display_resolution, R.string.dpi, R.string.refresh_rate),
                        arrayOf(it.screenSize, it.screenResolution, it.dpi, it.refreshRate),
                        null)
                )
            } else {
                data = mutableListOf(
                    Data(
                        R.string.general,
                        arrayOf(R.string.identifier, R.string.battery),
                        arrayOf(it.identifier, it.battery),
                        null),
                    Data(
                        R.string.chipset,
                        arrayOf(R.string.processor, R.string.graphic_card, R.string.architecture),
                        arrayOf(it.cpu, it.gpu, it.arch),
                        null),
                    Data(
                        R.string.memory,
                        arrayOf(R.string.ram, R.string.intenal_memory, R.string.external_memory),
                        arrayOf(it.ram, it.internalMemory, it.externalMemory),
                        null),
                    Data(
                        R.string.display,
                        arrayOf(R.string.dimensions, R.string.display_resolution, R.string.dpi, R.string.refresh_rate),
                        arrayOf(it.screenSize, it.screenResolution, it.dpi, it.refreshRate),
                        null)
                )
            }
            binding.recyclerView.adapter = RecyclerViewAdapter(context, data)
            binding.progressBar.visibility = ProgressBar.INVISIBLE
        })


        viewModel.liveDataAdDevice.observe(viewLifecycleOwner, {
            if (isDetached) {
                it.destroy()
                return@observe
            }
            lifecycleScope.launch {
                val layout = LayoutNativeAdBinding.inflate(layoutInflater)
                val nativeAdView = layout.root
                with(layout) {
                    adMediaView.setImageScaleType(ImageView.ScaleType.CENTER)
                    adMediaView.setMediaContent(it.mediaContent)
                    adHeadline.text = it.headline
                    adBody.text = it.body
                    if (it.icon != null)
                        adIcon.setImageDrawable(it.icon.drawable)
                    adClick.text = it.callToAction
                    if (it.store.isNullOrEmpty()) {
                        adStorePrice.visibility = View.GONE
                        adRating.visibility = View.GONE
                    } else {
                        adStorePrice.text = "${it.store} | ${it.price}"
                        adRating.rating = it.starRating?.toFloat() ?: 0f
                    }
                    nativeAdView.callToActionView = adClick
                    println(it.mediaContent.mainImage)
                    nativeAdView.setNativeAd(it)
                }
                data.add(1, nativeAdView)
                binding.recyclerView.adapter?.notifyItemInserted(1)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}