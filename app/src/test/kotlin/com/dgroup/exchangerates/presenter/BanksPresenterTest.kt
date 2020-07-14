package com.dgroup.exchangerates.presenter;

import android.util.Log
import com.dgroup.exchangerates.data.BanksRepository
import com.dgroup.exchangerates.features.banks.BankCoursesMvpView
import com.dgroup.exchangerates.features.banks.BankCoursesPresenter
import com.dgroup.exchangerates.utils.pref.TinyDbWrap
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import rx.Observable


/**
 * Created by gabber on 16.04.17.
 */
@RunWith(PowerMockRunner::class)
@PrepareForTest(Log::class, TinyDbWrap::class)
class BanksPresenterTest : BasePresenterTest() {

    @Mock
    lateinit var view: BankCoursesMvpView

    @Mock
    lateinit var interactor: BanksRepository

    @Mock
    lateinit var codesObs: Observable<Map<String, Integer>>

    @InjectMocks
    lateinit var presenter: BankCoursesPresenter

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun testFirstSubscribe() {
        PowerMockito.mockStatic(Log::class.java)
        PowerMockito.mockStatic(TinyDbWrap::class.java)
//        `when`(TinyDbWrap.getInstance().getInt("", 0)).thenReturn(7701)
        `when`(Log.i("", "")).thenReturn(0)

        presenter.loadBanksData()

        Mockito.verify(view, Mockito.times(1)).showLoading()
        //    Mockito.verify(interactor, Mockito.times(1)).getPokemonList(presenter)
    }
}
