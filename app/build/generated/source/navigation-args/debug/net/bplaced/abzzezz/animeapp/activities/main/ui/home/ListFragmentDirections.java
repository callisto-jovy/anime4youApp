package net.bplaced.abzzezz.animeapp.activities.main.ui.home;

import androidx.annotation.NonNull;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavDirections;
import net.bplaced.abzzezz.animeapp.R;

public class ListFragmentDirections {
  private ListFragmentDirections() {
  }

  @NonNull
  public static NavDirections actionNavAnimeListToNavSettings() {
    return new ActionOnlyNavDirections(R.id.action_nav_anime_list_to_nav_settings);
  }
}
