package io.appwrite.realboardtime.menu

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import io.appwrite.Client
import io.appwrite.realboardtime.ClientViewModelFactory
import io.appwrite.realboardtime.PROJECT_ID
import io.appwrite.realboardtime.R
import io.appwrite.realboardtime.databinding.MenuFragmentBinding
import io.appwrite.realboardtime.model.MenuMessage
import io.appwrite.realboardtime.model.MenuMessage.*
import io.appwrite.realboardtime.model.Room

class MenuFragment : Fragment() {

    private val viewModel by viewModels<MenuViewModel> {
        ClientViewModelFactory(
            Client(requireContext())
                .setEndpoint("https://realtime.appwrite.org/v1")
                .setProject(PROJECT_ID)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: MenuFragmentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.menu_fragment,
            container,
            false
        )

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        val view = binding.root
        val constraintLayout = view.findViewById<ConstraintLayout>(R.id.layout)
        val animationDrawable = constraintLayout.background as AnimationDrawable

        with(animationDrawable) {
            setEnterFadeDuration(2000)
            setExitFadeDuration(4000)
            start()
        }

        viewModel.room.observe(viewLifecycleOwner, ::navigateToRoom)
        viewModel.message.observe(viewLifecycleOwner, ::showMessage)

        return view
    }

    private fun navigateToRoom(room: Room) {
        findNavController().navigate(
            MenuFragmentDirections.menuToBoardAction(room.id, room.name)
        )
    }

    private fun showMessage(message: MenuMessage?) {
        val builder = AlertDialog.Builder(requireContext())
        when (message!!) {
            ROOM_EXISTS -> {
                builder.setTitle(R.string.oops)
                    .setMessage(R.string.room_exists)
            }
            ROOM_CREATE_FAILED -> {
                builder.setTitle(R.string.aw_no)
                    .setMessage(R.string.error_room_create)
            }
            ROOM_NAME_INVALID -> {
                builder.setTitle(R.string.oops)
                    .setMessage(R.string.room_name_invalid)
            }
            ROOM_PASSWORD_INVALID -> {
                builder.setTitle(R.string.oops)
                    .setMessage(R.string.room_password_invalid)
            }
            ROOM_INVALID_CREDENTIALS -> {
                builder.setTitle(R.string.oops)
                    .setMessage(R.string.room_credentials_invalid)
            }
        }
        builder.setNeutralButton(R.string.ok) { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }
}