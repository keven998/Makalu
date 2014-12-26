package com.aizou.core.widget.section;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Rjm on 2014/12/26.
 */
public interface SectionAdapter {
    public boolean isHeader ( int globalPosition );

    public int getSection ( int globalPosition );

    public int getPositionInSection ( int globalPosition );

    public View getView ( int section, int position, View convertView, ViewGroup parent );

    public View getHeaderView ( int section, View convertView, ViewGroup parent );

    public Object getItem ( int section, int position );

    public long getItemId ( int section, int position );

    public int getHeaderItemViewType ( int section );

    public int getItemViewType ( int section, int position );

    public int getItemViewTypeCount ();

    public int getHeaderViewTypeCount ();

    public int getGlobalCount ();

    public int getGlobalPositionForHeader ( int section );

    public int getGlobalPositionForItem ( int section, int position );

    public boolean doesSectionHaveHeader ( int section );

    public boolean shouldListHeaderFloat ( int headerIndex );
}
