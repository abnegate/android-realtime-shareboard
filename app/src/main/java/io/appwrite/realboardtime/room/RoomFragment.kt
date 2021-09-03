package io.appwrite.realboardtime.room

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import io.appwrite.Client
import io.appwrite.realboardtime.R
import io.appwrite.realboardtime.core.ClientViewModelFactory
import io.appwrite.realboardtime.core.PROJECT_ID
import io.appwrite.realboardtime.databinding.FragmentRoomBinding
import io.appwrite.realboardtime.drawing.DrawingFragment
import io.appwrite.realboardtime.drawing.DrawingMessage
import io.appwrite.realboardtime.room.ParticipantLeaveWatcherService.Companion.ROOM_ID_EXTRA

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

        requireActivity().startService(
            Intent(requireContext(), ParticipantLeaveWatcherService::class.java).apply {
                putExtra(ROOM_ID_EXTRA, args.roomId)
            }
        )

        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            handleMessage(RoomMessage.GO_BACK)
        }
        callback.isEnabled = true

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

        viewModel.message.observe(viewLifecycleOwner, ::handleMessage)

        return view
    }

    private fun handleMessage(message: RoomMessage?) {
        when (message!!) {
            RoomMessage.GO_BACK -> {
                parentFragment?.findNavController()
                    ?.popBackStack(R.id.menuFragment, false)
            }
        }
    }
}