package net.bplaced.abzzezz.animeapp.activities.main.ui.chagelog;

import androidx.annotation.NonNull;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavDirections;
import net.bplaced.abzzezz.animeapp.R;

public class ChangelogFragmentDirections {
  private ChangelogFragmentDirections() {
  }

  @NonNull
  public static NavDirections actionNavChangelogToNavAnimeList() {
    return new ActionOnlyNavDirections(R.id.action_nav_changelog_to_nav_anime_list);
  }
}
