package com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.books.readbooktxt.adapters.SWTextFontSettingsAdapter
import com.sanwashoseki.bookskozuchi.books.readbooktxt.listener.SWTextBookListenerActivity
import com.sanwashoseki.bookskozuchi.books.readbooktxt.models.SWTextSettingModel
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.SWConstants
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.SWFontsManager.Companion.fontFileNames
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.SWTextBookReaderActivity
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.models.SWTextFontSettingModel
import com.sanwashoseki.bookskozuchi.databinding.FragmentTextSizeBinding
import com.sanwashoseki.bookskozuchi.others.Const
import com.sanwashoseki.bookskozuchi.utilities.SWBookCacheManager

class SWTextFontSettingsFragment : Fragment(), View.OnClickListener {

    private var binding: FragmentTextSizeBinding? = null
    private var fontModel = SWTextSettingModel.FontSetting()
    private var idBook: Int? = null

    companion object {
        @JvmStatic
        fun newInstance(id: Int?) = SWTextFontSettingsFragment().apply {
            arguments = Bundle().apply {
                idBook = id
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_text_size, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun initUI() {
        val fontAdapter = SWTextFontSettingsAdapter(arrayListOf(),
            object : SWTextFontSettingsAdapter.Callback {
                override fun onFontClicked(fontSettingModel: SWTextFontSettingModel) {
                    if (activity is SWTextBookListenerActivity) {
                        (activity as SWTextBookListenerActivity).setFontName(fontSettingModel.name)
                    } else if (activity is SWTextBookReaderActivity) {
                        (activity as SWTextBookReaderActivity).setFontName(fontSettingModel.name)
                    }
                }
            })
        binding?.recyclerViewFont?.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false)
        binding?.recyclerViewFont?.adapter = fontAdapter

        binding?.let { it ->
            it.btnSpacingSmall.setOnClickListener(this)
            it.btnSpacingNormal.setOnClickListener(this)
            it.btnSpacingLager.setOnClickListener(this)
            it.btnPaddingSmall.setOnClickListener(this)
            it.btnPaddingNormal.setOnClickListener(this)
            it.btnPaddingLager.setOnClickListener(this)
            it.seekBarTextSize.setOnSeekBarChangeListener(object :
                SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean,
                ) {
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    if (activity is SWTextBookListenerActivity) {
                        (activity as SWTextBookListenerActivity).setFontSize(it.seekBarTextSize.progress)
                    } else if (activity is SWTextBookReaderActivity) {
                        (activity as SWTextBookReaderActivity).setFontSize(it.seekBarTextSize.progress)
                    }
                }
            })
        }
    }

    fun loadData() {
        if (SWBookCacheManager.checkExistFile(idBook, Const.FILE_SETTING_FONT)) {
            fontModel =
                Gson().fromJson(SWBookCacheManager.readFileCache(SWBookCacheManager.fileConfigText(
                    idBook,
                    Const.FILE_SETTING_FONT)),
                    SWTextSettingModel.FontSetting::class.java)
            Log.d("TAG", "onViewCreated: $fontModel")
        }
        displayData()
        updateData()
    }

    private fun displayData() {
        val adapter = (binding?.recyclerViewFont?.adapter as? SWTextFontSettingsAdapter)
        adapter?.data = createModelData()
        adapter?.notifyDataSetChanged()
    }

