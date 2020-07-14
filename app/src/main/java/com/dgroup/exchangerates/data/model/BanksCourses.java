package com.dgroup.exchangerates.data.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "Exchange_Rates", strict=false)
public class BanksCourses {

    @Element(name = "Actual_Rates")
    private ActualRates mActualRates;

    @Element(name = "Not_Actual_Rates", required = false)
    public NonActualRates mNonActualRates;

    public List<ServBankCourse> getActualCourses(){
        return mActualRates.mServBankCourses;
    }

    public List<ServBankCourse> getNonActualCourses(){
        return mNonActualRates.mServBankCourses;
    }
}

class ActualRates{
    @ElementList(inline = true)
    public List<ServBankCourse> mServBankCourses;
}

class NonActualRates{
    @ElementList(inline = true, required = false)
    public List<ServBankCourse> mServBankCourses;
}