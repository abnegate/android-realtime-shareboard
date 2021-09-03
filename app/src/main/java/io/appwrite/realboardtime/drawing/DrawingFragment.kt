package io.appwrite.realboardtime.drawing

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.appwrite.realboardtime.R
import io.appwrite.realboardtime.colorpicker.ColorPickerDialog
import io.appwrite.realboardtime.colorpicker.ColorSwatch
import io.appwrite.realboardtime.databinding.FragmentDrawingBinding
import io.appwrite.realboardtime.model.SyncPath
import java.io.Serializable


class DrawingFragment : Fragment() {

    companion object {
        fun newInstance(
            onProducePathSegment: (SyncPath) -> Unit
        ): DrawingFragment {
            val args = bundleOf(
                "onProducePathSegment" to onProducePathSegment as Serializable
            )
            val frag = DrawingFragment()
            frag.arguments = args
            return frag
        }
    }

    private val viewModel by viewModels<DrawingViewModel>()
    private var drawingBoard: DrawingView? = null
    private var colorButton: FloatingActionButton? = null
    private var logoImage: ImageView? = null

    private lateinit var bounceAnimation: Animation

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val args = requireArguments()
        val onProduce = args.getSerializable("onProducePathSegment") as (SyncPath) -> Unit
        val binding = DataBindingUtil.inflate<FragmentDrawingBinding>(
            LayoutInflater.from(requireContext()),
            R.layout.fragment_drawing,
            container,
            false
        )

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        val view = binding.root

        colorButton = view.findViewById(R.id.colorFab)
        logoImage = view.findViewById(R.id.appwriteLogo)
        drawingBoard = view.findViewById(R.id.viewDraw)
        drawingBoard?.setOnProducePathSegmentListener {
            onProduce.invoke(it)
        }

        bounceAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.bounce)

        viewModel.paintColor.observe(viewLifecycleOwner) {
            drawingBoard!!.paintColor = it
            colorButton!!.backgroundTintList = ColorStateList.valueOf(it)
            logoImage!!.imageTintList = ColorStateList.valueOf(it)
            logoImage!!.startAnimation(bounceAnimation)
        }

        viewModel.message.observe(viewLifecycleOwner, ::handleMessage)

        return view
    }

    fun consumePathSegment(path: SyncPath) {
        drawingBoard?.drawLine(path)
    }

    private fun handleMessage(message: DrawingMessage?) {
        when (message!!) {
            DrawingMessage.CHANGE_COLOR -> {
                ColorPickerDialog.Builder(requireActivity())
                    .setTitle(R.string.pick_color)
                    .setColorSwatch(ColorSwatch._500)
                    .setColorListener { color, _ ->
                        viewModel.setPaintColor(color)
                    }
                    .build()
                    .show()
            }
        }
    }
}