package io.appwrite.realboardtime.drawing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import io.appwrite.realboardtime.R
import java.io.Serializable

class DrawingFragment : Fragment() {

    companion object {
        fun newInstance(
            onProducePathSegment: (DrawPath) -> Unit
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val args = requireArguments()
        val onProduce = args.getSerializable("onProducePathSegment") as (DrawPath) -> Unit
        val view = inflater.inflate(R.layout.fragment_drawing, container, false)

        drawingBoard = view.findViewById(R.id.viewDraw)
        drawingBoard?.setOnProducePathSegmentListener {
            onProduce.invoke(it)
        }

        viewModel.penMode.observe(viewLifecycleOwner) {
            when (it) {
                PenMode.DRAW -> drawingBoard?.setEraserEnabled(false)
                PenMode.ERASE -> drawingBoard?.setEraserEnabled(true)
                null -> return@observe
            }
        }
        viewModel.paintColor.observe(viewLifecycleOwner) {
            drawingBoard?.paintColor = it
        }
        viewModel.strokeWidth.observe(viewLifecycleOwner) {
            drawingBoard?.strokeWidth = it.toFloat()
        }

        return view
    }

    fun consumePathSegment(path: DrawPath) {
        drawingBoard?.drawLine(
            path.x0 * drawingBoard!!.width,
            path.y0 * drawingBoard!!.height,
            path.x1 * drawingBoard!!.width,
            path.y1 * drawingBoard!!.height,
            path.color
        )
    }
}