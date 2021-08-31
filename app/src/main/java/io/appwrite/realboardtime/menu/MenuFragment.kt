package io.appwrite.realboardtime.menu

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import io.appwrite.Client
import io.appwrite.realboardtime.R
import io.appwrite.realboardtime.core.ClientViewModelFactory
import io.appwrite.realboardtime.core.ENDPOINT
import io.appwrite.realboardtime.core.PROJECT_ID
import io.appwrite.realboardtime.core.hideSoftKeyBoard
import io.appwrite.realboardtime.databinding.FragmentMenuBinding
import io.appwrite.realboardtime.menu.MenuMessage.*
import io.appwrite.realboardtime.room.Room

class MenuFragment : Fragment() {

    private val viewModel by viewModels<MenuViewModel> {
        ClientViewModelFactory(
            Client(requireContext())
                .setEndpoint(ENDPOINT)
                .setProject(PROJECT_ID)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.inflate<FragmentMenuBinding>(
            inflater,
            R.layout.fragment_menu,
            container,
            false
        )

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        val view = binding.root
        val constraintLayout = view.findViewById<ConstraintLayout>(R.id.layout)
        val animationDrawable = constraintLayout.background as AnimationDrawable
        val inputs = view.findViewById<Group>(R.id.inputs)
        val pg = view.findViewById<ProgressBar>(R.id.progress)

        with(animationDrawable) {
            setEnterFadeDuration(2000)
            setExitFadeDuration(4000)
            start()
        }

        viewModel.isBusy.observe(viewLifecycleOwner) { showBusy(it, pg, inputs) }
        viewModel.room.observe(viewLifecycleOwner, ::navigateToRoom)
        viewModel.message.observe(viewLifecycleOwner, ::showMessage)

        return view
    }

    private fun showBusy(enabled: Boolean, progress: View, inputs: Group) {
        when (enabled) {
            true -> {
                requireActivity().hideSoftKeyBoard()
                progress.visibility = View.VISIBLE
                inputs.visibility = View.INVISIBLE
            }
            false -> {
                progress.visibility = View.INVISIBLE
                inputs.visibility = View.VISIBLE
            }
        }
    }

    private fun navigateToRoom(room: Room) {
        findNavController().navigate(
            MenuFragmentDirections.menuToBoardAction(room.id, room.name)
        )
    }

    private fun showMessage(message: MenuMessage?) {
        when (message!!) {
            ROOM_EXISTS -> {
                Snackbar.make(requireView(), R.string.room_exists, Snackbar.LENGTH_LONG)
            }
            ROOM_CREATE_FAILED -> {
                Snackbar.make(requireView(), R.string.error_room_create, Snackbar.LENGTH_LONG)
            }
            ROOM_NAME_INVALID -> {
                Snackbar.make(requireView(), R.string.room_name_invalid, Snackbar.LENGTH_LONG)
            }
            ROOM_PASSWORD_INVALID -> {
                Snackbar.make(requireView(), R.string.room_password_invalid, Snackbar.LENGTH_LONG)
            }
            ROOM_INVALID_CREDENTIALS -> {
                Snackbar.make(
                    requireView(),
                    R.string.room_credentials_invalid,
                    Snackbar.LENGTH_LONG
                )
            }
        }.show()
    }
}