package com.example.android.iscrape;

import java.util.ArrayList;

public class ProfileItems {
    ArrayList<String> profilePostShortcodeList = new ArrayList<>();
    ArrayList<String> profilePostAccessibility_captionList = new ArrayList<>();

    public ProfileItems(ArrayList<String> profilePostShortcodeList, ArrayList<String> profilePostAccessibility_captionList) {
        this.profilePostShortcodeList = profilePostShortcodeList;
        this.profilePostAccessibility_captionList = profilePostAccessibility_captionList;
    }

    public ArrayList<String> getProfilePostShortcodeList() {
        return profilePostShortcodeList;
    }

    public void setProfilePostShortcodeList(ArrayList<String> profilePostShortcodeList) {
        this.profilePostShortcodeList = profilePostShortcodeList;
    }

    public ArrayList<String> getProfilePostAccessibility_captionList() {
        return profilePostAccessibility_captionList;
    }

    public void setProfilePostAccessibility_captionList(ArrayList<String> profilePostAccessibility_captionList) {
        this.profilePostAccessibility_captionList = profilePostAccessibility_captionList;
    }
}
