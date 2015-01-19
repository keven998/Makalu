package com.aizou.core.widget.section;

import android.view.View;
import android.widget.AdapterView;
import android.widget.HeaderViewListAdapter;

abstract public class SectionListViewOnItemClickListener implements AdapterView.OnItemClickListener {
	@Override
	public void onItemClick ( AdapterView<?> parent, View view, int globalPosition, long id ) {
		SectionAdapter sectionedAdapter;
		int trueGlobalPostion = globalPosition;

		if (parent.getAdapter() instanceof HeaderViewListAdapter) {
			sectionedAdapter = (SectionAdapter) ((HeaderViewListAdapter) parent.getAdapter()).getWrappedAdapter();
			globalPosition -= ((HeaderViewListAdapter) parent.getAdapter()).getHeadersCount();
		} else {
			sectionedAdapter = (SectionAdapter) parent.getAdapter();
		}

		if (globalPosition >= sectionedAdapter.getGlobalCount()) {
			onListFooterClick(parent, view, globalPosition - sectionedAdapter.getGlobalCount(), id);
		} else if (globalPosition >= 0) {
			int section = sectionedAdapter.getSection(globalPosition);
			int position = sectionedAdapter.getPositionInSection(globalPosition);

			if (sectionedAdapter.isHeader(globalPosition)) {
				onSectionHeaderClick(parent, view, section, id);
			} else {
				onItemClick(parent, view, section, position, id);
			}
		} else {
			onListHeaderClick(parent, view, trueGlobalPostion, id);
		}
	}

	abstract public void onItemClick ( AdapterView<?> adapterView, View view, int section, int position, long id );

	abstract public void onSectionHeaderClick ( AdapterView<?> adapterView, View view, int section, long id );

	abstract public void onListHeaderClick ( AdapterView<?> adapterView, View view, int headerNumber, long id );

	abstract public void onListFooterClick ( AdapterView<?> adapterView, View view, int footerNumber, long id );
}
