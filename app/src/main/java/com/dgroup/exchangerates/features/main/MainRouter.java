package com.dgroup.exchangerates.features.main;

import com.dgroup.exchangerates.features.banks.BanksRouter;
import com.dgroup.exchangerates.features.cb_rates.CBRouter;



public interface MainRouter extends CBRouter, BanksRouter{
}
