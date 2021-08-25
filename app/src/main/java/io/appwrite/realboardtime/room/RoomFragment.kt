package io.appwrite.realboardtime.room

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import io.appwrite.Client
import io.appwrite.realboardtime.R
import io.appwrite.realboardtime.core.ClientViewModelFactory
import io.appwrite.realboardtime.core.PROJECT_ID
import io.appwrite.realboardtime.databinding.FragmentRoomBinding
import io.appwrite.realboardtime.drawing.DrawingFragment

class RoomFragment : Fragment() {

    private val args by navArgs<RoomFragmentArgs>()

    private val viewModel by viewModels<RoomViewModel> {
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
        val binding: FragmentRoomBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_room,
            container,
            false
        )

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        val view = binding.root

        val fragment = DrawingFragment.newInstance(onProducePathSegment = {
            viewModel.createPathDocument(it)
        }).apply {
            // Consume remote paths
            viewModel.incomingSegments.observe(this@RoomFragment.viewLifecycleOwner) {
                consumePathSegment(it)
            }
        }

        childFragmentManager.beginTransaction()
            .add(R.id.fragmentContainer, fragment)
            .commit()

        viewModel.message.observe(viewLifecycleOwner, ::showMessage)

        return view
    }

    private fun showMessage(message: RoomMessage?) {
        val builder = AlertDialog.Builder(requireContext())
        when (message!!) {

        }
        builder.setNeutralButton(R.string.ok) { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }
}