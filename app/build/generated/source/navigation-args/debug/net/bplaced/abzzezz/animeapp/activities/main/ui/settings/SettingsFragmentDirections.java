package net.bplaced.abzzezz.animeapp.activities.main.ui.settings;

import androidx.annotation.NonNull;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavDirections;
import net.bplaced.abzzezz.animeapp.R;

public class SettingsFragmentDirections {
  private SettingsFragmentDirections() {
  }

  @NonNull
  public static NavDirections actionNavChangelogToNavAnimeList() {
    return new ActionOnlyNavDirections(R.id.action_nav_changelog_to_nav_anime_list);
  }
}
