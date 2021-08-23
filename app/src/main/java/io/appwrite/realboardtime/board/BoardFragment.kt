package io.appwrite.realboardtime.board

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import io.appwrite.Client
import io.appwrite.realboardtime.ClientViewModelFactory
import io.appwrite.realboardtime.PROJECT_ID
import io.appwrite.realboardtime.R
import io.appwrite.realboardtime.databinding.BoardFragmentBinding
import io.appwrite.realboardtime.drawing.DrawingView
import io.appwrite.realboardtime.model.BoardMessage

class BoardFragment : Fragment() {

    private val args by navArgs<BoardFragmentArgs>()

    private val viewModel by viewModels<BoardViewModel> {
        ClientViewModelFactory(
            Client(requireContext())
                .setEndpoint("https://realtime.appwrite.org/v1")
                .setProject(PROJECT_ID),
            args.roomId
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: BoardFragmentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.board_fragment,
            container,
            false
        )

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        val view = binding.root

        val drawingFragment = childFragmentManager.findFragmentByTag("drawing_view")
        val drawingBoard = view.findViewById<DrawingView>(R.id.viewDraw)



        viewModel.message.observe(viewLifecycleOwner, ::showMessage)

        return view
    }


    private fun showMessage(message: BoardMessage?) {
        val builder = AlertDialog.Builder(requireContext())
        when (message!!) {

        }
        builder.setNeutralButton(R.string.ok) { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }
}