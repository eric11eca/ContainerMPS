package com.chen.eric.ui.components.navigation.tab;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.chen.eric.ui.util.UIUtils;
import com.chen.eric.ui.util.css.Overflow;
import com.chen.eric.ui.views.dashboard.DashboardView;

public class NaviTabs extends Tabs {
	private static final long serialVersionUID = 1L;
	private ComponentEventListener<SelectedChangeEvent> listener = (
				ComponentEventListener<SelectedChangeEvent>) 
					selectedChangeEvent -> navigateToSelectedTab();

	public NaviTabs() {
		addSelectedChangeListener(listener);
		getElement().setAttribute("overflow", "end");
		UIUtils.setOverflow(Overflow.HIDDEN, this);
	}

	public NaviTabs(NaviTab... naviTabs) {
		this();
		add(naviTabs);
	}

	public Tab addTab(String text) {
		Tab tab = new Tab(text);
		add(tab);
		return tab;
	}

	public Tab addTab(String text,
	                  Class<? extends Component> navigationTarget) {
		Tab tab = new NaviTab(text, navigationTarget);
		add(tab);
		return tab;
	}

	public Tab addClosableTab(String text,
	                          Class<? extends Component> navigationTarget) {
		ClosableNaviTab tab = new ClosableNaviTab(text, navigationTarget);
		add(tab);

		tab.getCloseButton().addClickListener(event -> {
			remove(tab);
			navigateToSelectedTab();
		});

		return tab;
	}

	public void navigateToSelectedTab() {
		if (getSelectedTab() instanceof NaviTab) {
			try {
				UI.getCurrent().navigate(
						((NaviTab) getSelectedTab()).getNavigationTarget());
			} catch (Exception e) {
				if (getTabCount() > 0) {
					setSelectedIndex(getTabCount() - 1);
				} else {
					UI.getCurrent().navigate(DashboardView.class);
				}
			}
		}
	}

	public void updateSelectedTab(String text,
	                              Class<? extends Component> navigationTarget) {
		Tab tab = getSelectedTab();
		tab.setLabel(text);

		if (tab instanceof NaviTab) {
			((NaviTab) tab).setNavigationTarget(navigationTarget);
		}

		if (tab instanceof ClosableNaviTab) {
			tab.add(((ClosableNaviTab) tab).getCloseButton());
		}

		navigateToSelectedTab();
	}

	public int getTabCount() {
		return Math.toIntExact(getChildren()
				.filter(component -> component instanceof Tab).count());
	}

}
