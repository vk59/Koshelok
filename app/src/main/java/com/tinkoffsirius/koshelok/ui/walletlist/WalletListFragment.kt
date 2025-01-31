package com.tinkoffsirius.koshelok.ui.walletlist

import android.content.Context
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.tinkoffsirius.koshelok.R
import com.tinkoffsirius.koshelok.appComponent
import com.tinkoffsirius.koshelok.databinding.FragmentWalletListBinding
import com.tinkoffsirius.koshelok.di.modules.ViewModelFactory
import com.tinkoffsirius.koshelok.entities.Currency
import com.tinkoffsirius.koshelok.ui.walletlist.adapters.WalletItem
import com.tinkoffsirius.koshelok.ui.walletlist.adapters.WalletRecyclerAdapter
import com.tinkoffsirius.koshelok.utils.DeleteDialog
import com.tinkoffsirius.koshelok.utils.ErrorSnackbarFactory
import com.tinkoffsirius.koshelok.utils.Event
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView
import java.net.ConnectException
import javax.inject.Inject


class WalletListFragment : Fragment() {

    private val binding by viewBinding(FragmentWalletListBinding::bind)

    private val navController by lazy { findNavController() }

    @Inject
    lateinit var walletListViewModelFactory: ViewModelFactory

    private val walletListViewModel: WalletListViewModel by viewModels(
        factoryProducer = { walletListViewModelFactory }
    )

    private val walletRecyclerAdapter by lazy {
        WalletRecyclerAdapter(
            { walletItem ->
                walletListViewModel.showWallet(walletItem)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribeBy(
                        onComplete = {
                            navController.navigate(R.id.action_walletListFragment_to_mainFragment)
                        },
                        onError = Timber::e
                    )
            },
            { item -> showDeleteDialog(item) },
            { item ->
                walletListViewModel.editWallet(item)
                navController.navigate(R.id.action_walletListFragment_to_walletEditingFragment)
            }
        ) { navController.navigate(R.id.action_walletListFragment_to_mainFragment) }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context.appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val contextThemeWrapper: Context =
            ContextThemeWrapper(requireContext(), R.style.Theme_Koshelok_Wallet)

        val localInflater = inflater.cloneInContext(contextThemeWrapper)

        return localInflater.inflate(R.layout.fragment_wallet_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAppbar()
        initRecycler()
        binding.buttonAddWallet.setOnClickListener {
            navController.navigate(R.id.action_walletListFragment_to_setNameWalletFragment)
        }
        observeStatus()
        firstEntranceShow()
    }

    private fun firstEntranceShow() {
        MaterialShowcaseView.Builder(requireActivity())
            .setTarget(binding.buttonAddWallet)
            .setTargetTouchable(true)
            .setDismissOnTargetTouch(true)
            .setMaskColour(R.color.black)
            .setContentText("Добро пожаловать! Создайте ваш первый кошелек!")
            .singleUse(SHOWCASE_ID_WALLET)
            .show()
    }

    override fun onResume() {
        super.onResume()
        requireActivity().window.statusBarColor = requireContext().getColor(R.color.main_blue)
    }

    override fun onStop() {
        super.onStop()
        requireActivity().window.statusBarColor = requireContext().getColor(R.color.white)
    }

    private fun initRecycler() {
        binding.walletsRecycler.apply {
            adapter = walletRecyclerAdapter
            layoutManager = LinearLayoutManager(this.context)
        }

        binding.textNoEntities.visibility = View.GONE
        walletListViewModel.items.observe(viewLifecycleOwner) {
            walletRecyclerAdapter.setData(it)
        }
    }

    private fun initAppbar() {
        binding.apply {
            cardIncome.icon.setImageResource(R.drawable.ic_green_point)
            cardIncome.typeCard.text = "Общий доход"
            cardOutcome.icon.setImageResource(R.drawable.ic_red_point)
            cardOutcome.typeCard.text = "Общий расход"
            walletListViewModel.userInfoBalance.observe(viewLifecycleOwner) {
                overallBalance.text = it.overallBalance
                cardIncome.textMoneyCard.text = it.overallIncome
                cardOutcome.textMoneyCard.text = it.overallOutcome
            }
        }
    }

    private fun observeStatus() {
        binding.swipeLayout.setOnRefreshListener {
            walletListViewModel.updateUserInfo()
        }

        walletListViewModel.currencyData.observe(viewLifecycleOwner) {
            with(binding) {
                usdExchange.currencyName.text = Currency.USD.name
                usdExchange.rate.text = it.usd.slice(0..4)
                if (it.usdUp) {
                    usdExchange.isUp.setImageResource(R.drawable.ic_more)
                } else {
                    usdExchange.isUp.setImageResource(R.drawable.ic_less)
                }

                eurExchange.currencyName.text = Currency.EUR.name
                eurExchange.rate.text = it.eur.slice(0..4)
                if (it.eurUp) {
                    eurExchange.isUp.setImageResource(R.drawable.ic_more)
                } else {
                    eurExchange.isUp.setImageResource(R.drawable.ic_less)
                }

                gbpExchange.currencyName.text = "GBP"
                gbpExchange.rate.text = it.gbp.slice(0..4)
                if (it.gbpUp) {
                    gbpExchange.isUp.setImageResource(R.drawable.ic_more)
                } else {
                    gbpExchange.isUp.setImageResource(R.drawable.ic_less)
                }
            }
        }

        walletListViewModel.status.observe(viewLifecycleOwner) { event ->
            when (event) {
                is Event.Success -> {
                    binding.swipeLayout.isRefreshing = false
                }
                is Event.Loading -> {
                    binding.swipeLayout.isRefreshing = true
                }
                is Event.Error -> {
                    binding.swipeLayout.isRefreshing = false
                    showError(event)
                }
            }
        }
    }

    private fun showError(event: Event.Error) {
        if (event.throwable is ConnectException) {
            ErrorSnackbarFactory(binding.root).create(
                R.drawable.ic_connection_off,
                getString(R.string.no_connection)
            ).show()
        } else {
            ErrorSnackbarFactory(binding.root).create(
                R.drawable.ic_warning,
                getString(R.string.something_went_wrong)
            ).show()
        }
    }

    private fun showDeleteDialog(item: WalletItem) {
        DeleteDialog(
            "Вы действительно хотите удалить кошелек?",
            { _, _ ->
                walletListViewModel.deleteWallet(item)
            },
            { dialog, _ -> dialog.cancel() },
            requireContext()
        ).create().show()
    }

    companion object {
        private const val SHOWCASE_ID_WALLET = "WALLET_LIST"
    }
}
