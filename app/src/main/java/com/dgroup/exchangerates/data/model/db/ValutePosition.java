package com.dgroup.exchangerates.data.model.db;

import org.greenrobot.greendao.annotation.*;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table "VALUTE_POSITION".
 */
@Entity
public class ValutePosition {

    @Id
    private Long id;
    private int position;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    @Generated(hash = 381711129)
    public ValutePosition() {
    }

    public ValutePosition(Long id) {
        this.id = id;
    }

    @Generated(hash = 1432429039)
    public ValutePosition(Long id, int position) {
        this.id = id;
        this.position = position;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}
