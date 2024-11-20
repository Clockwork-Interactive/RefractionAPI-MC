package net.refractionapi.refraction.feature.screen.tab;

public class TabNavigator {

    private Tab[] tabs = new Tab[0];

    protected TabNavigator() {

    }

    protected void addTab(Tab tab) {
        Tab[] newTabs = new Tab[this.tabs.length + 1];
        System.arraycopy(this.tabs, 0, newTabs, 0, this.tabs.length);
        newTabs[this.tabs.length] = tab;
        this.tabs = newTabs;
    }

    public Tab getTab(int index) {
        return this.tabs[index];
    }

}