    private fun updateData() {
        binding?.let { it ->
            when (fontModel.fontSize) {
                SWConstants.FONT_SIZE_14 -> it.seekBarTextSize.progress =
                    SWConstants.PROGRESS_TEXT_SIZE_1
                SWConstants.FONT_SIZE_16 -> it.seekBarTextSize.progress =
                    SWConstants.PROGRESS_TEXT_SIZE_2
                SWConstants.FONT_SIZE_18 -> it.seekBarTextSize.progress =
                    SWConstants.PROGRESS_TEXT_SIZE_3
                SWConstants.FONT_SIZE_20 -> it.seekBarTextSize.progress =
                    SWConstants.PROGRESS_TEXT_SIZE_4
                SWConstants.FONT_SIZE_22 -> it.seekBarTextSize.progress =
                    SWConstants.PROGRESS_TEXT_SIZE_5
                SWConstants.FONT_SIZE_24 -> it.seekBarTextSize.progress =
                    SWConstants.PROGRESS_TEXT_SIZE_6
                SWConstants.FONT_SIZE_26 -> it.seekBarTextSize.progress =
                    SWConstants.PROGRESS_TEXT_SIZE_7
                SWConstants.FONT_SIZE_28 -> it.seekBarTextSize.progress =
                    SWConstants.PROGRESS_TEXT_SIZE_8
                SWConstants.FONT_SIZE_30 -> it.seekBarTextSize.progress =
                    SWConstants.PROGRESS_TEXT_SIZE_9
                SWConstants.FONT_SIZE_32 -> it.seekBarTextSize.progress =
                    SWConstants.PROGRESS_TEXT_SIZE_10
            }
            setSpacingSelected(fontModel.spacing)
            setPaddingSelected(fontModel.padding)
            if (requireActivity() is SWTextBookReaderActivity) {
                (activity as SWTextBookReaderActivity).setVertical(fontModel.isVertical)
                (activity as SWTextBookReaderActivity).setFontName(fontModel.fontName)
                (activity as SWTextBookReaderActivity).setFontSize(it.seekBarTextSize.progress)
                (activity as SWTextBookReaderActivity).setSpacing(fontModel.spacing)
                (activity as SWTextBookReaderActivity).setPadding(fontModel.padding)
            } else {
                (activity as SWTextBookListenerActivity).setVertical(fontModel.isVertical)
                (activity as SWTextBookListenerActivity).setFontName(fontModel.fontName)
                (activity as SWTextBookListenerActivity).setFontSize(it.seekBarTextSize.progress)
                (activity as SWTextBookListenerActivity).setSpacing(fontModel.spacing)
                (activity as SWTextBookListenerActivity).setPadding(fontModel.padding)
            }
        }
    }

