package com.tinkoff_sirius.koshelok.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import com.tinkoff_sirius.koshelok.R
import com.tinkoff_sirius.koshelok.adapters.MainRecyclerAdapter
import com.tinkoff_sirius.koshelok.databinding.FragmentMainBinding
import com.tinkoff_sirius.koshelok.model.MainItem

class MainFragment : Fragment() {

    private val WAIT_FOR_PUSH_AGAIN = 3000L
    private val viewModel: MainViewModel by viewModels()
    private val binding by viewBinding(FragmentMainBinding::bind)
    private var exitFlag = false

    private val recyclerView: RecyclerView by lazy(LazyThreadSafetyMode.NONE) {
        binding.recyclerView
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecycler()
        initButtons()
    }

    private fun initButtons() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun onBackPressed() {
        if (exitFlag) {
            requireActivity().finish()
        } else {
            exitFlag = true
            Snackbar
                .make(binding.root, getString(R.string.snackbar_push_exit), Snackbar.LENGTH_LONG)
                .show()
            Handler(Looper.getMainLooper()!!).postDelayed({
                    exitFlag = false
                }, WAIT_FOR_PUSH_AGAIN)
        }
    }

    private fun initRecycler() {
        val mainRecyclerAdapter = MainRecyclerAdapter()

        recyclerView.apply {
            adapter = mainRecyclerAdapter
            layoutManager = LinearLayoutManager(this@MainFragment.context)
        }

        val mTransaction = listOf<MainItem>(
            MainItem.Header("Кошелек 1", "60 000", "170 000", "66400", "230 000")
        )
        mainRecyclerAdapter.setData(mTransaction)
    }
}
