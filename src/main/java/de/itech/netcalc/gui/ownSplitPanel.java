package de.itech.netcalc.gui;

import javax.swing.*;

/**
 * Created by it5-senkjo on 02.06.2017.
 */


public class ownSplitPanel extends JSplitPane
{
    ownSplitPanel(int split)
    {
        super(split);
    }
    private final int location = this.getHeight() -5;
    {
        setDividerLocation( location );
    }
    @Override
    public int getDividerLocation() {
        return location ;
    }
    @Override
    public int getLastDividerLocation() {
        return location ;
    }
}