    private fun createModelData(): ArrayList<SWTextFontSettingModel> {
        val items = ArrayList<SWTextFontSettingModel>()
        fontFileNames.forEach {
            val textFont = SWTextFontSettingModel()
            textFont.name = it
            textFont.isSelected = fontModel.fontName == it
            items.add(textFont)
        }
        return items
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setSpacingSelected(spacing: Float) {
        binding?.let { it ->
            when (spacing) {
                SWConstants.SPACING_SMALL -> {
                    it.btnSpacingSmall.setImageDrawable(context?.getDrawable(R.drawable.ic_reading_spacing_1_select))
                    it.btnSpacingNormal.setImageDrawable(context?.getDrawable(R.drawable.ic_reading_spacing_2))
                    it.btnSpacingLager.setImageDrawable(context?.getDrawable(R.drawable.ic_reading_spacing_3))
                }
                SWConstants.SPACING_MEDIUM -> {
                    it.btnSpacingSmall.setImageDrawable(context?.getDrawable(R.drawable.ic_reading_spacing_1))
                    it.btnSpacingNormal.setImageDrawable(context?.getDrawable(R.drawable.ic_reading_spacing_2_select))
                    it.btnSpacingLager.setImageDrawable(context?.getDrawable(R.drawable.ic_reading_spacing_3))
                }
                SWConstants.SPACING_LAGER -> {
                    it.btnSpacingSmall.setImageDrawable(context?.getDrawable(R.drawable.ic_reading_spacing_1))
                    it.btnSpacingNormal.setImageDrawable(context?.getDrawable(R.drawable.ic_reading_spacing_2))
                    it.btnSpacingLager.setImageDrawable(context?.getDrawable(R.drawable.ic_reading_spacing_3_select))
                }
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setPaddingSelected(padding: Float) {
        binding?.let { it ->
            when (padding) {
                SWConstants.PADDING_SMALL -> {
                    it.btnPaddingSmall.setImageDrawable(context?.getDrawable(R.drawable.ic_settings_padding_narrow_selected))
                    it.btnPaddingNormal.setImageDrawable(context?.getDrawable(R.drawable.ic_settings_padding_normal))
                    it.btnPaddingLager.setImageDrawable(context?.getDrawable(R.drawable.ic_settings_padding_large))
                }
                SWConstants.PADDING_MEDIUM -> {
                    it.btnPaddingSmall.setImageDrawable(context?.getDrawable(R.drawable.ic_settings_padding_narrow))
                    it.btnPaddingNormal.setImageDrawable(context?.getDrawable(R.drawable.ic_settings_padding_normal_selected))
                    it.btnPaddingLager.setImageDrawable(context?.getDrawable(R.drawable.ic_settings_padding_large))
                }
                SWConstants.PADDING_LAGER -> {
                    it.btnPaddingSmall.setImageDrawable(context?.getDrawable(R.drawable.ic_settings_padding_narrow))
                    it.btnPaddingNormal.setImageDrawable(context?.getDrawable(R.drawable.ic_settings_padding_normal))
                    it.btnPaddingLager.setImageDrawable(context?.getDrawable(R.drawable.ic_settings_padding_large_selected))
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnSpacingSmall -> {
                if (activity is SWTextBookListenerActivity) {
                    (activity as SWTextBookListenerActivity).setSpacing(SWConstants.SPACING_SMALL)
                } else if (activity is SWTextBookReaderActivity) {
                    (activity as SWTextBookReaderActivity).setSpacing(SWConstants.SPACING_SMALL)
                }
                setSpacingSelected(SWConstants.SPACING_SMALL)
            }
            R.id.btnSpacingNormal -> {
                if (activity is SWTextBookListenerActivity) {
                    (activity as SWTextBookListenerActivity).setSpacing(SWConstants.SPACING_MEDIUM)
                } else if (activity is SWTextBookReaderActivity) {
                    (activity as SWTextBookReaderActivity).setSpacing(SWConstants.SPACING_MEDIUM)
                }
                setSpacingSelected(SWConstants.SPACING_MEDIUM)
            }
            R.id.btnSpacingLager -> {
                if (activity is SWTextBookListenerActivity) {
                    (activity as SWTextBookListenerActivity).setSpacing(SWConstants.SPACING_LAGER)
                } else if (activity is SWTextBookReaderActivity) {
                    (activity as SWTextBookReaderActivity).setSpacing(SWConstants.SPACING_LAGER)
                }
                setSpacingSelected(SWConstants.SPACING_LAGER)
            }
            R.id.btnPaddingSmall -> {
                if (activity is SWTextBookListenerActivity) {
                    (activity as SWTextBookListenerActivity).setPadding(SWConstants.PADDING_SMALL)
                } else if (activity is SWTextBookReaderActivity) {
                    (activity as SWTextBookReaderActivity).setPadding(SWConstants.PADDING_SMALL)
                }
                setPaddingSelected(SWConstants.PADDING_SMALL)
            }
            R.id.btnPaddingNormal -> {
                if (activity is SWTextBookListenerActivity) {
                    (activity as SWTextBookListenerActivity).setPadding(SWConstants.PADDING_MEDIUM)
                } else if (activity is SWTextBookReaderActivity) {
                    (activity as SWTextBookReaderActivity).setPadding(SWConstants.PADDING_MEDIUM)
                }
                setPaddingSelected(SWConstants.PADDING_MEDIUM)
            }
            R.id.btnPaddingLager -> {
                if (activity is SWTextBookListenerActivity) {
                    (activity as SWTextBookListenerActivity).setPadding(SWConstants.PADDING_LAGER)
                } else if (activity is SWTextBookReaderActivity) {
                    (activity as SWTextBookReaderActivity).setPadding(SWConstants.PADDING_LAGER)
                }
                setPaddingSelected(SWConstants.PADDING_LAGER)
            }
        }
    }

}