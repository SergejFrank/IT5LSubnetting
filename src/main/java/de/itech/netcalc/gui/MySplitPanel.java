package de.itech.netcalc.gui;

import javax.swing.*;

public class MySplitPanel extends JSplitPane
{
    MySplitPanel(int split)
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