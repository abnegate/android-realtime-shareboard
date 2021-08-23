package io.appwrite.realboardtime.drawing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import io.appwrite.realboardtime.R

class DrawingFragment : Fragment() {

    private val viewModel by viewModels<DrawingViewModel>(
        ownerProducer = { requireParentFragment() }
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.drawing_fragment, container, false)
        val drawingBoard = view.findViewById<DrawingView>(R.id.viewDraw)

        viewModel.penMode.observe(viewLifecycleOwner) {
            when (it) {
                PenMode.DRAW -> drawingBoard.enableEraser(false)
                PenMode.ERASE -> drawingBoard.enableEraser(true)
                null -> return@observe
            }
        }
        viewModel.paintColor.observe(viewLifecycleOwner) {
            drawingBoard.paintColor = it
        }
        viewModel.strokeWidth.observe(viewLifecycleOwner) {
            drawingBoard.strokeWidth = it
        }
        viewModel.strokeJoin.observe(viewLifecycleOwner) {
            drawingBoard.strokeJoin = it
        }
        viewModel.strokeCap.observe(viewLifecycleOwner) {
            drawingBoard.strokeCap = it
        }

        return view
    }
}