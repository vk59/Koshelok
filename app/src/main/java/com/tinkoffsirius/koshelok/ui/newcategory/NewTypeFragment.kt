package com.tinkoffsirius.koshelok.ui.newcategory

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.tinkoffsirius.koshelok.Dependencies
import com.tinkoffsirius.koshelok.R
import com.tinkoffsirius.koshelok.databinding.NewTypeFragmentBinding
import com.tinkoffsirius.koshelok.entities.TransactionType

class NewTypeFragment : Fragment() {

    private val viewModel: NewCategoryViewModel by viewModels(
        factoryProducer = {
            Dependencies.newCategoryViewModelFactory
        })

    private val binding by viewBinding(NewTypeFragmentBinding::bind)

    private val navController by lazy { findNavController() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.new_type_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.newCategory.observe(viewLifecycleOwner, {
            when (it.typeName) {
                TransactionType.INCOME -> binding.operationTypeRadioButtonIncome.isChecked = true
                TransactionType.OUTCOME -> binding.operationTypeRadioButtonExpense.isChecked = true
                else -> {
                    binding.setType.isEnabled = false
                }
            }
        })

        initListeners(view)
    }

    private fun initListeners(v: View) {

        binding.operationTypeRadioButtonIncome.setOnClickListener {
            binding.setType.isClickable = false
            viewModel.updateNewCategoryType(TransactionType.INCOME)
                .observe(viewLifecycleOwner) {
                    binding.setType.isEnabled = true
                    binding.setType.isClickable = true
                }
        }

        binding.operationTypeRadioButtonExpense.setOnClickListener {
            binding.setType.isClickable = false
            viewModel.updateNewCategoryType(TransactionType.OUTCOME)
                .observe(viewLifecycleOwner) {
                    binding.setType.isEnabled = true
                    binding.setType.isClickable = true
                }
        }

        binding.setType.setOnClickListener {
            navController.popBackStack()
        }

        binding.toolbar.setOnClickListener {
            navController.popBackStack()
        }
    }
}
